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
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FilenameUtils;
import rommanager.utils.Popup;
import rommanager.utils.ProcessAbstract;
import rommanager.utils.ProgressBar;

/**
 *
 * @author phramusca ( https://github.com/phramusca/JaMuz/ )
 */
public class ProcessList extends ProcessAbstract {

	private final String sourcePath;
	private final ProgressBar progressBar;
	private final TableModelRom tableModel;
	private final ICallBackProcess callBack;
		
	public ProcessList(
			String sourcePath, 
			ProgressBar progressBar, 
			TableModelRom tableModel, 
			ICallBackProcess callBack) {
		super("Thread.ProcessList");
		this.sourcePath = sourcePath;
		this.progressBar = progressBar;
		this.tableModel = tableModel;
		this.callBack = callBack;
	}

	@Override
	public void run() {
		
		try {
			int nbSelected=0;
			for(Console console : Console.values()) {
				checkAbort();
				if(console.isSelected()) {
					nbSelected+=console.getNbFiles();
				}
			}
			progressBar.setup(nbSelected);
			for(Console console : Console.values()) {
				checkAbort();
				if(console.isSelected()) {
					list(console, FilenameUtils.concat(sourcePath, console.name()));
				}
			}
			Popup.info("Listing complete.");
		} catch (InterruptedException ex) {
//			Popup.info("Aborted by user");
		} finally {
			progressBar.setIndeterminate("Saving ods file");
			RomManagerOds.createFile(tableModel, progressBar, sourcePath);
			progressBar.reset();
			callBack.completed();
		}
	}
	
	void browseNbFiles() {				
		progressBar.setIndeterminate("Getting number of files");
		for(Console console : Console.values()) {
			nbFiles=0;
			browseNbFiles(new File(FilenameUtils.concat(sourcePath, console.name())), console);
			console.setNbFiles(nbFiles);
		}
		progressBar.reset();
	}
	
	boolean refresh;
	
	private void list(Console console, String path) throws InterruptedException {
		File file = new File(path);
        if(!file.exists()) {
            Logger.getLogger(ProcessList.class.getName())
					.log(Level.SEVERE, "No such path: {0}", path);
            return;
        }
        
        if(console.getName().trim().toUpperCase().equals("CHANGEME")) {
            Popup.warning("Unsupported console: "+console.name()+" ("+console.getName()+")");
            return;
        }
		if(refresh) {
			tableModel.getRoms().values().removeIf(r -> r.getConsole().equals(console));	
		}
		browseFoldersFS(console, path, new File(path));
		for(RomContainerFlat romAmstrad : amstradRoms.values()) {
			checkAbort();
			romAmstrad.setBestExportable();
			tableModel.addRow(romAmstrad);
		}  
	}
	
	private final Map<String, RomContainerFlat> amstradRoms = new HashMap<>();
	
	private static int nbFiles=0;
	private void browseNbFiles(File path, Console console) {
		if(path.isDirectory()) {
			File[] files = path.listFiles((File pathname) -> {
			String ext = FilenameUtils.getExtension(
					pathname.getAbsolutePath())
					.toLowerCase();
			return ext.equals("7z")
					|| ext.equals("dsk")
					|| pathname.isDirectory();
			});
			if (files != null && files.length>0) {
				for (File file : files) {
					if (file.isDirectory()) {
						browseNbFiles(file, console);
					} else {
						nbFiles+=1;
					}
				}
			}
		}
	}
	
    private void browseFoldersFS(Console console, String rootPath, File path) 
			throws InterruptedException {
        if(!path.isDirectory()) {
			return;
        }
		File[] files = path.listFiles((File pathname) -> {
			String ext = FilenameUtils.getExtension(
					pathname.getAbsolutePath())
					.toLowerCase();
			return ext.equals("7z")
					|| ext.equals("dsk")
					|| pathname.isDirectory();
		});
		if (files == null || files.length<=0) {
			return;
		} 
		for (File file : files) {
			checkAbort();
			String msg=console.getName()+" \\ "+FilenameUtils.getName(file.getAbsolutePath());
			progressBar.progress(msg);
			if (file.isDirectory()) {
				browseFoldersFS(
						console,
						rootPath,
						file);
			}
			else {
				switch (FilenameUtils.getExtension(file.getAbsolutePath())) {
					case "7z":
						try {
							RomContainer7z romSevenZipFile;
							if(!tableModel.getRoms().containsKey(
									FilenameUtils.getName(
											file.getAbsolutePath()))) {
								romSevenZipFile = 
										new RomContainer7z(
												console, FilenameUtils.getName(file.getAbsolutePath()));
								romSevenZipFile.setVersions(progressBar, FilenameUtils.getFullPath(file.getAbsolutePath()));
								tableModel.addRow(romSevenZipFile);
							} 
						} catch (IOException | OutOfMemoryError ex) {
							//FIXME 4 Manage errors (here and elsewhere): log in a file & display in gui somehow (with a filter ideally)
							Logger.getLogger(ProcessList.class.getName()).log(Level.SEVERE, null, ex);
						}	break;
					
					//FIXME 3 Manage  other sets no grouped in 7z files, such as :
					//From /media/raph/Maxtor1/Emulation/Roms/4_Sources => FAIRE MENAGE/archive.org  ___ NOUVEAU   _____ PAS MEME FORMAT  :-(
						// - GoodNGPxNonGood.7z		ngc. Others ?
						// - GoodVBoy				vb. Others ?
						// - GoodVect				vec. Others ?
						// - GoodGenV321 (Latest but Is it really better than GoodGen3.00 ?)
						// - GoodColNonGood			rom,col. Others ? (Attention, ces roms ne marchent pas)
						//
						//=> Maybe dot not check for extension, just default: ?
						
					case "dsk":
						try {
							String romName = RomContainerFlat.getRomName(FilenameUtils.getBaseName(file.getAbsolutePath()));
							if(!tableModel.getRoms().containsKey(romName)) {
								RomContainerFlat containerAmstrad;
								if(amstradRoms.containsKey(romName)) {
									containerAmstrad = amstradRoms.get(romName);
								} else {
									containerAmstrad = 
											new RomContainerFlat(
													console,
													romName);
									amstradRoms.put(romName, containerAmstrad);
								}
								String versionPath = 
										file.getAbsolutePath()
												.substring(rootPath.length()+1);
								containerAmstrad.addVersion(new RomVersion(
										romName,
										versionPath,
                                        Console.amstradcpc));
							}
						} catch (IOException ex) {
							Logger.getLogger(ProcessList.class.getName())
									.log(Level.SEVERE, null, ex);
						}	break;
					default:
						break;
				}
			}
		}
    }

	void start(boolean refresh) {
		this.refresh=refresh;
		this.start();
	}
}