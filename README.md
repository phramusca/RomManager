# RomManager

Rom Manager allows filtering full rom sets and extract only good rom versions for your desired region.

## Process

### Scan Source

1) Browse source folder for roms (refer to [Roms Source folder](#roms-source-folder))
1) Creates (overwrites!) "RomManager.ods" output file

### Set Score

1) Set score of each rom version, based on [GoodToolsConfig.ods](#GoodToolsConfig) configuration.
1) Updates (overwrites!) "RomManager.ods" output file

### Export

Export all consoles, only best version of each rom and all good dsk (amstrad) files.

Next step is to change this into "Sync" and to be able to select which versions/games to export (best by default)

### Read gamelist.xml

Reads "gamelist.xml" from each destination subfolder (each console) and updates table. 

Note that "RomManager.ods" is NOT (yet) updated.

## Configuration

### <a name="roms-source-folder"></a>  Roms Source folder

Select folder containing roms. 

It must include subfolders:

* named as in [Supported consoles](#supported-consoles) list.
* containing 7z files (or .dsk files for Amstrad CPC (amstradcpc) only)

### Destination folder

* where to export selected roms
* where to read "gamelist.xml" files

### <a name="GoodToolsConfig"></a> GoodToolsConfig.ods

This configures how scores are computed. 

**French / Europe games favored by default. Change as desired !**

| Tab | Content |
| :--- |:---|
| Translation | Score by translation. |
| ALL | Score by code. Note: also include some language codes! |
| README | More information |

### <a name="supported-consoles"></a> Supported consoles

List of supported consoles can be found in "Console.java" 

Here is current list, knowing that "CHANGEME" console are NOT (yet) supported :

| Folder name | Console |
| :--- |:---|
| amiga1200 | Amiga 1200 |
| amiga600 | Amiga 600 |
| amstradcpc | Amstrad CPC |
| apple2 | Apple 2 |
| atari2600 | Atari 2600 |
| atari7800 | Atari 7800 |
| atarist | Atari ST |
| c64 | Commodore 64 |
| cavestory | Cave Story |
| colecovision | CHANGEME |
| daphne | CHANGEME |
| dos | DOS |
| dreamcast | Sega DreamCast |
| fba | CHANGEME |
| fba_libretro | CHANGEME |
| fds | CHANGEME |
| gamegear | Sega Game Gear |
| gb | Nintendo Game Boy |
| gba | Nintendo Game Boy Advance |
| gbc | Nintendo Game Boy Color |
| gw | Nintendo Game & Watch |
| imageviewer | CHANGEME |
| lutro | CHANGEME |
| lynx | Atari Lynx |
| mame | MAME (Arcade) |
| mastersystem | Sega Master System |
| megadrive | Sega Megadrive |
| moonlight | CHANGEME |
| msx | CHANGEME |
| msx1 | CHANGEME |
| msx2 | CHANGEME |
| n64 | Nintendo 64 |
| neogeo | SNK Neo Geo |
| nes | Nintendo Entertainment System |
| ngp | SNK Neo Geo Pocket |
| ngpc | SNK Neo Geo Pocket Color |
| o2em | CHANGEME |
| pcengine | NEC PC engine |
| pcenginecd | NEC PC engine CD |
| prboom | CHANGEME |
| psp | Sony PSP |
| psx | Sony PSX (PS1) |
| scummvm | CHANGEME |
| sega32x | Sega Mega Drive 32X |
| segacd | Sega Mega CD |
| sg1000 | CHANGEME |
| snes | Super Nintendo |
| supergrafx | NEC SuperGrafX |
| thomson | CHANGEME |
| vectrex | CHANGEME |
| virtualboy | Nintendo Virtual Boy |
| wswan | CHANGEME |
| wswanc | CHANGEME |
| x68000 | CHANGEME |
| zx81 | CHANGEME |
| zxspectrum | CHANGEME |
