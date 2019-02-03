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

import java.io.File;
import java.io.IOException;

/**
 *
 * @author phramusca ( https://github.com/phramusca/ )
 */
public class RomContainerAmstrad extends RomContainer {
	
	/**
	 * For dsk (Amstrad) files that are not groupped in 7z
	 * @param console
	 * @param file
	 * @param filename
	 * @throws IOException
	 */
	public RomContainerAmstrad(Console console, File file, String filename) throws IOException {
		super(console, file);
		this.filename=filename;
	}

	public void addVersion(RomVersion version) {
		versions.add(version);
		
		//FIXME 4 Amstrad: Only extract several if "Disk" inside versions, otherwise do as for 7z: take best version
		if(version.getErrorLevel()==0) {
			version.setBest(true);
		}
	}
}
