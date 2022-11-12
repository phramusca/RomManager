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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import rommanager.utils.ProcessAbstract;

/**
 * Sync process class
 * @author phramusca ( https://github.com/phramusca/JaMuz/ )
 */
public class JeuxVideos extends ProcessAbstract {
	
	private final ICallBack callback;
    
	/**
	 * Creates a new sync process instance  
	 * @param progressBar
	 */
	public JeuxVideos(ICallBack progressBar) {
        super("Thread.ViaFerrataNet");
		this.callback = progressBar;
	}
	
	/**
	 * Starts file synchronisation process in a new thread
	 * Called by MainGUI
	 */
    @Override
	public void run() {
		this.resetAbort();

        try {
            List<JeuVideo> psx = read(390); //PSOne
            System.out.println("Fini");
        } catch (InterruptedException ex) {
            callback.interrupted();
        }
        finally {
            callback.completed();
        }
	}
	
	private List<JeuVideo> read(int idMachine) throws InterruptedException {
        List<JeuVideo> read = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            read.addAll(read("https://www.jeuxvideo.com/meilleurs/machine-"+idMachine+"/?p="+i));
        }
        return read;
	}
		
	public List<JeuVideo> read(String url) throws InterruptedException {
		checkAbort();
        List<JeuVideo> jeux = new ArrayList<>();
		try {
			Document doc = Jsoup.connect(url).get();
			Elements aElements = doc.select(".container__3eUfTL li");
            for(Element element : aElements) {
                checkAbort();
                String title = element.select("div:nth-child(1) > div:nth-child(1) > div:nth-child(2) > span:nth-child(1) > h2:nth-child(1) > a:nth-child(1)").text();
                if(!title.equals("")) {
                    String description = element.select("div:nth-child(1) > div:nth-child(1) > div:nth-child(2) > p:nth-child(2)").text();
                    String urlJeuxVideo = element.select("div:nth-child(1) > div:nth-child(1) > div:nth-child(2) > span:nth-child(1) > h2:nth-child(1) > a:nth-child(1)").get(0).absUrl("href");
                    String releaseDate = element.select("div:nth-child(1) > div:nth-child(1) > div:nth-child(2) > span:nth-child(3)").text();
                    String rating = element.select("div:nth-child(1) > div:nth-child(2) > div:nth-child(1) > span:nth-child(1)").text();
                    String userRating = element.select("div:nth-child(1) > div:nth-child(2) > div:nth-child(2) > span:nth-child(1)").text();
                    jeux.add(new JeuVideo(urlJeuxVideo, title, releaseDate, rating, userRating, description));
//                    System.out.println("");
//                    System.out.println("-------------------------");
//                    System.out.println(title);
//                    System.out.println("\t"+urlJeuxVideo);
//                    System.out.println("\t"+releaseDate);
//                    System.out.println("\t"+rating);
//                    System.out.println("\t"+userRating);
//                    System.out.println("-------------------------");
//                    System.out.println("\t"+description);
//                    System.out.println("-------------------------");
                }
            }
		} catch (IndexOutOfBoundsException | IOException ex) {
			Logger.getLogger(JeuxVideos.class.getName()).log(Level.SEVERE, null, ex);
    	}
        return jeux;
	}
	
}
