TODO - RomManager gamelist sync (ordered)

1) Quick manual test (one-off)

   - Configure SSH defaults in `RomManager.properties`:
     romset.recalbox.ssh.host=recalbox.local
     romset.recalbox.ssh.user=root
     romset.recalbox.ssh.port=22
     romset.recalbox.ssh.key=
   - From RomManager GUI start "Sync gamelist" and confirm the popup to stop EmulationStation.
   - Verify ES stops, RomManager runs sync, and ES restarts in background (no blocking). Check UI logs.

2) Temporary console-only test

   - Add FIXME to `ProcessSyncGamelist` to limit sync to `atari2600` (temporary). Run and inspect results.

3) Improve log aggregation

   - Group logs per console and per type (missing, missingMedia, updated, deleted) and write `cache/gamelists/sync-<timestamp>.log`.
   - Display grouped summary dialog at end.

4) Finalize compare/set behavior

   - Confirm fields to preserve vs prefer remote; currently timestamp-based merge for name/favorite/hidden/adult.
   - Add unit tests for `Gamelist.compareGame`.

5) GUI: add options panel

   - Add UI to edit SSH config, toggle "stop ES during sync", and advanced options for import rules (NoIntro/Redump/RomM later).

6) Long-term

   - Consider patching EmulationStation to support merge rather than overwrite (optional). Document build/deploy steps.

Notes for next IA session

   - To continue: run `mvn -DskipTests package` to ensure compile.
   - When testing SSH with a password prompt, the user can either enter the password interactively or configure an ssh key and set `romset.recalbox.ssh.key`.

Changelog (this session):

- Added SSH defaults to `RomManager.properties` and added this TODO.md file.

*** End of TODO.md
