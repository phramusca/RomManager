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
        read();
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

    void deleteGame(Element element) {
        element.getParentNode().removeChild(element);
        nbGamesDeleted++;
    }

    boolean hasChanged() {
        return nbGamesDeleted>0 || nbGamesFixed>0 || nbGamesModified>0;
    }

    public Game compareGame(Game localGame, Game remoteGame) {
        Game newGame = new Game(remoteGame.getPath(), 
                remoteGame.getHash(), 
                remoteGame.getName(), 
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
                remoteGame.isFavorite(), 
                remoteGame.getTimestamp(), 
                remoteGame.isHidden(), 
                remoteGame.isAdult(), 
                remoteGame.getRatio(), 
                remoteGame.getRegion());

        if(localGame.isFavorite()) {
            
        }

        
        return newGame;
    }

    public void setGame(Game newGame) {
        String key = FilenameUtils.getBaseName(newGame.getPath());
        Element elementGame = games.get(key).getLeft();
        setGame(elementGame, newGame);
    }
    
    //FIXME 0 CHECK IF MODIFICATIONS ARE PRESERVED IN RECALBOX
    //And which one(s)
    // (From memory only removeScraped works, but need to retest all and document this time)
    private void setGame(Element elementGame, Game newGame) {
        // TODO Gamelist - Do all other modified values !!
        
        setElementValue(elementGame, "favorite", newGame.isFavorite());
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
