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
import org.apache.commons.io.FilenameUtils;
import rommanager.utils.FileSystem;
import rommanager.utils.StringManager;

/**
 *
 * @author phramusca ( https://github.com/phramusca/JaMuz/ )
 */


public class BufferVideo {
    private static final Map<String, File> VIDEOS = new HashMap<>();

    /**
     * Get video File from cache if exists, from internet if not
	 * @param key
     * @param file
     * @return
     */
    public static File getCacheFile(String key, File file) {
        File cacheFile = null;
        if(VIDEOS.containsKey(key)) {
            cacheFile = VIDEOS.get(key);
        } else {
            try {
                //TODO: Offer at least a cache cleanup function (better would be a smart auto cleanup)
                cacheFile = new File(FilenameUtils.concat("cache", FilenameUtils.concat("video", StringManager.removeIllegal(key)+".mp4")));
                if(!cacheFile.exists()) {
                    if(file.exists() && file.isFile()) {
                        FileSystem.copyFile(file, cacheFile);
                    } else {
                        return null;
                    }
                }
                if(cacheFile.exists()) {
                    VIDEOS.put(key, cacheFile);
                }
            } catch (IOException ex) {
                return null;
            }
        }
        return cacheFile;
	}
}
