/*
 * Copyright (C) 2025 raph
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
package rommanager.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for 7z archive operations
 * @author raph
 */
public class FileSystem7z {
    
    private static String cached7zCommand = null;
    
    /**
     * Find available 7z command (7z, 7za, or 7zr)
     * Tries in order: 7z (full version), 7za (standalone), 7zr (lite version)
     * @return Command name or null if not found
     */
    public static String find7zCommand() {
        // Cache the result to avoid repeated lookups
        if (cached7zCommand != null) {
            return cached7zCommand;
        }
        
        try {
            // Try 7z first (full version)
            ProcessBuilder pb = new ProcessBuilder("which", "7z");
            Process process = pb.start();
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                cached7zCommand = "7z";
                return cached7zCommand;
            }
            
            // Try 7za (standalone version, supports multiple formats)
            pb = new ProcessBuilder("which", "7za");
            process = pb.start();
            exitCode = process.waitFor();
            if (exitCode == 0) {
                cached7zCommand = "7za";
                return cached7zCommand;
            }
            
            // Try 7zr (lite version, 7z format only)
            pb = new ProcessBuilder("which", "7zr");
            process = pb.start();
            exitCode = process.waitFor();
            if (exitCode == 0) {
                cached7zCommand = "7zr";
                return cached7zCommand;
            }
        } catch (IOException | InterruptedException ex) {
            LogManager.getInstance().error(FileSystem7z.class, 
                "Error finding 7z command", ex);
        }
        return null;
    }
    
    /**
     * Extract a 7z archive to a directory
     * @param archiveFile Source 7z file
     * @param outputDir Destination directory
     * @return true if successful
     */
    public static boolean extract7z(File archiveFile, File outputDir) throws IOException, InterruptedException {
        String cmd7z = find7zCommand();
        if (cmd7z == null) {
            LogManager.getInstance().error(FileSystem7z.class, 
                "7z command not found. Cannot extract archive.");
            return false;
        }
        
        ProcessBuilder pb = new ProcessBuilder(cmd7z, "x", "-o" + outputDir.getAbsolutePath(), 
            archiveFile.getAbsolutePath(), "-y");
        Process process = pb.start();
        int exitCode = process.waitFor();
        
        return exitCode == 0;
    }
    
    /**
     * Create a 7z archive from files in a directory, filtering by extension
     * @param sourceDir Directory containing files to archive
     * @param outputFile Output 7z file
     * @param extensionFilter File extension to include (e.g., ".gb" or ".gbc"), null for all files
     * @return true if successful
     */
    public static boolean create7zArchive(File sourceDir, File outputFile, String extensionFilter) {
        try {
            String cmd7z = find7zCommand();
            if (cmd7z == null) {
                LogManager.getInstance().error(FileSystem7z.class, 
                    "7z or 7za not found. Cannot create 7z archive.");
                return false;
            }
            
            // Ensure output directory exists
            File outputDir = outputFile.getParentFile();
            if (outputDir != null && !outputDir.exists()) {
                outputDir.mkdirs();
            }
            
            // Delete output file if it exists (to avoid conflicts with 7z exit code 2)
            if (outputFile.exists()) {
                outputFile.delete();
            }
            
            // Build 7z command
            ProcessBuilder archivePb;
            if (extensionFilter != null && !extensionFilter.isEmpty()) {
                // Create archive with specific extension filter
                // Find all files with the extension and add them individually
                File[] files = sourceDir.listFiles((dir, name) -> name.toLowerCase().endsWith(extensionFilter.toLowerCase()));
                if (files == null || files.length == 0) {
                    // No files match - return false but don't log as error (might be expected)
                    return false;
                }
                List<String> cmd = new ArrayList<>();
                cmd.add(cmd7z);
                cmd.add("a");
                cmd.add("-t7z");
                cmd.add(outputFile.getAbsolutePath());
                cmd.add("-y");
                for (File file : files) {
                    cmd.add(file.getAbsolutePath());
                }
                archivePb = new ProcessBuilder(cmd);
            } else {
                // Create archive with all files
                archivePb = new ProcessBuilder(cmd7z, "a", "-t7z", outputFile.getAbsolutePath(), 
                    sourceDir.getAbsolutePath() + "/*", "-y");
            }
            
            Process archiveProcess = archivePb.start();
            int archiveExitCode = archiveProcess.waitFor();
            
            if (archiveExitCode == 0 && outputFile.exists()) {
                return true;
            } else {
                LogManager.getInstance().error(FileSystem7z.class, 
                    "Failed to create 7z archive: " + outputFile.getAbsolutePath());
                return false;
            }
        } catch (IOException | InterruptedException ex) {
            LogManager.getInstance().error(FileSystem7z.class, "Error creating 7z archive", ex);
            return false;
        }
    }
}

