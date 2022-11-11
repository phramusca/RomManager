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
	// 3do("Panasonic 3DO"),  //FIXME 5 Cannot start with a number, but that is folder name :(
	amiga1200("Amiga 1200"),
	amiga600("Amiga 600"),
	amstradcpc("Amstrad CPC"),
	apple2("Apple 2"),
	atari2600("Atari 2600", true, false),
    atari5200("Atari 5200", false, false),
	atari7800("Atari 7800", true, false),
	atarist("Atari ST"),
	c64("Commodore 64"),
	cavestory("Cave Story"),
	colecovision("ColecoVision"),
	daphne("Daphne (Arcade / Laserdisc)"),
	dos("DOS"),
	dreamcast("Sega DreamCast"),
	fba("Final Burn Alpha (Arcade)"),
	fba_libretro("Final Burn Alpha (Arcade / Libretro)"),
	fds("Nintendo Famicom Disk System"),
	gamecube("Nintendo GameCube"),
	gamegear("Sega Game Gear"),
	gb("Nintendo Game Boy"),
	gba("Nintendo Game Boy Advance"),
	gbc("Nintendo Game Boy Color"),
	gw("Nintendo Game & Watch"),
	//imageviewer("Visionneur screenshot (PAS une console)"),
    jaguar("Atari Jaguar", false, false),
	//lutro("Libreto lua games (PAS une console)"),
	lynx("Atari Lynx", false ,false),
	mame("MAME (Arcade)"),
	mastersystem("Sega Master System"),
	megadrive("Sega Megadrive"),
	//moonlight("Streaming de jeu (PAS une console)"),
	msx("Machines with Software eXchangeability"),
	msx1("Machines with Software eXchangeability"),
	msx2("Machines with Software eXchangeability"),
	n64("Nintendo 64", false, true),
	nds("CHANGEME"),
	neogeo("SNK Neo Geo"),
	nes("Nintendo Entertainment System"),
	ngp("SNK Neo Geo Pocket"),
	ngpc("SNK Neo Geo Pocket Color"),
	o2em("Odyssey2 / VideoPac"),
	pcengine("NEC PC engine"),
	pcenginecd("NEC PC engine CD"),
	//prboom("DOOM 1 et 2 (JEU uniquement)"),
	psp("Sony PSP"),
	psx("Sony PSX (PS1)"),
	//scummvm("Moteur de jeu LucasArts (PAS une console)"),
	sega32x("Sega Mega Drive 32X"),
	segacd("Sega Mega CD"),
	sg1000("Sega Game 1000 (SG-1000)"),
	snes("Nintendo Super Nintendo"),      
	supergrafx("NEC SuperGrafX"),
	thomson("Thomson TO8"),
	vectrex("Vectrex"),
	virtualboy("Nintendo Virtual Boy"),
	wii("Nintendo Wii"),
	wswan("Bandai WonderSwan", true, false),
	wswanc("Bandai WonderSwan Color", true, false),
	x68000("Sharp X68000 (L’Arcade à la maison)"),
	zx81("ZX81 (Ordinateur personnel)"),
	zxspectrum("ZX Spectrum (Ordinateur personnel)");

	private final String name;
	private int nbFiles;
	private boolean isSelected;
	private boolean zip;
    private boolean excludeUnknownAttributes;

	private Console(String name) {
		this(name, true, true);
	}

	private Console(String name, boolean zip, boolean excludeUnknownAttributes) {
		this.name = name;
		this.zip = zip;
        this.excludeUnknownAttributes = excludeUnknownAttributes;
	}

	public void setNbFiles(int nbFiles) {
		this.nbFiles = nbFiles;
	}

	public int getNbFiles() {
		return nbFiles;
	}

	public void setSelected(boolean selected) {
		isSelected=selected;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public String getName() {
		return name;
	}

	public boolean isZip() {
		return zip;
	}
    
    public boolean excludeUnknownAttributes() {
		return excludeUnknownAttributes;
	}

	@Override
	public String toString() {
		return getName()+" ("+getNbFiles()+")";
	}
}
