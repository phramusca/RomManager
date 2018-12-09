/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rommanager.gamelist;

import java.io.File;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author phramusca ( https://github.com/phramusca/JaMuz/ )
 */
public class Game {

	private final String path;
	private final String name;
	private final String desc;
	private final String image;
	private final String thumbnail; //Not used
	private final float rating;
	private final String releaseDate;
	private final String developer;
	private final String publisher;
	private final String genre;
	private final String players;
	private final int playcount;
	private final String lastplayed;
	private final boolean favorite;

	public Game(String path, String name, String desc, String image, 
			String thumbnail, float rating, String releaseDate, 
			String developer, String publisher, String genre, String players, 
			int playcount, String lastplayed, boolean favorite) {
		this.path = path;
		this.name = name;
		this.desc = desc;
		this.image = image;
		this.thumbnail = thumbnail;
		this.rating = rating;
		this.releaseDate = releaseDate;
		this.developer = developer;
		this.publisher = publisher;
		this.genre = genre;
		this.players = players;
		this.playcount = playcount;
		this.lastplayed = lastplayed;
		this.favorite = favorite;
	}

	public boolean delete(String rootPath) {
		return getFile(rootPath).delete();
	}
	
	public boolean exists(String rootPath) {
		return getFile(rootPath).exists();
	}

	public String getName() {
		return name;
	}

	public String getDesc() {
		return desc;
	}

	public float getRating() {
		return rating;
	}

	public String getDeveloper() {
		return developer;
	}

	public String getPublisher() {
		return publisher;
	}

	public String getGenre() {
		return genre;
	}

	public String getPlayers() {
		return players;
	}

	public boolean isFavorite() {
		return favorite;
	}
	
	private File getFile(String rootPath) {
		return new File(FilenameUtils.concat(rootPath, path));
	}
	
	@Override
	public String toString() {
		return "Game{"  + "\n"
					+ "path=" + path + "\n"
					+ ", name=" + name + "\n"
					+ ", desc=" + desc + "\n"
					+ ", image=" + image + "\n"
					+ ", thumbnail=" + thumbnail + "\n"
					+ ", rating=" + rating + "\n"
					+ ", releaseDate=" + releaseDate + "\n"
					+ ", developer=" + developer + "\n"
					+ ", publisher=" + publisher + "\n"
					+ ", genre=" + genre + "\n"
					+ ", players=" + players + "\n"
					+ ", playcount=" + playcount + "\n"
					+ ", lastplayed=" + lastplayed + "\n"
					+ ", favorite=" + favorite + "\n"
				+ '}';
	}

	String getPath() {
		return path;
	}

	public String getImage() {
		return image;
	}
}