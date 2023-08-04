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
import java.util.LinkedHashSet;
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
            LinkedHashSet<Console> moveTo = new LinkedHashSet<>();
            while(entry!=null){
                name=entry.getName();
                progressBar.setString(msg+" : "+name);
                RomVersion romVersion = new RomVersion(
                        FilenameUtils.getBaseName(filename), 
                        name,
                        console);
                String ext = FilenameUtils.getExtension(name);
                switch (ext) {
                    case "gb":
                        moveTo.add(Console.gb);
                        break;
                    case "gbc":
                        moveTo.add(Console.gbc);
                        break;
                    case "ws":
                        moveTo.add(Console.wswan);
                        break;
                    case "wsc":
                        moveTo.add(Console.wswanc);
                        break;
                }
                versions.add(romVersion);
                entry = sevenZFile.getNextEntry();
            }
            if(!moveTo.isEmpty()) {
                Console moveToConsole = moveTo.stream().findFirst().get();
                if(moveTo.size()>1) {
                    //Prefer the color version //TODO: Offer the choice. Sometimes color is not the best choice (japan only, hack,...)
                    if(moveTo.contains(Console.gb) && moveTo.contains(Console.gbc)) {
                        moveToConsole = Console.gbc;
                    }
                    else if(moveTo.contains(Console.wswan) && moveTo.contains(Console.wswanc)) {
                        moveToConsole = Console.wswanc;
                    }
                    else {
                        Popup.warning("Bad moveTo"); //FIXME 9
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