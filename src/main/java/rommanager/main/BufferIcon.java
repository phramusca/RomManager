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

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import org.apache.commons.io.FilenameUtils;
import rommanager.utils.Popup;
import rommanager.utils.StringManager;

/**
 *
 * @author phramusca ( https://github.com/phramusca/JaMuz/ )
 */


public class BufferIcon {
    private static final Map<String, ImageIcon> ICONS = new HashMap<>();

    /**
     * Icon height.
     * Height set to this value
     */
    public static final int ICON_HEIGHT = 140;

    /**
     * Icon width.
     * Width is set auto based on image ratio, this value is to set column width
     */
    public static final int ICON_WIDTH = 140;
    
    /**
     * Get cover icon from cache if exists, from internet if not
	 * @param key
     * @param file
     * @return
     */
    public static boolean checkOrGetCoverIcon(String key, String file) {
        if(ICONS.containsKey(key)) {
            return true;
        } else {
            File cacheFile = getCacheFile(key);
            if(cacheFile.exists()) {
                ICONS.put(key, null);
                return true;
            }
            else if(!file.equals("")) {
                ImageIcon icon = readIcon(key, file);
                if(icon != null) {
                    ICONS.put(key, null);
                    icon = null; //to avoid errors OutOfMemoryError: Java heap space
                    return true;
                }
            }
        }
        return false;
	}
    
    public static ImageIcon getCoverIcon(String key) {
        if(ICONS.containsKey(key)) {
            return readIconFromCache(key);
        }
        return null;
    }
 
    //TODO: Offer at least a cache cleanup function (better would be a smart auto cleanup)
    private static ImageIcon readIconFromCache(String key) {
        try {
            File file = getCacheFile(key);
            if(file.exists()) {
                return new ImageIcon(ImageIO.read(file));
            }
            return null;
        } catch (IOException ex) {
            Popup.error(ex);
            return null;
        }
    }
    
    private static File getCacheFile(String key) {
        return new File(FilenameUtils.concat(FilenameUtils.concat("cache", "images"), StringManager.removeIllegal(key)+".png")); //, "data", "cache", "book");
    }

    private static ImageIcon readIcon(String key, String file) {
        ImageIcon icon=null;
        try {
            File iconFile = new File(file);
			if(!iconFile.exists()) {
                return icon;
            }
			
			BufferedImage myImage = ImageIO.read(iconFile);
            icon = new ImageIcon(((new ImageIcon(myImage).getImage()).getScaledInstance(-1, BufferIcon.ICON_HEIGHT, java.awt.Image.SCALE_SMOOTH)));
            
            //Write to cache
            BufferedImage bi = new BufferedImage(icon.getImage().getWidth(null),icon.getImage().getHeight(null),BufferedImage.TYPE_3BYTE_BGR);
            Graphics2D g2 = bi.createGraphics();
            g2.drawImage(icon.getImage(), 0, 0, null);
            g2.dispose();
            ImageIO.write(bi, "png", getCacheFile(key)); //NOI18N
		} catch (IIOException ex) {
            Logger.getLogger(BufferIcon.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException | NullPointerException ex) {
			Logger.getLogger(BufferIcon.class.getName()).log(Level.SEVERE, null, ex);
		}
        return icon;
    }
}
