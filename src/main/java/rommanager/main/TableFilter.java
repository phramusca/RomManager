/*
 * Copyright (C) 2022 raph
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

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import rommanager.utils.TriStateCheckBox;
import rommanager.utils.TriStateCheckBox.State;

/**
 *
 * @author raph
 */
public class TableFilter {

    private String search = "";
    private String console = null;
    private String genre = null;
    private String rating = null;
    private String players = null;
    private String playCount = null;
    private String decade = null;
    private ExportFilesNumber exportFilesNumber = ExportFilesNumber.ALL;
    
    private State displayFavorite = State.ALL;
    private State displayHidden = State.UNSELECTED;
    private State displayAdult = State.UNSELECTED;
    
    private TableModelColumn sortBy = TableModelColumn.Console;

	/**
	 *
	 * @param genre
	 */
	public void displayByGenre(String genre) {
        if(genre.equals("All")) {
            this.genre=null;
        }
        else {
            this.genre=genre;
        }
    }
    
	/**
	 *
	 * @param rating
	 */
	public void displayByRating(String rating) {
        if(rating.equals("All")) {
            this.rating=null;
        }
        else {
            this.rating=rating;
        }
    }
    
    /**
	 *
	 * @param players
	 */
	public void displayByPlayers(String players) {
        if(players.equals("All")) {
            this.players=null;
        }
        else {
            this.players=players;
        }
    }
    
    /**
	 *
	 * @param playCount
	 */
	public void displayByPlayCount(String playCount) {
        if(playCount.equals("All")) {
            this.playCount=null;
        }
        else {
            this.playCount=playCount;
        }
    }
    
    void displayByDecade(String decade) {
        if(decade.equals("All")) {
            this.decade=null;
        }
        else {
            this.decade=decade;
        }
    }
    
	/**
	 *
	 * @param console
	 */
	public void displayByConsole(String console) {
        if(console.equals("All")) {
            this.console=null;
        }
        else {
            this.console=console;
        }
    }
    
    public void displayByNumberExportFiles(ExportFilesNumber exportFilesNumber) {
        this.exportFilesNumber=exportFilesNumber;
    }
    
    public void displayFavorite(TriStateCheckBox.State display) {
        this.displayFavorite=display;
    }
    
    public void displayHidden(TriStateCheckBox.State display) {
        this.displayHidden=display;
    }
    
    public void displayAdult(TriStateCheckBox.State display) {
        this.displayAdult=display;
    }
       
    List<RomContainer> getFiltered(Collection<RomContainer> values) {
        Stream<RomContainer> stream = values.stream();
        if(!this.search.isEmpty()) {
            stream = stream.filter(r -> r.getName().toLowerCase().contains(this.search.toLowerCase()));
        }
        if(this.console!=null) {
            stream = stream.filter(r -> r.console.getName().equals(this.console));
        }
        if(this.genre!=null) {
            stream = stream.filter(r -> r.getGame().getGenres().contains(this.genre));
        }
        if(this.rating!=null) {
            stream = stream.filter(r -> String.valueOf(r.getGame().getRating()).equals(this.rating));
        }
        if(this.players!=null) {
            stream = stream.filter(r -> String.valueOf(r.getGame().getPlayers()).equals(this.players));
        }
        if(this.playCount!=null) {
            stream = stream.filter(r -> String.valueOf(r.getGame().getPlaycount()).equals(this.playCount));
        }
        if(this.decade!=null) {
            stream = stream.filter(r -> String.valueOf(r.getGame().getReleaseDecade()).equals(this.decade));
        }
        switch(displayFavorite) {
            case ALL:
                break;
            case SELECTED:
                stream = stream.filter(r -> r.hasAnyVersionFavorite());
                break;
            case UNSELECTED:
                stream = stream.filter(r -> !r.hasAnyVersionFavorite());
                break;
        }
        switch(displayHidden) {
            case ALL:
                break;
            case SELECTED:
                stream = stream.filter(r -> r.hasAnyVersionHidden());
                break;
            case UNSELECTED:
                stream = stream.filter(r -> !r.hasAnyVersionHidden());
                break;
        }
        switch(displayAdult) {
            case ALL:
                break;
            case SELECTED:
                stream = stream.filter(r -> r.hasAnyVersionAdult());
                break;
            case UNSELECTED:
                stream = stream.filter(r -> !r.hasAnyVersionAdult());
                break;
        }
        switch(exportFilesNumber) {
            case ALL:
                break;
            case LESS_OR_EQUAL_ZERO:
                stream = stream.filter(r -> r.getExportableVersions().size()<=0);
                break;
            case MORE_THAN_ONE:
                stream = stream.filter(r -> r.getExportableVersions().size()>1);
                break;
            case MORE_THAN_ZERO:
                stream = stream.filter(r -> !r.getExportableVersions().isEmpty());
                break;
            case ONE:
                stream = stream.filter(r -> r.getExportableVersions().size()==1);
                break;
        }
        Comparator<RomContainer> comparator;
        switch(sortBy) {
            case Rating:
                comparator = (RomContainer o1, RomContainer o2) -> {
                    return Float.compare(o1.getGame().getRating(), o2.getGame().getRating());
                };
                comparator = comparator.reversed();
                break;

            case Genre:
                comparator = (RomContainer o1, RomContainer o2) -> {
                    String genre1 = o1.getGame().getGenre();
                    String genre2 = o2.getGame().getGenre();

                    if (genre1.isEmpty() && genre2.isEmpty()) {
                        return 0; // Both are empty, consider them equal
                    } else if (genre1.isEmpty()) {
                        return 1; // o1 is empty, move it to the end
                    } else if (genre2.isEmpty()) {
                        return -1; // o2 is empty, move it to the end
                    } else {
                        return genre1.compareTo(genre2); // Compare non-empty genres
                    }
                };break;
            case Players:
                comparator = (RomContainer o1, RomContainer o2) -> {
                    return o1.getGame().getPlayers().compareTo(o2.getGame().getPlayers());
                };
                comparator = comparator.reversed();
                break;
            case ReleaseDate:
                comparator = (RomContainer o1, RomContainer o2) -> {
                    return o1.getGame().getReleaseDateSql().compareTo(o2.getGame().getReleaseDateSql());
                };break;
            default:
            case Console:
                comparator = (RomContainer o1, RomContainer o2) -> {
                    return o1.getConsole().getName().compareTo(o2.getConsole().getName());
                };break;
        }        
        stream = stream.sorted(comparator.thenComparing((RomContainer o1, RomContainer o2) -> {
            return o1.getGame().getName().compareTo(o2.getGame().getName());
        }));
        return stream.collect(Collectors.toList());
    }

    void sortBy(TableModelColumn tableModelColumn) {
        this.sortBy = tableModelColumn;
    }

    void search(String text) {
        this.search = text;
    }
    
    public enum ExportFilesNumber {
        ALL,
        LESS_OR_EQUAL_ZERO,
        MORE_THAN_ZERO,
        ONE,
        MORE_THAN_ONE
    }
}
