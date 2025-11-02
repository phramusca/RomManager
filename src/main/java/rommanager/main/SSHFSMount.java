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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Utility class to manage SSHFS mounts for Recalbox and Romm destinations
 *
 * @author raph
 */
public class SSHFSMount {

    /**
     * Mount a remote filesystem via SSHFS for the given destination
     *
     * @param destination The destination (recalbox or romM)
     * @return Pair of (success, message)
     */
    public static Pair<Boolean, String> mount(Destination destination) {
        String prefix = destination == Destination.recalbox ? "recalbox" : "romm";
        SSHHelper.SSHConfig sshConfig = SSHHelper.loadConfig(prefix);
        
        String remotePath = RomManager.options.get(prefix + ".ssh.remotePath");
        String mountPoint = RomManager.options.get(prefix + ".ssh.mountPoint");
        
        if (!isConfigured(sshConfig.host, remotePath, mountPoint)) {
            return Pair.of(false, "[Info] SSHFS not configured for " + destination.getName() + ".");
        }
        
        // Check if already mounted
        File mountPointFile = new File(mountPoint);
        if (mountPointFile.exists() && mountPointFile.listFiles() != null) {
            // Try to check if it's already a mount point by checking if . is a mount point
            // This is a simple check - if the directory exists and is accessible, assume it might be mounted
            return Pair.of(true, "[Info] Mount point " + mountPoint + " already exists and may be mounted.");
        }
        
        // Create mount point directory if it doesn't exist
        if (!mountPointFile.exists()) {
            if (!mountPointFile.mkdirs()) {
                return Pair.of(false, "[Error] Could not create mount point directory: " + mountPoint);
            }
        }
        
        try {
            boolean success = mountSSHFS(sshConfig, remotePath, mountPoint);
            if (success) {
                return Pair.of(true, "[Info] Successfully mounted " + destination.getName() + " at " + mountPoint);
            } else {
                return Pair.of(false, "[Error] Failed to mount " + destination.getName() + " at " + mountPoint);
            }
        } catch (Exception ex) {
            return Pair.of(false, "[Error] Exception while mounting: " + ex.getMessage());
        }
    }
    
    /**
     * Unmount a remote filesystem mounted via SSHFS for the given destination
     *
     * @param destination The destination (recalbox or romM)
     * @return Pair of (success, message)
     */
    public static Pair<Boolean, String> unmount(Destination destination) {
        String prefix = destination == Destination.recalbox ? "recalbox" : "romm";
        String mountPoint = RomManager.options.get(prefix + ".ssh.mountPoint");
        
        if (mountPoint == null || mountPoint.equals("{Missing}") || mountPoint.trim().isEmpty()) {
            return Pair.of(false, "[Info] SSHFS not configured for " + destination.getName() + ".");
        }
        
        File mountPointFile = new File(mountPoint);
        if (!mountPointFile.exists()) {
            return Pair.of(true, "[Info] Mount point " + mountPoint + " does not exist.");
        }
        
        try {
            boolean success = unmountSSHFS(mountPoint);
            if (success) {
                return Pair.of(true, "[Info] Successfully unmounted " + destination.getName() + " from " + mountPoint);
            } else {
                return Pair.of(false, "[Error] Failed to unmount " + destination.getName() + " from " + mountPoint);
            }
        } catch (Exception ex) {
            return Pair.of(false, "[Error] Exception while unmounting: " + ex.getMessage());
        }
    }
    
    /**
     * Check if SSHFS is configured for the given destination
     *
     * @param destination The destination (recalbox or romM)
     * @return true if configured
     */
    public static boolean isConfigured(Destination destination) {
        String prefix = destination == Destination.recalbox ? "recalbox" : "romm";
        SSHHelper.SSHConfig sshConfig = SSHHelper.loadConfig(prefix);
        String remotePath = RomManager.options.get(prefix + ".ssh.remotePath");
        String mountPoint = RomManager.options.get(prefix + ".ssh.mountPoint");
        return isConfigured(sshConfig.host, remotePath, mountPoint);
    }
    
    private static boolean isConfigured(String sshHost, String remotePath, String mountPoint) {
        return (sshHost != null && !sshHost.equals("{Missing}") && !sshHost.trim().isEmpty())
                && (remotePath != null && !remotePath.equals("{Missing}") && !remotePath.trim().isEmpty())
                && (mountPoint != null && !mountPoint.equals("{Missing}") && !mountPoint.trim().isEmpty());
    }
    
    private static boolean mountSSHFS(SSHHelper.SSHConfig config, String remotePath, String mountPoint) 
            throws IOException, InterruptedException {
        // Build SSH connection string
        StringBuilder sshConnection = new StringBuilder();
        sshConnection.append(SSHHelper.buildSSHTarget(config));
        if (remotePath != null && !remotePath.equals("{Missing}") && !remotePath.trim().isEmpty()) {
            sshConnection.append(":").append(remotePath);
        }
        
        // Add SSH options for sshfs
        List<String> sshOptions = new ArrayList<>();
        sshOptions.add("reconnect");
        
        if (config.port != null && !config.port.equals("{Missing}") && !config.port.trim().isEmpty()) {
            sshOptions.add("Port=" + config.port);
        }
        
        if (config.hasKey()) {
            sshOptions.add("IdentityFile=" + config.key);
        }
        
        // Build sshfs command
        List<String> cmd = new ArrayList<>();
        
        // If password is used, we need to use sshpass (sshfs doesn't support password directly)
        if (config.hasPassword()) {
            cmd.add("sshpass");
            cmd.add("-p");
            cmd.add(config.password);
        }
        
        cmd.add("sshfs");
        cmd.add("-o");
        cmd.add(String.join(",", sshOptions));
        cmd.add(sshConnection.toString());
        cmd.add(mountPoint);
        
        return SSHHelper.executeCommand(cmd);
    }
    
    private static boolean unmountSSHFS(String mountPoint) throws IOException, InterruptedException {
        List<String> cmd = new ArrayList<>();
        cmd.add("fusermount");
        cmd.add("-u");
        cmd.add(mountPoint);
        
        return SSHHelper.executeCommand(cmd);
    }
}

