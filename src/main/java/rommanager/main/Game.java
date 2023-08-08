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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FilenameUtils;
import rommanager.utils.DateTime;

/**
 *
 * @author phramusca ( https://github.com/phramusca/JaMuz/ )
 */
public class Game {

    //FIXME 2 Use unused fields (display and/or filter)
    
	private final String path;
    private final String hash; //Not used, what for ?
	private final String name;
	private final String desc;
	private final String image;
    private final String video;
	private final String thumbnail; //Not used, same as image
	private final float rating;
	private final String releaseDate;
	private final String developer;
	private final String publisher;
    private final String genre;
	private final List<String> genres;
    private final String genreId; //Not used, not useful
	private final String players;
	private final int playcount; //FIXME 0 Display playcount
	private final String lastplayed; //FIXME 0 Display lastplayed
	private final boolean favorite; //FIXME 0 Display favorite
    private final long timestamp; //Not used, what for ?
    private final boolean hidden; //FIXME 0 Display hidden
    private final boolean adult; //FIXME 0 Display adult
    private final String ratio; //Not used, what for ?
    private final String region; //Not used, what for ?

	public Game(String path, String hash, String name, String desc, String image, 
            String video, String thumbnail, float rating, String releaseDate, 
			String developer, String publisher, String genre, String genreId, 
            String players, int playcount, String lastplayed, boolean favorite,
            long timestamp, boolean hidden, boolean adult, String ratio, String region) {
		this.path = path;
        this.hash = hash;
		this.name = name;
		this.desc = desc;
		this.image = image;
        this.video = video;
		this.thumbnail = thumbnail;
		this.rating = rating;
		this.releaseDate = releaseDate;
		this.developer = developer;
		this.publisher = publisher;
        this.genre = genre;
		this.genres = Arrays.asList(genre.trim().split("\\s*,\\s*"));
        this.genreId = genreId;
		this.players = players;
		this.playcount = playcount;
		this.lastplayed = lastplayed;
		this.favorite = favorite;
        this.timestamp = timestamp;
        this.hidden = hidden;
        this.adult = adult;
        this.ratio = ratio;
        this.region = region;
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
    
    public List<String> getGenres() {
        return genres;
    }
    
    public String getGenreId() {
		return genreId;
	}

	public String getPlayers() {
		return players;
	}

	public boolean isFavorite() {
		return favorite;
	}

    public String getVideo() {
		return video;
	}
    
	public String getThumbnail() {
		return thumbnail;
	}

	public String getReleaseDate() {
		return DateTime.formatUTC(getReleaseDate(releaseDate, "^(\\d{4})(\\d{2})(\\d{2})T(\\d{6})$"), "MM/yyyy", false);
	}
    
    private Date getReleaseDate(String line, String pattern) {
		Pattern patterner = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
		Matcher matcher = patterner.matcher(line);
		boolean matchFound = matcher.find();
		if(matchFound) {
			int year = Integer.parseInt(matcher.group(1));
            int month = Integer.parseInt(matcher.group(2));
            int day = Integer.parseInt(matcher.group(3));
            Calendar c = Calendar.getInstance();
            c.set(year, month, day);
            return c.getTime();
		}
		return new Date(0);
	}

	public int getPlaycount() {
		return playcount;
	}

	public String getLastplayed() {
		return lastplayed;
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
					+ ", players=" + players + "\n"
					+ ", playcount=" + playcount + "\n"
					+ ", lastplayed=" + lastplayed + "\n"
					+ ", favorite=" + favorite + "\n"
				+ '}';
	}

	public String getPath() {
		return path;
	}
    
    public String getHash() {
		return hash;
	}

	public String getImage() {
		return image;
	}

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isHidden() {
        return hidden;
    }

    public boolean isAdult() {
        return adult;
    }

    public String getRatio() {
        return ratio;
    }

    public String getRegion() {
        return region;
    }
}