/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rommanager.main;

import rommanager.utils.ProgressBar;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author phramusca ( https://github.com/phramusca/JaMuz/ )
 */
public class RomDevice {

	private final Console console;
    private final String path;

    /**
     *
	 * @param console
     * @param path
     */
    public RomDevice(Console console, String path) {
		this.console = console;
        this.path = path;
    }

    public String getName() {
        return console.toString();
    }

    public String getPath() {
        return path;
    }

    public void list(TableModelRomSevenZip model, ProgressBar progressBar) {
//        model.clear();
		browseFoldersFS(path, new File(path), progressBar, model);
		for(RomSevenZipFile file : amstradRoms.values()) {
			file.setScore(false);
			model.addRow(file);
		}
		progressBar.reset();        
    }
    
    private void browseFoldersFS(String rootPath, File path, 
			ProgressBar progressBar, TableModelRomSevenZip model) {
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
                        progressBar.progress(FilenameUtils.getName(file.getAbsolutePath()));
                        if (file.isDirectory()) {
                            browseFoldersFS(rootPath, file, progressBar, model);
                        }
                        else {
							switch (FilenameUtils.getExtension(file.getAbsolutePath())) {
								case "7z":
									RomSevenZipFile sevenZipRomFile;
									try {
										sevenZipRomFile = new RomSevenZipFile(console, file);
										sevenZipRomFile.setVersions();
										model.addRow(sevenZipRomFile);
									} catch (IOException ex) {
										Logger.getLogger(RomDevice.class.getName()).log(Level.SEVERE, null, ex);
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
										Logger.getLogger(RomDevice.class.getName()).log(Level.SEVERE, null, ex);
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
	
	//FIXME 3 Amstrad: Remove amstradRoms: model should be enough + TEST/fix Amstrad (list & extract)
	private final Map<String, RomSevenZipFile> amstradRoms = new HashMap<>();
    
	//FIXME 1 Read all recursively
	// - Add a source text filed  as for export path
	// - make a nice gui for setting all this
	
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
            Logger.getLogger(RomDevice.class.getName()).log(Level.SEVERE, null, ex);
			return false;
        }
    }
}
