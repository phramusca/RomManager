/*
 * Copyright (C) 2019 phramusca ( https://github.com/phramusca/ )
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

/**
 *
 * @author phramusca ( https://github.com/phramusca/ )
 */
public class RomContainerFlat extends RomContainer {

	/**
	 * For files that are not groupped in 7z
	 * @param console
	 * @param filename
	 */
	public RomContainerFlat(Console console, String filename) {
		super(console, filename);
	}
	@Override
	public void setExportableVersions() {
        switch(console) {
            case amstradcpc:
                for(RomVersion version : versions) {
                    //FIXME 3 Amstrad: Only extract several if "Disk" inside versions, otherwise do as default: take best version
                    version.setExportable(version.getErrorLevel()==0);
                }        
                break;
            case virtualboy:
            default:
                setBestExportable();
        }
		
	}
	
	static String getRomName(String filename, String ext) {
		String romName = filename;
		int pos = romName.indexOf("(");
		if(pos>=0) {
			romName=romName.substring(0, pos).trim();
		}
		romName=romName.concat(".").concat(ext);
		return romName;
	}
}
