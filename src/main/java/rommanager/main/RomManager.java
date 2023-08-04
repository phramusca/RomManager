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
import rommanager.utils.Options;
import rommanager.utils.Popup;

/**
 *
 * @author phramusca ( https://github.com/phramusca/JaMuz/ )
 */
public class RomManager {

    protected static Options options;
    protected static final String TAG_JEUX_VIDEO = "Culte JeuxVideo.com";
    
    public static void main(String[] args) {
        options = new Options("RomManager.properties");
        options.read();
        File cachePath = new File("cache");
        if(!cachePath.exists() && !cachePath.mkdirs()) {
            Popup.error("Error creating cache folder.");
            System.exit(1);
        }
        RomManagerGUI.main(args);
    }
}
