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

import lombok.Data;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.List;

@Data
public class Platform {
    private int id;
    private String slug;
    private String fs_slug;
    private String name;
    private int rom_count;
    private int igdb_id;
    private int sgdb_id;
    private int moby_id;
    private String logo_path;
    private List<Firmware> firmware;
    private String created_at;
    private String updated_at;

    @Override
    public String toString() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
    }

    @Data
    public static class Firmware {
        private int id;
        private String file_name;
        private String file_name_no_tags;
        private String file_name_no_ext;
        private String file_extension;
        private String file_path;
        private long file_size_bytes;
        private String full_path;
        private boolean is_verified;
        private String crc_hash;
        private String md5_hash;
        private String sha1_hash;
        private String created_at;
        private String updated_at;

        @Override
        public String toString() {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            return gson.toJson(this);
        }
    }
}
