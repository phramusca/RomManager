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
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
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
	private Map<String, Game> games;
	private final TableModelRom tableModel;
	private final ICallBackProcess callBack;
	private final String sourcePath;
	
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
			
			List<Console> collect = tableModel.getRoms().values().stream()
					.map(v -> v.getConsole())
					.distinct()
					.collect(Collectors.toList());
			
			for(Console console : collect) {
				checkAbort();
				String consolePath = FilenameUtils.concat(FilenameUtils.concat(exportPath, console.name()), console.toString());
				if(!new File(consolePath).exists()) {
					if(!new File(consolePath).mkdirs()) {
						Popup.error("Error creating "+consolePath);
						callBack.completed();
					}
				}
			}
			export();
			Popup.info("Export complete.");
			progressBar.reset();
		} catch (InterruptedException ex) {
//			Popup.info("Aborted by user");
		} finally {
			callBack.completed();
		}
	}


	
	//FIXME 6 Change "Export" into "Sync" => remove files not selected for export from exportPath
	
    public void export() throws InterruptedException {
        try {	
			progressBar.setup(tableModel.getRowCount());
			String sourceFolder;
			String exportFolder;
			for(RomContainer romContainer : tableModel.getRoms().values()) {
				checkAbort();
				String filename = romContainer.getFilename();
				progressBar.progress(romContainer.getConsoleStr()+" \\ "+romContainer.getFilename());
				for(RomVersion romVersion : 
						romContainer.getVersions().stream()
	// FIXME 4 Select with a checkbox (keep that in ods) what roms/versions for which console to export
		// Make filters (console but also genre, rating         ,"selected for export" - how?)												
	//(now export all consoles, only best version and all good dsk (amstrad) files)

							.filter(r -> r.isExportable() && r.getScore()>0)
							.collect(Collectors.toList())) {
					checkAbort();
					sourceFolder = FilenameUtils.concat(
							sourcePath, romContainer.getConsole().name());
					exportFolder = FilenameUtils.concat(
							FilenameUtils.concat(
									exportPath, 
									romContainer.getConsole().name()), 
							romContainer.getConsole().toString());
					if(FilenameUtils.getExtension(filename).equals("7z")) {
						String exportFileName = FilenameUtils.concat(
										exportFolder, 
										FilenameUtils.getBaseName(
												romVersion.getFilename())
											.concat(".zip"));
						
						if(new File(exportFileName).exists()) {
							continue;
						}
						try (SevenZFile sevenZFile = new SevenZFile(new File(
							FilenameUtils.concat(sourceFolder, filename)))) {
							SevenZArchiveEntry entry = sevenZFile.getNextEntry();
							while(entry!=null){
								if(entry.getName().equals(romVersion.getFilename())) { 
									File unzippedFile=new File(FilenameUtils.concat(
													exportFolder,
													entry.getName()));
									try (FileOutputStream out = new FileOutputStream(unzippedFile)) {
										byte[] content = new byte[(int) entry.getSize()];
										sevenZFile.read(content, 0, content.length);
										out.write(content);
									}
									//FIXME 5 Some need zip, some not (n64 for instance)
									if(zipFile(unzippedFile, exportFileName)) {
										unzippedFile.delete();
									}
									break;
								}
								entry = sevenZFile.getNextEntry();
							}
						}
					} else if(FilenameUtils.getExtension(filename).equals("dsk")) {
						File exportFile=new File(FilenameUtils.concat(exportFolder, romVersion.getFilename()));
						if(exportFile.exists()) {
							continue;
						}
						FileSystem.copyFile(
								new File(FilenameUtils.concat(sourceFolder, romVersion.getFilename())), 
								exportFile);
					}
				}
			}
		} catch (IOException ex) {
			Logger.getLogger(ProcessExport.class.getName()).log(Level.SEVERE, null, ex);
		} 
    }
	
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