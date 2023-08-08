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
    private final ProgressBar progressBarConsole;
	private final ProgressBar progressBarGame;
	private final TableModelRom tableModel;
	private final ICallBackProcess callBack;
	
	public ProcessRead(
            String sourcePath,
			String exportPath, 
            ProgressBar progressBarConsole, 
			ProgressBar progressBarGame, 
			TableModelRom tableModel, 
			ICallBackProcess callBack) {
		super("Thread.ProcessRead");
        this.sourcePath = sourcePath;
		this.exportPath = exportPath;
        this.progressBarConsole = progressBarConsole;
		this.progressBarGame = progressBarGame;
		this.tableModel = tableModel;
		this.callBack = callBack;
	}
   
	@Override
	public void run() {
		try {
            //Read all gamelist.xml files from both local and export folders
			Map<String, Game> games = new HashMap<>();
            progressBarConsole.setup(Console.values().length);
			for(Console console : Console.values()) {
				checkAbort();
                progressBarConsole.progress(console.getName());
//                File localFile = new File(FilenameUtils.concat(FilenameUtils.concat(sourcePath, console.name()), "gamelist.xml"));
                File remoteFile = new File(FilenameUtils.concat(FilenameUtils.concat(exportPath, console.name()), "gamelist.xml"));
//                Map<String, Game> gamesLocal = read(localFile);
                Map<String, Game> gamesRemote = read(remoteFile);
//                progressBar.setup(tableModel.getRoms().size());
//                for(Map.Entry<String, Game> entrySet : gamesRemote.entrySet()) {
////                    Game gameRemote = entrySet.getValue();
////                    if(gamesLocal.containsKey(entrySet.getKey())) {
////                        Game gameLocal = gamesLocal.get(entrySet.getKey());
////                        if(gameRemote.GetTimeStamp() > gameLocal.GetTimeStamp()) {
////                            
////                        }
////                    }
////                    gamesLocal.put(entrySet.getKey(), gameRemote);
//                    progressBar.progress(entrySet.getValue().getName());
//                }
//                save(gamesLocal, localFile);
                games.putAll(gamesRemote);
			}
			progressBarConsole.reset();
            
            //Match gamelist.xml read with local files and display information
			progressBarGame.setup(tableModel.getRoms().size());
			String consolePath;
			for(RomContainer romContainer : tableModel.getRoms().values()) {
				checkAbort();
				consolePath = FilenameUtils.concat(exportPath, romContainer.getConsole().name());
				for(RomVersion romVersion : romContainer.getVersions()) {
					checkAbort();
					String key = FilenameUtils.getBaseName(romVersion.getFilename());
					if(games.containsKey(key)) {
						Game game = games.get(key);
                        if(!game.getImage().isBlank()) {
                            BufferIcon.getCoverIcon(game.getName(), FilenameUtils.concat(consolePath, game.getImage()), true);
                        }
						romVersion.setGame(game);
					}
				}
				progressBarGame.progress(romContainer.getFilename());
			}
			tableModel.fireTableDataChanged();
			
            progressBarGame.setIndeterminate("Saving ods file");
			RomManagerOds.createFile(tableModel, progressBarGame, sourcePath);
			progressBarGame.reset();
            
			Popup.info("Reading complete.");
			progressBarGame.reset();
		} catch (InterruptedException ex) {
//			Popup.info("Aborted by user");
		} finally {
			callBack.completed();
		}
	}

    //FIXME 8 What is the best way for: rom launching, rom edition ? (web interface does not work well :()
    //FIXME 8 Handle default roms from recalbox (move to "recalbox-default-roms" folder, get in local and integrate in export feature)
    
    private void save(Map<String, Game> games, File gamelistXmlFile) {

        Document document = XML.newDoc();
        Element root = document.createElement("gameList");
        document.appendChild(root);
        
        //FIXME 8 Include <folder> entries if we want to send back entries when local modifications will be available to the user
        //FIXME 8 Make a sync process to sync metadata (rating, ...) from/to local/recalbox
        
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
        progressBarGame.setup(elements.size());
        int playCounter;
        float ratingLocal;
        for(Element element : elements) {
            checkAbort();
            long timestamp = Long.parseLong(XML.getAttribute(element, "timestamp"));
            String pc = XML.getElementValue(element, "playcount");
            playCounter=pc.equals("")?-1:Integer.parseInt(pc);
            String r = XML.getElementValue(element, "rating");
            ratingLocal=r.equals("")?-1:Float.parseFloat(r);
            Game game = new Game(XML.getElementValue(element, "path"),
                    XML.getElementValue(element, "hash"),
                    XML.getElementValue(element, "name"),
                    XML.getElementValue(element, "desc"),
                    XML.getElementValue(element, "image"),
                    XML.getElementValue(element, "video"),
                    XML.getElementValue(element, "thumbnail"),
                    ratingLocal,
                    XML.getElementValue(element, "releasedate"),
                    XML.getElementValue(element, "developer"),
                    XML.getElementValue(element, "publisher"),
                    XML.getElementValue(element, "genre"),
                    XML.getElementValue(element, "genreid"),
                    XML.getElementValue(element, "players"),
                    playCounter,
                    XML.getElementValue(element, "lastplayed"),
                    Boolean.parseBoolean(XML.getElementValue(element, "favorite")),
                    timestamp,
                    Boolean.parseBoolean(XML.getElementValue(element, "hidden")),
                    Boolean.parseBoolean(XML.getElementValue(element, "adult")),
                    XML.getElementValue(element, "ratio"),
                    XML.getElementValue(element, "region"));
            games.put(FilenameUtils.getBaseName(game.getPath()), game);
            progressBarGame.progress(game.getName());
        }
        return games;
	}
}