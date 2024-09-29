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

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author raph
 */
public class RomM {
    
    public static void main(String[] args) {
        try {
            RomMclient mclient = new RomMclient("admin", "admin");
            List<Collection> collections = mclient.getCollections();
            collections.toString();
        } catch (IOException ex) {
            Logger.getLogger(RomM.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RomMclient.ServerException ex) {
            Logger.getLogger(RomM.class.getName()).log(Level.SEVERE, null, ex);
        }
       
    }

}
