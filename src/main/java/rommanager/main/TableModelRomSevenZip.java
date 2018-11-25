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

import rommanager.utils.TableModelGeneric;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author phramusca ( https://github.com/phramusca/JaMuz/ )
 */

public class TableModelRomSevenZip extends TableModelGeneric {

    private List<RomSevenZipFile> files;
//    private long lengthAll;
    private long lengthSelected;
    private int nbSelected;
    
    /**
	 * Create the table model
	 */
	public TableModelRomSevenZip() {
        this.files = new ArrayList<>();
        
        //Set column names
        this.setColumnNames(new String [] {
            "FileName", //NOI18N
            "Best Version" //NOI18N
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
	public List<RomSevenZipFile> getFiles() {
		return files;
	}

    /**
     * get list of files
     * @param index
     * @return
     */
    public RomSevenZipFile getFile(int index) {
        return this.files.get(index);
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
        return this.files.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        RomSevenZipFile fileInfoRomSevenZip = files.get(rowIndex);

        switch (columnIndex) {
            case 0: return fileInfoRomSevenZip.getFilename();
            case 1: return fileInfoRomSevenZip.toString();
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
		RomSevenZipFile file = files.get(row);

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
        this.files = new ArrayList<>();
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
		this.files.add(file);
		this.fireTableDataChanged();
    }

    public void removeRow(RomSevenZipFile file){
		this.files.remove(file);
		this.fireTableDataChanged();
    }
    
}
