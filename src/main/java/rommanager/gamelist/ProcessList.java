/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rommanager.gamelist;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
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
public class ProcessList extends ProcessAbstract {

	private final String rootPath;
	private final String path;
	private final ProgressBar progressBar;
	private List<Game> games;
	
	public ProcessList(String rootPath, String path, ProgressBar progressBar) {
		super("Thread.gamelist.ProcessList");
		this.rootPath = rootPath;
		this.path = path;
		this.progressBar = progressBar;
	}

	@Override
	public void run() {
		try {
			read(true);
//			progressBar.reset();
		} catch (InterruptedException ex) {
			Popup.info("Aborted by user");
		} 
	}

	private void read(boolean clean) throws InterruptedException {
		try {
			games = new ArrayList<>();
			Document doc = XML.open(path);
			if(doc==null) {
				Popup.warning("File open failed.");
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
						XML.getElementValue(element, "path"),
						Boolean.parseBoolean(XML.getElementValue(element, "favorite")));

				if(clean && !game.exists(rootPath)) {
					//FIXME: How to remove the node ?
//					doc.removeChild(element);
//					doc.getElementsByTagName("game").item(0).removeChild(element);
				} else {
					games.add(game);
				}
				progressBar.progress(game.getName());
			}
			
			if(clean) {
				// write the content into xml file
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);

				//FIXME: Save to source (back it up first) when really cleaned
				StreamResult result = new StreamResult(new File("gamelist-purged.xml"));
				transformer.transform(source, result);
				StreamResult consoleResult = new StreamResult(System.out);
				transformer.transform(source, consoleResult);
			}
		} catch (TransformerException ex) {
			Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
		} 
	}

	public List<Game> getGames() {
		return games;
	}
	
	
}
