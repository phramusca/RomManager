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
package rommanager.romM.models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Data;
import java.util.List;

@Data
public class Collection {

    private int id;
    private String name;
    private String description;
    private String path_cover_l;
    private String path_cover_s;
    private boolean has_cover;
    private String url_cover;
    private List<Integer> roms;
    private int rom_count;
    private int user_id;
    private String user__username;
    private boolean is_public;
    private String created_at;
    private String updated_at;
    
    @Override
    public String toString() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
    }
}
