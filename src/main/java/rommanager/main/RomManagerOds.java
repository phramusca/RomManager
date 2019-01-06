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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import org.apache.commons.io.FilenameUtils;
import org.jopendocument.dom.OOUtils;
import org.jopendocument.dom.spreadsheet.Sheet;
import org.jopendocument.dom.spreadsheet.SpreadSheet;
import rommanager.utils.Popup;
import rommanager.utils.ProgressBar;
import rommanager.utils.Row;

/**
 *
 * @author phramusca ( https://github.com/phramusca/JaMuz/ )
 */
public class RomManagerOds {
	
	private final static File DOC_FILE = new File("RomManager.ods");
	private final static String SHEET_NAME = "List";
	
	public RomManagerOds() {
	}

	public static void createFile(
			TableModelRomSevenZip model, 
			ProgressBar progressBar) {
		
		createFile(model, progressBar, false);
	}
	
	public static void createFile(
			TableModelRomSevenZip model, 
			ProgressBar progressBar, 
			boolean open) {
		
        int nbColumns=23;
        int nbRows=0;
        for(RomSevenZipFile sevenZipRomFile : model.getRoms().values()) {
            nbRows+=sevenZipRomFile.getVersions().size();
        }
        final Object[][] data = new Object[nbRows][nbColumns];
        
        int i=0; 
        for(RomSevenZipFile sevenZipRomFile : model.getRoms().values()) {
            for (RomVersion romVersion : sevenZipRomFile.getVersions()) {
				
				Game game = romVersion.getGame();
				if(game==null) {
					game = new Game("", "", "", "", "", -1, "", "", "", "", "", -1, "", false);
				}
				
                data[i++] = new Object[] { 
					sevenZipRomFile.getConsole().name(),
					sevenZipRomFile.getFilename(), 
					romVersion.getFilename(), 
					romVersion.getAlternativeName(),
					romVersion.getCountries(), 
					romVersion.getStandards(), 
					romVersion.getScore(),
					romVersion.getErrorLevel(),
					romVersion.isBest(),
					game.getName(),
					game.getDesc(),
					game.getGenre(),
					game.getRating(),
					game.isFavorite(),
					game.getPlayers(),
					game.getDeveloper(),
					game.getPublisher(),
					game.getImage(),
					game.getReleaseDate(),
					game.getLastplayed(),
					game.getPlaycount(),
					game.getThumbnail(),
					game.getPath()
				};
            }
        }
        i=0;
        String[] columns = new String[nbColumns];
		columns[i++] = "Console";
        columns[i++] = "File Name";
        columns[i++] = "Version";
		columns[i++] = "Alternative Name";
        columns[i++] = "Countries";
        columns[i++] = "Standards";
        columns[i++] = "Score";
		columns[i++] = "Error Level";
		columns[i++] = "Best";
		columns[i++] = "Name";
		columns[i++] = "Description";
		columns[i++] = "Genre";
		columns[i++] = "Rating";
		columns[i++] = "Favorite";
		columns[i++] = "Players";
		columns[i++] = "Developer";
		columns[i++] = "Publisher";
		columns[i++] = "Image";
		columns[i++] = "Release Date";
		columns[i++] = "Last Played";
		columns[i++] = "Play Count";
		columns[i++] = "Thumbnail";
		columns[i++] = "Path";
        TableModel docModel = new DefaultTableModel(data, columns);
        if(DOC_FILE.exists()) {
            DOC_FILE.delete();
        }
        SpreadSheet spreadSheet = SpreadSheet.createEmpty(docModel);
        spreadSheet.getFirstSheet().setName(SHEET_NAME);
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
    
	public static void readFile( 
			TableModelRomSevenZip model, 
			ProgressBar progressBar) {
		if(!DOC_FILE.exists()) {
			Logger.getLogger(RomManagerOds.class.getName())
					.log(Level.WARNING, "{0} does not exists", DOC_FILE);
			return;
		}
        try {
			SpreadSheet spreadSheet = SpreadSheet.createFromFile(DOC_FILE);
			Sheet sheet = spreadSheet.getSheet(SHEET_NAME);
			int nRowCount = sheet.getRowCount();
			progressBar.setup(nRowCount-1);	
			for(int nRowIndex = 1; nRowIndex < nRowCount; nRowIndex++) {
				Row row = new Row(sheet, nRowIndex);
				Console console=null;
				String consoleName=row.getValue(0);
				try {
					console = Console.valueOf(consoleName);
				} catch (IllegalArgumentException ex) {
					Popup.warning("Unknown console: "+consoleName);
				}
				String filename = row.getValue(1);
				String version = row.getValue(2);
				String alternativeName = row.getValue(3);
				String countries = row.getValue(4);
				String standards = row.getValue(5);
				int score = Integer.valueOf(row.getValue(6));
				int errorLevel = Integer.valueOf(row.getValue(7));
				boolean isBest = Boolean.parseBoolean(row.getValue(8));
				String gameName = row.getValue(9);
				String desc = row.getValue(10);
				String genre = row.getValue(11);
				float rating = Float.valueOf(row.getValue(12));
				boolean isFavorite = Boolean.parseBoolean(row.getValue(13));
				String players = row.getValue(14);
				String developer = row.getValue(15);
				String publisher = row.getValue(16);
				String image = row.getValue(17);
				String releaseDate = row.getValue(18);
				String lastplayed = row.getValue(19);
				int playcount = Integer.valueOf(row.getValue(20));
				String thumbnail = row.getValue(21);
				String path = row.getValue(22);
				model.addRow(console, filename);
				RomVersion romVersion = new RomVersion(
						FilenameUtils.getBaseName(filename),
						version, 
						alternativeName, 
						countries, standards, 
						score, errorLevel, isBest);
				
				Game game = new Game(path, gameName, desc, image, 
					thumbnail, rating, releaseDate, 
					developer, publisher, genre, players, 
					playcount, lastplayed, isFavorite);
				
				IconBuffer.getCoverIcon(game.getName(), "", true);
				romVersion.setGame(game);
				model.getRoms().get(filename).getVersions().add(romVersion);
				progressBar.progress(filename);
			}
			progressBar.reset();
		} catch (IOException ex) {
			Logger.getLogger(RomManagerOds.class.getName())
					.log(Level.SEVERE, null, ex);
		} 
    }
}
