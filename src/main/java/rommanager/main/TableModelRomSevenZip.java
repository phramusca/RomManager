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

import java.io.File;
import java.io.IOException;
import rommanager.utils.TableModelGeneric;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.ImageIcon;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author phramusca ( https://github.com/phramusca/JaMuz/ )
 */

public class TableModelRomSevenZip extends TableModelGeneric {

	private Map<String, RomSevenZipFile> roms;
//    private long lengthAll;
    private long lengthSelected;
    private int nbSelected;
	private String rootPath;
    
    /**
	 * Create the table model
	 */
	public TableModelRomSevenZip() {
        this.roms = new LinkedHashMap<>();
        //Set column names
        this.setColumnNames(new String [] {
            "FileName", 
            "Best Version",
			"Name",
			"",
			"Genre",
			"Rating",
			"Players",
			"Developer",
			"Publisher"
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
	 * Return list of files
	 * @return
	 */
	public Map<String, RomSevenZipFile> getRoms() {
		return roms;
	}
	

	public RomSevenZipFile getRom(int index) {
		return (new ArrayList<>(roms.values())).get(index);
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
        return this.roms.values().size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        RomSevenZipFile fileInfoRomSevenZip = getRom(rowIndex);

		ImageIcon icon = IconBuffer.getCoverIcon(fileInfoRomSevenZip.getGame().getName(), 
				FilenameUtils.concat(
						rootPath, 
						fileInfoRomSevenZip.getGame().getImage()), false);
		
        switch (columnIndex) {
            case 0: return fileInfoRomSevenZip.getFilename();
            case 1: return fileInfoRomSevenZip.toString();
			case 2: return fileInfoRomSevenZip.getGame().getName();
			case 3: return icon!= null ? icon: new ImageIcon();
			case 4: return fileInfoRomSevenZip.getGame().getGenre();
			case 5: return fileInfoRomSevenZip.getGame().getRating();
			case 6: return fileInfoRomSevenZip.getGame().getPlayers();
			case 7: return fileInfoRomSevenZip.getGame().getDeveloper();
			case 8: return fileInfoRomSevenZip.getGame().getPublisher();
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
		RomSevenZipFile file = getRom(row);

//        switch (col) {
//            case 0: 
//                select(file, (boolean)value);
//                break;
//            case 7:
//                file.setRating((int) value);
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
        this.lengthSelected=0;
        this.nbSelected=0;
        //Update table
        this.fireTableDataChanged();
    }
	
	/**
    * Add a row to the table
	 * @param file
    */
    public void addRow(RomSevenZipFile file){
		this.roms.put(file.getFilename(), file);
		this.fireTableDataChanged();
    }

	public void addRow(String filename) throws IOException {
		if(!roms.containsKey(filename)) {
			roms.put(filename, new RomSevenZipFile(new File(filename)));
		}
	}
	
    public void removeRow(RomSevenZipFile file){
		roms.remove(file.getFilename());
		this.fireTableDataChanged();
    }

	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}
}