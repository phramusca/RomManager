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
	// 3do(120, "", "Panasonic 3DO", "3do"),  //FIXME 5 Cannot start with a number, but that is folder name :(
	amiga1200(130, " sur Amiga", "Amiga 1200", "amiga"),
	amiga600(130, " sur Amiga", "Amiga 600", "amiga"),
	amstradcpc(140, " sur CPC", "Amstrad CPC", "acpc"),
	apple2(-1, "", "Apple 2", "appleii"),
	atari2600(-1, "", "Atari 2600", "atari2600", true, false),
    atari5200(-1, "", "Atari 5200", "atari5200", true, false),
	atari7800(-1, "", "Atari 7800", "atari7800", true, false),
	atarist(160, " sur ST", "Atari ST", "atari-st"),
	c64(180, " sur C64", "Commodore 64", "c64"),
	cavestory(-1, "", "Cave Story", "cavestory-NA"),
	colecovision(-1, "", "ColecoVision", "colecovision"),
	daphne(-1, "", "Daphne (Arcade / Laserdisc)", "daphne-NA"),
	dos(-1, "", "DOS", "dos"),
	dreamcast(190, " sur DCAST", "Sega DreamCast", "dc"),
	fba(-1, "", "Final Burn Alpha (Arcade)", "fba-NA"),
	fba_libretro(-1, "", "Final Burn Alpha (Arcade / Libretro)", "fba_libretro-NA"),
	fds(-1, "", "Nintendo Famicom Disk System", "fds"),
	gamecube(220, " sur NGC", "Nintendo GameCube", "ngc"),
	gamegear(230, " sur G.GEAR", "Sega Game Gear", "gamegear"),
	gb(200, " sur GB", "Nintendo Game Boy", "gb", false, true),
	gba(210, " sur GBA", "Nintendo Game Boy Advance", "gba"),
	gbc(200, " sur GB", "Nintendo Game Boy Color", "gbc"),
	gw(-1, "", "Nintendo Game & Watch", "g-and-w"),
	//imageviewer("Visionneur screenshot (PAS une console)"),
    jaguar(-1, "", "Atari Jaguar", "jaguar", true, false),
	//lutro("Libreto lua games (PAS une console)"),
	lynx(-1, "", "Atari Lynx", "lynx", true ,false),
	mame(-1, "", "MAME (Arcade)", "arcade"),
	mastersystem(290, " sur MS", "Sega Master System", "sms"),
	megadrive(300, " sur MD", "Sega Megadrive", "genesis-slash-megadrive"),
	//moonlight("Streaming de jeu (PAS une console)"),
	msx(-1, "", "Machines with Software eXchangeability", "msx"),
	msx1(-1, "", "Machines with Software eXchangeability", "msx"),
	msx2(-1, "", "Machines with Software eXchangeability", "msx2"),
	n64(-1, "", "Nintendo 64", "n64", false, true),
	nds(380, " sur DS", "Nintendo DS", "nds"),
	neogeo(340, " sur NEO", "SNK Neo Geo", "neogeoaes"),
	nes(360, " sur Nes", "Nintendo Entertainment System", "nes"),
	ngp(-1, "", "SNK Neo Geo Pocket", "neo-geo-pocket"),
	ngpc(-1, "", "SNK Neo Geo Pocket Color", "neo-geo-pocket-color"),
	o2em(-1, "", "Odyssey2 / VideoPac", "odyssey-2-slash-videopac-g7000"),
	pcengine(440, " sur PC ENG", "NEC PC engine", "turbografx16--1"),
	pcenginecd(-1, "", "NEC PC engine CD", "turbografx-16-slash-pc-engine-cd"),
	//prboom("DOOM 1 et 2 (JEU uniquement)"),
	psp(410, " sur PSP", "Sony PSP", "psp"),
	psx(390, " sur PS1", "Sony PSX (PS1)", "ps"),
    saturn(420, " sur Saturn", "Sega Saturn", "saturn"),
	//scummvm("Moteur de jeu LucasArts (PAS une console)"),
	sega32x(-1, "", "Sega Mega Drive 32X", "sega32"),
	segacd(320, " sur Mega-CD", "Sega Mega CD", "segacd"),
	sg1000(-1, "", "Sega Game 1000 (SG-1000)", "spectravideo"),
	snes(430, " sur SNES", "Nintendo Super Nintendo", "snes"),      
	supergrafx(-1, "", "NEC SuperGrafX", "supergrafx"),
	thomson(-1, "", "Thomson TO8", "thomson-to"),
	vectrex(-1, "", "Vectrex", "vectrex"),
	virtualboy(-1, "", "Nintendo Virtual Boy", "virtualboy", true, false),
	wii(460, " sur Wii", "Nintendo Wii", "wii"),
	wswan(-1, "", "Bandai WonderSwan", "wonderswan", true, false),
	wswanc(-1, "", "Bandai WonderSwan Color", "wonderswan-color", true, false),
	x68000(-1, "", "Sharp X68000 (L’Arcade à la maison)", "sharp-x68000"),
	zx81(-1, "", "ZX81 (Ordinateur personnel)", "sinclair-zx81"),
	zxspectrum(-1, "", "ZX Spectrum (Ordinateur personnel)", "zxs");
    
    private final int idJeuxVideo;
    private final String suffixJeuxVideo;
    private final String name;
	private int nbFiles;
	private boolean isSelected;
    private final String romM;
	private boolean zip;
    private boolean excludeUnknownAttributes;

	private Console(int idJeuxVideo, String suffixJeuxVideo, String name, String romM) {
		this(idJeuxVideo, suffixJeuxVideo, name, romM, true, true);
	}

	private Console(int idJeuxVideo, String suffixJeuxVideo, String name, String romM, boolean zip, boolean excludeUnknownAttributes) {
        this.idJeuxVideo = idJeuxVideo;
        this.suffixJeuxVideo = suffixJeuxVideo;
		this.name = name;
        this.romM = romM;
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

    public int getIdJeuxVideo() {
        return idJeuxVideo;
    }

    public String getSuffixJeuxVideo() {
        return suffixJeuxVideo;
    }

    public String getRomM() {
        return romM;
    }
    
	@Override
	public String toString() {
		return getName()+" ("+getNbFiles()+")";
	}
}
