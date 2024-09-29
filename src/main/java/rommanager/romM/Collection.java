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

import java.util.List;

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

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPath_cover_l() {
        return path_cover_l;
    }

    public void setPath_cover_l(String path_cover_l) {
        this.path_cover_l = path_cover_l;
    }

    public String getPath_cover_s() {
        return path_cover_s;
    }

    public void setPath_cover_s(String path_cover_s) {
        this.path_cover_s = path_cover_s;
    }

    public boolean isHas_cover() {
        return has_cover;
    }

    public void setHas_cover(boolean has_cover) {
        this.has_cover = has_cover;
    }

    public String getUrl_cover() {
        return url_cover;
    }

    public void setUrl_cover(String url_cover) {
        this.url_cover = url_cover;
    }

    public List<Integer> getRoms() {
        return roms;
    }

    public void setRoms(List<Integer> roms) {
        this.roms = roms;
    }

    public int getRom_count() {
        return rom_count;
    }

    public void setRom_count(int rom_count) {
        this.rom_count = rom_count;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUser__username() {
        return user__username;
    }

    public void setUser__username(String user__username) {
        this.user__username = user__username;
    }

    public boolean isIs_public() {
        return is_public;
    }

    public void setIs_public(boolean is_public) {
        this.is_public = is_public;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    @Override
    public String toString() {
        return "Collection{" + "id=" + id + ", name=" + name + ", description=" + description + ", path_cover_l=" + path_cover_l + ", path_cover_s=" + path_cover_s + ", has_cover=" + has_cover + ", url_cover=" + url_cover + ", roms=" + roms + ", rom_count=" + rom_count + ", user_id=" + user_id + ", user__username=" + user__username + ", is_public=" + is_public + ", created_at=" + created_at + ", updated_at=" + updated_at + '}';
    }

    
}
