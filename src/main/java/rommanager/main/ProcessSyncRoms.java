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
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import rommanager.utils.LogManager;
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
    private final Destination destination;
    private final Map<String, Map<String, List<String>>> groupedLogs = new HashMap<>();
    private final String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));

    public ProcessSyncRoms(
            String sourcePath,
            String exportPath,
            ProgressBar progressBarConsole,
            ProgressBar progressBarGame,
            TableModelRom tableModel,
            ICallBackProcess callBack, 
            Destination destination) {
        super("Thread.ProcessExport");
        this.sourcePath = sourcePath;
        this.exportPath = exportPath;
        this.progressBarConsole = progressBarConsole;
        this.progressBarGame = progressBarGame;
        this.tableModel = tableModel;
        this.callBack = callBack;
        this.destination = destination;
    }

    @Override
    public void run() {
        try {
            progressBarConsole.setup(3);
            addLogEntry("system", "info", "Starting ROM export to " + destination.getName());
            
            // Get all selected consoles (from DialogConsole selection)
            // This includes consoles that might not be in tableModel anymore (e.g., after deletion)
            List<Console> selectedConsoles = new ArrayList<>();
            for (Console console : Console.values()) {
                if (console.isSelected()) {
                    selectedConsoles.add(console);
                }
            }
            if (selectedConsoles.isEmpty()) {
                addLogEntry("system", "warning", "No console selected for export");
            }

            //Get files currently on destination (only for selected consoles)
            progressBarConsole.progress("Listing files on destination");
            progressBarGame.setup(selectedConsoles.size());
            romDestinationList = new ArrayList<>();
            for (Console console : selectedConsoles) {
                checkAbort();
                String consolePath = FilenameUtils.concat(exportPath, console.getDestinationFolderName(destination));
                int beforeCount = romDestinationList.size();
                if (new File(consolePath).exists()) {
                    File consoleFilePath = new File(consolePath);
                    if(destination.isFlat()) {
                        browseFiles(consoleFilePath);
                    } else {
                        browsePath(consoleFilePath);
                    }
                } else {
                    addLogEntry(console.getName(), "missingDestinationFolder", consolePath);
                }
                int afterCount = romDestinationList.size();
                addLogEntry(console.getName(), "destinationFilesFound", String.valueOf(afterCount - beforeCount));
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
            addLogEntry("system", "sourceEntries", String.valueOf(romSourceList.size()));

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
                    addLogEntry(pair.getLeft().getConsole().getName(), "alreadyExported", file.getAbsolutePath());
                } else {
                    //Not a file to be copied, or it is a bad file: removing it on destination
                    String reason = (pair == null) ? "not present in source selection" : "failed destination validation";
                    if (file.delete()) {
                        nbDeleted++;
                        addLogEntry(getConsoleName(pair), "deletedDestinationFile", file.getAbsolutePath() + " - " + reason);
                    } else {
                        addLogEntry(getConsoleName(pair), "deleteFailed", file.getAbsolutePath() + " - " + reason);
                    }
                }
                progressBarGame.progress(file.getAbsolutePath());
            }

            //Copy (not already exported) files to destination
            Long nbToCopy = romSourceList.stream().flatMap(r -> r.versions.stream()).filter(v -> v.isToCopy()).count();
            progressBarGame.setup(nbToCopy.intValue());
            progressBarConsole.progress("Exporting files to " + destination.getName());
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
                    sourceFolder = FilenameUtils.concat(sourcePath, romContainer.getConsole().getSourceFolderName());
                    File sourceFile = new File(FilenameUtils.concat(sourceFolder, romVersion.getFilename()));
                    File exportFile = new File(romVersion.getExportPath(romContainer.getConsole(), exportPath, destination));
                    String ext = FilenameUtils.getExtension(filename);
                    
                    // Check if source file exists
                    File sourceContainerFile = new File(FilenameUtils.concat(sourceFolder, filename));
                    if (!sourceContainerFile.exists()) {
                        nbFailed++;
                        addLogEntry(romContainer.getConsole().getName(), "sourceMissing",
                                sourceContainerFile.getAbsolutePath() + " for " + romVersion.getFilename());
                        LogManager.getInstance().error(ProcessSyncRoms.class, 
                            "Source file does not exist: " + sourceContainerFile.getAbsolutePath());
                        progressBarGame.progress(romContainer.getConsoleStr() + " \\ " + romContainer.getFilename());
                        continue;
                    }
                    
                    if (ext.equals("7z")) {
                        try (SevenZFile sevenZFile = SevenZFile.builder().setFile(new File(
                                FilenameUtils.concat(sourceFolder, filename))).get()) {
                            SevenZArchiveEntry entry = sevenZFile.getNextEntry();
                            boolean found = false;
                            while (entry != null) {
                                if (entry.getName().equals(romVersion.getFilename())) {
                                    found = true;
                                    String exportFolder = romVersion.getExportFolder(romContainer.getConsole(), exportPath, destination);
                                    File file = new File(exportFolder);
                                    if (!file.exists()) {
                                        file.mkdirs();
                                    }
                                    File unzippedFile = new File(FilenameUtils.concat(exportFolder, romVersion.getFilename()));
                                    try (FileOutputStream out = new FileOutputStream(unzippedFile)) {
                                        byte[] content = new byte[(int) entry.getSize()];
                                        int bytesRead = sevenZFile.read(content, 0, content.length);
                                        if (bytesRead != entry.getSize()) {
                                            addLogEntry(romContainer.getConsole().getName(), "readMismatch",
                                                    "7z read size mismatch for " + romVersion.getFilename()
                                                    + " in " + filename + ": expected " + entry.getSize()
                                                    + ", got " + bytesRead);
                                            LogManager.getInstance().error(ProcessSyncRoms.class, 
                                                "Incomplete read from 7z archive: expected " + entry.getSize() + " bytes, got " + bytesRead);
                                        }
                                        out.write(content, 0, bytesRead);
                                        out.close();
                                        if (romContainer.getConsole().isZip(destination)) {
                                            if (!FileSystem.zipFile(unzippedFile, exportFile)) {
                                                addLogEntry(romContainer.getConsole().getName(), "zipCreationFailed",
                                                        "Failed to create " + exportFile.getAbsolutePath()
                                                        + " from extracted " + unzippedFile.getAbsolutePath());
                                                LogManager.getInstance().error(ProcessSyncRoms.class, 
                                                    "Failed to create zip file from extracted 7z entry: " + exportFile.getAbsolutePath());
                                            }
                                            unzippedFile.delete();
                                        }
                                    } catch (IOException ex) {
                                        addLogEntry(romContainer.getConsole().getName(), "extractFailed",
                                                "Error extracting " + romVersion.getFilename() + " from " + filename
                                                + ": " + ex.getMessage());
                                        LogManager.getInstance().error(ProcessSyncRoms.class, 
                                            "Error extracting file from 7z archive: " + romVersion.getFilename() + " from " + filename, ex);
                                    }
                                    break;
                                }
                                entry = sevenZFile.getNextEntry();
                            }
                            if (!found) {
                                addLogEntry(romContainer.getConsole().getName(), "entryMissingInArchive",
                                        romVersion.getFilename() + " not found in " + filename);
                                LogManager.getInstance().error(ProcessSyncRoms.class, 
                                    "File not found in 7z archive: " + romVersion.getFilename() + " not found in " + filename);
                            }
                        } catch (IOException ex) {
                            addLogEntry(romContainer.getConsole().getName(), "archiveOpenFailed",
                                    "Error opening archive " + filename + ": " + ex.getMessage());
                            LogManager.getInstance().error(ProcessSyncRoms.class, 
                                "Error opening 7z archive: " + filename, ex);
                        }
                    } else if (ProcessList.allowedExtensions.contains(ext)) {
                        try {
                            if (romContainer.getConsole().isZip(destination)) {
                                if (!FileSystem.zipFile(sourceFile, exportFile)) {
                                    addLogEntry(romContainer.getConsole().getName(), "zipCreationFailed",
                                            "Failed to create " + exportFile.getAbsolutePath() + " from "
                                            + sourceFile.getAbsolutePath());
                                    LogManager.getInstance().error(ProcessSyncRoms.class, 
                                        "Failed to create zip file: " + exportFile.getAbsolutePath());
                                }
                            } else {
                                FileSystem.copyFile(sourceFile, exportFile);
                            }
                        } catch (IOException ex) {
                            addLogEntry(romContainer.getConsole().getName(), "copyFailed",
                                    "Error copying " + sourceFile.getAbsolutePath() + " -> "
                                    + exportFile.getAbsolutePath() + ": " + ex.getMessage());
                            LogManager.getInstance().error(ProcessSyncRoms.class, 
                                "Error copying file: " + sourceFile.getAbsolutePath() + " -> " + exportFile.getAbsolutePath(), ex);
                        }
                    }
                    if (checkFile(romContainer, romVersion)) {
                        nbExported++;
                        addLogEntry(romContainer.getConsole().getName(), "exported", getRomLabel(romContainer, romVersion)
                                + " -> " + exportFile.getAbsolutePath());
                    } else {
                        nbFailed++;
                        String errorMsg = getCheckFileError(romContainer, romVersion);
                        addLogEntry(romContainer.getConsole().getName(), "validationFailed",
                                getRomLabel(romContainer, romVersion) + " -> " + exportFile.getAbsolutePath()
                                + " - " + errorMsg);
                        LogManager.getInstance().error(ProcessSyncRoms.class, 
                            "Export validation failed for: " + romContainer.getConsoleStr() + " / " + romVersion.getFilename() + 
                            " -> " + exportFile.getAbsolutePath() + ". " + errorMsg);
                        if (exportFile.exists()) {
                            if (!exportFile.delete()) {
                                addLogEntry(romContainer.getConsole().getName(), "deleteFailed",
                                        "Failed to delete invalid export: " + exportFile.getAbsolutePath());
                            }
                        }
                    }
                    progressBarGame.progress(romContainer.getConsoleStr() + " \\ " + romContainer.getFilename());
                }
            }

            addLogEntry("system", "info", "Export complete: " + nbExported + "/" + nbToCopy + " exported, "
                    + nbFailed + " error(s), " + nbDeleted + " deleted, " + nbAlreadyExported + " already exported");
            writeLogFile();
            String logFilePath = "cache/roms/sync-" + timestamp + ".log";
            Popup.showTextWithLogLink("ROM export complete",
                    buildSummary(nbAlreadyExported, nbExported, nbToCopy.intValue(), nbFailed, nbDeleted, selectedConsoles.size(), logFilePath),
                    logFilePath);
            callBack.actionPerformed();
            progressBarConsole.reset();
            progressBarGame.reset();
        } catch (InterruptedException ex) {
//			Popup.info("Aborted by user");
        } catch (IOException ex) {
            addLogEntry("system", "error", "Fatal export error: " + ex.getMessage());
            LogManager.getInstance().error(ProcessSyncRoms.class, "Error during ROM export", ex);
            writeLogFile();
            Popup.error("ROM export failed. See log file: cache/roms/sync-" + timestamp + ".log");
        } finally {
            callBack.completed();
        }
    }

    private boolean checkFile(RomContainer romContainer, RomVersion romVersion) throws IOException {
        String sourceFolder = FilenameUtils.concat(sourcePath, romContainer.getConsole().getSourceFolderName());
        File sourceFile = new File(FilenameUtils.concat(sourceFolder, romVersion.getFilename()));
        File exportFile = new File(romVersion.getExportPath(romContainer.getConsole(), exportPath, destination));
        String containerFileExtension = FilenameUtils.getExtension(romContainer.getFilename());
        if (containerFileExtension.equals("7z")) {
            if (romContainer.getConsole().isZip(destination)) {
                return exportFile.exists() && checkFile(exportFile, romVersion.getFilename(), romVersion.getCrcValue(), romVersion.getSize());
            } else {
                return exportFile.exists() && (exportFile.length() == romVersion.getSize());
            }
        } else if (ProcessList.allowedExtensions.contains(containerFileExtension)) {
            if (romContainer.getConsole().isZip(destination)) {
                return exportFile.exists() && checkFile(exportFile, sourceFile);
            } else {
                return exportFile.exists() && (exportFile.length() == sourceFile.length());
            }
        }
        return false;
    }
    
    private String getCheckFileError(RomContainer romContainer, RomVersion romVersion) {
        try {
            String sourceFolder = FilenameUtils.concat(sourcePath, romContainer.getConsole().getSourceFolderName());
            File sourceFile = new File(FilenameUtils.concat(sourceFolder, romVersion.getFilename()));
            File exportFile = new File(romVersion.getExportPath(romContainer.getConsole(), exportPath, destination));
            String containerFileExtension = FilenameUtils.getExtension(romContainer.getFilename());
            
            if (!exportFile.exists()) {
                return "Export file does not exist";
            }
            
            if (containerFileExtension.equals("7z")) {
                if (romContainer.getConsole().isZip(destination)) {
                    return getCheckFileErrorDetails(exportFile, romVersion.getFilename(), romVersion.getCrcValue(), romVersion.getSize());
                } else {
                    long exportSize = exportFile.length();
                    long expectedSize = romVersion.getSize();
                    if (exportSize != expectedSize) {
                        return "Size mismatch: expected " + expectedSize + ", got " + exportSize;
                    }
                }
            } else if (ProcessList.allowedExtensions.contains(containerFileExtension)) {
                if (romContainer.getConsole().isZip(destination)) {
                    return getCheckFileErrorDetails(exportFile, sourceFile);
                } else {
                    long exportSize = exportFile.length();
                    long sourceSize = sourceFile.length();
                    if (exportSize != sourceSize) {
                        return "Size mismatch: expected " + sourceSize + ", got " + exportSize;
                    }
                }
            }
            return "Unknown validation error";
        } catch (Exception ex) {
            return "Error getting validation details: " + ex.getMessage();
        }
    }
    
    private String getCheckFileErrorDetails(File exportFile, File sourceFile) {
        try {
            ZipFile zipFile = new ZipFile(exportFile);
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            if (!entries.hasMoreElements()) {
                zipFile.close();
                return "Zip file is empty";
            }
            ZipEntry exportEntry = entries.nextElement();
            if (!exportEntry.getName().equals(sourceFile.getName())) {
                zipFile.close();
                return "Name mismatch: expected " + sourceFile.getName() + ", got " + exportEntry.getName();
            }
            if (exportEntry.getSize() != sourceFile.length()) {
                zipFile.close();
                return "Size mismatch: expected " + sourceFile.length() + ", got " + exportEntry.getSize();
            }
            if (entries.hasMoreElements()) {
                zipFile.close();
                return "Zip file contains multiple entries (expected 1)";
            }
            zipFile.close();
        } catch (IOException ex) {
            return "Error reading zip file: " + ex.getMessage();
        }
        return "Unknown zip validation error";
    }
    
    private String getCheckFileErrorDetails(File exportFile, String name, long crcValue, long size) {
        try (ZipFile zipFile = new ZipFile(exportFile)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            if (!entries.hasMoreElements()) {
                return "Zip file is empty";
            }
            ZipEntry exportEntry = entries.nextElement();
            if (!exportEntry.getName().equals(name)) {
                return "Name mismatch: expected " + name + ", got " + exportEntry.getName();
            }
            if (exportEntry.getSize() != size) {
                return "Size mismatch: expected " + size + ", got " + exportEntry.getSize();
            }
            if (exportEntry.getCrc() != crcValue) {
                return "CRC mismatch: expected " + crcValue + ", got " + exportEntry.getCrc();
            }
            if (entries.hasMoreElements()) {
                return "Zip file contains multiple entries (expected 1)";
            }
        } catch (IOException ex) {
            return "Error reading zip file: " + ex.getMessage();
        }
        return "Unknown zip validation error";
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
            LogManager.getInstance().error(ProcessSyncRoms.class, 
                "Error checking exported zip file", ex);
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
            LogManager.getInstance().error(ProcessSyncRoms.class, 
                "Error checking exported zip file with CRC", ex);
            return false;
        }
        return true;
    }

    private String getConsoleName(Pair<RomContainer, RomVersion> pair) {
        if (pair == null || pair.getLeft() == null || pair.getLeft().getConsole() == null) {
            return "unknown";
        }
        return pair.getLeft().getConsole().getName();
    }

    private String getRomLabel(RomContainer romContainer, RomVersion romVersion) {
        return romContainer.getConsole().getName() + " / " + romVersion.getFilename();
    }

    private void addLogEntry(String console, String type, String message) {
        groupedLogs.computeIfAbsent(console, k -> new HashMap<>())
                .computeIfAbsent(type, k -> new ArrayList<>())
                .add(message);
    }

    private String buildSummary(int nbAlreadyExported, int nbExported, int nbToCopy, int nbFailed, int nbDeleted,
                                int selectedConsoleCount, String logFilePath) {
        StringBuilder summary = new StringBuilder();
        summary.append("Export complete\n\n");
        summary.append("Destination: ").append(destination.getName()).append("\n");
        summary.append("Selected consoles: ").append(selectedConsoleCount).append("\n");
        summary.append("Already exported: ").append(nbAlreadyExported).append("\n");
        summary.append("Exported: ").append(nbExported).append(" / ").append(nbToCopy).append("\n");
        summary.append("Errors: ").append(nbFailed).append("\n");
        summary.append("Deleted from destination: ").append(nbDeleted).append("\n\n");

        summary.append("Summary by console and type:\n");
        for (Map.Entry<String, Map<String, List<String>>> consoleEntry : groupedLogs.entrySet()) {
            String console = consoleEntry.getKey();
            summary.append("- ").append(console).append(":\n");
            for (Map.Entry<String, List<String>> typeEntry : consoleEntry.getValue().entrySet()) {
                summary.append("  * ").append(formatTypeName(typeEntry.getKey()))
                        .append(": ").append(typeEntry.getValue().size()).append("\n");
            }
        }

        summary.append("\nLog file: ").append(logFilePath);
        return summary.toString();
    }

    private String formatTypeName(String type) {
        switch (type) {
            case "destinationFilesFound": return "Destination files found";
            case "missingDestinationFolder": return "Missing destination folder";
            case "sourceEntries": return "Source entries";
            case "alreadyExported": return "Already exported";
            case "deletedDestinationFile": return "Deleted destination file";
            case "exported": return "Exported";
            case "sourceMissing": return "Missing source file";
            case "entryMissingInArchive": return "Missing file in archive";
            case "archiveOpenFailed": return "Archive open failed";
            case "extractFailed": return "Archive extract failed";
            case "readMismatch": return "Read size mismatch";
            case "copyFailed": return "Copy failed";
            case "zipCreationFailed": return "Zip creation failed";
            case "validationFailed": return "Validation failed";
            case "deleteFailed": return "Delete failed";
            case "info": return "Information";
            case "warning": return "Warning";
            case "error": return "Error";
            default: return type;
        }
    }

    private void writeLogFile() {
        try {
            File cacheDir = new File("cache/roms");
            if (!cacheDir.exists() && !cacheDir.mkdirs()) {
                LogManager.getInstance().warning(ProcessSyncRoms.class, "Could not create log directory: " + cacheDir.getAbsolutePath());
            }

            File logFile = new File(cacheDir, "sync-" + timestamp + ".log");
            try (PrintWriter writer = new PrintWriter(logFile)) {
                writer.println("RomManager ROM Export Log - " + timestamp);
                writer.println("=================================");
                writer.println();

                for (Map.Entry<String, Map<String, List<String>>> consoleEntry : groupedLogs.entrySet()) {
                    String console = consoleEntry.getKey();
                    writer.println("Console: " + console);
                    writer.println(new String(new char[console.length() + 9]).replace('\0', '-'));
                    for (Map.Entry<String, List<String>> typeEntry : consoleEntry.getValue().entrySet()) {
                        writer.println("  " + typeEntry.getKey().toUpperCase() + " (" + typeEntry.getValue().size() + "):");
                        for (String message : typeEntry.getValue()) {
                            writer.println("    - " + message);
                        }
                        writer.println();
                    }
                    writer.println();
                }
            }
        } catch (IOException ex) {
            LogManager.getInstance().error(ProcessSyncRoms.class, "Failed to write ROM export log file", ex);
        }
    }

    public void setOnlyCultes(boolean onlyCultes) {
        this.onlyCultes = onlyCultes;
    }

    //TODO: Use a Map instead ...
    private Pair<RomContainer, RomVersion> searchInSourceList(File file) throws InterruptedException {
        for (RomContainer romContainer : romSourceList) {
            for (RomVersion romVersion : romContainer.getToCopyVersions()) {
                this.checkAbort();
                String exportFilename = romVersion.getExportPath(romContainer.getConsole(), exportPath, destination);
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
