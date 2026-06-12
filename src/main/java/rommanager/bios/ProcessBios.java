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
import rommanager.utils.LogManager;
import rommanager.utils.ProcessAbstract;

public class ProcessBios extends ProcessAbstract {

    private final BiosPlatform platform;
    private final String referencePath;
    private final String sourcePath;
    private final String destinationPath;
    private final BatoceraMode batoceraMode;
    private final boolean copyFiles;
    private final ICallBackBios callback;

    public ProcessBios(
            BiosPlatform platform,
            String referencePath,
            String sourcePath,
            String destinationPath,
            BatoceraMode batoceraMode,
            boolean copyFiles,
            ICallBackBios callback) {
        super("Thread.ProcessBios");
        this.platform = platform;
        this.referencePath = referencePath;
        this.sourcePath = sourcePath;
        this.destinationPath = destinationPath;
        this.batoceraMode = batoceraMode;
        this.copyFiles = copyFiles;
        this.callback = callback;
    }

    @Override
    public void run() {
        try {
            resetAbort();
            callback.onLog("Resolving BIOS entries for " + platform.name() + "...");
            Map<String, List<BiosInfo>> entries = BiosService.resolveEntries(
                    platform, referencePath, destinationPath, batoceraMode);
            int total = entries.values().stream().mapToInt(List::size).sum();
            callback.onStarted(total);
            callback.onLog((copyFiles ? "Copying " : "Analyzing ") + total + " BIOS entries...");

            List<BiosEntryResult> results = BiosService.processEntries(
                    platform, entries, sourcePath, destinationPath, copyFiles, this);

            for (BiosEntryResult result : results) {
                checkAbort();
                callback.onResult(result);
                callback.onLog(result.getSystem() + " / " + result.getFileName()
                        + " -> " + result.getStatus().getLabel()
                        + (result.getMessage().isBlank() ? "" : " (" + result.getMessage() + ")"));
            }
            callback.onCompleted();
        } catch (InterruptedException ex) {
            callback.onLog("Aborted.");
            callback.onCompleted();
        } catch (IOException ex) {
            LogManager.getInstance().error(ProcessBios.class, "BIOS process failed", ex);
            callback.onError(ex);
        }
    }
}
