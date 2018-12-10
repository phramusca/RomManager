/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rommanager.main;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import org.jopendocument.dom.OOUtils;
import org.jopendocument.dom.spreadsheet.Sheet;
import org.jopendocument.dom.spreadsheet.SpreadSheet;
import rommanager.utils.ProgressBar;
import rommanager.utils.Row;

/**
 *
 * @author phramusca ( https://github.com/phramusca/JaMuz/ )
 */
public class RomManagerOds {
	
	private final static File DOC_FILE = new File("RomManager.ods");
	
	public RomManagerOds() {
	}

	public static void createFile(TableModelRomSevenZip model
			, ProgressBar progressBar) {
		createFile(model, progressBar, false);
	}
	
	public static void createFile(TableModelRomSevenZip model, 
			ProgressBar progressBar, boolean open) {
        int nbColumns=8;
        int nbRows=0;
        for(RomSevenZipFile sevenZipRomFile : model.getRoms().values()) {
            nbRows+=sevenZipRomFile.getVersions().size();
        }
        final Object[][] data = new Object[nbRows][nbColumns];
        
        int i=0; 
        for(RomSevenZipFile sevenZipRomFile : model.getRoms().values()) {
            for (RomVersion romVersion : sevenZipRomFile.getVersions()) {
                data[i++] = new Object[] { 
					sevenZipRomFile.getFilename(), 
					romVersion.getFilename(), 
					romVersion.getAlternativeName(),
					romVersion.getCountries(), 
					romVersion.getStandards(), 
					romVersion.getScore(),
					romVersion.getErrorLevel(),
					romVersion.isBest() 
				};
            }
        }
        i=0;
        String[] columns = new String[nbColumns];
        columns[i++] = "FileName";
        columns[i++] = "Version";
		columns[i++] = "Alternative Name";
        columns[i++] = "Countries";
        columns[i++] = "Standards";
        columns[i++] = "Score";
		columns[i++] = "Error Level";
		columns[i++] = "Best";
        TableModel docModel = new DefaultTableModel(data, columns);
        if(DOC_FILE.exists()) {
            DOC_FILE.delete();
        }
        SpreadSheet spreadSheet = SpreadSheet.createEmpty(docModel);
        spreadSheet.getFirstSheet().setName("List");
        try {
            spreadSheet.saveAs(DOC_FILE);
			if(open) {
				OOUtils.open(DOC_FILE);
			}
        } catch (IOException ex) {
            Logger.getLogger(RomManagerOds.class.getName())
					.log(Level.SEVERE, null, ex);
        }
    }
    
	public static void readFile(TableModelRomSevenZip model
			, ProgressBar progressBar) {
        try {
			SpreadSheet spreadSheet = SpreadSheet.createFromFile(DOC_FILE);
			Sheet sheet = spreadSheet.getSheet("List");
			int nRowCount = sheet.getRowCount();
			progressBar.setup(nRowCount-1);	
			for(int nRowIndex = 1; nRowIndex < nRowCount; nRowIndex++) {
				Row row = new Row(sheet, nRowIndex);
				String filename = row.getValue(0);
				String version = row.getValue(1);
				String alternativeName = row.getValue(2);
				String countries = row.getValue(3);
				String standards = row.getValue(4);
				int score = Integer.valueOf(row.getValue(5));
				int errorLevel = Integer.valueOf(row.getValue(6));
				boolean isBest = Boolean.parseBoolean(row.getValue(7));
				model.addRow(filename);
				RomVersion romVersion = new RomVersion(version, 
						alternativeName, 
						countries, standards, 
						score, errorLevel, isBest);
				model.getRoms().get(filename).getVersions().add(romVersion);
				progressBar.progress(filename);
			}
		} catch (IOException ex) {
			Logger.getLogger(RomManagerOds.class.getName())
					.log(Level.SEVERE, null, ex);
		} 
    }
}
