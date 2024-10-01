/*
 * Copyright (C) 2024 raph
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
package rommanager.romM;

import rommanager.romM.models.Rom;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rommanager.romM.models.Collection;
import rommanager.romM.models.Platform;

/**
 *
 * @author raph
 */
public class RomM {
    
    public static void main(String[] args) {
        try {
            RomMclient mclient = new RomMclient("192.168.1.12", "admin", "admin");
            
//            List<Collection> collections = mclient.getCollections();
//            System.out.println(collections);
            
//            List<Platform> platforms = mclient.getPlatforms();
//            System.out.println(platforms);
            
//            List<Rom> roms = mclient.getRoms(2);
//            System.out.println(roms);
            
//            Rom rom = mclient.getRom(190);
//            System.out.println(rom);
            
//            rom.getCollections().add("TOTO");
            
//            boolean b = mclient.putRom(rom);
//            System.out.println("putRom: " + b);
            
            Collection collection = new Collection();
            collection.setName("TUTU");
            collection.set_public(true);
            
            mclient.postCollection(collection);
            System.out.println("postCollection: " + collection);
            
            
        } catch (IOException | RomMclient.ServerException ex) {
            Logger.getLogger(RomM.class.getName()).log(Level.SEVERE, null, ex);
        }
       
    }

}
