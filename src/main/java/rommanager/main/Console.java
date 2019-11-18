/* 
 * Copyright (C) 2018 phramusca ( https://github.com/phramusca/ )
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
 * @author phramusca ( https://github.com/phramusca/JaMuz/ )
 */
public enum Console {

		amiga1200("Amiga 1200"),
		amiga600("Amiga 600"),
		amstradcpc("Amstrad CPC"),
		apple2("Apple 2"),
		atari2600("Atari 2600"),
		atari7800("Atari 7800"),
		atarist("Atari ST"),
		c64("Commodore 64"),
		cavestory("Cave Story"),
		colecovision("CHANGEME"),
		daphne("CHANGEME"),
		dos("DOS"),
		dreamcast("Sega DreamCast"),
		fba("CHANGEME"),
		fba_libretro("CHANGEME"),
		fds("CHANGEME"),
		gamegear("Sega Game Gear"),
		gb("Nintendo Game Boy"),
		gba("Nintendo Game Boy Advance"),
		gbc("Nintendo Game Boy Color"),
		gw("Nintendo Game & Watch"),
		imageviewer("CHANGEME"),
		lutro("CHANGEME"),
		lynx("Atari Lynx"),
		mame("MAME (Arcade)"),
		mastersystem("Sega Master System"),
		megadrive("Sega Megadrive"),
		moonlight("CHANGEME"),
		msx("CHANGEME"),
		msx1("CHANGEME"),
		msx2("CHANGEME"),
		n64("Nintendo 64"),
		neogeo("SNK Neo Geo"),
		nes("Nintendo Entertainment System"),
		ngp("SNK Neo Geo Pocket"),
		ngpc("SNK Neo Geo Pocket Color"),
		o2em("CHANGEME"),
		pcengine("NEC PC engine"),
		pcenginecd("NEC PC engine CD"),
		prboom("CHANGEME"),
		psp("Sony PSP"),
		psx("Sony PSX (PS1)"),
		scummvm("CHANGEME"),
		sega32x("Sega Mega Drive 32X"),
		segacd("Sega Mega CD"),
		sg1000("CHANGEME"),
		snes("Super Nintendo"),      
		supergrafx("NEC SuperGrafX"),
		thomson("CHANGEME"),
		vectrex("CHANGEME"),
		virtualboy("Nintendo Virtual Boy"),
		wswan("CHANGEME"),
		wswanc("CHANGEME"),
		x68000("CHANGEME"),
		zx81("CHANGEME"),
		zxspectrum("CHANGEME");
		
        private final String name;
		private int nbFiles;
		private boolean isSelected;
		
        private Console(String name) {
            this.name = name;
		}
		
		public void setNbFiles(int nbFiles) {
			this.nbFiles = nbFiles;
		}

		public void setSelected(boolean selected) {
			isSelected=selected;
		}
		
		public int getNbFiles() {
			return nbFiles;
		}
		
		public String getName() {
			return name;
		}
		
        @Override
		public String toString() {
			return getName()+" ("+getNbFiles()+")";
		}

		boolean isSelected() {
			return isSelected;
		}
	}
