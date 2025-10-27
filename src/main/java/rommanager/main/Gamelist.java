/*
 * Copyright (C) 2023 raph
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

import org.apache.commons.lang3.tuple.Pair;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FilenameUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import rommanager.utils.XML;

/**
 *
 * @author raph
 */
public class Gamelist {
    private Document doc;
	private final File file;
    private Map<String, Pair<Element, Game>> games;
    private int nbGamesDeleted=0;
    private int nbGamesModified=0;
    private int nbGamesFixed=0;

    public Gamelist(File file) {
        this.file = file;
        if (file != null) {
            read();
        }
    }
    
    public void save() {
        XML.save(file, doc);
        read();
    }

    private void read() {
        games = new HashMap<>();
        if(!file.exists()) {
            Logger.getLogger(Gamelist.class.getName())
                    .log(Level.WARNING, "File not found: {0}", file.getAbsolutePath());
            return;
        }
        
        doc = XML.open(file.getAbsolutePath());
        if(doc==null) {
            Logger.getLogger(Gamelist.class.getName())
                    .log(Level.SEVERE, "Error with: Document doc = XML.open(\"{0}\")", file.getAbsolutePath());
            return;
        }
        ArrayList<Element> elements = XML.getElements(doc, "game");
        for(Element element : elements) {
            Game game = getGame(element);
            games.put(FilenameUtils.getBaseName(game.getPath()), Pair.of(element, game));
        }
	}

    public Map<String, Pair<Element, Game>> getGames() {
        return games;
    }
    
    private Game getGame(Element element) {
        String r = XML.getElementValue(element, "rating");
        float ratingLocal=r.equals("")?-1:Float.parseFloat(r);
        String pc = XML.getElementValue(element, "playcount");
        int playCounter=pc.equals("")?-1:Integer.parseInt(pc);
        String t = XML.getAttribute(element, "timestamp");
        long timestamp = (t != null && !t.trim().isEmpty() ? Long.parseLong(t) : -1);
        String tp = XML.getElementValue(element, "timeplayed");
        int timeplayed = tp.equals("") ? 0 : Integer.parseInt(tp);
               
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
                    XML.getElementValue(element, "region"),
                    timeplayed,
                    file.lastModified()
                    );
    }

    void deleteGame(Element element) {
        element.getParentNode().removeChild(element);
        nbGamesDeleted++;
    }

    boolean hasChanged() {
        return nbGamesDeleted>0 || nbGamesFixed>0 || nbGamesModified>0;
    }

    public Game compareGame(Game localGame, Game remoteGame) {
        // Synchronization rules based on timestamps according to ETAT_DES_LIEUX.md
        
        // Locally modifiable fields: use the most recently modified
        // Compare local lastModifiedDate (from RomManager/ODS) with remote lastModifiedDate (from XML)
        // If local lastModifiedDate is 0 (old code), use fallback logic (local takes precedence)
        boolean isLocalNewer;
        if (localGame.getLastModifiedDate() == 0) {
            // Old code: recalbox always takes precedence
            isLocalNewer = false;
        } else {
            // New code: compare timestamps
            isLocalNewer = localGame.getLastModifiedDate() > remoteGame.getLastModifiedDate();
        }
        
        boolean favorite = isLocalNewer ? localGame.isFavorite() : remoteGame.isFavorite();
        boolean hidden = isLocalNewer ? localGame.isHidden() : remoteGame.isHidden();
        boolean adult = isLocalNewer ? localGame.isAdult() : remoteGame.isAdult();
        String name = isLocalNewer ? localGame.getName() : remoteGame.getName();

        // Note: remoteGame.getLastModifiedDate() contains the lastModified of the XML file
        //       localGame.getLastModifiedDate() contains the local modification date (RomManager/ODS)
        
        return new Game(
            remoteGame.getPath(),
            remoteGame.getHash(),
            name,
            remoteGame.getDesc(),
            remoteGame.getImage(),
            remoteGame.getVideo(),
            remoteGame.getThumbnail(),
            remoteGame.getRating(),
            remoteGame.getReleaseDate(),
            remoteGame.getDeveloper(),
            remoteGame.getPublisher(),
            remoteGame.getGenre(),
            remoteGame.getGenreId(),
            remoteGame.getPlayers(),
            remoteGame.getPlaycount(),
            remoteGame.getLastplayed(),
            favorite,
            remoteGame.getTimestamp(),
            hidden,
            adult,
            remoteGame.getRatio(),
            remoteGame.getRegion(),
            remoteGame.getTimeplayed(),
            localGame.getLastModifiedDate() // FIXME: We need to use the last modified/updated date. But this may be only available after writing xml and/or ods. So, need to be careful with this.
        );
    }
    
    public void setGame(Game newGame) {
        String key = FilenameUtils.getBaseName(newGame.getPath());
        Element elementGame = games.get(key).getLeft();
        setGame(elementGame, newGame);
    }
    
    private void setGame(Element elementGame, Game newGame) {
        setElementValue(elementGame, "favorite", newGame.isFavorite());
        setElementValue(elementGame, "hidden", newGame.isHidden());
        setElementValue(elementGame, "adult", newGame.isAdult());
        setElementValue(elementGame, "name", newGame.getName());       
        nbGamesModified++;
    }

    private void setElementValue(Element elementGame, String key, boolean value) {
        Element element = XML.getElement(elementGame, key);
        if(element!=null) {
            element.setTextContent(String.valueOf(value));
        } else {
            Element createElement = doc.createElement(key);
            createElement.setTextContent(String.valueOf(value));
            elementGame.appendChild(createElement);
        }
    }

    private void setElementValue(Element elementGame, String key, String value) {
        Element element = XML.getElement(elementGame, key);
        if(element!=null) {
            element.setTextContent(value);
        } else {
            Element createElement = doc.createElement(key);
            createElement.setTextContent(value);
            elementGame.appendChild(createElement);
        }
    }

    private void setElementAttribute(Element elementGame, String attributeName, String value) {
        if (value != null && !value.equals("0")) {
            elementGame.setAttribute(attributeName, value);
        }
    }

    void removeScraped(Element remoteGameElement) {
        removeElement(remoteGameElement, "hash");
        removeElement(remoteGameElement, "name");
        removeElement(remoteGameElement, "region");
        removeElement(remoteGameElement, "genreid");
        removeElement(remoteGameElement, "genre");
        removeElement(remoteGameElement, "publisher");
        removeElement(remoteGameElement, "developer");
        removeElement(remoteGameElement, "releasedate");
        removeElement(remoteGameElement, "thumbnail");
        removeElement(remoteGameElement, "image");
        removeElement(remoteGameElement, "desc");
        removeElement(remoteGameElement, "video");
        removeElement(remoteGameElement, "players");
        nbGamesFixed++;
    }

    void removeElement(Element elementGame, String key) {
        Element element = XML.getElement(elementGame, key);
        if(element != null) {
            elementGame.removeChild(element);
        }
    }
}
