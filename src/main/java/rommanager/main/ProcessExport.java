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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import rommanager.utils.FileSystem;
import rommanager.utils.Popup;
import rommanager.utils.ProcessAbstract;
import rommanager.utils.ProgressBar;

/**
 *
 * @author phramusca ( https://github.com/phramusca/JaMuz/ )
 */
public class ProcessExport extends ProcessAbstract {

	private final String exportPath;
	private final ProgressBar progressBar;
	private final TableModelRom tableModel;
	private final ICallBackProcess callBack;
	private final String sourcePath;
	
	private List<RomContainer> romSourceList;
	private List<File> romDestinationList;
	
	public ProcessExport(
			String sourcePath, 
			String exportPath, 
			ProgressBar progressBar, 
			TableModelRom tableModel, 
			ICallBackProcess callBack) {
		super("Thread.ProcessExport");
		this.sourcePath = sourcePath;
		this.exportPath = exportPath;
		this.progressBar = progressBar;
		this.tableModel = tableModel;
		this.callBack = callBack;
	}

	@Override
	public void run() {
		try {
			
			List<Console> consoles = tableModel.getRoms().values().stream()
					.map(v -> v.getConsole())
					.distinct()
					.collect(Collectors.toList());
			
			//Get files currently on destination
			romDestinationList = new ArrayList<>();
			for(Console console : consoles) {
				checkAbort();
				if(console.isSelected()) {
					String consolePath = FilenameUtils.concat(FilenameUtils.concat(exportPath, console.name()), console.getName());
					if(!new File(consolePath).exists()) {
						if(!new File(consolePath).mkdirs()) {
							Popup.error("Error creating "+consolePath);
							callBack.completed();
						}
					} else {
						browseFS(new File(consolePath));
					}
				}
			}
			
			//Get source roms & setToCopyTrue(true)
			romSourceList = tableModel.getRoms().values()
					.stream().filter(r->r.getConsole().isSelected() && r.getExportableVersions().size()>0)
					.peek(r -> r.setToCopyTrue())
					.collect(Collectors.toList());
			
			//Remove files on destination
			this.checkAbort();
			progressBar.setup(romSourceList.size() + romDestinationList.size());
			for (File file : romDestinationList) {
				this.checkAbort();
				if(!searchInSourceList(file)) {
					//Not a file to be copied, removing it on destination
					file.delete();
				}
				progressBar.progress(file.getAbsolutePath());
			}

			//Copy files to destination
			romSourceList = romSourceList
					.stream().filter(r->r.getConsole().isSelected() 
							&& r.getExportableVersions().size()>0)
					.collect(Collectors.toList());
			String sourceFolder;
			for(RomContainer romContainer : romSourceList) {
				checkAbort();
				String filename = romContainer.getFilename();
				progressBar.progress(romContainer.getConsoleStr()+" \\ "+romContainer.getFilename());
				for(RomVersion romVersion : 
						romContainer.getVersions().stream()							
							.filter(r -> r.isToCopy())
							.collect(Collectors.toList())) {
					checkAbort();
					sourceFolder = FilenameUtils.concat(sourcePath, romContainer.getConsole().name());
                    File sourceFile = new File(FilenameUtils.concat(sourceFolder, romVersion.getFilename()));
                    File exportFile = new File(romVersion.getExportFilename(romContainer.getConsole(), exportPath));
                    String ext = FilenameUtils.getExtension(filename);
                    if(ext.equals("7z")) {
						try (SevenZFile sevenZFile = new SevenZFile(new File(
							FilenameUtils.concat(sourceFolder, filename)))) {
							SevenZArchiveEntry entry = sevenZFile.getNextEntry();
							while(entry!=null){
								if(entry.getName().equals(romVersion.getFilename())) {
                                    File unzippedFile = new File(FilenameUtils.concat(romVersion.getExportFolder(romContainer.getConsole(), exportPath), romVersion.getFilename()));
									try (FileOutputStream out = new FileOutputStream(unzippedFile)) {
										byte[] content = new byte[(int) entry.getSize()];
										sevenZFile.read(content, 0, content.length);
										out.write(content);
									}
                                    if(romContainer.getConsole().isZip()) {
                                        zipFile(unzippedFile, exportFile.getAbsolutePath());
                                        unzippedFile.delete();
                                    }
									break;
								}
								entry = sevenZFile.getNextEntry();
							}
						}
                    } else if(ProcessList.allowedExtensions.contains(ext)) {
                        if(romContainer.getConsole().isZip()) {
                            zipFile(sourceFile, exportFile.getAbsolutePath());
                        } else {
                            FileSystem.copyFile(sourceFile, exportFile);
                        }
                    }
				}
			}
			Popup.info("Export complete.");
			progressBar.reset();
		} catch (InterruptedException ex) {
//			Popup.info("Aborted by user");
		} catch (IOException ex) {
			Logger.getLogger(ProcessExport.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			callBack.completed();
		}
	}
	
	//TODO: Use a Map instead ...
	private boolean searchInSourceList(File file) throws InterruptedException {
		
		//FileSystem.copyFile preserves datetime
		//Unfortunatly on some devices it does not work
		//ex: Android (https://stackoverflow.com/questions/18677438/android-set-last-modified-time-for-the-file)
		//=> TODO Make options of these, must be one or the other
		//to detect if file is different
		
		// !!! DO NOT THOSE BELOW to true
		// unless you set fileSource properly
		// and that you can manage the case of a RomVersion in a 7z (H2 getlength, size and content ?)
		boolean doCheckLength = false;
		boolean doCheckLastModified = false;
		boolean doCheckContent = false;

		for(RomContainer romContainer : romSourceList) {
			for(RomVersion romVersion : romContainer.getToCopyVersions()) {
				this.checkAbort();
				try {
				//TODO: maybe support ignoreCase as an option
	//			if(fileInfo.getRelativeFullPath().equalsIgnoreCase(relativeFullPath)) { return true; }
				//We want sync to be case sensitive
                    String exportFilename = romVersion.getExportFilename(romContainer.getConsole(), exportPath);
					if(exportFilename.equals(file.getAbsolutePath())) { 
						File fileSource = new File(FilenameUtils.concat(
								sourcePath, romVersion.getFilename()));
						File fileDestination = new File(exportFilename);
						if(!doCheckLength || fileSource.length()==fileDestination.length()) {
							if(!doCheckLastModified || fileSource.lastModified()==fileDestination.lastModified() ) {
									if(!doCheckContent || 
											FileUtils.contentEquals(fileSource, 
													fileDestination)) {
										romVersion.setToCopy(false); 
										return true; 
									}
							}
						}
					}
				} catch (IOException ex) {
					Logger.getLogger(ProcessExport.class.getName())
							.log(Level.SEVERE, null, ex);
				}
			}
		}
		return false;
	}
	
	private void browseFS(File path) throws InterruptedException {
        this.checkAbort();
        //Verifying we have a path and not a file
        if (path.isDirectory()) {
            File[] files = path.listFiles();
            if (files != null) {
				//TODO: Either use (is this needed ? safe ?) OR remove commented
//                if(files.length<=0) {
//                    if(!FilenameUtils.equalsNormalizedOnSystem(
//							this.device.getDestination(), 
//							path.getAbsolutePath())) {
//                        Jamuz.getLogger().log(Level.FINE, 
//								"Deleted empty folder \"{0}\"", 
//								path.getAbsolutePath());  //NOI18N
//                        path.delete();
//                    }
//                }
//                else {
                    for (File file : files) {
                        this.checkAbort();
                        if (file.isDirectory()) {
                            browseFS(file);
                        }
                        else {
                            this.romDestinationList.add(file);
                        }
                    }
//                }
            } 
        }
	}
	
     //FIXME 2 Zip sometimes contains 0b files :( Only over sshfs ?
	private static boolean zipFile(File inputFile, String zipFilePath) {
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
            System.out.println("Regular file :" + inputFile.getCanonicalPath()+" is zipped to archive :"+zipFilePath);
			return true;
        } catch (IOException ex) {
            Logger.getLogger(ProcessExport.class.getName()).log(Level.SEVERE, null, ex);
			return false;
        }
    }
}