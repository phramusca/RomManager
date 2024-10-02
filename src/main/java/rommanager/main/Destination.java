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
package rommanager.main;

/**
 *
 * @author raph
 */
public enum Destination {
	recalbox("Recalbox", false, false),
	romM("RomM", true, true);
    private final String name;
    
    private final boolean forceZip;
    private final boolean flat;

    private Destination(String name, boolean forceZip, boolean flat) {
        this.name = name;
        this.forceZip = forceZip;
        this.flat = flat;
    }

    public boolean forceZip() {
        return forceZip;
    }

    public boolean isFlat() {
        return flat;
    }

    public String getName() {
        return name;
    }

}
