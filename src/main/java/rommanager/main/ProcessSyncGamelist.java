/* 
 * Copyright (C) 2018 phramusca ( https://github.com/phramusca/ )
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package rommanager.main;

import org.apache.commons.lang3.tuple.Pair;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.io.FilenameUtils;
import org.w3c.dom.Element;
import rommanager.utils.FileSystem;
import rommanager.utils.Popup;
import rommanager.utils.ProcessAbstract;
import rommanager.utils.ProgressBar;

//FIXME 7 Handle default roms from recalbox (move to "recalbox-default-roms" folder, get in local and integrate in export feature)
/**
 *
 * @author phramusca ( https://github.com/phramusca/JaMuz/ )
 */
public class ProcessSyncGamelist extends ProcessAbstract {

    private final String sourcePath;
    private final String exportPath;
    private final ProgressBar progressBarConsole;
    private final ProgressBar progressBarGame;
    private final TableModelRom tableModel;
    private final ICallBackProcess callBack;
    private final Map<String, Map<String, List<String>>> groupedLogs = new HashMap<>();
    private final String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
    // Counters for summary
    private int consolesProcessed = 0;
    private int totalGamesProcessed = 0;
    private int gamesDeleted = 0;
    private int gamesMissingMedia = 0;
    private int gamesMissingLocal = 0;
    private int gamesUpdated = 0;
    private int gamesUpdatedFromRecalbox = 0;  // Recalbox → RomManager
    private int gamesUpdatedFromRomManager = 0; // RomManager → Recalbox
    private int gamesUnchanged = 0;
    private int gamesSkipped = 0;
    private int gamelistsSaved = 0;
    private int gamelistsDeleted = 0;
    private int gamelistsCreated = 0;
    private int mediaFilesDeleted = 0;

    public ProcessSyncGamelist(
            String sourcePath,
            String exportPath,
            ProgressBar progressBarConsole,
            ProgressBar progressBarGame,
            TableModelRom tableModel,
            ICallBackProcess callBack) {
        super("Thread.ProcessRead");
        this.sourcePath = sourcePath;
        this.exportPath = exportPath;
        this.progressBarConsole = progressBarConsole;
        this.progressBarGame = progressBarGame;
        this.tableModel = tableModel;
        this.callBack = callBack;
    }

    @Override
    public void run() {

        try {
            Pair<Boolean, String> stopResult = EmulStation.stop();
            addLogEntry("system", "info", stopResult.getRight().trim());

            boolean emulationStationStopped = stopResult.getLeft();

            //Read all gamelist.xml files from export folder
            Map<Console, Gamelist> gamelists = new HashMap<>();
            progressBarConsole.setup(Console.values().length);
            for (Console console : Console.values()) {
                // FIXME !!! temporary !!! (limit sync to atari2600 for quick testing. Remove this after verification.)
                if (console != Console.atari2600) {
                    continue;
                }
                checkAbort();
                consolesProcessed++;
                progressBarConsole.progress(console.getName());
                File remoteFile = new File(FilenameUtils.concat(FilenameUtils.concat(exportPath, console.getSourceFolderName()), "gamelist.xml"));
                File backupFile = new File(FilenameUtils.concat(FilenameUtils.concat(exportPath, console.getSourceFolderName()), "gamelist.xml.bak"));
                if (remoteFile.exists()) {
                    FileSystem.copyFile(remoteFile, backupFile);
                    String consolePath = FilenameUtils.concat(exportPath, console.getSourceFolderName());
                    
                    List<RomVersion> romVersionsForConsole = tableModel.getRoms().values()
                            .stream()
                            .filter(r -> r.console.equals(console))
                            .map(r -> r.getExportableVersions())
                            .flatMap(List::stream)
                            .collect(Collectors.toList());

                    Gamelist gamelist = new Gamelist(remoteFile);
                    progressBarGame.setup(gamelist.getGames().values().size());
                    for (Pair<Element, Game> entry : gamelist.getGames().values()) {
                        checkAbort();
                        Element remoteGameElement = entry.getLeft();
                        Game remoteGame = entry.getRight();
                        progressBarGame.progress(remoteGame.getName());
                        totalGamesProcessed++;
                        File gameFile = new File(FilenameUtils.concat(FilenameUtils.concat(exportPath, console.getSourceFolderName()), remoteGame.getPath()));
                        if (!gameFile.exists()) {
                            gamelist.deleteGame(remoteGameElement);
                            gamesDeleted++;
                            addLogEntry(console.getName(), "deleted", remoteGame.getName() + " - file not found");
                            continue;
                        }
                        if (!checkMedia(console, gamelist, remoteGameElement, remoteGame.getImage())) {
                            gamesMissingMedia++;
                            addLogEntry(console.getName(), "missingMedia", remoteGame.getName() + " - image: " + remoteGame.getImage());
                            continue;
                        }
                        if (!checkMedia(console, gamelist, remoteGameElement, remoteGame.getThumbnail())) {
                            gamesMissingMedia++;
                            addLogEntry(console.getName(), "missingMedia", remoteGame.getName() + " - thumbnail: " + remoteGame.getThumbnail());
                            continue;
                        }
                        if (!checkMedia(console, gamelist, remoteGameElement, remoteGame.getVideo())) {
                            gamesMissingMedia++;
                            addLogEntry(console.getName(), "missingMedia", remoteGame.getName() + " - video: " + remoteGame.getVideo());
                            continue;
                        }

                        String keyVersion = FilenameUtils.getName(gameFile.getAbsolutePath());
                        List<RomVersion> collect = romVersionsForConsole.stream()
                                .filter(v -> v.getExportFilename(console, Destination.recalbox).equals(keyVersion))
                                .collect(Collectors.toList());
                        if (collect.size() == 1) {
                            RomVersion localVersion = collect.get(0);
                            Game localGame = localVersion.getGame();
                            
                            // Compare games to get synchronized result and change detection
                            GameComparisonResult comparisonResult = gamelist.compareGame(localGame, remoteGame);
                            Game synchronizedGame = comparisonResult.getGame();
                            
                            if (emulationStationStopped) {
                                if (comparisonResult.hasChanged()) {
                                    // Game has changes that need to be saved
                                    gamelist.setGame(synchronizedGame);
                                    
                                    if (comparisonResult.isLocalNewer()) {
                                        // RomManager data is newer: RomManager → Recalbox
                                        gamesUpdatedFromRomManager++;
                                        addLogEntry(console.getName(), "updatedFromRomManager", remoteGame.getName() + " - RomManager changes applied to Recalbox");
                                    } else {
                                        // Recalbox data is newer: Recalbox → RomManager
                                        gamesUpdatedFromRecalbox++;
                                        addLogEntry(console.getName(), "updatedFromRecalbox", remoteGame.getName() + " - Recalbox changes applied to RomManager");
                                    }
                                    
                                    gamesUpdated++;
                                    
                                    if (synchronizedGame.getImage() != null && !synchronizedGame.getImage().trim().equals("")) {
                                        BufferIcon.checkOrGetCoverIcon(synchronizedGame.getName(), FilenameUtils.concat(consolePath, synchronizedGame.getImage()));
                                    }
                                } else {
                                    // No local changes, just synchronized for display
                                    gamesUnchanged++;
                                    addLogEntry(console.getName(), "unchanged", remoteGame.getName() + " - no local changes");
                                }
                            } else {
                                // EmulationStation is running, only read from Recalbox
                                gamesSkipped++;
                                addLogEntry(console.getName(), "skipped", remoteGame.getName() + " - EmulationStation running");
                            }
                            
                            // Always log the read operation (retrieving info from Recalbox)
                            addLogEntry(console.getName(), "read", remoteGame.getName() + " - data retrieved from Recalbox");
                            
                            // Update local version with synchronized game for display
                            // The lastModifiedDate will be updated after XML save if needed
                            localVersion.setGame(synchronizedGame);

                        } else {
                            gamesMissingLocal++;
                            addLogEntry(console.getName(), "missing", keyVersion);
                        }
                    }
                    if (gamelist.hasChanged()) {
                        gamelist.save();
                        gamelistsSaved++;
                        addLogEntry(console.getName(), "saved", "gamelist updated with " + gamesUpdated + " changes");
                        
                        // Update lastModifiedDate for all synchronized games after XML save
                        // This ensures consistency between game.lastModifiedDate and file.lastModified()
                        for (RomVersion romVersion : romVersionsForConsole) {
                            Game currentGame = romVersion.getGame();
                            Game updatedGame = gamelist.updateGameLastModifiedDate(currentGame);
                            romVersion.setGame(updatedGame);
                        }
                    }
                    if (gamelist.getGames().isEmpty()) {
                        remoteFile.delete();
                        gamelistsDeleted++;
                        addLogEntry(console.getName(), "deleted", "empty gamelist removed");
                    } else {
                        // Clean up orphaned media files not referenced in gamelist.xml
                        cleanupOrphanedMedia(console, gamelist);
                        gamelists.put(console, gamelist);
                    }
                } else {
                    List<RomVersion> romVersionsForConsole = tableModel.getRoms().values()
                            .stream()
                            .filter(r -> r.console.equals(console))
                            .map(r -> r.getExportableVersions())
                            .flatMap(List::stream)
                            .collect(Collectors.toList());
                    if (romVersionsForConsole.isEmpty()) {
                        // No gamelist and no local data: expected, ignore
                    } else {
                        addLogEntry(console.getName(), "missingGamelist", "No gamelist.xml on remote - local data found: " + romVersionsForConsole.size() + " entries");
                        // If creation implemented elsewhere, increment created counter when used
                        // gamelistsCreated++;
                    }
                }
            }
            progressBarConsole.reset();
            tableModel.fireTableDataChanged();

            if (emulationStationStopped) {
                Pair<Boolean, String> startResult = EmulStation.start();
                addLogEntry("system", "info", startResult.getRight().trim());
            }

            progressBarGame.setIndeterminate("Saving ods file");
            if (RomManagerOds.createFile(tableModel, progressBarGame, sourcePath)) {
                callBack.actionPerformed();
            }
            progressBarGame.reset();

            writeLogFile();

            if (!groupedLogs.isEmpty()) {
                StringBuilder summary = new StringBuilder();
                summary.append("Sync complete\n\n");
                summary.append("Consoles processed: ").append(consolesProcessed).append("\n");
                summary.append("Games processed: ").append(totalGamesProcessed).append("\n");
                summary.append("Games updated: ").append(gamesUpdated).append("\n");
                summary.append("  - Recalbox → RomManager: ").append(gamesUpdatedFromRecalbox).append("\n");
                summary.append("  - RomManager → Recalbox: ").append(gamesUpdatedFromRomManager).append("\n");
                summary.append("Games unchanged: ").append(gamesUnchanged).append("\n");
                summary.append("Games skipped (EmulationStation running): ").append(gamesSkipped).append("\n");
                summary.append("Games missing locally: ").append(gamesMissingLocal).append("\n");
                summary.append("Games missing media: ").append(gamesMissingMedia).append("\n");
                summary.append("Games deleted (orphaned entries): ").append(gamesDeleted).append("\n");
                summary.append("Media files deleted (orphaned): ").append(mediaFilesDeleted).append("\n");
                summary.append("Gamelists saved: ").append(gamelistsSaved).append("\n");
                summary.append("Gamelists deleted (empty): ").append(gamelistsDeleted).append("\n");
                summary.append("Gamelists created: ").append(gamelistsCreated).append("\n\n");

                // Add grouped summary (exclude system logs)
                summary.append("Summary by console and type:\n");
                for (Map.Entry<String, Map<String, List<String>>> consoleEntry : groupedLogs.entrySet()) {
                    String console = consoleEntry.getKey();
                    if (!console.equals("system")) {  // Skip system logs
                        summary.append("- ").append(console).append(":\n");
                        for (Map.Entry<String, List<String>> typeEntry : consoleEntry.getValue().entrySet()) {
                            String type = typeEntry.getKey();
                            int count = typeEntry.getValue().size();
                            // Format type names nicely
                            String formattedType = formatTypeName(type);
                            summary.append("  * ").append(formattedType).append(": ").append(count).append("\n");
                        }
                    }
                }
                String logFilePath = "cache/gamelists/sync-" + timestamp + ".log";
                summary.append("\nLog file: ").append(logFilePath).append("\n");
                Popup.showTextWithLogLink("Sync game data complete", summary.toString(), logFilePath);
            } else {
                Popup.warning("Process ended without any logs. This is unexpected.");
            }
        } catch (InterruptedException ex) {
//			Popup.info("Aborted by user");
        } catch (Exception ex) {
            Popup.error(ex);
        } finally {
            callBack.completed();
        }
    }

    private boolean checkMedia(Console console, Gamelist gamelist, Element remoteGameElement, String filename) {
        File mediaFile = new File(FilenameUtils.concat(FilenameUtils.concat(exportPath, console.getSourceFolderName()), filename));
        if (!filename.contains("ZZZ") && !mediaFile.exists()) {
            gamelist.removeScraped(remoteGameElement);
            //Some file is missing and the only way (I found) to force re-scrap is removing entry from xml
//                            gamelist.deleteGame(remoteGameElement);
            return false;
        }
        return true;
    }
    
    /**
     * Clean up orphaned media files (images, videos, thumbnails) that are not referenced in gamelist.xml
     * @param console The console to clean up media for
     * @param gamelist The gamelist containing referenced media files
     */
    private void cleanupOrphanedMedia(Console console, Gamelist gamelist) {
        String consolePath = FilenameUtils.concat(exportPath, console.getSourceFolderName());
        
        // Collect all media files referenced in gamelist.xml
        Set<String> referencedMediaFiles = new HashSet<>();
        for (Game game : gamelist.getGames().values().stream()
                .map(Pair::getRight)
                .collect(Collectors.toList())) {
            
            // Add image file if not empty (including ZZZ files)
            if (game.getImage() != null && !game.getImage().trim().isEmpty()) {
                referencedMediaFiles.add(game.getImage());
            }
            
            // Add video file if not empty (including ZZZ files)
            if (game.getVideo() != null && !game.getVideo().trim().isEmpty()) {
                referencedMediaFiles.add(game.getVideo());
            }
            
            // Add thumbnail file if not empty (including ZZZ files)
            if (game.getThumbnail() != null && !game.getThumbnail().trim().isEmpty()) {
                referencedMediaFiles.add(game.getThumbnail());
            }
        }
        
        // Skip cleanup if no media files are referenced (optimization)
        if (referencedMediaFiles.isEmpty()) {
            return;
        }
        
        // Check each media directory for orphaned files
        String[] mediaDirectories = {"media/images", "media/videos", "media/thumbnails"};
        for (String mediaDir : mediaDirectories) {
            File mediaDirectory = new File(FilenameUtils.concat(consolePath, mediaDir));
            if (mediaDirectory.exists() && mediaDirectory.isDirectory()) {
                File[] mediaFiles = mediaDirectory.listFiles();
                if (mediaFiles != null) {
                    for (File mediaFile : mediaFiles) {
                        if (mediaFile.isFile()) {
                            // Get relative path from console directory (same format as in gamelist.xml)
                            String relativePath = FilenameUtils.concat(mediaDir, mediaFile.getName());
                            
                            // Check if this file is referenced in gamelist.xml
                            if (!referencedMediaFiles.contains(relativePath)) {
                                // This is an orphaned file, delete it
                                if (mediaFile.delete()) {
                                    mediaFilesDeleted++;
                                    addLogEntry(console.getName(), "mediaDeleted", "Orphaned " + mediaDir + ": " + mediaFile.getName());
                                } else {
                                    addLogEntry(console.getName(), "mediaDeleteFailed", "Failed to delete orphaned " + mediaDir + ": " + mediaFile.getName());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void addLogEntry(String console, String type, String message) {
        groupedLogs.computeIfAbsent(console, k -> new HashMap<>())
                   .computeIfAbsent(type, k -> new ArrayList<>())
                   .add(message);
    }

    private String formatTypeName(String type) {
        switch (type) {
            case "updatedFromRecalbox": return "Updated from Recalbox";
            case "updatedFromRomManager": return "Updated from RomManager";
            case "unchanged": return "Unchanged";
            case "skipped": return "Skipped (EmulationStation running)";
            case "missing": return "Missing locally";
            case "missingMedia": return "Missing media";
            case "deleted": return "Deleted (orphaned)";
            case "mediaDeleted": return "Media files deleted (orphaned)";
            case "mediaDeleteFailed": return "Failed to delete media files";
            case "saved": return "Gamelist saved";
            case "read": return "Read from Recalbox";
            default: return type;
        }
    }

    private void writeLogFile() {
        try {
            File cacheDir = new File("cache/gamelists");
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }
            File logFile = new File(cacheDir, "sync-" + timestamp + ".log");
            try (PrintWriter writer = new PrintWriter(logFile)) {
                writer.println("RomManager Gamelist Sync Log - " + timestamp);
                writer.println("=====================================");
                writer.println();

                for (Map.Entry<String, Map<String, List<String>>> consoleEntry : groupedLogs.entrySet()) {
                    String console = consoleEntry.getKey();
                    writer.println("Console: " + console);
                    String dashes = new String(new char[console.length() + 9]).replace('\0', '-');
                    writer.println(dashes);

                    for (Map.Entry<String, List<String>> typeEntry : consoleEntry.getValue().entrySet()) {
                        String type = typeEntry.getKey();
                        List<String> messages = typeEntry.getValue();
                        writer.println("  " + type.toUpperCase() + " (" + messages.size() + "):");
                        for (String message : messages) {
                            writer.println("    - " + message);
                        }
                        writer.println();
                    }
                    writer.println();
                }
            }
        } catch (IOException ex) {
            // Log error to console or handle it appropriately
            System.err.println("[Error] Failed to write log file: " + ex.getMessage());
        }
    }
}
