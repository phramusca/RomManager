/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rommanager.main;

import rommanager.utils.Row;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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
    private String filename;
    private final List<RomVersion> versions;
	
	//FIXME 2 Remove docFile from RomSevenZipFile and use the global one for extraction instead as a cache
    private File docFile;
	
	//For 7z files including versions
    public RomSevenZipFile(File file) throws IOException {
        this.path = FilenameUtils.getFullPath(file.getAbsolutePath());
        this.filename = FilenameUtils.getName(file.getAbsolutePath());
		versions = new ArrayList<>();
    }
	
	//For dsk (Amstrad) files that are not groupped in 7z
	public RomSevenZipFile(File file, String filename) throws IOException {
		this(file);
		this.filename=filename;
	}
	
	public void setVersions() throws IOException {
        docFile = new File(FilenameUtils.concat(path, FilenameUtils.getBaseName(filename)).concat(".ods"));
        if(docFile.exists()) {
            readFromDoc();
			setScore(true);
        }
        else {
			if(FilenameUtils.getExtension(filename).equals("7z")) {
				readFrom7z();
				setScore(true);
			}
        }
	}
	
	public final void setScore(boolean addBestForExport) {
		int bestScore=Integer.MIN_VALUE;
		RomVersion bestVersion=null;
		for(RomVersion version : versions) {
			if(version.getScore()>bestScore) {
				bestVersion=version;
				bestScore=version.getScore();
			} 
		}
		if(addBestForExport && bestVersion!=null) {
			bestVersion.setSelected(true);
		}
	}
	
    public List<RomVersion> getVersions() {
        return versions;
    }

	//Amstrad only
	public void addVersion(RomVersion version) {
		versions.add(version);
		
		//FIXME 3 Only extract several if "Disk" inside versions, otherwise do as for 7z: take best version
		if(version.getErrorLevel()==0) {
			version.setSelected(true);
		}
	}
	
	public List<RomVersion> getExportVersions() {
		return versions.stream().filter(v -> v.isSelected())
				.sorted()
				.collect(Collectors.toList());
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
				entry = sevenZFile.getNextEntry();
			}
		}

        createFile();
    }

    private void readFromDoc() throws IOException {
        
        SpreadSheet spreadSheet = SpreadSheet.createFromFile(docFile);
        Sheet sheet = spreadSheet.getSheet("Summary");
        int nRowCount = sheet.getRowCount();
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
        return getExportVersions().size()==1
					?getExportVersions().get(0).toString()
					:getExportVersions().isEmpty()
						?RomVersion.colorField("NO files to export.", 2, true)
						:RomVersion.colorField(getExportVersions().size()+" files to export.", 3, true);
    }
}
