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
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
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

	private final String exportPath;
	private final ProgressBar progressBar;
	private Map<String, Game> games;
	private final TableModelRom tableModel;
	private final ICallBackProcess callBack;
	
	public ProcessRead(
			String exportPath, 
			ProgressBar progressBar, 
			TableModelRom tableModel, 
			ICallBackProcess callBack) {
		super("Thread.ProcessRead");
		this.exportPath = exportPath;
		this.progressBar = progressBar;
		this.tableModel = tableModel;
		this.callBack = callBack;
	}

	@Override
	public void run() {
		try {
			for(Console console : Console.values()) {
				checkAbort();
				read(FilenameUtils.concat(exportPath, console.name()), true);
			}
			Popup.info("Reading complete.");
			progressBar.reset();
		} catch (InterruptedException ex) {
//			Popup.info("Aborted by user");
		} finally {
			callBack.completed();
		}
	}

	private void read(String consolePath, boolean clean) 
			throws InterruptedException {
		try {
			games = new HashMap<>();
			String filename=FilenameUtils.concat(consolePath, "gamelist.xml");
			Document doc = XML.open(filename);
			if(doc==null) {
				Logger.getLogger(ProcessRead.class.getName())
						.log(Level.SEVERE, "File not found: {0}", filename);
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
//					//FIXME 7 How to remove the node  [Why not  recreating the file ?]
////					doc.removeChild(element);
////					doc.getElementsByTagName("game").item(0).removeChild(element);
//				} else {
//					games.put(FilenameUtils.getName(game.getPath()), game);
//				}
				games.put(FilenameUtils.getBaseName(game.getPath()), game);
				progressBar.progress(game.getName());
			}
			
			if(clean) {
				// write the content into xml file
				TransformerFactory transformerFactory = 
						TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);

				//FIXME 7 Save to source (back it up first) when really cleaned
				StreamResult result = new StreamResult(
						new File("gamelist-purged.xml"));
				transformer.transform(source, result);
				StreamResult consoleResult = new StreamResult(System.out);
				transformer.transform(source, consoleResult);
			}
			
			//FIXME: Set only once for all consoles, as for ProcessList
			progressBar.setup(tableModel.getRoms().size());
			for(RomContainer romContainer : tableModel.getRoms().values()) {
				checkAbort();
				for(RomVersion romVersion : romContainer.getVersions()) {
					checkAbort();
					String key = FilenameUtils.getBaseName(
							romVersion.getFilename());
					if(games.containsKey(key)) {
						Game game = games.get(key);
						IconBuffer.getCoverIcon(
								game.getName(), 
								FilenameUtils.concat(consolePath, 
										game.getImage()), 
								true);
						romVersion.setGame(game);
					}
				}
				progressBar.progress(romContainer.getFilename());
			}
			tableModel.fireTableDataChanged();
		} catch (TransformerException ex) {
			Logger.getLogger(ProcessRead.class.getName())
					.log(Level.SEVERE, null, ex);
		} 
	}

	public Map<String, Game> getGames() {
		return games;
	}	
}