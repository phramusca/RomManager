/*
 * Copyright (C) 2014 phramusca ( https://github.com/phramusca/JaMuz/ )
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

import javax.swing.RowFilter;

/**
 *
 * @author phramusca ( https://github.com/phramusca/JaMuz/ )
 */

//TODO: Include this in TableModelRom, so we can refresh table (fire) when filter changes
public class TableRowFilter extends RowFilter {

    private String console = null;
    private String genre = null;
    private String rating = null;

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
	 * @param genre
	 */
	public void displayByConsole(String genre) {
        if(genre.equals("All")) {
            this.console=null;
        }
        else {
            this.console=genre;
        }
    }
    // FIXME 6 Add GUI filter on number of files exported (only <=0 ; 1 and >1)
    @Override
    public boolean include(Entry entry) {
        RomContainer romFile = (RomContainer) entry.getValue(6);
        
        return isToDisplayConsole(romFile.getConsoleStr())
                && isToDisplayGenre(romFile.getGame().getGenre())
                && isToDisplayRating(String.valueOf(romFile.getGame().getRating()));
    } 
    
    private boolean isToDisplayConsole(String console) {
        return this.console==null ? true : console.equals(this.console);
    }
    
    private boolean isToDisplayGenre(String genre) {
        return this.genre==null ? true : genre.equals(this.genre);
    }
    
    private boolean isToDisplayRating(String rating) {
        return this.rating==null ? true : rating.equals(this.rating);
    }
    
}
