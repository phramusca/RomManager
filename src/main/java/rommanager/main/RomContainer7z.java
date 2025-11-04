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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedHashSet;
import rommanager.utils.LogManager;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.io.FilenameUtils;
import rommanager.utils.FileSystem;
import rommanager.utils.FileSystem7z;
import rommanager.utils.ProgressBar;

/**
 *
 * @author phramusca ( https://github.com/phramusca/JaMuz/ )
 */
public class RomContainer7z extends RomContainer {

    /**
     * For 7z files including rom versions
     * @param console
     * @param filename
     */
    public RomContainer7z(Console console, String filename) {
        super(console, filename);
    }

    public void setVersions(ProgressBar progressBar, String path) throws IOException {
        File sourceFile = new File(FilenameUtils.concat(path, filename));
        try (SevenZFile sevenZFile = SevenZFile.builder().setFile(sourceFile).get()) {
            SevenZArchiveEntry entry = sevenZFile.getNextEntry();
            String name;
            String msg = progressBar.getString();
            LinkedHashSet<Console> moveTo = new LinkedHashSet<>();
            while(entry!=null){
                name=entry.getName();
                progressBar.setString(msg+" : "+name);
                String ext = FilenameUtils.getExtension(name);
                
                // Determine the correct console based on file extension
                Console versionConsole = console;
                switch (ext) {
                    case "gb":
                        moveTo.add(Console.gb);
                        versionConsole = Console.gb;
                        break;
                    case "gbc":
                        moveTo.add(Console.gbc);
                        versionConsole = Console.gbc;
                        break;
                    case "ws":
                        moveTo.add(Console.wswan);
                        versionConsole = Console.wswan;
                        break;
                    case "wsc":
                        moveTo.add(Console.wswanc);
                        versionConsole = Console.wswanc;
                        break;
                }
                
                RomVersion romVersion = new RomVersion(
                        FilenameUtils.getBaseName(filename), 
                        name,
                        versionConsole,
                        entry.getCrcValue(),
                        entry.getSize());
                versions.add(romVersion);
                entry = sevenZFile.getNextEntry();
            }
            
            if(!moveTo.isEmpty()) {
                if(moveTo.size()>1) {
                    // Split the 7z file into separate files by console
                    // Split 7z files containing both GB and GBC (or WS and WSC) versions
                    boolean splitSuccess = split7zByConsole(sourceFile, path, moveTo, progressBar);
                    if (splitSuccess) {
                        // After successful split, filter versions to keep only those matching current console
                        // The split files will be detected in their respective console folders
                        String currentConsoleExt = getExtensionForConsole(console);
                        if (currentConsoleExt != null) {
                            versions.removeIf(v -> {
                                String versionExt = FilenameUtils.getExtension(v.getFilename());
                                return !versionExt.equals(currentConsoleExt.substring(1)); // Remove leading dot
                            });
                        }
                        // If no versions remain for this console, mark container as empty
                        // (it will be skipped when adding to table)
                    } else {
                        // Fallback to old behavior if split fails
                        Console moveToConsole = moveTo.stream().findFirst().get();
                        if(moveTo.contains(Console.gb) && moveTo.contains(Console.gbc)) {
                            moveToConsole = Console.gbc;
                        }
                        else if(moveTo.contains(Console.wswan) && moveTo.contains(Console.wswanc)) {
                            moveToConsole = Console.wswanc;
                        }
                        if(!console.equals(moveToConsole)) {
                            File destFile = new File(sourceFile.getAbsolutePath().replace("/"+console.getSourceFolderName()+"/", "/"+moveToConsole.getSourceFolderName()+"/"));
                            if(FileSystem.moveFile(sourceFile, destFile)) {
                                console = moveToConsole;
                            }
                        }
                    }
                } else {
                    // Single console - just move if needed
                    Console moveToConsole = moveTo.stream().findFirst().get();
                    if(!console.equals(moveToConsole)) {
                        File destFile = new File(sourceFile.getAbsolutePath().replace("/"+console.getSourceFolderName()+"/", "/"+moveToConsole.getSourceFolderName()+"/"));
                        if(FileSystem.moveFile(sourceFile, destFile)) {
                            console = moveToConsole;
                        }
                    }
                }
            }
        }
		setExportableVersions();
	}
    
    /**
     * Split a 7z file containing multiple console types (e.g., GB and GBC) into separate 7z files
     * @param sourceFile Original 7z file
     * @param path Base path for source file
     * @param consoles Set of consoles found in the archive
     * @param progressBar Progress bar for status updates
     * @return true if split was successful, false otherwise
     */
    private boolean split7zByConsole(File sourceFile, String path, LinkedHashSet<Console> consoles, ProgressBar progressBar) {
        File tempDir = null;
        File tempFileForSourceConsole = null;
        try {
            // Create temporary directory to extract files
            tempDir = Files.createTempDirectory("rommanager_split_").toFile();
            
            // Extract all files to temp directory
            progressBar.setString("Extracting files for split...");
            if (!FileSystem7z.extract7z(sourceFile, tempDir)) {
                LogManager.getInstance().error(RomContainer7z.class, 
                    "Failed to extract 7z archive for splitting.");
                return false;
            }
            
            // Create separate 7z files for each console
            boolean allSuccess = true;
            for (Console targetConsole : consoles) {
                String extension = getExtensionForConsole(targetConsole);
                if (extension == null) continue;
                
                progressBar.setString("Creating " + targetConsole.getSourceFolderName() + " archive...");
                
                File target7zFile;
                
                if (targetConsole.equals(console)) {
                    // For the source console, create in temp directory first to avoid conflict
                    // We'll move it to replace the original later
                    tempFileForSourceConsole = Files.createTempFile("rommanager_split_", ".7z").toFile();
                    // Delete the empty file created by createTempFile - 7z will create it
                    if (tempFileForSourceConsole.exists()) {
                        tempFileForSourceConsole.delete();
                    }
                    target7zFile = tempFileForSourceConsole;
                } else {
                    // For other consoles, create in their respective folders
                    String targetPath = path.replace("/" + console.getSourceFolderName() + "/", 
                        "/" + targetConsole.getSourceFolderName() + "/");
                    File targetDir = new File(targetPath);
                    if (!targetDir.exists()) {
                        targetDir.mkdirs();
                    }
                    target7zFile = new File(targetPath, filename);
                }
                
                // Create 7z with filtered extension
                boolean created = FileSystem7z.create7zArchive(tempDir, target7zFile, extension);
                if (!created) {
                    allSuccess = false;
                    LogManager.getInstance().error(RomContainer7z.class, 
                        "Failed to create 7z archive for console: " + targetConsole.getSourceFolderName());
                }
            }
            
            // If all splits succeeded, replace original with the split version for source console
            if (allSuccess) {
                if (tempFileForSourceConsole != null && tempFileForSourceConsole.exists()) {
                    // Replace original file with the split version for source console
                    if (sourceFile.delete()) {
                        FileSystem.moveFile(tempFileForSourceConsole, sourceFile);
                    } else {
                        LogManager.getInstance().error(RomContainer7z.class, 
                            "Failed to delete original file for replacement.");
                        allSuccess = false;
                    }
                } else if (consoles.contains(console)) {
                    // Source console was in the list but no temp file was created
                    // This shouldn't happen, but if it does, we still need to delete the original
                    // since we created files for other consoles
                    sourceFile.delete();
                }
                return allSuccess;
            } else {
                // Clean up temp file if it was created
                if (tempFileForSourceConsole != null && tempFileForSourceConsole.exists()) {
                    tempFileForSourceConsole.delete();
                }
                return false;
            }
            
        } catch (IOException | InterruptedException ex) {
            LogManager.getInstance().error(RomContainer7z.class, 
                "Error splitting 7z archive", ex);
            // Clean up temp file if it was created
            if (tempFileForSourceConsole != null && tempFileForSourceConsole.exists()) {
                tempFileForSourceConsole.delete();
            }
            return false;
        } finally {
            // Clean up temp directory
            if (tempDir != null && tempDir.exists()) {
                try {
                    org.apache.commons.io.FileUtils.deleteDirectory(tempDir);
                } catch (IOException ex) {
                    LogManager.getInstance().warning(RomContainer7z.class, 
                        "Failed to delete temp directory", ex);
                }
            }
        }
    }
    
    /**
     * Get file extension for a console
     * @param console Console enum
     * @return File extension (e.g., ".gb") or null
     */
    private String getExtensionForConsole(Console console) {
        if (console == Console.gb) return ".gb";
        if (console == Console.gbc) return ".gbc";
        if (console == Console.wswan) return ".ws";
        if (console == Console.wswanc) return ".wsc";
        return null;
    }
	
	@Override
	public final void setExportableVersions() {
		setBestExportable();
	}
}