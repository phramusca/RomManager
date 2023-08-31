/*
 * Copyright (C) 2023 raph
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
public class JdgEntry {

    private final String container;
    private final String console;
    private final String tag;

    public JdgEntry(String container, String console, String tag) {
        this.container = container;
        this.console = console;
        this.tag = tag;
    }

    public String getContainer() {
        return container;
    }

    public String getConsole() {
        return console;
    }

    public String getTag() {
        return tag;
    }
    
    
}
