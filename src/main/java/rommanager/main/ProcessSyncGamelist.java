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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;
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
    private final StringBuilder gamelistLog = new StringBuilder();
    // Counters for summary
    private int consolesProcessed = 0;
    private int totalGamesProcessed = 0;
    private int gamesDeleted = 0;
    private int gamesMissingMedia = 0;
    private int gamesMissingLocal = 0;
    private int gamesUpdateAttempts = 0;
    private int gamelistsSaved = 0;
    private int gamelistsDeleted = 0;
    private int gamelistsCreated = 0;

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
    String sshHost = RomManager.options.get("romset.recalbox.ssh.host");
    String sshUser = RomManager.options.get("romset.recalbox.ssh.user");
    String sshPort = RomManager.options.get("romset.recalbox.ssh.port");
    String sshKey = RomManager.options.get("romset.recalbox.ssh.key");
    String sshPassword = RomManager.options.get("romset.recalbox.ssh.password");
        try {
            // If SSH host is configured, ask user whether we should stop EmulationStation during sync
            if (sshHost != null && !sshHost.equals("{Missing}") && !sshHost.trim().isEmpty()) {
                int ans = JOptionPane.showConfirmDialog(null, "Stop EmulationStation on " + sshHost + " during sync (will be restarted after)?", "Confirm sync", JOptionPane.YES_NO_OPTION);
                if (ans == JOptionPane.YES_OPTION) {
                    try {
                        if (stopEmulationStation(sshHost, sshUser, sshPort, sshKey, sshPassword)) {
                            gamelistLog.append("[Info] EmulationStation stopped on ").append(sshHost).append("\n");
                        } else {
                            gamelistLog.append("[Warn] Could not stop EmulationStation on ").append(sshHost).append(" - continuing without stop\n");
                        }
                    } catch (Exception ex) {
                        Popup.error(ex);
                        gamelistLog.append("[Error] Exception while stopping EmulationStation: ").append(ex.getMessage()).append("\n");
                    }
                }
            }
            //Read all gamelist.xml files from export folder
            Map<Console, Gamelist> gamelists = new HashMap<>();
            progressBarConsole.setup(Console.values().length);
            for (Console console : Console.values()) {
                // FIXME (temporary): limit sync to atari2600 for quick testing. Remove this after verification.
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
                    // FIXME 1c Gamelist - Use this 
//                    long remoteLastModified = remoteFile.lastModified();
                    
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
                            continue;
                        }
                        if (!checkMedia(console, gamelist, remoteGameElement, remoteGame.getImage())) {
                            gamesMissingMedia++;
                            continue;
                        }
                        if (!checkMedia(console, gamelist, remoteGameElement, remoteGame.getThumbnail())) {
                            gamesMissingMedia++;
                            continue;
                        }
                        if (!checkMedia(console, gamelist, remoteGameElement, remoteGame.getVideo())) {
                            gamesMissingMedia++;
                            continue;
                        }

                        String keyVersion = FilenameUtils.getName(gameFile.getAbsolutePath());
                        List<RomVersion> collect = romVersionsForConsole.stream()
                                .filter(v -> v.getExportFilename(console, Destination.recalbox).equals(keyVersion))
                                .collect(Collectors.toList());
                        if (collect.size() == 1) {
                            RomVersion localVersion = collect.get(0);
                                // Use below code to enable updating, but needs to really implement compareGame function
                            Game localGame = localVersion.getGame();
                            Game newGame = gamelist.compareGame(localGame, remoteGame);
                            gamelist.setGame(newGame); // Gamelist - set as changed (if changed of course) so that it is saved later
                            gamesUpdateAttempts++;
                            if (newGame.getImage() != null && !newGame.getImage().trim().equals("")) {
                                BufferIcon.checkOrGetCoverIcon(newGame.getName(), FilenameUtils.concat(consolePath, newGame.getImage()));
                            }
                            localVersion.setGame(newGame);

                        } else {
                            String warn = keyVersion + " could not be found on " + console.getName();
                            gamelistLog.append("[Missing] ").append(warn).append("\n");
                            gamesMissingLocal++;
                        }
                    }
                    if (gamelist.hasChanged()) {
                        gamelist.save();
                        gamelistLog.append("[Saved] gamelist for ").append(console.getName()).append(" updated.\n");
                        gamelistsSaved++;
                    }
                    if (gamelist.getGames().isEmpty()) {
                        remoteFile.delete();
                        gamelistLog.append("[Deleted] empty gamelist for ").append(console.getName()).append(" removed.\n");
                        gamelistsDeleted++;
                    } else {
                        //FIXME 1d Gamelist - Delete all media files not in gamelist
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
                        gamelistLog.append("[MissingGamelist] No gamelist.xml on remote for ").append(console.getName()).append(" - local data found: ").append(romVersionsForConsole.size()).append(" entries.\n");
                        // If creation implemented elsewhere, increment created counter when used
                        // gamelistsCreated++;
                    }
                }
            }
            progressBarConsole.reset();
            tableModel.fireTableDataChanged();

            progressBarGame.setIndeterminate("Saving ods file");
            if (RomManagerOds.createFile(tableModel, progressBarGame, sourcePath)) {
                callBack.actionPerformed();
            }
            progressBarGame.reset();
            //FIXME 1b Gamelist - display modification counters
            if (gamelistLog.length() > 0) {
                // If we stopped ES earlier, try to (re)start it now before showing the logs so the UI is not blocked
                try {
                    if (sshHost != null && !sshHost.equals("{Missing}") && !sshHost.trim().isEmpty()) {
                        if (startEmulationStation(sshHost, sshUser, sshPort, sshKey, sshPassword)) {
                            gamelistLog.append("[Info] EmulationStation started on ").append(sshHost).append(" before showing logs.\n");
                        } else {
                            gamelistLog.append("[Warn] EmulationStation could not be started on ").append(sshHost).append(" before showing logs.\n");
                        }
                    }
                } catch (Exception ex) {
                    gamelistLog.append("[Warn] Exception while starting EmulationStation before popup: ").append(ex.getMessage()).append("\n");
                }
                StringBuilder summary = new StringBuilder();
                summary.append("Sync complete\n\n");
                summary.append("Consoles processed: ").append(consolesProcessed).append("\n");
                summary.append("Games checked: ").append(totalGamesProcessed).append("\n");
                summary.append("Update attempts: ").append(gamesUpdateAttempts).append("\n");
                summary.append("Missing locally: ").append(gamesMissingLocal).append("\n");
                summary.append("Missing media: ").append(gamesMissingMedia).append("\n");
                summary.append("Games deleted: ").append(gamesDeleted).append("\n");
                summary.append("Gamelists saved: ").append(gamelistsSaved).append("\n");
                summary.append("Gamelists deleted: ").append(gamelistsDeleted).append("\n");
                summary.append("Gamelists created: ").append(gamelistsCreated).append("\n\n");
                summary.append("Details:\n");
                summary.append(gamelistLog.toString());
                Popup.showText("Sync game data complete", summary.toString());
            } else {
                Popup.info("Sync game data complete.");
            }
        } catch (InterruptedException ex) {
//			Popup.info("Aborted by user");
        } catch (Exception ex) {
            Popup.error(ex);
        } finally {
            // If we stopped EmulationStation earlier, try to start it again
            try {
                if (sshHost != null && !sshHost.equals("{Missing}") && !sshHost.trim().isEmpty()) {
                    // attempt to restart (best-effort)
                    startEmulationStation(sshHost, sshUser, sshPort, sshKey, sshPassword);
                }
            } catch (Exception ex) {
                gamelistLog.append("[Warn] could not restart EmulationStation: ").append(ex.getMessage()).append("\n");
            }
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

    private boolean stopEmulationStation(String host, String user, String port, String key, String password) throws IOException, InterruptedException {
        List<String> cmd = new ArrayList<>();
        boolean useSshPass = password != null && !password.equals("{Missing}") && !password.trim().isEmpty();
        if (useSshPass) {
            cmd.add("sshpass");
            cmd.add("-p");
            cmd.add(password);
        }
        cmd.add("ssh");
        if (port != null && !port.equals("{Missing}") && !port.trim().isEmpty()) { cmd.add("-p"); cmd.add(port); }
        if (key != null && !key.equals("{Missing}") && !key.trim().isEmpty()) { cmd.add("-i"); cmd.add(key); }
        String target = (user != null && !user.equals("{Missing}") ? user + "@" + host : host);
        cmd.add(target);
        cmd.add("/etc/init.d/S31emulationstation stop");
        ProcessBuilder pb = new ProcessBuilder(cmd);
        Process p = pb.start();
        int rc = p.waitFor();
        return rc == 0;
    }

    private boolean startEmulationStation(String host, String user, String port, String key, String password) throws IOException, InterruptedException {
        List<String> cmd = new ArrayList<>();
        boolean useSshPass = password != null && !password.equals("{Missing}") && !password.trim().isEmpty();
        if (useSshPass) {
            cmd.add("sshpass");
            cmd.add("-p");
            cmd.add(password);
        }
        cmd.add("ssh");
        if (port != null && !port.equals("{Missing}") && !port.trim().isEmpty()) { cmd.add("-p"); cmd.add(port); }
        if (key != null && !key.equals("{Missing}") && !key.trim().isEmpty()) { cmd.add("-i"); cmd.add(key); }
        String target = (user != null && !user.equals("{Missing}") ? user + "@" + host : host);
        cmd.add(target);
        // start in background so ssh returns
        cmd.add("/etc/init.d/S31emulationstation start &>/dev/null &");
        ProcessBuilder pb = new ProcessBuilder(cmd);
        Process p = pb.start();
        int rc = p.waitFor();
        return rc == 0;
    }

}
