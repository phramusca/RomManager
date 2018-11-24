/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.rommanager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.io.FileExistsException;
import org.apache.commons.io.FilenameUtils;
import org.jopendocument.dom.spreadsheet.Sheet;
import org.jopendocument.dom.spreadsheet.SpreadSheet;

/**
 *
 * @author phramusca ( https://github.com/phramusca/JaMuz/ )
 */
public class RomSevenZipFile {
    
//    private final File file;
    
    private final String path;
    private final String filename;
    private final ArrayList<RomVersion> versions;
    private final File docFile;
	private RomVersion bestVersion;

    public RomSevenZipFile(File file) throws IOException {
        this.path = FilenameUtils.getFullPath(file.getAbsolutePath());
        this.filename = FilenameUtils.getName(file.getAbsolutePath());
        versions = new ArrayList<>();
        docFile = new File(FilenameUtils.concat(path, FilenameUtils.getBaseName(filename)).concat(".ods"));
        if(docFile.exists()) {
            readFromDoc();
        }
        else {
            readFrom7z();
        }
		int bestScore=Integer.MIN_VALUE;
		for(RomVersion version : versions) {
			if(version.getScore()>bestScore) {
				bestVersion=version;
				bestScore=bestVersion.getScore();
			}
		}
    }

    public ArrayList<RomVersion> getVersions() {
        return versions;
    }

	public RomVersion getBestVersion() {
		return bestVersion;
	}
    
    public String getFilename() {
        return filename;
    }

    private void readFrom7z() throws IOException {
		try (SevenZFile sevenZFile = new SevenZFile(new File(FilenameUtils.concat(path, filename)))) {
			SevenZArchiveEntry entry = sevenZFile.getNextEntry();
			while(entry!=null){
				versions.add(new RomVersion(
						FilenameUtils.getBaseName(filename), 
						FilenameUtils.getBaseName(entry.getName())));
				//Below is to extract the file
//                FileOutputStream out = new FileOutputStream(entry.getName());
//                byte[] content = new byte[(int) entry.getSize()];
//                sevenZFile.read(content, 0, content.length);
//                out.write(content);
//                out.close();
				entry = sevenZFile.getNextEntry();
			}
		}

        createFile();
    }
    
    
    
    private void readFromDoc() throws IOException {
        
        SpreadSheet spreadSheet = SpreadSheet.createFromFile(docFile);
        Sheet sheet = spreadSheet.getSheet("Summary");
        int nRowCount = sheet.getRowCount();
//        int nColCount = sheet.getColumnCount();
        versions.clear();
        for(int nRowIndex = 1; nRowIndex < nRowCount; nRowIndex++)
        {
            Row row = new Row(sheet, nRowIndex);
            versions.add(new RomVersion(
					FilenameUtils.getBaseName(filename), 
					row.getValue(0)));
        }
    }
    
    private void createFile() throws IOException {

        int i=0; int nbColumns=3;
        final Object[][] data = new Object[versions.size()][nbColumns];
        
        for (RomVersion romVersion : versions) {
            data[i++] = new Object[] { 
				romVersion.getVersion(), 
				romVersion.getCountries(), 
				romVersion.getStandards() 
			};
        }
         
        i=0;
        String[] columns = new String[nbColumns];
        columns[i++] = "Version";
        columns[i++] = "Countries";
        columns[i++] = "Standards";

        // Save the data to an ODS file and open it.
        TableModel model = new DefaultTableModel(data, columns);

        if(docFile.exists()) {
            throw new FileExistsException(docFile);
        }
        SpreadSheet spreadSheet = SpreadSheet.createEmpty(model);
        spreadSheet.getFirstSheet().setName("Summary");
        
        spreadSheet.saveAs(docFile);
//        OOUtils.open(docFile);
    }

    @Override
    public String toString() {
        return bestVersion==null?RomVersion.colorField("NO GOOD VERSION FOUND", 2, true):bestVersion.toString();
    }
    
    
}
