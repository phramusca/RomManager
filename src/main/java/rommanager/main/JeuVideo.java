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

/**
 *
 * @author phramusca ( https://github.com/phramusca/JaMuz/ )
 */
public class JeuVideo {
	public String url;
	public String title;
    public String description;
    public String releaseDate;
	public String rating;
	public String userRating;

    public JeuVideo(String url, String title, String releaseDate, String rating, String userRating, String description) {
        this.url = url;
        this.title = title;
        this.releaseDate = releaseDate;
        this.rating = rating;
        this.userRating = userRating;
        this.description = description;
    }

    @Override
    public String toString() {
        return "JeuVideo{" + "url=" + url + ", title=" + title + ", description=" + description + ", releaseDate=" + releaseDate + ", rating=" + rating + ", userRating=" + userRating + '}';
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getRating() {
        return rating;
    }

    public String getUserRating() {
        return userRating;
    }   
}
