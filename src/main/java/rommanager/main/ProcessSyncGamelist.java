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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private final boolean forceZip;

    public ProcessSyncGamelist(
            String sourcePath,
            String exportPath,
            ProgressBar progressBarConsole,
            ProgressBar progressBarGame,
            TableModelRom tableModel,
            ICallBackProcess callBack,
             boolean forceZip) {
        super("Thread.ProcessRead");
        this.sourcePath = sourcePath;
        this.exportPath = exportPath;
        this.progressBarConsole = progressBarConsole;
        this.progressBarGame = progressBarGame;
        this.tableModel = tableModel;
        this.callBack = callBack;
        this.forceZip = forceZip;
    }

    @Override
    public void run() {
        try {
            //Read all gamelist.xml files from export folder
            Map<Console, Gamelist> gamelists = new HashMap<>();
            progressBarConsole.setup(Console.values().length);
            for (Console console : Console.values()) {
                checkAbort();
                progressBarConsole.progress(console.getName());
                File remoteFile = new File(FilenameUtils.concat(FilenameUtils.concat(exportPath, console.name()), "gamelist.xml"));
                File backupFile = new File(FilenameUtils.concat(FilenameUtils.concat(exportPath, console.name()), "gamelist.xml.bak"));
                if (remoteFile.exists()) {
                    FileSystem.copyFile(remoteFile, backupFile);
                    String consolePath = FilenameUtils.concat(exportPath, console.name());
                    //FIXME 0 Gamelist - Use this 
                    long remoteLastModified = remoteFile.lastModified();

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
                        File gameFile = new File(FilenameUtils.concat(FilenameUtils.concat(exportPath, console.name()), remoteGame.getPath()));
                        if (!gameFile.exists()) {
                            gamelist.deleteGame(remoteGameElement);
                            continue;
                        }
                        if (!checkMedia(console, gamelist, remoteGameElement, remoteGame.getImage())) {
                            continue;
                        }
                        if (!checkMedia(console, gamelist, remoteGameElement, remoteGame.getThumbnail())) {
                            continue;
                        }
                        if (!checkMedia(console, gamelist, remoteGameElement, remoteGame.getVideo())) {
                            continue;
                        }

                        String keyVersion = FilenameUtils.getName(gameFile.getAbsolutePath());
                        List<RomVersion> collect = romVersionsForConsole.stream()
                                .filter(v -> v.getExportFilename(console, forceZip).equals(keyVersion))
                                .collect(Collectors.toList());
                        if (collect.size() == 1) {
                            RomVersion localVersion = collect.get(0);
                            Game localGame = localVersion.getGame();

                            //FIXME 0 Gamelist - !! CONTINUE FROM HERE !!! (for now, it only reads from remote)
                            Game newGame = gamelist.compareGame(localGame, remoteGame);
                            gamelist.setGame(newGame); //FIXME 0 Gamelist - set as changed (if changed of course) so that it is saved later
                            if (!newGame.getImage().isBlank()) {
                                BufferIcon.checkOrGetCoverIcon(newGame.getName(), FilenameUtils.concat(consolePath, newGame.getImage()));
                            }
                            localVersion.setGame(newGame);

                        } else {
                            //FIXME 1 Gamelist - manage if file not found, though should not happen
                            Popup.warning(keyVersion + " could not be found on " + console.getName());
                        }
                    }
                    if (gamelist.hasChanged()) {
                        gamelist.save();
                    }
                    if (gamelist.getGames().isEmpty()) {
                        remoteFile.delete();
                    } else {
                        //FIXME 1 Gamelist - Delete all media files not in gamelist
                        gamelists.put(console, gamelist);
                    }
                } else {
                    //FIXME 1 Gamelist - Create the file and fill it up with local data (if any)

//                    Popup.warning("No gamelist.xml on remote for " + console.getName());
                }
            }
            progressBarConsole.reset();
            tableModel.fireTableDataChanged();

            progressBarGame.setIndeterminate("Saving ods file");
            if (RomManagerOds.createFile(tableModel, progressBarGame, sourcePath)) {
                callBack.actionPerformed();
            }
            progressBarGame.reset();
            //FIXME 1 Gamelist - display modification counters
            Popup.info("Sync game data complete.");
        } catch (InterruptedException ex) {
//			Popup.info("Aborted by user");
        } catch (IOException ex) {
            Popup.error(ex);
        } finally {
            callBack.completed();
        }
    }

    private boolean checkMedia(Console console, Gamelist gamelist, Element remoteGameElement, String filename) {
        File mediaFile = new File(FilenameUtils.concat(FilenameUtils.concat(exportPath, console.name()), filename));
        if (!filename.contains("ZZZ") && !mediaFile.exists()) {
            gamelist.removeScraped(remoteGameElement);
            //Some file is missing and the only way (I found) to force re-scrap is removing entry from xml
//                            gamelist.deleteGame(remoteGameElement);
            return false;
        }
        return true;
    }

}
