/*
 * Copyright (C) 2026 phramusca ( https://github.com/phramusca/RomManager/ )
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

package rommanager.bios;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Legacy CLI entry point. Prefer {@link BiosService} and the BIOS Manager UI.
 */
public class BiosParser {

    public static Map<String, List<BiosInfo>> parseBiosFile(String filePath) throws IOException {
        return BiosService.parseRecalboxReport(filePath);
    }

    public static void main(String[] args) {
        try {
            Map<String, List<BiosInfo>> biosMap = BiosService.parseRecalboxReport(
                    "/media/recalbox/bios/missing_bios_report.txt");
            String sourceFolder = "/home/Documents/06-Jeux/Emulation/Bios/Source/";
            String destinationFolder = "/home/Documents/06-Jeux/Emulation/Bios/Destination/";

            List<BiosEntryResult> results = BiosService.processEntries(
                    BiosPlatform.RECALBOX,
                    biosMap,
                    sourceFolder,
                    destinationFolder,
                    true,
                    null);

            results.stream()
                    .filter(result -> result.getStatus() == BiosCopyStatus.NOT_IN_SOURCE)
                    .forEach(result -> System.out.println("Not copied: "
                            + result.getBiosInfo().getName()
                            + " (" + result.getBiosInfo().getPath() + ") for system "
                            + result.getSystem()
                            + " with MD5: " + result.getBiosInfo().getMd5List()));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
