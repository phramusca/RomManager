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
			progressBar.setIndeterminate("Getting number of files");
			for(Console console : Console.values()) {
				checkAbort();
				browseNbFiles(new File(FilenameUtils.concat(sourcePath, console.name())), console);
			}
			progressBar.setup(nbFiles);
			
			for(Console console : Console.values()) {
				checkAbort();
				list(console, FilenameUtils.concat(sourcePath, console.name()));
			}
			
			Popup.info("Listing complete.");
		} catch (InterruptedException ex) {
//			Popup.info("Aborted by user");
		} finally {
			progressBar.setIndeterminate("Saving ods file");
			RomManagerOds.createFile(tableModel, progressBar);
			progressBar.reset();
			callBack.completed();
		}
	}

	private void list(Console console, String path) throws InterruptedException {

		File file = new File(path);
        if(!file.exists()) {
            Logger.getLogger(ProcessList.class.getName())
					.log(Level.SEVERE, "No such path: {0}", path);
            return;
        }
        
        if(console.toString().trim().toUpperCase().equals("CHANGEME")) {
            Popup.warning("Unsupported console: "+console.name()+" ("+console.toString()+")");
            return;
        }
		
		browseFoldersFS(console, path, new File(path));
		for(RomContainerAmstrad romAmstrad : amstradRoms.values()) {
			checkAbort();
			romAmstrad.setScore(false);
			tableModel.addRow(romAmstrad);
		}  
	}
	
	private final Map<String, RomContainerAmstrad> amstradRoms = new HashMap<>();
  
	private int nbFiles=0;
	
	private void browseNbFiles(File path, Console console) throws InterruptedException {
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
					checkAbort();
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
			String msg=console.toString()+" \\ "+FilenameUtils.getName(file.getAbsolutePath());
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
												console, file);
								romSevenZipFile.setVersions(progressBar);
								tableModel.addRow(romSevenZipFile);
							} 
						} catch (IOException ex) {
							Logger.getLogger(ProcessList.class.getName())
									.log(Level.SEVERE, null, ex);
						}	break;
					case "dsk":
						try {
							String romName = FilenameUtils
									.getBaseName(file.getAbsolutePath());
							int pos = romName.indexOf("(");
							if(pos>=0) {
								romName=romName.substring(0, pos).trim();
							}
							romName=romName.concat(".dsk");
							if(!tableModel.getRoms().containsKey(romName)) {
								RomContainerAmstrad containerAmstrad;
								if(amstradRoms.containsKey(romName)) {
									containerAmstrad = amstradRoms.get(romName);
								} else {
									containerAmstrad = 
											new RomContainerAmstrad(
													console,
													file, 
													romName);
									amstradRoms.put(romName, containerAmstrad);
								}
								String versionPath = 
										file.getAbsolutePath()
												.substring(rootPath.length()+1);
								containerAmstrad.addVersion(new RomVersion(
										romName,
										versionPath));
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
}