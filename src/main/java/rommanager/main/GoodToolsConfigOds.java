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
import java.util.HashMap;
import java.util.Map;
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
	
	private static Map<String, GoodCode> codes;
	private static Map<String, GoodCountry> translations;

	public static Map<String, GoodCode> getCodes() {
		if(codes==null) {
			codes = new HashMap<>();
			readFileCodes();
		}
		return codes;
	}
	
	public static Map<String, GoodCountry> getTranslations() {
		if(translations==null) {
			translations = new HashMap<>();
			readFileTranslations();
		}
		return translations;
	}

	private static void readFileTranslations() {
		if(!DOC_FILE.exists()) {
			Logger.getLogger(GoodToolsConfigOds.class.getName())
					.log(Level.WARNING, "{0} does not exists", DOC_FILE);
			return;
		}
        try {
			SpreadSheet spreadSheet = SpreadSheet.createFromFile(DOC_FILE);
			Sheet sheet = spreadSheet.getSheet("Translation");
			int nRowCount = sheet.getRowCount();
			for(int nRowIndex = 1; nRowIndex < nRowCount; nRowIndex++) {
				Row row = new Row(sheet, nRowIndex);
				// String category = row.getValue(0).trim(); // TODO: use category if needed
				
				
				if(row.getValue(1).trim().equals("")) {
					break;
				} else {
					String code = row.getValue(0).trim();
					int score = Integer.valueOf(row.getValue(1).trim());
					String description = row.getValue(2).trim();

					GoodCountry goodCountry = new GoodCountry(code, score, description);
					translations.put(code, goodCountry);
				}
			}
		} catch (IOException ex) {
			Logger.getLogger(GoodToolsConfigOds.class.getName())
					.log(Level.SEVERE, null, ex);
		} 
    }
	
	// https://segaretro.org/GoodTools
	// http://emulation.gametechwiki.com/index.php/GoodTools
	private static void readFileCodes() {
		if(!DOC_FILE.exists()) {
			Logger.getLogger(GoodToolsConfigOds.class.getName())
					.log(Level.WARNING, "{0} does not exists", DOC_FILE);
			return;
		}
        try {
			SpreadSheet spreadSheet = SpreadSheet.createFromFile(DOC_FILE);
			Sheet sheet = spreadSheet.getSheet("ALL");
			int nRowCount = sheet.getRowCount();
			for(int nRowIndex = 1; nRowIndex < nRowCount; nRowIndex++) {
				Row row = new Row(sheet, nRowIndex);
				String category = row.getValue(0).trim();
				if(row.getValue(2).trim().equals("")) {
					break;
				} else {
					String code = row.getValue(1).trim();
					int score = Integer.valueOf(row.getValue(2).trim());
					String description = row.getValue(3).trim();

					String type = (code.startsWith("(") || code.startsWith("["))
							? code.substring(0, 1) : "";

					GoodCode goodCode = new GoodCode(category, type, code, score, description);
					codes.put(code, goodCode);
				}
			}
		} catch (IOException ex) {
			Logger.getLogger(GoodToolsConfigOds.class.getName())
					.log(Level.SEVERE, null, ex);
		} 
    }
}
