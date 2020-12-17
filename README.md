# RomManager

## Features

- Scan full rom sets 
- Set score, based on your preferences
- Sync selected rom version(s) to given destination
- Read gamelist.xml to get game info (cover, name, description, ...)

## Process

At startup [RomManager.ods](#RomManager-ods) is read and displayed.

### Scan Source

1) Browse [Roms Source folder](#roms-source-folder) for roms.
1) Create a new revision of [RomManager.ods](#RomManager-ods) output file.

### Set Score

1. Set score of each rom version, based on [GoodToolsConfig.ods](#GoodToolsConfig) configuration.
1. Set exportable:
    * all good dsk (amstrad) files.
    * only best rom version (highest score) for other consoles.
1. Create a new revision of [RomManager.ods](#RomManager-ods) output file.

### Sync

Export selected rom versions to [Destination folder](#destination-folder), removing unwanted versions.

### Read gamelist.xml

Read [gamelist.xml](https://github.com/recalbox/recalbox-emulationstation/blob/master/GAMELISTS.md) from each destination subfolder (each console) and updates table. 

The [gamelist.xml](https://github.com/recalbox/recalbox-emulationstation/blob/master/GAMELISTS.md) file defines metadata for a system's games, such as a name, image (like a screenshot or box art), description, release date, and rating. 

Note that [RomManager.ods](#RomManager-ods) is NOT (yet) updated.

## Configuration

### <a name="roms-source-folder"></a>  Roms Source folder

Select folder containing roms. 

It must include subfolders:

* named as in [Supported consoles](#supported-consoles) list.
* containing 7z files (or .dsk files for Amstrad CPC (amstradcpc) only)

### <a name="destination-folder"></a> Destination folder

Select folder where to:
* export selected roms
* read [gamelist.xml](https://github.com/recalbox/recalbox-emulationstation/blob/master/GAMELISTS.md) files

### <a name="GoodToolsConfig"></a> GoodToolsConfig.ods

This configures how scores are computed. 

**French / Europe games favored by default. Change it as desired !**

| Tab | Content |
| :--- |:---|
| Translation | Score by translation. |
| ALL | Score by code. Note: also include some language codes! |
| README | More information |

## 

### <a name="RomManager-ods"></a> RomManager.ods

Output file, after "Scan Source" and "Set Score". 

Read at startup (cheap but convenient sort of database).

### <a name="supported-consoles"></a> Supported consoles

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
| dos | DOS |
| dreamcast | Sega DreamCast |
| gamegear | Sega Game Gear |
| gb | Nintendo Game Boy |
| gba | Nintendo Game Boy Advance |
| gbc | Nintendo Game Boy Color |
| gw | Nintendo Game & Watch |
| lynx | Atari Lynx |
| mame | MAME (Arcade) |
| mastersystem | Sega Master System |
| megadrive | Sega Megadrive |
| n64 | Nintendo 64 |
| neogeo | SNK Neo Geo |
| nes | Nintendo Entertainment System |
| ngp | SNK Neo Geo Pocket |
| ngpc | SNK Neo Geo Pocket Color |
| pcengine | NEC PC engine |
| pcenginecd | NEC PC engine CD |
| psp | Sony PSP |
| psx | Sony PSX (PS1) |
| sega32x | Sega Mega Drive 32X |
| segacd | Sega Mega CD |
| snes | Super Nintendo |
| supergrafx | NEC SuperGrafX |
| virtualboy | Nintendo Virtual Boy |
