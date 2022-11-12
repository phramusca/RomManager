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
	// 3do(120, "Panasonic 3DO"),  //FIXME 5 Cannot start with a number, but that is folder name :(
	amiga1200(130, "Amiga 1200"),
	amiga600(130, "Amiga 600"),
	amstradcpc(140, "Amstrad CPC"),
	apple2(-1, "Apple 2"),
	atari2600(-1, "Atari 2600", true, false),
    atari5200(-1, "Atari 5200", false, false),
	atari7800(-1, "Atari 7800", true, false),
	atarist(160, "Atari ST"),
	c64(180, "Commodore 64"),
	cavestory(-1, "Cave Story"),
	colecovision(-1, "ColecoVision"),
	daphne(-1, "Daphne (Arcade / Laserdisc)"),
	dos(-1, "DOS"),
	dreamcast(190, "Sega DreamCast"),
	fba(-1, "Final Burn Alpha (Arcade)"),
	fba_libretro(-1, "Final Burn Alpha (Arcade / Libretro)"),
	fds(-1, "Nintendo Famicom Disk System"),
	gamecube(220, "Nintendo GameCube"),
	gamegear(230, "Sega Game Gear"),
	gb(200, "Nintendo Game Boy"),
	gba(210, "Nintendo Game Boy Advance"),
	gbc(200, "Nintendo Game Boy Color"),
	gw(-1, "Nintendo Game & Watch"),
	//imageviewer("Visionneur screenshot (PAS une console)"),
    jaguar(-1, "Atari Jaguar", false, false),
	//lutro("Libreto lua games (PAS une console)"),
	lynx(-1, "Atari Lynx", false ,false),
	mame(-1, "MAME (Arcade)"),
	mastersystem(290, "Sega Master System"),
	megadrive(300, "Sega Megadrive"),
	//moonlight("Streaming de jeu (PAS une console)"),
	msx(-1, "Machines with Software eXchangeability"),
	msx1(-1, "Machines with Software eXchangeability"),
	msx2(-1, "Machines with Software eXchangeability"),
	n64(-1, "Nintendo 64", false, true),
	nds(70, "CHANGEME"),
	neogeo(340, "SNK Neo Geo"),
	nes(360, "Nintendo Entertainment System"),
	ngp(-1, "SNK Neo Geo Pocket"),
	ngpc(-1, "SNK Neo Geo Pocket Color"),
	o2em(-1, "Odyssey2 / VideoPac"),
	pcengine(440, "NEC PC engine"),
	pcenginecd(-1, "NEC PC engine CD"),
	//prboom("DOOM 1 et 2 (JEU uniquement)"),
	psp(410, "Sony PSP"),
	psx(390, "Sony PSX (PS1)"),
    saturn(420, "Sega Saturn"),
	//scummvm("Moteur de jeu LucasArts (PAS une console)"),
	sega32x(-1, "Sega Mega Drive 32X"),
	segacd(320, "Sega Mega CD"),
	sg1000(-1, "Sega Game 1000 (SG-1000)"),
	snes(430, "Nintendo Super Nintendo"),      
	supergrafx(-1, "NEC SuperGrafX"),
	thomson(-1, "Thomson TO8"),
	vectrex(-1, "Vectrex"),
	virtualboy(-1, "Nintendo Virtual Boy"),
	wii(460, "Nintendo Wii"),
	wswan(-1, "Bandai WonderSwan", true, false),
	wswanc(-1, "Bandai WonderSwan Color", true, false),
	x68000(-1, "Sharp X68000 (L’Arcade à la maison)"),
	zx81(-1, "ZX81 (Ordinateur personnel)"),
	zxspectrum(-1, "ZX Spectrum (Ordinateur personnel)");
    
    private final int idJeuxVideo;
    private final String name;
	private int nbFiles;
	private boolean isSelected;
	private boolean zip;
    private boolean excludeUnknownAttributes;

	private Console(int idJeuxVideo, String name) {
		this(idJeuxVideo, name, true, true);
	}

	private Console(int idJeuxVideo, String name, boolean zip, boolean excludeUnknownAttributes) {
        this.idJeuxVideo = idJeuxVideo;
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

    public int getIdJeuxVideo() {
        return idJeuxVideo;
    }
    
	@Override
	public String toString() {
		return getName()+" ("+getNbFiles()+")";
	}
}
