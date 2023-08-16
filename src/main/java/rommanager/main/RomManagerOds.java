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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import org.apache.commons.io.FilenameUtils;
import org.jopendocument.dom.OOUtils;
import org.jopendocument.dom.spreadsheet.Sheet;
import org.jopendocument.dom.spreadsheet.SpreadSheet;
import rommanager.utils.DateTime;
import rommanager.utils.Popup;
import rommanager.utils.ProgressBar;
import rommanager.utils.Row;

/**
 *
 * @author phramusca ( https://github.com/phramusca/JaMuz/ )
 */
public class RomManagerOds {
	
	private final static String DOC_FILE = "RomManager";
	private final static String SHEET_NAME = "List";
	
	public RomManagerOds() {
	}

	public static boolean createFile(
			TableModelRom model, 
			ProgressBar progressBar, String sourceFolder) {
		
		return createFile(model, progressBar, false, sourceFolder);
	}
	
	public static boolean createFile(
			TableModelRom model, 
			ProgressBar progressBar, 
			boolean open, String sourceFolder) {

        int nbColumns=39;
        int nbRows=0;
        for(RomContainer romContainer : model.getRoms().values()) {
            nbRows+=romContainer.getVersions().size();
        }
        final Object[][] data = new Object[nbRows][nbColumns];
        
        int i=0; 
        for(RomContainer romContainer : model.getRoms().values()) {
            for (RomVersion romVersion : romContainer.getVersions()) {
				
				Game game = romVersion.getGame();
				if(game==null) {
					game = new Game("", "","","", "", "", "", -1, "", "","", "", "", "", -1, "", false, 0, false, false, "", "");
				}
                
                JeuVideo jeuVideo = romVersion.getJeuVideo();
                if(jeuVideo == null) {
                    jeuVideo = new JeuVideo("", "", "", "", "", "");
                }
				
                data[i++] = new Object[] { 
					romContainer.getConsole().name(),
					romContainer.getFilename(), 
					romVersion.getFilename(), 
					romVersion.getAlternativeName(),
                    romVersion.getAttributes(),
					romVersion.getScore(),
					romVersion.getErrorLevel(),
					romVersion.isExportable(),
					romVersion.getTags().stream().collect(Collectors.joining(",")),
                    romVersion.getCrcValue(),
                    romVersion.getSize(),
                    game.getName(),
					game.getDesc(),
					game.getGenre(),
					game.getRating(),
					game.isFavorite(),
                    game.isHidden(),
                    game.isAdult(),
					game.getPlayers(),
					game.getDeveloper(),
					game.getPublisher(),
					game.getImage(),
					game.getReleaseDate(),
					game.getLastplayed(),
					game.getPlaycount(),
					game.getThumbnail(),
					game.getPath(),
                    game.getHash(),
                    game.getVideo(),
                    game.getGenreId(),
                    game.getRatio(),
                    game.getRegion(),
                    game.getTimestamp(),
                    jeuVideo.getUrl(),
                    jeuVideo.getTitle(),
                    jeuVideo.getReleaseDate(),
                    jeuVideo.getRating(),
                    jeuVideo.getUserRating(),
                    jeuVideo.getDescription()
				};
            }
        }
        i=0;
        String[] columns = new String[nbColumns];
		columns[i++] = "Console";
        columns[i++] = "File Name";
        columns[i++] = "Version";
		columns[i++] = "Alternative Name";
        columns[i++] = "Attributes";
        columns[i++] = "Score";
		columns[i++] = "Error Level";
		columns[i++] = "Export";
        columns[i++] = "Tags";
        columns[i++] = "CrcValue";
        columns[i++] = "Size";
        
		columns[i++] = "Name";
		columns[i++] = "Description";
		columns[i++] = "Genre";
		columns[i++] = "Rating";
		columns[i++] = "Favorite";
        columns[i++] = "Hidden";
        columns[i++] = "Adult";
		columns[i++] = "Players";
		columns[i++] = "Developer";
		columns[i++] = "Publisher";
		columns[i++] = "Image";
		columns[i++] = "Release Date";
		columns[i++] = "Last Played";
		columns[i++] = "Play Count";
		columns[i++] = "Thumbnail";
		columns[i++] = "Path";
        columns[i++] = "Hash";
        columns[i++] = "Video";
        columns[i++] = "GenreId";
        columns[i++] = "Ratio";
        columns[i++] = "Region";
        columns[i++] = "TimeStamp";
        
        columns[i++] = "JeuxVideo.com";
        columns[i++] = "Title";
        columns[i++] = "Release Date";
        columns[i++] = "Rating";
        columns[i++] = "User Rating";
        columns[i++] = "Description";

        TableModel docModel = new DefaultTableModel(data, columns);
		File odsFile = new File(FilenameUtils.concat(sourceFolder, DOC_FILE+"_"+DateTime.getCurrentLocal(DateTime.DateTimeFormat.FILE)+".ods"));
        if(odsFile.exists()) {
            odsFile.delete();
        }
        SpreadSheet spreadSheet = SpreadSheet.createEmpty(docModel);
        spreadSheet.getFirstSheet().setName(SHEET_NAME);
        try {
            spreadSheet.saveAs(odsFile);
			if(open) {
				OOUtils.open(odsFile);
			}
        } catch (IOException ex) {
            Logger.getLogger(RomManagerOds.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }
    
	public static void readFile( 
			TableModelRom model,
			ProgressBar progressBarGame, String sourceFolder) {
	
		//TODO Eventually Let user choose ods file !
		// Currently, it opens latest one
		String[] arrayList = new File(sourceFolder).list((File dir, String name) -> {
			return name.matches(DOC_FILE+".+\\.ods");
		});
		if(arrayList==null || arrayList.length<=0) {
			Logger.getLogger(RomManagerOds.class.getName())
					.log(Level.WARNING, "No "+DOC_FILE+"*.ods in {0}", sourceFolder);
			return;
		}
		List<String> list = Arrays.asList(arrayList);
		Collections.sort(list);
		File odsFile = new File(FilenameUtils.concat(sourceFolder, list.get(list.size()-1)));
		if(!odsFile.exists()) {
			Logger.getLogger(RomManagerOds.class.getName())
					.log(Level.WARNING, "{0} does not exists", DOC_FILE);
			return;
		}
        try {
			SpreadSheet spreadSheet = SpreadSheet.createFromFile(odsFile);
			Sheet sheet = spreadSheet.getSheet(SHEET_NAME);
			int nRowCount = sheet.getRowCount();
			progressBarGame.setup(nRowCount-1);	
			for(int nRowIndex = 1; nRowIndex < nRowCount; nRowIndex++) {
				Row row = new Row(sheet, nRowIndex);
				Console console=null;
                int i = 0;
				String consoleName=row.getValue(i++);
				try {
					console = Console.valueOf(consoleName);
				} catch (IllegalArgumentException ex) {
					Popup.warning("Unknown console: "+consoleName);
				}
                String filename = row.getValue(i++);
				String version = row.getValue(i++);
				String alternativeName = row.getValue(i++);
				String attributes = row.getValue(i++);
				int score = Integer.parseInt(row.getValue(i++));
				int errorLevel = Integer.parseInt(row.getValue(i++));
				boolean isExportable = Boolean.parseBoolean(row.getValue(i++));
                String tags = row.getValue(i++);
                String crcStr = row.getValue(i++);
                Long crcValue = Long.valueOf(crcStr.isBlank()?"-1":crcStr);
                String sizeStr = row.getValue(i++);
                Long size = Long.valueOf(sizeStr.isBlank()?"-1":sizeStr);
                
				String gameName = row.getValue(i++);
				String desc = row.getValue(i++);
				String genre = row.getValue(i++);
				float rating = Float.parseFloat(row.getValue(i++));
				boolean isFavorite = Boolean.parseBoolean(row.getValue(i++));
                boolean isHidden = Boolean.parseBoolean(row.getValue(i++));
                boolean isAdult = Boolean.parseBoolean(row.getValue(i++));
				String players = row.getValue(i++);
				String developer = row.getValue(i++);
				String publisher = row.getValue(i++);
				String image = row.getValue(i++);
				String releaseDate = row.getValue(i++);
				String lastplayed = row.getValue(i++);
				int playcount = Integer.parseInt(row.getValue(i++));
				String thumbnail = row.getValue(i++);
				String path = row.getValue(i++);
                String hash = row.getValue(i++);
                String video = row.getValue(i++);
                String genreId = row.getValue(i++);
                String ratio = row.getValue(i++);
                String region = row.getValue(i++);
                Long timestamp = Long.valueOf(row.getValue(i++));
                
                String url = row.getValue(i++);
                String title = row.getValue(i++);
                String releaseDateJeuVideo = row.getValue(i++);
                String ratingJeuVideo = row.getValue(i++);
                String userRating = row.getValue(i++);
                String description = row.getValue(i++);
                
				model.addRow(console, filename);
				RomVersion romVersion = new RomVersion(
						FilenameUtils.getBaseName(filename),
						version, 
						alternativeName, 
						attributes, 
						score, errorLevel, isExportable, tags, crcValue, size);
				
				Game game = new Game(path, hash, gameName, desc, image, 
					video,thumbnail, rating, releaseDate, 
					developer, publisher, genre, genreId, players, 
					playcount, lastplayed, isFavorite, timestamp, isHidden, isAdult, ratio, region);
				BufferIcon.checkOrGetCoverIcon(game.getName(), "");
				romVersion.setGame(game);
                
                JeuVideo jeuVideo = new JeuVideo(url, title, releaseDateJeuVideo, ratingJeuVideo, userRating, description);
                romVersion.setJeuVideo(jeuVideo);
                
				model.getRoms().get(console.name()+"/"+filename).getVersions().add(romVersion);
				progressBarGame.progress(filename);
			}
			progressBarGame.reset();
		} catch (IOException ex) {
			Logger.getLogger(RomManagerOds.class.getName())
					.log(Level.SEVERE, null, ex);
		} 
    }
}
