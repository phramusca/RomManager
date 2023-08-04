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
import java.util.stream.Collectors;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.io.FilenameUtils;
import rommanager.utils.FileSystem;
import rommanager.utils.Popup;
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
        File sourceFile = new File(FilenameUtils.concat(path, filename));
        try (SevenZFile sevenZFile = new SevenZFile(sourceFile)) {
            SevenZArchiveEntry entry = sevenZFile.getNextEntry();
            String name;
            String msg = progressBar.getString();
            List<Console> moveTo = new ArrayList<>();
            while(entry!=null){
                name=entry.getName();
                progressBar.setString(msg+" : "+name);
                RomVersion romVersion = new RomVersion(
                        FilenameUtils.getBaseName(filename), 
                        name,
                        console);
                //TODO: What if moveTo differs from on entry to another in the same zip ?
                if(romVersion.getMoveTo() != null) {
                    moveTo.add(romVersion.getMoveTo());
                }
                versions.add(romVersion);
                entry = sevenZFile.getNextEntry();
            }
            moveTo = moveTo.stream().distinct().collect(Collectors.toList());
            if(!moveTo.isEmpty()) {
                Console moveToConsole = moveTo.get(0);
                if(moveTo.size()>1) {
                    //Prefer the color version
                    if(moveTo.contains(Console.gb) && moveTo.contains(Console.gbc)) {
                        moveToConsole = Console.gbc;
                    }
                    else if(moveTo.contains(Console.wswan) && moveTo.contains(Console.wswanc)) {
                        moveToConsole = Console.wswanc;
                    }
                    else {
                        Popup.warning("Bad moveTo"); //FIXME
                    }
                }
                if(!console.equals(moveToConsole)) {
                    File destFile = new File(sourceFile.getAbsolutePath().replace("/"+console.name()+"/", "/"+moveToConsole.name()+"/"));
                    if(FileSystem.moveFile(sourceFile, destFile)) {
                        console = moveToConsole;
                    }
                }
            }
        }
		setExportableVersions();
	}
	
	@Override
	public final void setExportableVersions() {
		setBestExportable();
	}
}