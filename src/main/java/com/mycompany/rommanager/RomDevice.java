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
        docFile = new File(FilenameUtils.concat(path, name).concat(".ods"));
        this.progressBar = progressBar;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

//    public ArrayList<RomSevenZipFile> getRoms() {
//        return roms;
//    }
    
    public void list(TableModelRomSevenZip model) {
        model.clear();
        browseFoldersFS(new File(path), progressBar, model);
        progressBar.setIndeterminate("Creating output file ... ");
        createFile();
        progressBar.reset();
    }
    
    private void browseFoldersFS(File path, ProgressBar progressBar, TableModelRomSevenZip model) {
        if (path.isDirectory()) {
            File[] files = path.listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
                    return FilenameUtils.getExtension(pathname.getAbsolutePath())
							.toLowerCase().equals("7z") && pathname.isFile();
				}
			});
            if (files != null) {
                if(files.length>0) {
                    progressBar.setup(files.length);
                    for (File file : files) {
                        progressBar.progress(FilenameUtils.getName(file.getAbsolutePath()));
                        if (file.isDirectory()) {
                            browseFoldersFS(file, progressBar, model);
                        }
                        else {
                            RomSevenZipFile sevenZipRomFile;
                            try {
                                sevenZipRomFile = new RomSevenZipFile(file);
                                model.addRow(sevenZipRomFile);
                            } catch (IOException ex) {
                                Logger.getLogger(RomManager.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                }
            } 
        }
    }
    
    private void createFile() {

        int nbColumns=5;
        int nbRows=0;
        for(RomSevenZipFile sevenZipRomFile : this.model.getFiles()) {
//            nbRows+=sevenZipRomFile.getVersions().size();
			nbRows+=1;
        }
        final Object[][] data = new Object[nbRows][nbColumns];
        
        int i=0; 
        for(RomSevenZipFile sevenZipRomFile : this.model.getFiles()) {
//            for (RomVersion romVersion : sevenZipRomFile.getVersions()) {
				RomVersion romVersion = sevenZipRomFile.getBestVersion();
                data[i++] = new Object[] { 
					sevenZipRomFile.getFilename(), 
					romVersion.getVersion(), 
					romVersion.getCountries(), 
					romVersion.getStandards(), 
					romVersion.getScore() };
//            }
        }
         
        i=0;
        String[] columns = new String[nbColumns];
        columns[i++] = "File";
        columns[i++] = "Version";
        columns[i++] = "Country";
        columns[i++] = "Standard";
        columns[i++] = "Score";

        // Save the data to an ODS file and open it.
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
                    for(int nRowIndex = 1; nRowIndex < nRowCount; nRowIndex++) {
                        Row row = new Row(sheet, nRowIndex);
                        String filename = row.getValue(0);
                        progressBar.progress(filename);
						String version = row.getValue(1);
						String country = row.getValue(2); //TODO: Use this
						String standard = row.getValue(3); //TODO: Use this
						int score = Integer.valueOf(row.getValue(4));  //TODO: Use this
						if(score>0) {
							try (SevenZFile sevenZFile = new SevenZFile(new File(
									FilenameUtils.concat(path, filename)))) {
								SevenZArchiveEntry entry = sevenZFile.getNextEntry();
								while(entry!=null){
									// version does not include extension => startsWith
									if(entry.getName().startsWith(version)) { 
										File unzippedFile=new File(FilenameUtils.concat(
														FilenameUtils.concat(path, "Extract"),
														entry.getName()));
										try (FileOutputStream out = new FileOutputStream(unzippedFile)) {
											byte[] content = new byte[(int) entry.getSize()];
											sevenZFile.read(content, 0, content.length);
											out.write(content);
										}
										if(zipFile(unzippedFile, FilenameUtils.concat(
														FilenameUtils.concat(path, "Extract"), 
															version.concat(".zip")))) {
											unzippedFile.delete();
										}
										break;
									}
									entry = sevenZFile.getNextEntry();
								}
							}
						}
					}
                } catch (IOException ex) {
                    Logger.getLogger(RomDevice.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    progressBar.reset();
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
