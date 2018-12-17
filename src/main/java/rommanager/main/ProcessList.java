/* 
 * Copyright (C) 2018 phramusca ( https://github.com/phramusca/JaMuz/ )
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
	private final TableModelRomSevenZip tableModel;
	private Map<String, Game> games;
	private final ICallBackProcess callBack;
		
	public ProcessList(String sourcePath, ProgressBar progressBar, TableModelRomSevenZip tableModel, ICallBackProcess callBack) {
		super("Thread.gamelist.ProcessList");
		this.sourcePath = sourcePath;
		this.progressBar = progressBar;
		this.tableModel = tableModel;
		this.callBack = callBack;
	}

	@Override
	public void run() {
		try {
			for(Console console : Console.values()) {
				checkAbort();
				list(console, FilenameUtils.concat(sourcePath, console.name()));
			}
			progressBar.setIndeterminate("Saving ods file");
			RomManagerOds.createFile(tableModel, progressBar);
			Popup.info("Listing complete.");
			progressBar.reset();
			
		} catch (InterruptedException ex) {
			Popup.info("Aborted by user");
		} finally {
			callBack.completed();
		}
	}

	private void list(Console console, String path) throws InterruptedException {

		File file = new File(path);
        if(!file.exists()) {
            Logger.getLogger(RomManagerOds.class.getName())
					.log(Level.SEVERE, "No such path: {0}", path);
            return;
        }
        
        if(console.toString().trim().toUpperCase().equals("CHANGEME")) {
            Popup.warning("Unsupported console: "+console);
            return;
        }
		
		browseFoldersFS(console, path, new File(path), progressBar, tableModel);
		for(RomSevenZipFile romSevenZipFile : amstradRoms.values()) {
			checkAbort();
			romSevenZipFile.setScore(false);
			tableModel.addRow(romSevenZipFile);
		}
		progressBar.reset();   
	}
	
	private final Map<String, RomSevenZipFile> amstradRoms = new HashMap<>();
  
    private void browseFoldersFS(Console console, String rootPath, File path, 
			ProgressBar progressBar, TableModelRomSevenZip model) throws InterruptedException {
        if (path.isDirectory()) {
            File[] files = path.listFiles((File pathname) -> {
				String ext = FilenameUtils.getExtension(pathname.getAbsolutePath())
						.toLowerCase();
				return ext.equals("7z")
						|| ext.equals("dsk")
						|| pathname.isDirectory();
			});
            if (files != null) {
                if(files.length>0) {
                    progressBar.setup(files.length);
                    for (File file : files) {
						checkAbort();
                        progressBar.progress(FilenameUtils.getName(file.getAbsolutePath()));
                        if (file.isDirectory()) {
                            browseFoldersFS(console, rootPath, file, progressBar, model);
                        }
                        else {
							switch (FilenameUtils.getExtension(file.getAbsolutePath())) {
								case "7z":
									try {
										RomSevenZipFile romSevenZipFile;
										if(!tableModel.getRoms().containsKey(FilenameUtils.getName(file.getAbsolutePath()))) {
											romSevenZipFile = new RomSevenZipFile(console, file);
											romSevenZipFile.setVersions();
											model.addRow(romSevenZipFile);
										} 
									} catch (IOException ex) {
										Logger.getLogger(ProcessList.class.getName()).log(Level.SEVERE, null, ex);
									}	break;
								case "dsk":
									try {
										String romName = FilenameUtils.getBaseName(file.getAbsolutePath());
										int pos = romName.indexOf("(");
										if(pos>=0) {
											romName=romName.substring(0, pos).trim();
										}
										romName=romName.concat(".dsk");
										
										RomSevenZipFile romSevenZipFile;
										if(amstradRoms.containsKey(romName)) {
											romSevenZipFile = amstradRoms.get(romName);
										} else {
											romSevenZipFile = new RomSevenZipFile(console, file, romName);
											amstradRoms.put(romName, romSevenZipFile);
										}
										String versionPath = file.getAbsolutePath().substring(rootPath.length()+1);
										
										romSevenZipFile.addAmstradVersion(new RomVersion(
												romName,
												versionPath));
									} catch (IOException ex) {
										Logger.getLogger(ProcessList.class.getName()).log(Level.SEVERE, null, ex);
									}	break;
								default:
									break;
							}
                        }
                    }
                }
            } 
        }
    }
}