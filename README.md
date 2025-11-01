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
    - all good dsk (amstrad) files.
    - only best rom version (highest score) for other consoles.
1. Create a new revision of [RomManager.ods](#RomManager-ods) output file.

### Sync

Export selected rom versions to [Destination folder](#destination-folder), removing unwanted versions.

### Sync Recalbox

Recalbox stores game data into `gamelist.xml` files.

During "Sync Game Data", we read `gamelist.xml` from each destination subfolder (each console) and sync data from/to recalbox.

The `gamelist.xml` file defines metadata for a system's games, such as a name, image (like a screenshot or box art), description, release date, and rating. References:
- https://gitlab.com/recalbox/recalbox/-/blob/master/projects/frontend/es-app/src/games/MetadataDescriptor.cpp
- https://gitlab.com/recalbox/recalbox/-/blob/master/projects/frontend/es-app/src/games/MetadataDescriptor.h

Règle fusion:
- "Recalbox": lecture seule recalbox vers RomManager
- "Plus récent": prend celui modifié le plus récemment. Fallback: "Recalbox"
- "-": N/A, non lu

| Champ XML          | Type Java | Type champ     | Lecture XML | Écriture XML | Mod. RomManager | Modif Recalbox  | Règle fusion | Utilisation                      |
| ------------------ | --------- | -------------- | ----------- | ------------ | --------------- | --------------- | ------------ | -------------------------------- |
| path               | String    | File info      | ✅          | ❌           | ❌              | ❌              | Recalbox     | Chemin du fichier ROM            |
| hash               | String    | File info      | ✅          | ❌           | ❌              | ❌              | Recalbox     | Hash CRC32 du ROM                |
| playcount          | int       | User Stats     | ✅          | ❌           | ❌              | ❌              | Recalbox     | Nombre de parties jouées         |
| lastplayed         | String    | User Stats     | ✅          | ❌           | ❌              | ❌              | Recalbox     | Dernière fois joué               |
| timeplayed         | int       | User Stats     | ✅          | ❌           | ❌              | ❌              | Recalbox     | Temps total de jeu (en secondes) |
| favorite           | boolean   | User           | ✅          | ✅           | ✅              | ✅              | Plus récent  | Jeu favori                       |
| hidden             | boolean   | User           | ✅          | ✅           | ✅              | ✅              | Plus récent  | Jeu caché                        |
| adult              | boolean   | Scrappé / User | ✅          | ✅           | ✅              | ✅              | Plus récent  | Jeu adulte                       |
| name               | String    | Scrappé / User | ✅          | ✅           | ✅              | ✅              | Plus récent  | Nom du jeu                       |
| desc               | String    | Scrappé        | ✅          | ❌           | ❌              | ✅              | Recalbox     | Description                      |
| rating             | float     | Scrappé        | ✅          | ❌           | ❌              | ✅              | Recalbox     | Note/évaluation                  |
| image              | String    | Scrappé        | ✅          | ❌           | ❌              | ❌              | Recalbox     | Chemin de l'image                |
| thumbnail          | String    | Scrappé        | ✅          | ❌           | ❌              | ❌              | Recalbox     | Chemin du thumbnail              |
| video              | String    | Scrappé        | ✅          | ❌           | ❌              | ❌              | Recalbox     | Chemin de la vidéo               |
| releasedate        | String    | Scrappé        | ✅          | ❌           | ❌              | ❌              | Recalbox     | Date de sortie                   |
| developer          | String    | Scrappé        | ✅          | ❌           | ❌              | ❌              | Recalbox     | Développeur                      |
| publisher          | String    | Scrappé        | ✅          | ❌           | ❌              | ❌              | Recalbox     | Éditeur                          |
| genre              | String    | Scrappé        | ✅          | ❌           | ❌              | ❌              | Recalbox     | Genre                            |
| genreid            | String    | Scrappé        | ✅          | ❌           | ❌              | ✅ (ou genre ?) | Recalbox     | ID du genre                      |
| players            | String    | Scrappé        | ✅          | ❌           | ❌              | ❌              | Recalbox     | Nombre de joueurs                |
| region             | String    | Scrappé        | ✅          | ❌           | ❌              | ❌              | Recalbox     | Région                           |
| ratio              | String    | Scrappé        | ✅          | ❌           | ❌              | ✅ (marche ?)   | Recalbox     | Ratio d'écran                    |
| emulator           | -         |                | ❌          | ❌           | ❌              | ✅              | -            | Émulateur                        |
| core               | -         |                | ❌          | ❌           | ❌              | ✅              | -            | Core de l'émulateur              |
| rotation           | -         |                | ❌          | ❌           | ❌              | ✅              | -            | Rotation de l'écran              |
| lastPatch          | -         |                | ❌          | ❌           | ❌              | ???             | -            | Dernier patch appliqué           |
| lightgunluminosity | -         |                | ❌          | ❌           | ❌              | ???             | -            | Luminosité du lightgun           |
| aliases            | -         |                | ❌          | ❌           | ❌              | ???             | -            | Alias du jeu                     |
| licences           | -         |                | ❌          | ❌           | ❌              | ???             | -            | Licences                         |
| timestamp          | long      | Scrap info     | ✅          | ❌           | ❌              | ❌              | Recalbox     | Timestamp du scrap (attribut).   |
| source             | -         | Scrap info     | ❌          | ❌           | ❌              | ❌              | -            | Toujours "Recalbox" (attribut).  |

## Configuration

### <a name="roms-source-folder"></a>  Roms Source folder

Select folder containing roms.

It must include subfolders:

- named as in [Supported consoles](#supported-consoles) list.
- containing 7z files (or .dsk files for Amstrad CPC (amstradcpc) only)

### <a name="destination-folder"></a> Destination folder

Select folder where to:

- export selected roms
- read `gamelist.xml` files

### <a name="GoodToolsConfig"></a> GoodToolsConfig.ods

This configures how scores are computed.

**French / Europe games favored by default. Change it as desired !**

| Tab | Content |
| :--- |:---|
| Translation | Score by translation. |
| ALL | Score by code. Note: also include some language codes! |
| README | More information |

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
| atari5200 | Atari 5200 |
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
| jaguar | Atari Jaguar |
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

## SSH Configuration for Recalbox

RomManager can stop/restart EmulationStation on a remote Recalbox during gamelist synchronization. Two SSH authentication modes are supported:

### SSH Key Authentication (recommended)

1. Generate an SSH key on the machine running RomManager:

   ```bash
   ssh-keygen
   ```

2. Copy the public key to the Recalbox:

   ```bash
   ssh-copy-id root@recalbox.local
   ```

3. In `RomManager.properties`, configure:

   ```properties
   romset.recalbox.ssh.key=~/.ssh/id_rsa
   # Or leave empty to use the default key
   ```

### Password Authentication (for testing)

1. Install `sshpass` on the machine running RomManager:

   ```bash
   sudo apt update && sudo apt install sshpass
   ```

2. In `RomManager.properties`, add:

   ```properties
   romset.recalbox.ssh.password=recalboxroot
   ```

Security note: Storing a password in plain text is not recommended for long-term use. Prefer SSH key authentication.

### SSH Diagnostics

If `sshpass` is not installed and a password is configured, you will see:

```text
[Error] Exception while stopping EmulationStation: Cannot run program "sshpass": error=2, No such file or directory
```

Solutions:

- Install `sshpass` as indicated above, or
- Switch to SSH key authentication (recommended)
