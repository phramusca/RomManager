/*
 * Copyright (C) 2026 phramusca ( https://github.com/phramusca/RomManager/ )
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

package rommanager.bios;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BiosInfo {

    private final String name;
    private final String path;
    private final String notes;
    private final String forSystems;
    private final List<String> md5List;
    private final boolean isRequired;

    private BiosInfo(String name, String path, String notes, String forSystems, List<String> md5List, boolean isRequired) {
        this.name = name;
        this.path = path;
        this.notes = notes;
        this.forSystems = forSystems;
        this.md5List = md5List == null ? Collections.emptyList() : Collections.unmodifiableList(new ArrayList<>(md5List));
        this.isRequired = isRequired;
    }

    public List<String> getMd5List() {
        return md5List;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public String getNotes() {
        return notes;
    }

    public String getForSystems() {
        return forSystems;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public String getRelativeDestPath(BiosPlatform platform) {
        return platform.getRelativeDestPath(path);
    }

    public static class Builder {

        private String name;
        private String path;
        private String notes;
        private String forSystems;
        private List<String> md5List = new ArrayList<>();
        private boolean isRequired = true;

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setPath(String path) {
            this.path = path;
            return this;
        }

        public Builder setNotes(String notes) {
            this.notes = notes;
            return this;
        }

        public Builder setForSystems(String forSystems) {
            this.forSystems = forSystems;
            return this;
        }

        public Builder setMd5List(List<String> md5List) {
            this.md5List = md5List == null ? new ArrayList<>() : new ArrayList<>(md5List);
            return this;
        }

        public Builder addMd5(String md5) {
            if (md5 != null && !md5.isBlank()) {
                this.md5List.add(md5.trim().toLowerCase());
            }
            return this;
        }

        public Builder setIsRequired(boolean isRequired) {
            this.isRequired = isRequired;
            return this;
        }

        public BiosInfo build() {
            return new BiosInfo(name, path, notes, forSystems, md5List, isRequired);
        }

        List<String> getMd5List() {
            return md5List;
        }
    }

    @Override
    public String toString() {
        return "BiosInfo{"
                + "name='" + name + '\''
                + ", path='" + path + '\''
                + ", notes='" + notes + '\''
                + ", forSystems='" + forSystems + '\''
                + ", md5List=" + md5List
                + ", isRequired=" + isRequired
                + '}';
    }
}
