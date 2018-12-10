/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
		nes("CHANGEME"),
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
		
        private Console(String name) {
            this.name = name;
		}

        @Override
		public String toString() {
			return name;
		}
	}
