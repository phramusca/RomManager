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
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.io.FilenameUtils;
import static rommanager.main.RomManager.TAG_JEUX_VIDEO;
import rommanager.utils.FileSystem;
import rommanager.utils.Popup;
import rommanager.utils.ProcessAbstract;
import rommanager.utils.ProgressBar;

/**
 *
 * @author phramusca ( https://github.com/phramusca/JaMuz/ )
 */
public class ProcessSyncRoms extends ProcessAbstract {

    private final String exportPath;
    private final ProgressBar progressBarConsole;
    private final ProgressBar progressBarGame;
    private final TableModelRom tableModel;
    private final ICallBackProcess callBack;
    private final String sourcePath;

    private List<RomContainer> romSourceList;
    private List<File> romDestinationList;
    private boolean onlyCultes;

    public ProcessSyncRoms(
            String sourcePath,
            String exportPath,
            ProgressBar progressBarConsole,
            ProgressBar progressBarGame,
            TableModelRom tableModel,
            ICallBackProcess callBack) {
        super("Thread.ProcessExport");
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
            progressBarConsole.setup(3);
            List<Console> consoles = tableModel.getRoms().values().stream()
                    .map(v -> v.getConsole())
                    .distinct()
                    .collect(Collectors.toList());

            //Get files currently on destination
            progressBarConsole.progress("Listing files on destination");
            progressBarGame.setup(consoles.size());
            romDestinationList = new ArrayList<>();
            for (Console console : consoles) {
                checkAbort();
                if (console.isSelected()) {
                    String consolePath = FilenameUtils.concat(exportPath, console.name());
                    if (new File(consolePath).exists()) {
                        browsePath(new File(consolePath));
                    }
                }
                progressBarGame.progress(console.getName());
            }

            //Get source roms & setToCopyTrue(true)
            if (onlyCultes) { //TODO: Handle when consoles with no tag TAG_JEUX_VIDEO at all
                romSourceList = tableModel.getRoms().values()
                        .stream().filter(r -> r.getConsole().isSelected()
                        && !r.getExportableVersions().isEmpty()
                        && r.getExportableVersions().get(0).getTags().contains(TAG_JEUX_VIDEO))
                        .peek(r -> r.setToCopyTrue())
                        .collect(Collectors.toList());
            }
            if (!onlyCultes || romSourceList.size() <= 0) {
                romSourceList = tableModel.getRoms().values()
                        .stream().filter(r -> r.getConsole().isSelected()
                        && !r.getExportableVersions().isEmpty())
                        .peek(r -> r.setToCopyTrue())
                        .collect(Collectors.toList());
            }

            //Remove files on destination and exclude already exported
            this.checkAbort();
            progressBarGame.setup(romDestinationList.size());
            progressBarConsole.progress("Checking files on destination");
            int nbAlreadyExported = 0;
            int nbDeleted = 0;
            for (File file : romDestinationList) {
                this.checkAbort();
                Pair<RomContainer, RomVersion> pair = searchInSourceList(file);
                if (pair != null && checkFile(pair.getLeft(), pair.getRight())) {
                    //Already exported
                    nbAlreadyExported++;
                    pair.getRight().setToCopy(false);
                } else {
                    //Not a file to be copied, or it is a bad file: removing it on destination
                    file.delete();
                    nbDeleted++;
                }
                progressBarGame.progress(file.getAbsolutePath());
            }

            //Copy (not already exported) files to destination
            Long nbToCopy = romSourceList.stream().flatMap(r -> r.versions.stream()).filter(v -> v.isToCopy()).count();
            progressBarGame.setup(nbToCopy.intValue());
            progressBarConsole.progress("Exporting files to destination");
            String sourceFolder;
            int nbFailed = 0;
            int nbExported = 0;
            for (RomContainer romContainer : romSourceList) {
                checkAbort();
                String filename = romContainer.getFilename();
                for (RomVersion romVersion
                        : romContainer.getVersions().stream()
                                .filter(r -> r.isToCopy())
                                .collect(Collectors.toList())) {
                    checkAbort();
                    sourceFolder = FilenameUtils.concat(sourcePath, romContainer.getConsole().name());
                    File sourceFile = new File(FilenameUtils.concat(sourceFolder, romVersion.getFilename()));
                    File exportFile = new File(romVersion.getExportPath(romContainer.getConsole(), exportPath));
                    String ext = FilenameUtils.getExtension(filename);
                    if (ext.equals("7z")) {
                        try (SevenZFile sevenZFile = new SevenZFile(new File(
                                FilenameUtils.concat(sourceFolder, filename)))) {
                            SevenZArchiveEntry entry = sevenZFile.getNextEntry();
                            while (entry != null) {
                                if (entry.getName().equals(romVersion.getFilename())) {
                                    String exportFolder = romVersion.getExportFolder(romContainer.getConsole(), exportPath);
                                    File file = new File(exportFolder);
                                    if (!file.exists()) {
                                        file.mkdirs();
                                    }
                                    File unzippedFile = new File(FilenameUtils.concat(exportFolder, romVersion.getFilename()));
                                    try (FileOutputStream out = new FileOutputStream(unzippedFile)) {
                                        byte[] content = new byte[(int) entry.getSize()];
                                        sevenZFile.read(content, 0, content.length);
                                        out.write(content);
                                        out.close();
                                        if (romContainer.getConsole().isZip()) {
                                            FileSystem.zipFile(unzippedFile, exportFile);
                                            unzippedFile.delete();
                                        }
                                    } catch (IOException ex) {
                                        Logger.getLogger(ProcessSyncRoms.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                    break;
                                }
                                entry = sevenZFile.getNextEntry();
                            }
                            sevenZFile.close();
                        }
                    } else if (ProcessList.allowedExtensions.contains(ext)) {
                        if (romContainer.getConsole().isZip()) {
                            FileSystem.zipFile(sourceFile, exportFile);
                        } else {
                            FileSystem.copyFile(sourceFile, exportFile);
                        }
                    }
                    if (checkFile(romContainer, romVersion)) {
                        nbExported++;
                    } else {
                        nbFailed++;
                        if (exportFile.exists()) {
                            exportFile.delete();
                        }
                    }
                    progressBarGame.progress(romContainer.getConsoleStr() + " \\ " + romContainer.getFilename());
                }
            }

            Popup.info("Export complete.\n" + nbAlreadyExported + " already exported\n" + nbExported + " exported / " + nbToCopy + "\n" + nbFailed + " error(s)\n" + nbDeleted + " deleted");
            //FIXME: If nbFailed>0, propose to open log (and make one file per process run)
            callBack.actionPerformed();
            progressBarConsole.reset();
            progressBarGame.reset();
        } catch (InterruptedException ex) {
//			Popup.info("Aborted by user");
        } catch (IOException ex) {
            Logger.getLogger(ProcessSyncRoms.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            callBack.completed();
        }
    }

    private boolean checkFile(RomContainer romContainer, RomVersion romVersion) throws IOException {
        String sourceFolder = FilenameUtils.concat(sourcePath, romContainer.getConsole().name());
        File sourceFile = new File(FilenameUtils.concat(sourceFolder, romVersion.getFilename()));
        File exportFile = new File(romVersion.getExportPath(romContainer.getConsole(), exportPath));
        String containerFileExtension = FilenameUtils.getExtension(romContainer.getFilename());
        if (containerFileExtension.equals("7z")) {
            if (romContainer.getConsole().isZip()) {
                return exportFile.exists() && checkFile(exportFile, romVersion.getFilename(), romVersion.getCrcValue(), romVersion.getSize());
            } else {
                return exportFile.exists() && (exportFile.length() == romVersion.getSize());
            }
        } else if (ProcessList.allowedExtensions.contains(containerFileExtension)) {
            if (romContainer.getConsole().isZip()) {
                return exportFile.exists() && checkFile(exportFile, sourceFile);
            } else {
                return exportFile.exists() && (exportFile.length() == sourceFile.length());
            }
        }
        return false;
    }

    private boolean checkFile(File exportFile, File sourceFile) {
        try {
            ZipFile zipFile = new ZipFile(exportFile);
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            if (entries.hasMoreElements()) {
                ZipEntry exportEntry = entries.nextElement();
                if (!exportEntry.getName().equals(sourceFile.getName())
                        || exportEntry.getSize() != sourceFile.length()) {
                    zipFile.close();
                    return false;
                }
                if (entries.hasMoreElements()) {
                    zipFile.close();
                    return false;
                }
            } else {
                zipFile.close();
                return false;
            }
            zipFile.close();
        } catch (IOException ex) {
            Logger.getLogger(ProcessSyncRoms.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    private boolean checkFile(File exportFile, String name, long crcValue, long size) {
        try (ZipFile zipFile = new ZipFile(exportFile)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            if (entries.hasMoreElements()) {
                ZipEntry exportEntry = entries.nextElement();
                if (!exportEntry.getName().equals(name)
                        || exportEntry.getSize() != size
                        || exportEntry.getCrc() != crcValue) {
                    zipFile.close();
                    return false;
                }
                if (entries.hasMoreElements()) {
                    zipFile.close();
                    return false;
                }
            } else {
                zipFile.close();
                return false;
            }
        } catch (IOException ex) {
            Logger.getLogger(ProcessSyncRoms.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    public void setOnlyCultes(boolean onlyCultes) {
        this.onlyCultes = onlyCultes;
    }

    //TODO: Use a Map instead ...
    private Pair<RomContainer, RomVersion> searchInSourceList(File file) throws InterruptedException {
        for (RomContainer romContainer : romSourceList) {
            for (RomVersion romVersion : romContainer.getToCopyVersions()) {
                this.checkAbort();
                String exportFilename = romVersion.getExportPath(romContainer.getConsole(), exportPath);
                if (exportFilename.equals(file.getAbsolutePath())) {
                    return Pair.of(romContainer, romVersion);
                }
            }
        }
        return null;
    }

    private void browsePath(File path) throws InterruptedException {
        this.checkAbort();
        if (path.isDirectory()) {
            File[] files = path.listFiles();
            if (files != null) {
                for (File file : files) {
                    this.checkAbort();
                    if (file.isDirectory()) {
                        Path pathToAFolderWithTrailingBackslash = Paths.get(file.getAbsolutePath());
                        if (Character.isDigit(pathToAFolderWithTrailingBackslash.getFileName().toString().charAt(0))) {
                            browseFiles(file);
                        }
                    }
                }
            }
        }
    }

    private void browseFiles(File path) throws InterruptedException {
        this.checkAbort();
        if (path.isDirectory()) {
            File[] files = path.listFiles();
            if (files != null) {
                for (File file : files) {
                    this.checkAbort();
                    if (!file.isDirectory()) {
                        this.romDestinationList.add(file);
                    }
                }
            }
        }
    }
}
