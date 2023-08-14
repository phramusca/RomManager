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
import java.util.ArrayList;
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
    private final ProgressBar progressBarConsole;
	private final ProgressBar progressBarGame;
	private final TableModelRom tableModel;
	private final ICallBackProcess callBack;
    private	boolean refresh;
	private final Map<String, RomContainerFlat> flatContainers = new HashMap<>();
    private static int nbFiles=0;
	
    public static final ArrayList<String> allowedExtensions = new ArrayList<String>() {
        {
            add("7z");
            add("dsk"); //amstradcpc
            add("vb"); // virtualboy
        }
    };	
    
		
	public ProcessList(
			String sourcePath, 
            ProgressBar progressBarConsole, 
			ProgressBar progressBar, 
			TableModelRom tableModel, 
			ICallBackProcess callBack) {
		super("Thread.ProcessList");
		this.sourcePath = sourcePath;
		this.progressBarConsole = progressBarConsole;
        this.progressBarGame = progressBar;
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
			progressBarGame.setup(nbSelected);
            progressBarConsole.setup(Console.values().length);
			for(Console console : Console.values()) {
                checkAbort();
                progressBarConsole.progress(console.getName());
				if(console.isSelected()) {
					list(console, FilenameUtils.concat(sourcePath, console.name()));
				}
			}
            progressBarGame.setIndeterminate("Saving ods file");
			RomManagerOds.createFile(tableModel, progressBarGame, sourcePath);
            progressBarConsole.reset();
			progressBarGame.reset();
			Popup.info("Listing complete.");
		} catch (InterruptedException ex) {
//			Popup.info("Aborted by user");
		} finally {
			callBack.completed();
		}
	}
	
	void browseNbFiles() {				
		progressBarGame.setIndeterminate("Getting number of files");
		for(Console console : Console.values()) {
			nbFiles=0;
			browseNbFiles(new File(FilenameUtils.concat(sourcePath, console.name())), console);
			console.setNbFiles(nbFiles);
		}
		progressBarGame.reset();
	}

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
        progressBarGame.setup(flatContainers.values().size());
		for(RomContainerFlat romContainerFlat : flatContainers.values()) {
			checkAbort();
			romContainerFlat.setExportableVersions();
			tableModel.addRow(romContainerFlat);
            String msg=console.getName()+" \\ "+FilenameUtils.getName(file.getAbsolutePath());
			progressBarGame.progress(msg);
		}  
	}
	
	private void browseNbFiles(File path, Console console) {
		if(path.isDirectory()) {
			File[] files = path.listFiles((File pathname) -> {
			String ext = FilenameUtils.getExtension(
					pathname.getAbsolutePath())
					.toLowerCase();
			return allowedExtensions.contains(ext)
					|| pathname.isDirectory();
			});
			if (files != null && files.length>0) {
				for (File file : files) {
					if (file.isDirectory()) {
						browseNbFiles(file, console);
					} else {
						nbFiles+=1;
                        progressBarGame.setMaximum(nbFiles);
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
			return allowedExtensions.contains(ext)
					|| pathname.isDirectory();
		});
		if (files == null || files.length<=0) {
			return;
		} 
		for (File file : files) {
			checkAbort();
			String msg=console.getName()+" \\ "+FilenameUtils.getName(file.getAbsolutePath());
			progressBarGame.progress(msg);
			if (file.isDirectory()) {
				browseFoldersFS(
						console,
						rootPath,
						file);
			}
			else {
                String ext = FilenameUtils.getExtension(file.getAbsolutePath());
                if(ext.equals("7z")) {
                    try {
                        RomContainer7z romSevenZipFile;
                        String key = console.name()+"/"+FilenameUtils.getName(file.getAbsolutePath());
                        if(!tableModel.getRoms().containsKey(key)) {
                            romSevenZipFile = 
                                    new RomContainer7z(
                                            console, FilenameUtils.getName(file.getAbsolutePath()));
                            romSevenZipFile.setVersions(progressBarGame, FilenameUtils.getFullPath(file.getAbsolutePath()));
                            tableModel.addRow(romSevenZipFile);
                        }
                    } catch (IOException ex) {
                        //FIXME 4 Manage errors (here and elsewhere): log in a file & display in gui somehow (with a filter ideally)
                        Logger.getLogger(ProcessList.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else if(allowedExtensions.contains(ext)) {
                    String romName = RomContainerFlat.getRomName(FilenameUtils.getBaseName(file.getAbsolutePath()), ext);
                    String key = console.name()+"/"+romName;
                    if(!tableModel.getRoms().containsKey(key)) {
                        RomContainerFlat romContainerFlat;
                        if(flatContainers.containsKey(key)) {
                            romContainerFlat = flatContainers.get(key);
                        } else {
                            romContainerFlat = 
                                    new RomContainerFlat(
                                            console,
                                            romName);
                            flatContainers.put(key, romContainerFlat);
                        }
                        String versionPath = 
                                file.getAbsolutePath()
                                        .substring(rootPath.length()+1);
                        romContainerFlat.addVersion(new RomVersion(
                                FilenameUtils.getBaseName(romName),
                                versionPath,
                                console, -1, file.length()));
                    }
                }
			}
		}
    }

	void start(boolean refresh) {
		this.refresh=refresh;
		this.start();
	}
}