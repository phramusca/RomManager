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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jopendocument.dom.spreadsheet.Sheet;
import org.jopendocument.dom.spreadsheet.SpreadSheet;
import rommanager.utils.Row;

/**
 *
 * @author phramusca ( https://github.com/phramusca/JaMuz/ )
 */
public class GoodToolsConfigOds {
	
	private final static File DOC_FILE = new File("GoodToolsConfig.ods");
	private final static String SHEET_NAME = "ALL";
	
	private static List<GoodCode> codes;

	public static List<GoodCode> getCodes() {
		if(codes==null) {
			codes = new ArrayList<>();
			readFile();
		}
		return codes;
	}

	private static void readFile() {
		if(!DOC_FILE.exists()) {
			Logger.getLogger(GoodToolsConfigOds.class.getName())
					.log(Level.WARNING, "{0} does not exists", DOC_FILE);
			return;
		}
        try {
			SpreadSheet spreadSheet = SpreadSheet.createFromFile(DOC_FILE);
			Sheet sheet = spreadSheet.getSheet(SHEET_NAME);
			int nRowCount = sheet.getRowCount();
			for(int nRowIndex = 1; nRowIndex < nRowCount; nRowIndex++) {
				Row row = new Row(sheet, nRowIndex);
				String category = row.getValue(0);
				String type = row.getValue(1);
				String code = row.getValue(2);
				int score = Integer.valueOf(row.getValue(3));
				String description = row.getValue(4);
				GoodCode goodCode = new GoodCode(category, type, code, score, description);
				codes.add(goodCode);
			}
		} catch (IOException ex) {
			Logger.getLogger(GoodToolsConfigOds.class.getName())
					.log(Level.SEVERE, null, ex);
		} 
    }
}
