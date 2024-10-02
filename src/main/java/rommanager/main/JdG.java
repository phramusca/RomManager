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

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jopendocument.dom.spreadsheet.Sheet;
import org.jopendocument.dom.spreadsheet.SpreadSheet;
import static rommanager.main.RomManager.TAG_JDG;
import rommanager.utils.ProcessAbstract;
import rommanager.utils.ProgressBar;
import rommanager.utils.Row;

/**
 * Sync process class
 * @author phramusca ( https://github.com/phramusca/JaMuz/ )
 */
public class JdG extends ProcessAbstract {
	
	private final ICallBack callback;
    private final TableModelRom tableModel;
    private final ProgressBar progressBarGame;
    private final String sourcePath;
    private final static File DOC_FILE = new File("JdG Tier list.ods");
    
	/**
	 * Creates a new sync process instance  
	 * @param callback
     * @param tableModel
     * @param progressBar
     * @param sourcePath
	 */
	public JdG(ICallBack callback, TableModelRom tableModel, ProgressBar progressBar, String sourcePath) {
        super("Thread.JdGRead");
		this.callback = callback;
        this.tableModel = tableModel;
        this.progressBarGame = progressBar;
        this.sourcePath = sourcePath;
	}
	
    @Override
	public void run() {
		this.resetAbort();
        try {
            progressBarGame.setIndeterminate("Reading JdG Tier List");
            Map<String, JdgEntry> tags = readTags();
            //Assign read value to rom versions
            Collection<RomContainer> romCollection = tableModel.getRoms().values();
            progressBarGame.setup(romCollection.size());
            for(RomContainer romContainer : romCollection) {
                checkAbort();
                progressBarGame.progress(romContainer.getFilename());
                String key = romContainer.getConsole().getSourceFolderName()+"_"+romContainer.getFilename();
                if(tags.containsKey(key)) {
                    JdgEntry jdgEntry = tags.get(key);
                    for(RomVersion romVersion : romContainer.getVersions()) {
                        romVersion.addTag(TAG_JDG+"_"+jdgEntry.getTag());
                    }
                }
            }
            progressBarGame.setIndeterminate("Saving ods file");
            if(RomManagerOds.createFile(tableModel, progressBarGame, sourcePath)) {
                callback.saved();
            }
            progressBarGame.reset();
        } catch (InterruptedException ex) {
            callback.interrupted();
        }
        finally {
			callback.completed();
        }
	}
    
    private static Map<String, JdgEntry> readTags() {
        Map<String, JdgEntry> tags = new HashMap<>();
		if(!DOC_FILE.exists()) {
			Logger.getLogger(JdG.class.getName())
					.log(Level.WARNING, "{0} does not exists", DOC_FILE);
			return tags;
		}
        try {
			SpreadSheet spreadSheet = SpreadSheet.createFromFile(DOC_FILE);
			Sheet sheet = spreadSheet.getSheet("TierList");
			int nRowCount = sheet.getRowCount();
			for(int nRowIndex = 1; nRowIndex < nRowCount; nRowIndex++) {
				Row row = new Row(sheet, nRowIndex);
                String category = row.getValue(2).trim();
//                String link = row.getValue(3).trim();
				String container = row.getValue(5).trim();
                String console = row.getValue(6).trim();
                String tag = row.getValue(8).trim();
				if(category.equals("")) {
					break; //End of file
				} else if(container.equals("") || console.equals("") || tag.equals("")) {
					//Not enough info to be retrieved
				} else {
                    JdgEntry jdgEntry = new JdgEntry(container, console, tag);
					tags.put(console+"_"+container, jdgEntry);
				}
			}
		} catch (IOException ex) {
			Logger.getLogger(JdG.class.getName())
					.log(Level.SEVERE, null, ex);
		}
        return tags;
    }
}
