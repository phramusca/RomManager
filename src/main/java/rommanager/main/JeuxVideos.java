/*
 * Copyright (C) 2018 phramusca ( https://github.com/phramusca/JaMuz/ )
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

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import rommanager.utils.FileSystem;
import rommanager.utils.ProcessAbstract;

/**
 * Sync process class
 * @author phramusca ( https://github.com/phramusca/JaMuz/ )
 */
public class JeuxVideos extends ProcessAbstract {
	
	private final ICallBack callback;
    private Map<String, List<JeuVideo>> jeuxVideos;
    
	/**
	 * Creates a new sync process instance  
	 * @param progressBar
	 */
	public JeuxVideos(ICallBack progressBar) {
        super("Thread.JeuxVideos");
		this.callback = progressBar;
	}
	
    @Override
	public void run() {
		this.resetAbort();
        try {
            //Read cache file
            String readJson = FileSystem.readTextFile(new File("jeuxVideos.json"));
            jeuxVideos = new HashMap<>();
            if (!readJson.equals("")) {
                Gson gson = new Gson();
                Type mapType = new TypeToken<Map<String, List<JeuVideo>>>(){}.getType();
                jeuxVideos = gson.fromJson(readJson, mapType);
            }
            
            //Read info from jeuxvide.com only if not already done
            boolean atLeastOneModif = false;
            for(Console console : Console.values()) {
                if(console.getIdJeuxVideo() > -1) {
                    if(!jeuxVideos.containsKey(console.name())) {
                        List<JeuVideo> list = read(console);
                        jeuxVideos.put(console.name(), list);
                        atLeastOneModif = true;
                    }
                }
            }
            
            //Save cache file
            if(atLeastOneModif) {
                Gson gson = new Gson();
                FileSystem.writeTextFile(new File("jeuxVideos.json"), gson.toJson(jeuxVideos));
            }
            
            //FIXME !!!! Faire correspondre les entrées du read map et la liste des jeux ET ajouter un tag "Culte jeuxvideo.com"
            //FIXME !!!! GUI (enable/ disable)
            
            
        } catch (InterruptedException ex) {
            callback.interrupted();
        } catch (IOException | JsonSyntaxException ex) {
            callback.error(ex);
        }
        finally {
            callback.completed();
        }
	}
 	
	private List<JeuVideo> read(Console console) throws InterruptedException, IOException {
        List<JeuVideo> readAll = new ArrayList<>();
        for (int page = 1; page < 100; page++) {
            List<JeuVideo> readPage = read(page, console);
            readAll.addAll(readPage);
            if(readPage.size()<20) { //Nb of games by page
                return readAll; // Last page, returns
            }
        }
        return readAll;
	}
		
	private List<JeuVideo> read(int page, Console console) throws InterruptedException, IOException {
		checkAbort();
        List<JeuVideo> jeux = new ArrayList<>();
        String url = "https://www.jeuxvideo.com/meilleurs/machine-"+console.getIdJeuxVideo()+"/?p="+page;
        Document doc = Jsoup.connect(url).get();
        Elements aElements = doc.select(".container__3eUfTL li");
        callback.setup(aElements.size());
        for(Element element : aElements) {
            checkAbort();
            String title = element.select("div:nth-child(1) > div:nth-child(1) > div:nth-child(2) > span:nth-child(1) > h2:nth-child(1) > a:nth-child(1)").text();
            JeuVideo jeuVideo = new JeuVideo("", "Not found", "", "", "", "");
            if(!title.equals("")) {
                int indexOf = title.indexOf(console.getSuffixJeuxVideo());
                if(indexOf>-1) {
                    title = title.substring(0, indexOf);
                }
                String description = element.select("div:nth-child(1) > div:nth-child(1) > div:nth-child(2) > p:nth-child(2)").text();
                String urlJeuxVideo = element.select("div:nth-child(1) > div:nth-child(1) > div:nth-child(2) > span:nth-child(1) > h2:nth-child(1) > a:nth-child(1)").get(0).absUrl("href");
                String releaseDate = element.select("div:nth-child(1) > div:nth-child(1) > div:nth-child(2) > span:nth-child(3)").text();
                String rating = element.select("div:nth-child(1) > div:nth-child(2) > div:nth-child(1) > span:nth-child(1)").text();
                String userRating = element.select("div:nth-child(1) > div:nth-child(2) > div:nth-child(2) > span:nth-child(1)").text();
                jeuVideo = new JeuVideo(urlJeuxVideo, title, releaseDate, rating, userRating, description);
                jeux.add(jeuVideo);
            }
            callback.read(jeuVideo);
        }
        return jeux;
	}
}
