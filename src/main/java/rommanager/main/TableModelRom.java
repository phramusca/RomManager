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

import java.io.IOException;
import rommanager.utils.TableModelGeneric;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.ImageIcon;
import org.apache.commons.io.FilenameUtils;
import static rommanager.main.ProcessList.allowedExtensions;

/**
 *
 * @author phramusca ( https://github.com/phramusca/JaMuz/ )
 */

public class TableModelRom extends TableModelGeneric {

	private Map<String, RomContainer> roms;
    private List<RomContainer> filteredRoms;
    private long lengthSelected;
    private int nbSelected;
    TableFilter tableFilter;
    
    /**
	 * Create the table model
	 */
	public TableModelRom() {
        this.roms = new LinkedHashMap<>();
        this.filteredRoms = new ArrayList<>();
        this.tableFilter = new TableFilter();
        setColumns();
	}

	private void setColumns() {
		this.setColumnNames(new String [] {
            "",
			"Name",
			"Description",
			"Console",
			"Genre",
            "Players",
            "Released",
			"Rating",
			"Export Selection"
        });
		this.fireTableStructureChanged();
	}
	
    @Override
    public boolean isCellEditable(int row, int col){
//		if(col==0) { //Selected checkbox
//            return true;
//        }
//        else if(col==7 || col==8 || col==9) { //TheMovieDb: Rating, WatchList and Favorite
//            VideoAbstract fileInfoVideo = files.get(row);
//            if(fileInfoVideo.getMyMovieDb().getId()!=0) {
//                return true;
//            }
//        }
		return false;
    }
    
    public boolean isCellEnabled(int row, int col) {
        return false;
    }
    
	/**
	 * Return list of roms
	 * @return
	 */
	public Map<String, RomContainer> getRoms() {
		return roms;
	}
	
	/**
	 *
	 * @param index
	 * @return
	 */
	public RomContainer getRom(int index) {
        if(index<filteredRoms.size() && index>=0) {
            return filteredRoms.get(index);
        } else {
            return null;
        }
	}

    /**
     * Return selected file's length
     * @return
     */
    public long getLengthSelected() {
        return lengthSelected;
    }

    /**
     *
     * @return
     */
    public int getNbSelected() {
        return nbSelected;
    }
    
    @Override
    public int getRowCount() {
        return this.filteredRoms.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        RomContainer romContainer = getRom(rowIndex);
        if(romContainer!=null) {
            ImageIcon icon = BufferIcon.getCoverIcon(romContainer.getGame().getName());
            switch (columnIndex) {
                case 0: return icon!= null ? icon: new ImageIcon();
                case 1: 
                    StringBuilder nameBuilder = new StringBuilder();
                    nameBuilder.append("<html>").append(romContainer.getName());
                    
                    // Add status indicators for multiple versions
                    String favoriteStatus = romContainer.getFavoriteStatus();
                    String hiddenStatus = romContainer.getHiddenStatus();
                    String adultStatus = romContainer.getAdultStatus();
                    
                    if (!favoriteStatus.equals("false")) {
                        nameBuilder.append("<BR/><BR/>");
                        if (favoriteStatus.equals("Mixed")) {
                            nameBuilder.append(" [Favorite: Mixed] ");
                        } else {
                            nameBuilder.append(" [Favorite] ");
                        }
                    }
                    
                    if (!hiddenStatus.equals("false")) {
                        if (favoriteStatus.equals("false")) nameBuilder.append("<BR/><BR/>");
                        if (hiddenStatus.equals("Mixed")) {
                            nameBuilder.append(" [Hidden: Mixed] ");
                        } else {
                            nameBuilder.append(" [Hidden] ");
                        }
                    }
                    
                    if (!adultStatus.equals("false")) {
                        if (favoriteStatus.equals("false") && hiddenStatus.equals("false")) nameBuilder.append("<BR/><BR/>");
                        if (adultStatus.equals("Mixed")) {
                            nameBuilder.append(" [Adult: Mixed] ");
                        } else {
                            nameBuilder.append(" [Adult] ");
                        }
                    }
                    
                    // Add playcount info (use first game for now)
                    if (romContainer.getGame().getPlaycount() > 0) {
                        if (favoriteStatus.equals("false") && hiddenStatus.equals("false") && adultStatus.equals("false")) {
                            nameBuilder.append("<BR/><BR/>");
                        }
                        nameBuilder.append("Played ").append(romContainer.getGame().getPlaycount())
                                .append(" times, last ").append(romContainer.getGame().getLastplayedFormatted());
                    }
                    
                    nameBuilder.append("</html>");
                    return nameBuilder.toString();
                case 2: 
                    StringBuilder builder = new StringBuilder();
                    builder.append("<html>")
                            .append(romContainer.getFilename())
                                .append("<BR/>(")
                            .append(romContainer.getGame().getDeveloper()).append(" / ")
                            .append(romContainer.getGame().getPublisher())
                                .append(")<BR/>").append("<BR/>")
                            .append(romContainer.getGame().getDesc().equals("")?romContainer.getJeuVideo().getDescription():romContainer.getGame().getDesc())
                            .append("</html>");
                    return builder.toString();
                case 3: return romContainer.getConsoleStr();
                case 4: return "<html>"+romContainer.getGame().getGenre().replace(",", "<BR/>")+"</html>";
                case 5: return romContainer.getGame().getPlayers();
                case 6: return romContainer.getGame().getReleaseDateFormatted();
                case 7: return romContainer.getGame().getRating();
                case 8: return romContainer; //need to return object for the filter (.toString() is auto anyway)
            }
		}
        return null;
    }
    
    /**
     * Sets given cell value
	 * @param value
	 * @param row
	 * @param col
	 */
    @Override
    public void setValueAt(Object value, int row, int col) {
		// RomContainer romContainer = getRom(row); // TODO: implement when needed

//        switch (col) {
//            case 0: 
//                break;
//            case 7:
//                break;
//		}
    }

    /**
	* Returns given column's data class
    * @param col
     * @return 
    */
    @Override
    public Class getColumnClass(int col){
        //Note: since all data on a given column are all the same
		//we return data class of given column first row
        return this.getValueAt(0, col).getClass();
    }

	/**
	 * Clears the table
	 */
	public void clear() {
		this.roms = new LinkedHashMap<>();
   		this.filteredRoms = new ArrayList<>();
        this.lengthSelected=0;
        this.nbSelected=0;
        //Update table
        this.fireTableDataChanged();
    }
	
	/**
    * Add a row to the table
	 * @param romContainer
    */
    public void addRow(RomContainer romContainer){
		roms.put(romContainer.getConsole().getSourceFolderName()+"/"+romContainer.getFilename(), romContainer);
		fireTableDataChanged();
    }

	/**
	 *
	 * @param console
	 * @param filename
	 * @throws IOException
	 */
	public void addRow(Console console, String filename) throws IOException {
        String key = console.getSourceFolderName()+"/"+filename;
		if(!roms.containsKey(key)) {
			RomContainer romContainer=null;
            String ext = FilenameUtils.getExtension(filename);
            if(ext.equals("7z")) {
                romContainer = new RomContainer7z(console, filename);
            } else if(allowedExtensions.contains(ext)) {
                String romName = RomContainerFlat.getRomName(FilenameUtils.getBaseName(filename), ext);
				romContainer = new RomContainerFlat(console, romName);
            }
			if(romContainer!=null) {
				roms.put(key, romContainer);
			}
		}
	}

    void filter() {
        this.filteredRoms = tableFilter.getFiltered(roms.values());
        this.fireTableDataChanged();
    }
}