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
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author raph
 */
public class TableFilter {

    private String console = null;
    private String genre = null;
    private String rating = null;
    private ExportFilesNumber exportFilesNumber = ExportFilesNumber.ALL;

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
    
    List<RomContainer> getFiltered(Collection<RomContainer> values) {
        Stream<RomContainer> stream = values.stream();
        if(this.console!=null) {
            stream = stream.filter(r -> r.console.getName().equals(this.console));
        }
        if(this.genre!=null) {
            stream = stream.filter(r -> r.getGame().getGenre().equals(this.genre));
        }
        if(this.rating!=null) {
            stream = stream.filter(r -> String.valueOf(r.getGame().getRating()).equals(this.rating));
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
                stream = stream.filter(r -> r.getExportableVersions().size()>0);
                break;
            case ONE:
                stream = stream.filter(r -> r.getExportableVersions().size()==1);
                break;
        }
        stream = stream.sorted((RomContainer o1, RomContainer o2) -> {
            int ConsoleCompare = o1.console.getName().compareTo(o2.console.getName());
            return (ConsoleCompare == 0)
                    ? o1.getGame().getName().compareTo(o2.getGame().getName())
                    : ConsoleCompare;
        });
        return stream.collect(Collectors.toList());
    }
    
    public enum ExportFilesNumber {
        ALL,
        LESS_OR_EQUAL_ZERO,
        MORE_THAN_ZERO,
        ONE,
        MORE_THAN_ONE
    }
}
