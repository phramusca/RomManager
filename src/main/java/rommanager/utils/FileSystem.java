/*
 * Copyright (C) 2014 phramusca ( https://github.com/phramusca/JaMuz/ )
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.io.FileExistsException;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author phramusca ( https://github.com/phramusca/JaMuz/ )
 */
public class FileSystem {

	/**
	 * Move a file
	 * @param sourceFile
	 * @param destFile
	 * @return
	 */
	public static boolean moveFile(File sourceFile, File destFile) {
		try {
			sourceFile=replaceHome(sourceFile);
			destFile=replaceHome(destFile);
			FileUtils.moveFile(sourceFile, destFile);
			return true;
		} catch(FileExistsException ex) {
            //This case will only occur in some particular circumstances
            //(exemple: moving a file (case modified only) under linux on a FAT system (not case sensitive)
            return false;
        } 
        catch (IOException ex) {
			Popup.error(ex);
			return false;
		}
	}
	
	/**
	 * Copy a file
	 * @param sourceFile
	 * @param destFile
     * @throws java.io.IOException
	 */
	public static void copyFile(File sourceFile, File destFile) throws IOException {
			sourceFile=replaceHome(sourceFile);
			destFile=replaceHome(destFile);
			
			FileUtils.copyFile(sourceFile, destFile, true);
    }
    
    public static void writeTextFile(File file, String text) throws IOException {
        try (BufferedWriter out = new BufferedWriter(new FileWriter(file.getAbsolutePath(), false))) {
            out.write(text);
            out.flush();
        }
    }
    
    public static String readTextFile(File file) throws FileNotFoundException, IOException {
        StringBuilder text = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
        }
        return text.toString();
    }

	public static File replaceHome(File file) {
		return replaceHome(file.getPath());
	}
	
	public static File replaceHome(String fileURL) {
		//Replacing ~ by real home path, ONLY IF AT START !
		if(fileURL.startsWith("~")) {  //NOI18N
			fileURL=fileURL.replace("~", System.getProperty("user.home"));  //NOI18N
		}
		return new File(fileURL);
	}
	
	/**
	* Attempts to calculate the size of a file or directory.
	* 
	* <p>
	* Since the operation is non-atomic, the returned value may be inaccurate.
	* However, this method is quick and does its best.
	 * @param path
	 * @return 
	*/
   public static long size(Path path) {

	   final AtomicLong size = new AtomicLong(0);

	   try {
		   Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
			   @Override
			   public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {

				   size.addAndGet(attrs.size());
				   return FileVisitResult.CONTINUE;
			   }

			   @Override
			   public FileVisitResult visitFileFailed(Path file, IOException exc) {

				   System.out.println("skipped: " + file + " (" + exc + ")");
				   // Skip folders that can't be traversed
				   return FileVisitResult.CONTINUE;
			   }

			   @Override
			   public FileVisitResult postVisitDirectory(Path dir, IOException exc) {

				   if (exc != null)
					   System.out.println("had trouble traversing: " + dir + " (" + exc + ")");
				   // Ignore errors traversing a folder
				   return FileVisitResult.CONTINUE;
			   }
		   });
	   } catch (IOException e) {
		   throw new AssertionError("walkFileTree will not throw IOException if the FileVisitor does not");
	   }

	   return size.get();
   }
   
	public static boolean zipFile(File inputFile, File zipFilePath) {
        try {
			try (FileOutputStream fileOutputStream = new FileOutputStream(zipFilePath); 
					ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream)) {
				
				ZipEntry zipEntry = new ZipEntry(inputFile.getName());
				zipOutputStream.putNextEntry(zipEntry);
				FileInputStream fileInputStream = new FileInputStream(inputFile);
				byte[] buf = new byte[1024];
				int bytesRead;
				while ((bytesRead = fileInputStream.read(buf)) > 0) {
					zipOutputStream.write(buf, 0, bytesRead);
				}
				zipOutputStream.closeEntry();
			}
            System.out.println("Regular file :" + inputFile.getCanonicalPath()+" is zipped to archive :"+zipFilePath.getAbsolutePath());
			return true;
        } catch (IOException ex) {
            Logger.getLogger(FileSystem.class.getName()).log(Level.SEVERE, null, ex);
			return false;
        }
    }
}
