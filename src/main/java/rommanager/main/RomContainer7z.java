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
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.io.FilenameUtils;
import rommanager.utils.ProgressBar;

/**
 *
 * @author phramusca ( https://github.com/phramusca/JaMuz/ )
 */
public class RomContainer7z extends RomContainer {

    /**
     * For 7z files including rom versions
     * @param console
     * @param filename
     * @throws IOException
     */
    public RomContainer7z(Console console, String filename) throws IOException {
        super(console, filename);
    }

    public void setVersions(ProgressBar progressBar, String path) throws IOException, OutOfMemoryError {
        try (SevenZFile sevenZFile = new SevenZFile(new File(FilenameUtils.concat(path, filename)))) {
            SevenZArchiveEntry entry = sevenZFile.getNextEntry();
            String name;
            String msg = progressBar.getString();
            while(entry!=null){
                name=entry.getName();
                progressBar.setString(msg+" : "+name);
                versions.add(new RomVersion(
                        FilenameUtils.getBaseName(filename), 
                        name,
                        console));
                entry = sevenZFile.getNextEntry();
            }
        }
		setBestExportable();
	}
	
	@Override
	public final void setBestExportable() {
		int bestScore=Integer.MIN_VALUE;
		RomVersion bestVersion=null;
		for(RomVersion version : versions) {
			version.setExportable(false);
			if(version.getScore()>bestScore) {
				bestVersion=version;
				bestScore=version.getScore();
			} 
		}
		if(bestVersion!=null && bestVersion.getScore()>=0) {
			bestVersion.setExportable(true);
		}
	}
}