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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import org.apache.commons.lang3.tuple.Pair;
import rommanager.utils.Popup;

/**
 *
 * @author raph
 */
public class EmulStation {

    static String sshHost = RomManager.options.get("recalbox.ssh.host");
    static String sshUser = RomManager.options.get("recalbox.ssh.user");
    static String sshPort = RomManager.options.get("recalbox.ssh.port");
    static String sshKey = RomManager.options.get("recalbox.ssh.key");
    static String sshPassword = RomManager.options.get("recalbox.ssh.password");
    
    public static Pair<Boolean, String> stop() {
        if (isConfigured()) {
            int ans = JOptionPane.showConfirmDialog(null, "Stop EmulationStation on " + sshHost + " during sync?\n\nWARNING: Any changes made in Recalbox UI will be lost if you haven't saved them first.\nMake sure to select 'Update game list' in Recalbox or restart Recalbox before syncing.\n\nEmulationStation will be restarted after sync.", "Confirm sync", JOptionPane.YES_NO_OPTION);
            if (ans == JOptionPane.YES_OPTION) {
                try {
                    boolean success = stopEmulationStation(sshHost, sshUser, sshPort, sshKey, sshPassword);
                    if (success) {
                        return Pair.of(true, "[Info] EmulationStation stopped on ".concat(sshHost).concat("\n"));
                    } else {
                        return Pair.of(false, "[Warn] Could not stop EmulationStation on ".concat(sshHost).concat(" - continuing without stop\n"));
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
        if (isConfigured()) {
            try {
                boolean success = startEmulationStation(sshHost, sshUser, sshPort, sshKey, sshPassword);
                if (success) {
                    return Pair.of(true, "[Info] EmulationStation started on ".concat(sshHost).concat(".\n"));
                } else {
                    return Pair.of(false, "[Warn] EmulationStation could not be started on ".concat(sshHost).concat(".\n"));
                }
            } catch (Exception ex) {
                return Pair.of(false, "[Warn] Exception while starting EmulationStation before popup: ".concat(ex.getMessage()).concat("\n"));
            }
        }
        return Pair.of(false, "[Info] SSH not configured, so EmulationStation NOT started.");
    }

    private static boolean isConfigured() {
        return (sshHost != null && !sshHost.equals("{Missing}") && !sshHost.trim().isEmpty());
    }

    private static boolean stopEmulationStation(String host, String user, String port, String key, String password) throws IOException, InterruptedException {
        List<String> cmd = new ArrayList<>();
        boolean useSshPass = password != null && !password.equals("{Missing}") && !password.trim().isEmpty();
        if (useSshPass) {
            cmd.add("sshpass");
            cmd.add("-p");
            cmd.add(password);
        }
        cmd.add("ssh");
        if (port != null && !port.equals("{Missing}") && !port.trim().isEmpty()) {
            cmd.add("-p");
            cmd.add(port);
        }
        if (key != null && !key.equals("{Missing}") && !key.trim().isEmpty()) {
            cmd.add("-i");
            cmd.add(key);
        }
        String target = (user != null && !user.equals("{Missing}") ? user + "@" + host : host);
        cmd.add(target);
        cmd.add("/etc/init.d/S31emulationstation stop");
        ProcessBuilder pb = new ProcessBuilder(cmd);
        Process p = pb.start();
        int rc = p.waitFor();
        return rc == 0;
    }

    private static boolean startEmulationStation(String host, String user, String port, String key, String password) throws IOException, InterruptedException {
        List<String> cmd = new ArrayList<>();
        boolean useSshPass = password != null && !password.equals("{Missing}") && !password.trim().isEmpty();
        if (useSshPass) {
            cmd.add("sshpass");
            cmd.add("-p");
            cmd.add(password);
        }
        cmd.add("ssh");
        if (port != null && !port.equals("{Missing}") && !port.trim().isEmpty()) {
            cmd.add("-p");
            cmd.add(port);
        }
        if (key != null && !key.equals("{Missing}") && !key.trim().isEmpty()) {
            cmd.add("-i");
            cmd.add(key);
        }
        String target = (user != null && !user.equals("{Missing}") ? user + "@" + host : host);
        cmd.add(target);
        // start in background so ssh returns
        cmd.add("/etc/init.d/S31emulationstation start &>/dev/null &");
        ProcessBuilder pb = new ProcessBuilder(cmd);
        Process p = pb.start();
        int rc = p.waitFor();
        return rc == 0;
    }
}
