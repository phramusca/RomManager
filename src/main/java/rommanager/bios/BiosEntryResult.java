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

public class BiosEntryResult {

    private final String system;
    private final BiosInfo biosInfo;
    private final BiosCopyStatus status;
    private final String message;

    public BiosEntryResult(String system, BiosInfo biosInfo, BiosCopyStatus status, String message) {
        this.system = system;
        this.biosInfo = biosInfo;
        this.status = status;
        this.message = message == null ? "" : message;
    }

    public String getSystem() {
        return system;
    }

    public BiosInfo getBiosInfo() {
        return biosInfo;
    }

    public BiosCopyStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getFileName() {
        if (biosInfo.getPath() == null || biosInfo.getPath().isBlank()) {
            return biosInfo.getName();
        }
        int slash = biosInfo.getPath().lastIndexOf('/');
        return slash >= 0 ? biosInfo.getPath().substring(slash + 1) : biosInfo.getPath();
    }

    public String getRelativePath(BiosPlatform platform) {
        return biosInfo.getRelativeDestPath(platform);
    }
}
