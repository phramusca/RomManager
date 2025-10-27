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
 * Result of comparing two Game objects during synchronization
 * 
 * @author raph
 */
public class GameComparisonResult {
    private final Game game;
    private final boolean hasChanged;
    
    public GameComparisonResult(Game game, boolean hasChanged) {
        this.game = game;
        this.hasChanged = hasChanged;
    }
    
    public Game getGame() { 
        return game; 
    }
    
    public boolean hasChanged() { 
        return hasChanged; 
    }
}
