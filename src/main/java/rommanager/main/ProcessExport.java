/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rommanager.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.io.FilenameUtils;
import rommanager.utils.Popup;
import rommanager.utils.ProcessAbstract;
import rommanager.utils.ProgressBar;

/**
 *
 * @author phramusca ( https://github.com/phramusca/JaMuz/ )
 */
public class ProcessExport extends ProcessAbstract {

	private final String rootPath;
	private final ProgressBar progressBar;
	private Map<String, Game> games;
	private final TableModelRomSevenZip tableModel;
	private final ICallBackProcess callBack;
	
	public ProcessExport(String rootPath, 
			ProgressBar progressBar, 
			TableModelRomSevenZip tableModel, 
			ICallBackProcess callBack) {
		super("Thread.gamelist.ProcessList");
		this.rootPath = rootPath;
		this.progressBar = progressBar;
		this.tableModel = tableModel;
		this.callBack = callBack;
	}

	@Override
	public void run() {
		try {
			for(Console console : Console.values()) {
				checkAbort();
//				extract(FilenameUtils.concat(rootPath, console.name()));
			}
			progressBar.setIndeterminate("Saving ods file");
			RomManagerOds.createFile(tableModel, progressBar);
			Popup.info("Export complete.");
			progressBar.reset();
		} catch (InterruptedException ex) {
			Popup.info("Aborted by user");
		} finally {
			callBack.completed();
		}
	}

	//FIXME 2 Rehabiliter extract() et changer en export()
	// Warning: isBest OR a new isSelected ?? Think of amstrad (or if we want to export several versions) !
	
//    public void extract() {
//        Thread t = new Thread("Thread.RomDevice.Extract") {
//            @Override
//            public void run() {
//                try {
//					
//					progressBar.setup(model.getRowCount());
//					
//					String extractPath = FilenameUtils.concat(path, "../Extract--"+name+"--"
//							.concat(DateTime.getCurrentLocal(DateTime.DateTimeFormat.FILE)));
//					if(!new File(extractPath).exists()) {
//						new File(extractPath).mkdirs();
//					}
//					
//					for(RomSevenZipFile romSevenZipFile : model.getRoms().values()) {
//						String filename = romSevenZipFile.getFilename();
//						for(RomVersion romVersion : 
//								romSevenZipFile.getVersions().stream()
//									.filter(r -> r.isBest() && r.getScore()>0)
//									.collect(Collectors.toList())) {
//							progressBar.progress(filename);
//							if(FilenameUtils.getExtension(filename).equals("7z")) {
//								try (SevenZFile sevenZFile = new SevenZFile(new File(
//									FilenameUtils.concat(path, filename)))) {
//									SevenZArchiveEntry entry = sevenZFile.getNextEntry();
//									while(entry!=null){
//										if(entry.getName().equals(romVersion.getFilename())) { 
//											File unzippedFile=new File(FilenameUtils.concat(
//															extractPath,
//															entry.getName()));
//											try (FileOutputStream out = new FileOutputStream(unzippedFile)) {
//												byte[] content = new byte[(int) entry.getSize()];
//												sevenZFile.read(content, 0, content.length);
//												out.write(content);
//											}
//											if(zipFile(unzippedFile, FilenameUtils.concat(
//															extractPath, 
//															FilenameUtils.getBaseName(romVersion.getFilename()).concat(".zip")))) {
//												unzippedFile.delete();
//											}
//											break;
//										}
//										entry = sevenZFile.getNextEntry();
//									}
//								}
//							} else if(FilenameUtils.getExtension(filename).equals("dsk")) {
//								FileSystem.copyFile(
//										new File(FilenameUtils.concat(path, romVersion.getFilename())), 
//										new File(FilenameUtils.concat(extractPath, romVersion.getFilename())));
//							}
//						}
//					}
//                } catch (IOException ex) {
//                    Logger.getLogger(RomDevice.class.getName()).log(Level.SEVERE, null, ex);
//                } finally {
//                    progressBar.reset();
//					Popup.info("Extraction complete.");
//					RomManagerGUI.enableGUI();
//                }
//            }
//        };
//        t.start();
//    }
	
	public static boolean zipFile(File inputFile, String zipFilePath) {
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