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

/**
 * Utility class for SSH operations
 * Provides common functionality for SSH command execution
 *
 * @author raph
 */
public class SSHHelper {

    /**
     * SSH configuration for a specific destination
     */
    public static class SSHConfig {
        public final String host;
        public final String user;
        public final String port;
        public final String key;
        public final String password;

        public SSHConfig(String host, String user, String port, String key, String password) {
            this.host = host;
            this.user = user;
            this.port = port;
            this.key = key;
            this.password = password;
        }

        public boolean isConfigured() {
            return host != null && !host.equals("{Missing}") && !host.trim().isEmpty();
        }

        public boolean hasPassword() {
            return password != null && !password.equals("{Missing}") && !password.trim().isEmpty();
        }

        public boolean hasKey() {
            return key != null && !key.equals("{Missing}") && !key.trim().isEmpty();
        }
    }

    /**
     * Load SSH configuration from properties for the given prefix
     *
     * @param prefix Configuration prefix (e.g., "recalbox" or "romm")
     * @return SSHConfig object with loaded values
     */
    public static SSHConfig loadConfig(String prefix) {
        String host = RomManager.options.get(prefix + ".ssh.host");
        String user = RomManager.options.get(prefix + ".ssh.user");
        String port = RomManager.options.get(prefix + ".ssh.port");
        String key = RomManager.options.get(prefix + ".ssh.key");
        String password = RomManager.options.get(prefix + ".ssh.password");
        return new SSHConfig(host, user, port, key, password);
    }

    /**
     * Build base SSH command (without target or remote command)
     * Includes sshpass if password is configured, port and key options
     *
     * @param config SSH configuration
     * @return List of command arguments for SSH
     */
    public static List<String> buildSSHCommand(SSHConfig config) {
        List<String> cmd = new ArrayList<>();

        // Add sshpass if password is configured
        if (config.hasPassword()) {
            cmd.add("sshpass");
            cmd.add("-p");
            cmd.add(config.password);
        }

        cmd.add("ssh");

        // Add port if configured
        if (config.port != null && !config.port.equals("{Missing}") && !config.port.trim().isEmpty()) {
            cmd.add("-p");
            cmd.add(config.port);
        }

        // Add key if configured (and not using password)
        if (config.hasKey()) {
            cmd.add("-i");
            cmd.add(config.key);
        }

        return cmd;
    }

    /**
     * Build SSH target string (user@host)
     *
     * @param config SSH configuration
     * @return Target string for SSH command
     */
    public static String buildSSHTarget(SSHConfig config) {
        if (config.user != null && !config.user.equals("{Missing}") && !config.user.trim().isEmpty()) {
            return config.user + "@" + config.host;
        }
        return config.host;
    }

    /**
     * Execute a remote SSH command
     *
     * @param config SSH configuration
     * @param remoteCommand Command to execute on remote host
     * @return true if command executed successfully (exit code 0)
     * @throws IOException if process execution fails
     * @throws InterruptedException if process is interrupted
     */
    public static boolean executeRemoteCommand(SSHConfig config, String remoteCommand) 
            throws IOException, InterruptedException {
        List<String> cmd = buildSSHCommand(config);
        cmd.add(buildSSHTarget(config));
        cmd.add(remoteCommand);

        ProcessBuilder pb = new ProcessBuilder(cmd);
        Process p = pb.start();
        int rc = p.waitFor();
        return rc == 0;
    }

    /**
     * Execute a command and return exit code
     *
     * @param command Command to execute
     * @return true if command executed successfully (exit code 0)
     * @throws IOException if process execution fails
     * @throws InterruptedException if process is interrupted
     */
    public static boolean executeCommand(List<String> command) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(command);
        Process p = pb.start();
        int rc = p.waitFor();
        return rc == 0;
    }
}

