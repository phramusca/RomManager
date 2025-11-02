/*
 * Copyright (C) 2025 raph
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

import javax.swing.JOptionPane;
import org.apache.commons.lang3.tuple.Pair;
import rommanager.utils.Popup;

/**
 *
 * @author raph
 */
public class EmulStation {

    private static SSHHelper.SSHConfig sshConfig = SSHHelper.loadConfig("recalbox");
    
    public static Pair<Boolean, String> stop() {
        if (sshConfig.isConfigured()) {
            int ans = JOptionPane.showConfirmDialog(null, "Stop EmulationStation on " + sshConfig.host + " during sync?\n\nWARNING: Any changes made in Recalbox UI will be lost if you haven't saved them first.\nMake sure to select 'Update game list' in Recalbox or restart Recalbox before syncing.\n\nEmulationStation will be restarted after sync.", "Confirm sync", JOptionPane.YES_NO_OPTION);
            if (ans == JOptionPane.YES_OPTION) {
                try {
                    boolean success = SSHHelper.executeRemoteCommand(sshConfig, "/etc/init.d/S31emulationstation stop");
                    if (success) {
                        return Pair.of(true, "[Info] EmulationStation stopped on ".concat(sshConfig.host).concat("\n"));
                    } else {
                        return Pair.of(false, "[Warn] Could not stop EmulationStation on ".concat(sshConfig.host).concat(" - continuing without stop\n"));
                    }
                } catch (Exception ex) {
                    Popup.error(ex);
                    return Pair.of(false, "[Error] Exception while stopping EmulationStation: ".concat(ex.getMessage()).concat("\n"));
                }
            }
        }
        return Pair.of(false, "[Info] SSH not configured, so EmulationStation NOT stopped.");
    }

    public static Pair<Boolean, String> start() {
        if (sshConfig.isConfigured()) {
            try {
                // start in background so ssh returns
                boolean success = SSHHelper.executeRemoteCommand(sshConfig, "/etc/init.d/S31emulationstation start &>/dev/null &");
                if (success) {
                    return Pair.of(true, "[Info] EmulationStation started on ".concat(sshConfig.host).concat(".\n"));
                } else {
                    return Pair.of(false, "[Warn] EmulationStation could not be started on ".concat(sshConfig.host).concat(".\n"));
                }
            } catch (Exception ex) {
                return Pair.of(false, "[Warn] Exception while starting EmulationStation before popup: ".concat(ex.getMessage()).concat("\n"));
            }
        }
        return Pair.of(false, "[Info] SSH not configured, so EmulationStation NOT started.");
    }
}
