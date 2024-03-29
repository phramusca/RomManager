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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FilenameUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import rommanager.utils.FileSystem;
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
                
                File localFile = new File(FilenameUtils.concat(FilenameUtils.concat(sourcePath, console.name()), "gamelist.xml"));
                File remoteFile = new File(FilenameUtils.concat(FilenameUtils.concat(exportPath, console.name()), "gamelist.xml"));
                if(remoteFile.exists()) {
                    FileSystem.copyFile(remoteFile, localFile);
                }
                Map<String, Game> gamesRemote = read(remoteFile);
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
                            BufferIcon.checkOrGetCoverIcon(game.getName(), FilenameUtils.concat(consolePath, game.getImage()));
                        }
						romVersion.setGame(game);
					}
				}
                romContainer.resetGame();
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
		} catch (IOException ex) {
            Popup.error(ex);
        } finally {
			callBack.completed();
		}
	}

    //FIXME 7 Handle default roms from recalbox (move to "recalbox-default-roms" folder, get in local and integrate in export feature)
    
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
        for(Element element : elements) {
            checkAbort();
            Game game = getGame(element);
            games.put(FilenameUtils.getBaseName(game.getPath()), game);
            progressBarGame.progress(game.getName());
        }
        return games;
	}
    
    public static Game getGame(Element element) {
        String r = XML.getElementValue(element, "rating");
        float ratingLocal=r.equals("")?-1:Float.parseFloat(r);
        String pc = XML.getElementValue(element, "playcount");
        int playCounter=pc.equals("")?-1:Integer.parseInt(pc);
        String t = XML.getAttribute(element, "timestamp");
        long timestamp = (!t.isBlank()?Long.parseLong(t):-1);
        return new Game(XML.getElementValue(element, "path"),
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
    }
}