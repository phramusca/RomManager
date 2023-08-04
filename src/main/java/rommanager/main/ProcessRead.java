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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FilenameUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import rommanager.utils.Popup;
import rommanager.utils.ProcessAbstract;
import rommanager.utils.ProgressBar;
import rommanager.utils.XML;

/**
 *
 * @author phramusca ( https://github.com/phramusca/JaMuz/ )
 */
public class ProcessRead extends ProcessAbstract {

    private final String sourcePath;
	private final String exportPath;
	private final ProgressBar progressBar;
	private final TableModelRom tableModel;
	private final ICallBackProcess callBack;
	
	public ProcessRead(
            String sourcePath,
			String exportPath, 
			ProgressBar progressBar, 
			TableModelRom tableModel, 
			ICallBackProcess callBack) {
		super("Thread.ProcessRead");
        this.sourcePath = sourcePath;
		this.exportPath = exportPath;
		this.progressBar = progressBar;
		this.tableModel = tableModel;
		this.callBack = callBack;
	}

    //FIXME 7 Ask user if he wants to overwrite OR show differences and propose how to handle the sync.
    //FIXME 0 Copy (rsync with no delete) all media files from exportPath to sourcePath (update IconBuffer accordingly)

	@Override
	public void run() {
		try {
            //Read all gamelist.xml files from both local and export folders
			Map<String, Game> games = new HashMap<>();
			for(Console console : Console.values()) {
				checkAbort();
                File localFile = new File(FilenameUtils.concat(FilenameUtils.concat(sourcePath, console.name()), "gamelist.xml"));
                File remoteFile = new File(FilenameUtils.concat(FilenameUtils.concat(exportPath, console.name()), "gamelist.xml"));
                Map<String, Game> gamesLocal = read(localFile);
                Map<String, Game> gamesRemote = read(remoteFile);
                progressBar.setup(tableModel.getRoms().size());
                for(Map.Entry<String, Game> entrySet : gamesRemote.entrySet()) {
                    //FIXME 0 What if game info changed on recalbox ? Nothing => Need to at least overwrite local for now and allow both ways sync later
                    //FIXME 0 What if game info changed locally ? Nothing as local is not (yet) editable. If so need to allow both ways sync
                    gamesLocal.putIfAbsent(entrySet.getKey(), entrySet.getValue());
                    progressBar.progress(entrySet.getValue().getName());
                }
                save(gamesLocal, localFile); //FIXME 0 Do we really need to save here since it is saved in ods ??
                games.putAll(gamesLocal);
			}
			
            //Match gamelist.xml read with local files and display information
			progressBar.setup(tableModel.getRoms().size());
			String consolePath;
			for(RomContainer romContainer : tableModel.getRoms().values()) {
				checkAbort();
				consolePath = FilenameUtils.concat(exportPath, romContainer.getConsole().name());
				for(RomVersion romVersion : romContainer.getVersions()) {
					checkAbort();
					String key = FilenameUtils.getBaseName(romVersion.getFilename());
					if(games.containsKey(key)) {
						Game game = games.get(key);
						IconBuffer.getCoverIcon(game.getName(), FilenameUtils.concat(consolePath, game.getImage()), true);
						romVersion.setGame(game);
					}
				}
				progressBar.progress(romContainer.getFilename());
			}
			tableModel.fireTableDataChanged();
			
            progressBar.setIndeterminate("Saving ods file");
			RomManagerOds.createFile(tableModel, progressBar, sourcePath);
			progressBar.reset();
            
			Popup.info("Reading complete.");
			progressBar.reset();
		} catch (InterruptedException ex) {
//			Popup.info("Aborted by user");
		} finally {
			callBack.completed();
		}
	}

    //FIXME 8 Handle default roms from recalbox (move to "recalbox-default-roms" folder, get in local and integrate in export feature)
    
    private void save(Map<String, Game> games, File gamelistXmlFile) {

        Document document = XML.newDoc();
        Element root = document.createElement("gameList");
        document.appendChild(root);
        
        //FIXME 9 Include <folder> entries if we want to send back entries when local modifications will be available to the user
        //FIXME 9 Make a sync process to sync metadata (rating, ...) from/to local/recalbox
        
        for(Game game: games.values()) {
            Element gameElement = document.createElement("game");
            root.appendChild(gameElement);

            Element path = document.createElement("path");
            path.appendChild(document.createTextNode(game.getPath()));
            gameElement.appendChild(path);
            
            Element hash = document.createElement("hash");
            hash.appendChild(document.createTextNode(game.getHash()));
            gameElement.appendChild(hash);
            
            Element players = document.createElement("players");
            players.appendChild(document.createTextNode(game.getPlayers()));
            gameElement.appendChild(players);
            
            Element genreid = document.createElement("genreid");
            genreid.appendChild(document.createTextNode(game.getGenreId()));
            gameElement.appendChild(genreid);
            
            Element genre = document.createElement("genre");
            genre.appendChild(document.createTextNode(game.getGenre()));
            gameElement.appendChild(genre);
            
            Element publisher = document.createElement("publisher");
            publisher.appendChild(document.createTextNode(game.getPublisher()));
            gameElement.appendChild(publisher);
            
            Element developer = document.createElement("developer");
            developer.appendChild(document.createTextNode(game.getDeveloper()));
            gameElement.appendChild(developer);
            
            Element releasedate = document.createElement("releasedate");
            releasedate.appendChild(document.createTextNode(game.getReleaseDate()));
            gameElement.appendChild(releasedate);
            
            Element video = document.createElement("video");
            video.appendChild(document.createTextNode(game.getVideo()));
            gameElement.appendChild(video);
            
            Element thumbnail = document.createElement("thumbnail");
            thumbnail.appendChild(document.createTextNode(game.getThumbnail()));
            gameElement.appendChild(thumbnail);
            
            Element image = document.createElement("image");
            image.appendChild(document.createTextNode(game.getImage()));
            gameElement.appendChild(image);
            
            Element desc = document.createElement("desc");
            desc.appendChild(document.createTextNode(game.getDesc()));
            gameElement.appendChild(desc);
            
            Element name = document.createElement("name");
            name.appendChild(document.createTextNode(game.getName()));
            gameElement.appendChild(name);
        }
        XML.save(gamelistXmlFile, document);
    }
    
	private Map<String, Game> read(File gamelistXmlFile) throws InterruptedException {
		Map<String, Game> games = new HashMap<>();
        if(!gamelistXmlFile.exists()) {
            Logger.getLogger(ProcessRead.class.getName())
                    .log(Level.WARNING, "File not found: {0}", gamelistXmlFile.getAbsolutePath());
            return games;
        }
        Document doc = XML.open(gamelistXmlFile.getAbsolutePath());
        if(doc==null) {
            Logger.getLogger(ProcessRead.class.getName())
                    .log(Level.SEVERE, "Error with: Document doc = XML.open(\"{0}\")", gamelistXmlFile.getAbsolutePath());
            return games;
        }
        ArrayList<Element> elements = XML.getElements(doc, "game");
        progressBar.setup(elements.size());
        int playCounter;
        float ratingLocal;
        for(Element element : elements) {
            checkAbort();
            String pc = XML.getElementValue(element, "playcount");
            playCounter=pc.equals("")?-1:Integer.valueOf(pc);
            String r = XML.getElementValue(element, "rating");
            ratingLocal=r.equals("")?-1:Float.valueOf(r);
            Game game = new Game(XML.getElementValue(element, "path"),
                    XML.getElementValue(element, "hash"),
                    XML.getElementValue(element, "name"),
                    XML.getElementValue(element, "desc"),
                    XML.getElementValue(element, "image"),
                    XML.getElementValue(element, "video"),
                    XML.getElementValue(element, "thumbnail"),
                    ratingLocal,
                    XML.getElementValue(element, "releaseDate"),
                    XML.getElementValue(element, "developer"),
                    XML.getElementValue(element, "publisher"),
                    XML.getElementValue(element, "genre"),
                    XML.getElementValue(element, "genreId"),
                    XML.getElementValue(element, "players"),
                    playCounter,
                    XML.getElementValue(element, "lastplayed"),
                    Boolean.parseBoolean(
                            XML.getElementValue(element, "favorite")));
            games.put(FilenameUtils.getBaseName(game.getPath()), game);
            progressBar.progress(game.getName());
        }
        return games;
	}
}