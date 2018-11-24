/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.rommanager;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.io.FilenameUtils;
import org.jopendocument.dom.spreadsheet.Sheet;
import org.jopendocument.dom.spreadsheet.SpreadSheet;

/**
 *
 * @author phramusca ( https://github.com/phramusca/JaMuz/ )
 */
public class RomDevice {
    private final String name;
    private final String path;
    private final File docFile;
    private final TableModelRomSevenZip model;
    private final ProgressBar progressBar;
    /**
     *
     * @param name
     * @param path
     * @param progressBar
     * @param model
     */
    public RomDevice(String name, String path, ProgressBar progressBar, TableModelRomSevenZip model) {
        this.name = name;
        this.path = path;
        this.model = model;
        docFile = new File(FilenameUtils.concat(path+"/..", name).concat(".ods"));
        this.progressBar = progressBar;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public void list(TableModelRomSevenZip model) {
        model.clear();
        browseFoldersFS(path, new File(path), progressBar, model);
		for(RomSevenZipFile file : amstradRoms.values()) {
			file.setScore(false);
			model.addRow(file);
		}
        progressBar.setIndeterminate("Creating output file ... ");
        createFile();
        progressBar.reset();
    }
    
    private void browseFoldersFS(String rootPath, File path, ProgressBar progressBar, TableModelRomSevenZip model) {
        if (path.isDirectory()) {
            File[] files = path.listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					String ext = FilenameUtils.getExtension(pathname.getAbsolutePath())
							.toLowerCase();
                    return ext.equals("7z") || ext.equals("dsk") || pathname.isDirectory();
				}
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
							if(FilenameUtils.getExtension(file.getAbsolutePath()).equals("7z")) {
								RomSevenZipFile sevenZipRomFile;
								try {
									sevenZipRomFile = new RomSevenZipFile(file);
									sevenZipRomFile.setVersions();
									model.addRow(sevenZipRomFile);
								} catch (IOException ex) {
									Logger.getLogger(RomManager.class.getName()).log(Level.SEVERE, null, ex);
								}
							} else if(FilenameUtils.getExtension(file.getAbsolutePath()).equals("dsk")) {
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
										romSevenZipFile = new RomSevenZipFile(file, romName);
										amstradRoms.put(romName, romSevenZipFile);
									}
									String versionPath = file.getAbsolutePath().substring(rootPath.length()+1);
									
									romSevenZipFile.addVersion(new RomVersion(
											romName,
											versionPath));
								} catch (IOException ex) {
									Logger.getLogger(RomDevice.class.getName()).log(Level.SEVERE, null, ex);
								}
							}
                        }
                    }
                }
            } 
        }
    }
	
	private final Map<String, RomSevenZipFile> amstradRoms = new HashMap<>();
    
    private void createFile() {
        int nbColumns=5;
        int nbRows=0;
        for(RomSevenZipFile sevenZipRomFile : this.model.getFiles()) {
            nbRows+=sevenZipRomFile.getExportVersions().size();
        }
        final Object[][] data = new Object[nbRows][nbColumns];
        
        int i=0; 
        for(RomSevenZipFile sevenZipRomFile : this.model.getFiles()) {
            for (RomVersion romVersion : sevenZipRomFile.getExportVersions()) {
                data[i++] = new Object[] { 
					sevenZipRomFile.getFilename(), 
					romVersion.getVersion(), 
					romVersion.getCountries(), 
					romVersion.getStandards(), 
					romVersion.getScore() };
            }
        }
        i=0;
        String[] columns = new String[nbColumns];
        columns[i++] = "File";
        columns[i++] = "Version";
        columns[i++] = "Country";
        columns[i++] = "Standard";
        columns[i++] = "Score";
        TableModel docModel = new DefaultTableModel(data, columns);
        if(docFile.exists()) {
            docFile.delete();
        }
        SpreadSheet spreadSheet = SpreadSheet.createEmpty(docModel);
        spreadSheet.getFirstSheet().setName("List");
        try {
            spreadSheet.saveAs(docFile);
//        OOUtils.open(docFile);
        } catch (IOException ex) {
            Logger.getLogger(RomDevice.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void extract() {
        Thread t = new Thread("Thread.RomDevice.Extract") {
            @Override
            public void run() {
                try {
                    SpreadSheet spreadSheet = SpreadSheet.createFromFile(docFile);
                    Sheet sheet = spreadSheet.getSheet("List");
                    int nRowCount = sheet.getRowCount();
        //        int nColCount = sheet.getColumnCount();
                    progressBar.setup(nRowCount);
					
					String extractPath = FilenameUtils.concat(path, "../Extract--"+name+"--"
							.concat(DateTime.getCurrentLocal(DateTime.DateTimeFormat.FILE)));
					if(!new File(extractPath).exists()) {
						new File(extractPath).mkdirs();
					}
					
                    for(int nRowIndex = 1; nRowIndex < nRowCount; nRowIndex++) {
                        Row row = new Row(sheet, nRowIndex);
                        String filename = row.getValue(0);
                        progressBar.progress(filename);
						String version = row.getValue(1);
						String country = row.getValue(2); //TODO: Use this
						String standard = row.getValue(3); //TODO: Use this
						int score = Integer.valueOf(row.getValue(4));  //TODO: Use this
						if(score>0) {
							if(FilenameUtils.getExtension(filename).equals("7z")) {
								try (SevenZFile sevenZFile = new SevenZFile(new File(
									FilenameUtils.concat(path, filename)))) {
									SevenZArchiveEntry entry = sevenZFile.getNextEntry();
									while(entry!=null){
										// version does not include extension => startsWith
										if(entry.getName().startsWith(version)) { 
											File unzippedFile=new File(FilenameUtils.concat(
															extractPath,
															entry.getName()));
											try (FileOutputStream out = new FileOutputStream(unzippedFile)) {
												byte[] content = new byte[(int) entry.getSize()];
												sevenZFile.read(content, 0, content.length);
												out.write(content);
											}
											if(zipFile(unzippedFile, FilenameUtils.concat(
															extractPath, 
															version.concat(".zip")))) {
												unzippedFile.delete();
											}
											break;
										}
										entry = sevenZFile.getNextEntry();
									}
								}
							} else if(FilenameUtils.getExtension(filename).equals("dsk")) {
								FileSystem.copyFile(
										new File(FilenameUtils.concat(path, version)), 
										new File(FilenameUtils.concat(extractPath, version)));
							}
						}
					}
                } catch (IOException ex) {
                    Logger.getLogger(RomDevice.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    progressBar.reset();
					Popup.info("Extraction complete.");
					RomManagerGUI.enableGUI();
                }
            }
        };
        t.start();
    }
	
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
