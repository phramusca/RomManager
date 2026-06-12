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

public enum BiosPlatform {

    RECALBOX {
        @Override
        public String getRelativeDestPath(String path) {
            String marker = "/recalbox/share/bios/";
            int idx = path.indexOf(marker);
            if (idx >= 0) {
                return path.substring(idx + marker.length());
            }
            return path;
        }

        @Override
        public String getReferenceOptionsKey() {
            return "bios.recalbox.reportPath";
        }

        @Override
        public String getDestinationOptionsKey() {
            return "bios.recalbox.destinationPath";
        }
    },
    BATOCERA {
        @Override
        public String getRelativeDestPath(String path) {
            if (path.startsWith("bios/")) {
                return path.substring("bios/".length());
            }
            return path;
        }

        @Override
        public String getReferenceOptionsKey() {
            return "bios.batocera.readmePath";
        }

        @Override
        public String getDestinationOptionsKey() {
            return "bios.batocera.destinationPath";
        }
    };

    public static final String SOURCE_OPTIONS_KEY = "bios.sourcePath";

    public abstract String getRelativeDestPath(String path);

    public abstract String getReferenceOptionsKey();

    public abstract String getDestinationOptionsKey();
}
