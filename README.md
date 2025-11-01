# RomManager

A powerful desktop application for managing and organizing ROM collections. Scan ROM sets, automatically score versions to select the best ones, and synchronize files and metadata with Recalbox and Romm platforms.

## ✨ Features

- **Smart ROM Version Detection**: Automatically identifies and scores multiple ROM versions within console folders
- **Customizable Scoring System**: Configure scoring rules to prioritize your preferred regions, languages, and ROM quality. The score determines which versions are automatically exported to Recalbox and Romm
- **ROM File Synchronization**: Export selected ROM files to Recalbox and Romm destination folders
- **Metadata Synchronization**: Bidirectional sync of game metadata (favorites, ratings, play stats) with Recalbox (via `gamelist.xml`). Romm metadata sync is planned
- **Multi-Console Support**: Manage ROMs across 35+ retro gaming consoles
- **Automatic Version Selection**: Exports the highest-scored ROM version for each game automatically

## 📋 Current Status

### ROM Sets Support
- ✅ **GoodSets** (fully supported)
- 🔜 **NoIntro** (planned)
- 🔜 **Redump** (planned)

### Platform Synchronization

**ROM File Sync:**
- ✅ **Recalbox** (fully supported)
- ✅ **Romm** (fully supported)

**Metadata Sync:**
- ✅ **Recalbox** (fully supported via `gamelist.xml` files)
- 🔜 **Romm** (planned via REST API - see [rommapp/romm](https://github.com/rommapp/romm))

## 🚀 Quick Start

### 1. Scan Source

Browse your ROM source folder and scan for ROM files. RomManager will:
- Detect all ROM files in supported console folders
- Identify multiple versions of the same game within each console folder
- Create a `RomManager.ods` spreadsheet with all discovered ROMs

**Requirements:**
- ROM files must be organized in subfolders named according to [supported console names](#supported-consoles)
- ROM files must be in `.7z` archives (except Amstrad CPC which uses `.dsk` files)

**Important**: RomManager expects ROMs to be organized in console-specific folders. It will not automatically group ROMs from different locations.

### 2. Set Score

Automatically score ROM versions based on your preferences:
- Each ROM version receives a score based on `GoodToolsConfig.ods` configuration
- By default, French/European games are favored (customizable)
- The highest-scored version is automatically marked for export:
  - All good `.dsk` files for Amstrad CPC
  - Only the best version (highest score) for other consoles

### 3. Sync ROM Files

Export selected ROM files to your destination folders (Recalbox or Romm):
- Export ROM files to destination console folders
- Maintains folder structure compatible with the selected destination platform
- Checks existing files on destination and skips those already exported
- Automatically removes unwanted files that are no longer in the selection

### 4. Sync Game Metadata

Synchronize game metadata (favorites, ratings, play stats) with supported platforms:

**Recalbox:**
- Reads `gamelist.xml` files from destination folders
- Bidirectional sync for user preferences (favorites, hidden, adult flags, name)
- One-way sync for scraped data (descriptions, ratings, images, videos)

**Romm** (planned):
- Metadata synchronization via REST API
- Will support bidirectional sync for user preferences
- See [Romm documentation](https://github.com/rommapp/romm) for more information

## ⚙️ Configuration

### ROM Source Folder

Select the folder containing your ROM sets. It must include subfolders:
- Named according to [supported console names](#supported-consoles)
- Containing `.7z` archive files (or `.dsk` files for Amstrad CPC only)

**Important**: RomManager expects ROMs to be organized in console-specific folders. It will not automatically group ROMs from different locations.

### Destination Folder

Select where to:
- Export selected ROM files (for both Recalbox and Romm)
- Read/write `gamelist.xml` files for Recalbox metadata synchronization

### GoodToolsConfig.ods

Configuration file that defines how ROM versions are scored. Configure:
- **Translation tab**: Scoring by language/translation
- **ALL tab**: Scoring by GoodTools codes (includes language codes)
- **README tab**: Additional documentation

**Note**: French/European games are favored by default. Adjust the configuration to match your preferences!

The scoring system determines which ROM versions are automatically selected for export to Recalbox and Romm.

### RomManager.ods

Output file generated after "Scan Source" and "Set Score" operations:
- Acts as a database of your ROM collection
- Read automatically at startup
- Can be opened in LibreOffice/Excel for manual review

### SSH Configuration for Recalbox

RomManager can automatically stop and restart EmulationStation on a remote Recalbox during gamelist synchronization. Two SSH authentication methods are supported:

#### SSH Key Authentication (Recommended)

1. Generate an SSH key on the machine running RomManager:
   ```bash
   ssh-keygen
   ```

2. Copy the public key to your Recalbox:
   ```bash
   ssh-copy-id root@recalbox.local
   ```

3. Configure in `RomManager.properties`:
   ```properties
   romset.recalbox.ssh.key=~/.ssh/id_rsa
   # Or leave empty to use the default key
   ```

#### Password Authentication (For Testing Only)

1. Install `sshpass`:
   ```bash
   sudo apt update && sudo apt install sshpass
   ```

2. Configure in `RomManager.properties`:
   ```properties
   romset.recalbox.ssh.password=recalboxroot
   ```

⚠️ **Security Note**: Storing passwords in plain text is not recommended. Use SSH key authentication for production use.

#### Troubleshooting SSH

If you see this error:
```
[Error] Exception while stopping EmulationStation: Cannot run program "sshpass": error=2, No such file or directory
```

**Solutions:**
- Install `sshpass` as shown above, or
- Switch to SSH key authentication (recommended)

## 📊 Metadata Synchronization

RomManager synchronizes game metadata with multiple platforms:

- **Recalbox**: Uses EmulationStation's `gamelist.xml` format
- **Romm**: Uses REST API (planned)

The synchronization follows these rules:

### Merge Rules

- **"Recalbox"**: Read-only from Recalbox → RomManager (scraped data, file info, user stats)
- **"Most Recent"**: Takes the version modified most recently (user preferences like favorites, hidden, adult, name). Fallback: Recalbox takes precedence if timestamps are equal
- **"-"**: Not applicable, not read

### Field Details

| XML Field        | Type    | Category    | Read XML | Write XML | Mod. RomManager | Mod. Recalbox | Merge Rule    | Description                           |
|-----------------|---------|-------------|----------|-----------|----------------|---------------|---------------|---------------------------------------|
| **File Info**    |         |             |          |           |                |               |               |                                       |
| path             | String  | File info   | ✅       | ❌        | ❌             | ❌            | Recalbox      | ROM file path                         |
| hash             | String  | File info   | ✅       | ❌        | ❌             | ❌            | Recalbox      | CRC32 hash                             |
| **User Stats**   |         |             |          |           |                |               |               |                                       |
| playcount        | int     | User Stats  | ✅       | ❌        | ❌             | ❌            | Recalbox      | Number of times played                 |
| lastplayed       | String  | User Stats  | ✅       | ❌        | ❌             | ❌            | Recalbox      | Last played date                       |
| timeplayed       | int     | User Stats  | ✅       | ❌        | ❌             | ❌            | Recalbox      | Total play time (seconds)              |
| **User Preferences** |      |             |          |           |                |               |               |                                       |
| favorite         | boolean | User        | ✅       | ✅        | ✅             | ✅            | Most Recent   | Favorite game flag                     |
| hidden           | boolean | User        | ✅       | ✅        | ✅             | ✅            | Most Recent   | Hidden game flag                      |
| adult            | boolean | User/Scrap  | ✅       | ✅        | ✅             | ✅            | Most Recent   | Adult content flag                    |
| name             | String  | User/Scrap  | ✅       | ✅        | ✅             | ✅            | Most Recent   | Game name                             |
| **Scraped Data** |         |             |          |           |                |               |               |                                       |
| desc             | String  | Scraped     | ✅       | ❌        | ❌             | ✅            | Recalbox      | Game description                      |
| rating           | float   | Scraped     | ✅       | ❌        | ❌             | ✅            | Recalbox      | Game rating                            |
| image            | String  | Scraped     | ✅       | ❌        | ❌             | ❌            | Recalbox      | Cover image path                      |
| thumbnail        | String  | Scraped     | ✅       | ❌        | ❌             | ❌            | Recalbox      | Thumbnail image path                  |
| video            | String  | Scraped     | ✅       | ❌        | ❌             | ❌            | Recalbox      | Preview video path                    |
| releasedate      | String  | Scraped     | ✅       | ❌        | ❌             | ❌            | Recalbox      | Release date                          |
| developer        | String  | Scraped     | ✅       | ❌        | ❌             | ❌            | Recalbox      | Developer                              |
| publisher        | String  | Scraped     | ✅       | ❌        | ❌             | ❌            | Recalbox      | Publisher                              |
| genre            | String  | Scraped     | ✅       | ❌        | ❌             | ❌            | Recalbox      | Genre                                  |
| genreid          | String  | Scraped     | ✅       | ❌        | ❌             | ✅            | Recalbox      | Genre ID                               |
| players          | String  | Scraped     | ✅       | ❌        | ❌             | ❌            | Recalbox      | Number of players                     |
| region           | String  | Scraped     | ✅       | ❌        | ❌             | ❌            | Recalbox      | Region                                 |
| ratio            | String  | Scraped     | ✅       | ❌        | ❌             | ✅            | Recalbox      | Screen ratio                           |
| timestamp        | long    | Scrap info  | ✅       | ❌        | ❌             | ❌            | Recalbox      | Scrap timestamp (attribute)            |
| source           | -       | Scrap info  | ❌       | ❌        | ❌             | ❌            | -            | Always "Recalbox" (attribute)          |
| **Not Supported** |         |             |          |           |                |               |               |                                       |
| emulator         | -       |             | ❌       | ❌        | ❌             | ✅            | -            | Emulator                               |
| core             | -       |             | ❌       | ❌        | ❌             | ✅            | -            | Emulator core                         |
| rotation         | -       |             | ❌       | ❌        | ❌             | ✅            | -            | Screen rotation                        |
| lastPatch        | -       |             | ❌       | ❌        | ❌             | ❓            | -            | Last applied patch                     |
| lightgunluminosity | -     |             | ❌       | ❌        | ❌             | ❓            | -            | Lightgun luminosity                    |
| aliases          | -       |             | ❌       | ❌        | ❌             | ❓            | -            | Game aliases                           |
| licences         | -       |             | ❌       | ❌        | ❌             | ❓            | -            | Licenses                               |

## 🎮 Supported Consoles

RomManager supports 35+ retro gaming consoles:

| Folder Name      | Console                    |
|------------------|----------------------------|
| amiga1200        | Amiga 1200                 |
| amiga600         | Amiga 600                  |
| amstradcpc       | Amstrad CPC                |
| apple2           | Apple II                   |
| atari2600        | Atari 2600                 |
| atari5200        | Atari 5200                 |
| atari7800        | Atari 7800                 |
| atarist          | Atari ST                   |
| c64              | Commodore 64               |
| cavestory        | Cave Story                 |
| dos              | DOS                        |
| dreamcast        | Sega Dreamcast             |
| gamegear         | Sega Game Gear             |
| gb               | Nintendo Game Boy          |
| gba              | Nintendo Game Boy Advance  |
| gbc              | Nintendo Game Boy Color    |
| gw               | Nintendo Game & Watch      |
| jaguar           | Atari Jaguar               |
| lynx             | Atari Lynx                 |
| mame             | MAME (Arcade)              |
| mastersystem     | Sega Master System         |
| megadrive        | Sega Mega Drive            |
| n64              | Nintendo 64                |
| neogeo           | SNK Neo Geo                |
| nes              | Nintendo Entertainment System |
| ngp              | SNK Neo Geo Pocket         |
| ngpc             | SNK Neo Geo Pocket Color   |
| pcengine         | NEC PC Engine              |
| pcenginecd       | NEC PC Engine CD           |
| psp              | Sony PlayStation Portable  |
| psx              | Sony PlayStation (PS1)      |
| sega32x          | Sega Mega Drive 32X        |
| segacd           | Sega Mega CD               |
| snes             | Super Nintendo             |
| supergrafx       | NEC SuperGrafX             |
| virtualboy       | Nintendo Virtual Boy        |

## 📚 Additional Information

### Gamelist.xml Format (Recalbox)

RomManager uses the standard EmulationStation `gamelist.xml` format for Recalbox synchronization. For reference:
- [Recalbox MetadataDescriptor.cpp](https://gitlab.com/recalbox/recalbox/-/blob/master/projects/frontend/es-app/src/games/MetadataDescriptor.cpp)
- [Recalbox MetadataDescriptor.h](https://gitlab.com/recalbox/recalbox/-/blob/master/projects/frontend/es-app/src/games/MetadataDescriptor.h)

### Romm API

Romm file synchronization uses the REST API provided by [Romm](https://github.com/rommapp/romm), a self-hosted ROM manager and player. Metadata synchronization with Romm is planned and will use the same REST API. Romm provides metadata for 400+ platforms and supports custom artwork, achievements, and more.

### Roadmap

**Planned Features:**
- 🔜 NoIntro ROM set support
- 🔜 Redump ROM set support  
- 🔜 Romm metadata synchronization via REST API

### Related Projects

- **[Recalbox](https://www.recalbox.com/)**: An open-source retrogaming operating system ([GitLab](https://gitlab.com/recalbox/recalbox)) that uses EmulationStation for game management
- **[Romm](https://romm.app/)**: A beautiful, powerful, self-hosted ROM manager and player ([GitHub](https://github.com/rommapp/romm)) that RomManager integrates with via REST API

## 📄 License

This project is licensed under the GNU General Public License v3.0.
