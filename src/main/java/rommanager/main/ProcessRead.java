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
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
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
	private final ProgressBar progressBar;
	private Map<String, Game> games;
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

	@Override
	public void run() {
		try {
			games = new HashMap<>();
			for(Console console : Console.values()) {
				checkAbort();
				read(console.name(), true);
			}
			
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
			
			Popup.info("Reading complete.");
			progressBar.reset();
		} catch (InterruptedException ex) {
//			Popup.info("Aborted by user");
		} finally {
			callBack.completed();
		}
	}

    //FIXME: Rename RecalMan (Recalbox Manager)
    //FIXME: Handle default roms from recalbox (move to "recalbox-default-roms" folder, get in local and integrate in export feature)
    //FIXME: Change export folder to "RecalMan - xxxxx" with xxx the console display name
    
	private void read(String consoleName, boolean clean) 
			throws InterruptedException {
		try {
            File remoteFile = new File(FilenameUtils.concat(FilenameUtils.concat(exportPath, consoleName), "gamelist.xml"));
            File localFile = new File(FilenameUtils.concat(FilenameUtils.concat(sourcePath, consoleName), "gamelist.xml"));
            //Get file if it does not exist yet locally
            //FIXME: Ask user if he wants to overwrite OR show differences and propose how to handle the sync. Meantime, need to delete the local files to refresh
            //FIXME: Copy (rsync with no delete) all media files from exportPath to sourcePath
            if(!localFile.exists() && remoteFile.exists()) {
                FileSystem.copyFile(remoteFile, localFile);
            }            
			Document doc = XML.open(localFile.getAbsolutePath());
			if(doc==null) {
				Logger.getLogger(ProcessRead.class.getName())
						.log(Level.SEVERE, "File not found: {0}", localFile.getAbsolutePath());
				return;
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
						XML.getElementValue(element, "name"),
						XML.getElementValue(element, "desc"),
						XML.getElementValue(element, "image"),
						XML.getElementValue(element, "thumbnail"),
						ratingLocal,
						XML.getElementValue(element, "releaseDate"),
						XML.getElementValue(element, "developer"),
						XML.getElementValue(element, "publisher"),
						XML.getElementValue(element, "genre"),
						XML.getElementValue(element, "players"),
						playCounter,
						XML.getElementValue(element, "lastplayed"),
						Boolean.parseBoolean(
								XML.getElementValue(element, "favorite")));

//				if(clean && !game.exists(rootPath)) {
//					// ??? How to remove the node  [Why not  recreating the file ?]
////					doc.removeChild(element);
////					doc.getElementsByTagName("game").item(0).removeChild(element);
//				} else {
//					games.put(FilenameUtils.getName(game.getPath()), game);
//				}
				games.put(FilenameUtils.getBaseName(game.getPath()), game);
				progressBar.progress(game.getName());
			}
			
			if(clean) {
				//FIXME: Either fix this feature or remove it
//				TransformerFactory transformerFactory = 
//						TransformerFactory.newInstance();
//				Transformer transformer = transformerFactory.newTransformer();
//				DOMSource source = new DOMSource(doc);
//                File cleanGameListXml = new File(FilenameUtils.concat(FilenameUtils.concat(sourcePath, consoleName), "gamelist-clean.xml"));
//				StreamResult result = new StreamResult(cleanGameListXml);
//				transformer.transform(source, result);
//				StreamResult consoleResult = new StreamResult(System.out);
//				transformer.transform(source, consoleResult);
			}
//		} catch (TransformerException ex) {
//			Logger.getLogger(ProcessRead.class.getName())
//					.log(Level.SEVERE, null, ex);
		} catch (IOException ex) { 
            Logger.getLogger(ProcessRead.class.getName()).log(Level.SEVERE, null, ex);
        } catch (OutOfMemoryError ex) { 
            Logger.getLogger(ProcessRead.class.getName()).log(Level.SEVERE, null, ex);
        } 
	}
}