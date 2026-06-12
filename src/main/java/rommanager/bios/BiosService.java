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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FileUtils;
import rommanager.utils.LogManager;
import rommanager.utils.ProcessAbstract;

/**
 * BIOS parsing, matching and copy logic for Recalbox and Batocera.
 */
public final class BiosService {

    private static final String SYSTEM_PREFIX = "SYSTEM:";
    private static final String MISSING_PREFIX = "MISSING";
    private static final String PATH_PREFIX = "Path:";
    private static final String NOTES_PREFIX = "Notes:";
    private static final String FOR_PREFIX = "For:";
    private static final String MD5_LIST_PREFIX = "Possible MD5 List:";
    private static final Pattern BATOCERA_SYSTEM = Pattern.compile("^(.+):$");
    private static final Pattern BATOCERA_PATH_ONLY = Pattern.compile("^\\s+(bios/.+)$");
    private static final Pattern BATOCERA_MD5_LINE = Pattern.compile("^([a-fA-F0-9]{32})\\s+(bios/.+)$");

    private BiosService() {
    }

    public static Map<String, List<BiosInfo>> parseRecalboxReport(String filePath) throws IOException {
        Map<String, List<BiosInfo>> systemBiosMap = new LinkedHashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            String currentSystem = null;
            BiosInfo.Builder biosBuilder = null;
            boolean firstMissing = true;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (firstMissing && line.startsWith(MISSING_PREFIX)) {
                    firstMissing = false;
                    continue;
                }

                if (line.startsWith(SYSTEM_PREFIX)) {
                    biosBuilder = flushBuilder(systemBiosMap, currentSystem, biosBuilder);
                    currentSystem = line.substring(SYSTEM_PREFIX.length()).trim();
                } else if (line.startsWith(MISSING_PREFIX)) {
                    biosBuilder = flushBuilder(systemBiosMap, currentSystem, biosBuilder);
                    biosBuilder = new BiosInfo.Builder()
                            .setName(line.substring(MISSING_PREFIX.length() + 1).trim())
                            .setIsRequired(line.contains("REQUIRED"));
                } else if (biosBuilder != null && line.startsWith(PATH_PREFIX)) {
                    biosBuilder.setPath(line.substring(PATH_PREFIX.length()).trim());
                } else if (biosBuilder != null && line.startsWith(NOTES_PREFIX)) {
                    biosBuilder.setNotes(line.substring(NOTES_PREFIX.length()).trim());
                } else if (biosBuilder != null && line.startsWith(FOR_PREFIX)) {
                    biosBuilder.setForSystems(line.substring(FOR_PREFIX.length()).trim());
                } else if (biosBuilder != null && line.startsWith(MD5_LIST_PREFIX)) {
                    biosBuilder.setMd5List(new ArrayList<>());
                } else if (biosBuilder != null && line.matches("[A-F0-9]{32}")) {
                    biosBuilder.addMd5(line.trim());
                }
            }

            flushBuilder(systemBiosMap, currentSystem, biosBuilder);
        }
        return systemBiosMap;
    }

    public static Map<String, List<BiosInfo>> parseBatoceraReadme(String filePath) throws IOException {
        Map<String, List<BiosInfo>> systemBiosMap = new LinkedHashMap<>();
        String currentSystem = null;
        Map<String, BiosInfo.Builder> buildersByPath = new LinkedHashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }

                Matcher systemMatcher = BATOCERA_SYSTEM.matcher(line.trim());
                if (systemMatcher.matches()) {
                    flushBatoceraBuilders(systemBiosMap, currentSystem, buildersByPath);
                    currentSystem = systemMatcher.group(1).trim();
                    continue;
                }

                Matcher md5Matcher = BATOCERA_MD5_LINE.matcher(line);
                if (md5Matcher.matches()) {
                    String path = md5Matcher.group(2).trim();
                    BiosInfo.Builder builder = buildersByPath.computeIfAbsent(path, BiosService::newBatoceraBuilder);
                    builder.addMd5(md5Matcher.group(1));
                    continue;
                }

                Matcher pathMatcher = BATOCERA_PATH_ONLY.matcher(line);
                if (pathMatcher.matches()) {
                    String path = pathMatcher.group(1).trim();
                    buildersByPath.computeIfAbsent(path, BiosService::newBatoceraBuilder);
                }
            }
            flushBatoceraBuilders(systemBiosMap, currentSystem, buildersByPath);
        }
        return systemBiosMap;
    }

    public static Map<String, List<BiosInfo>> resolveEntries(
            BiosPlatform platform,
            String referencePath,
            String destinationPath,
            BatoceraMode batoceraMode) throws IOException {
        if (platform == BiosPlatform.RECALBOX) {
            return parseRecalboxReport(referencePath);
        }
        Map<String, List<BiosInfo>> catalog = parseBatoceraReadme(referencePath);
        if (batoceraMode == BatoceraMode.MISSING_ONLY) {
            return filterMissingInDestination(platform, catalog, destinationPath);
        }
        return catalog;
    }

    public static Map<String, List<BiosInfo>> filterMissingInDestination(
            BiosPlatform platform,
            Map<String, List<BiosInfo>> catalog,
            String destinationPath) throws IOException {
        Map<String, List<BiosInfo>> missing = new LinkedHashMap<>();
        for (Map.Entry<String, List<BiosInfo>> entry : catalog.entrySet()) {
            List<BiosInfo> missingForSystem = new ArrayList<>();
            for (BiosInfo biosInfo : entry.getValue()) {
                if (!isPresentWithValidMd5(platform, biosInfo, destinationPath)) {
                    missingForSystem.add(biosInfo);
                }
            }
            if (!missingForSystem.isEmpty()) {
                missing.put(entry.getKey(), missingForSystem);
            }
        }
        return missing;
    }

    public static Map<String, String> indexSourceByMd5(String folderPath) throws IOException {
        Map<String, String> fileMd5Map = new HashMap<>();
        Path root = Paths.get(folderPath);
        if (!Files.isDirectory(root)) {
            throw new IOException("Source folder does not exist: " + folderPath);
        }
        String normalizedRoot = root.toString();
        if (!normalizedRoot.endsWith(File.separator)) {
            normalizedRoot += File.separator;
        }
        final String rootPrefix = normalizedRoot;
        Files.walk(root)
                .filter(Files::isRegularFile)
                .forEach(file -> {
                    try {
                        String relativePath = file.toString().substring(rootPrefix.length());
                        fileMd5Map.put(relativePath, calculateMd5(file).toLowerCase());
                    } catch (NoSuchAlgorithmException | IOException e) {
                        LogManager.getInstance().error(BiosService.class, "Error hashing " + file, e);
                    }
                });
        return fileMd5Map;
    }

    public static List<BiosEntryResult> processEntries(
            BiosPlatform platform,
            Map<String, List<BiosInfo>> entriesBySystem,
            String sourcePath,
            String destinationPath,
            boolean copyFiles,
            ProcessAbstract process) throws IOException, InterruptedException {
        List<BiosEntryResult> results = new ArrayList<>();
        Map<String, String> sourceByRelativePath = indexSourceByMd5(sourcePath);
        Map<String, String> md5ToSourcePath = invertMd5Index(sourceByRelativePath);

        for (Map.Entry<String, List<BiosInfo>> systemEntry : entriesBySystem.entrySet()) {
            for (BiosInfo biosInfo : systemEntry.getValue()) {
                if (process != null) {
                    process.checkAbort();
                }
                results.add(processSingleEntry(
                        platform,
                        systemEntry.getKey(),
                        biosInfo,
                        destinationPath,
                        md5ToSourcePath,
                        sourcePath,
                        copyFiles));
            }
        }
        return results;
    }

    private static BiosEntryResult processSingleEntry(
            BiosPlatform platform,
            String system,
            BiosInfo biosInfo,
            String destinationPath,
            Map<String, String> md5ToSourcePath,
            String sourcePath,
            boolean copyFiles) {
        try {
            if (isPresentWithValidMd5(platform, biosInfo, destinationPath)) {
                return new BiosEntryResult(system, biosInfo, BiosCopyStatus.ALREADY_OK, "Valid file already in destination");
            }

            Optional<String> matchingMd5 = biosInfo.getMd5List().stream()
                    .map(String::toLowerCase)
                    .filter(md5ToSourcePath::containsKey)
                    .findFirst();

            if (matchingMd5.isEmpty()) {
                return new BiosEntryResult(system, biosInfo, BiosCopyStatus.NOT_IN_SOURCE,
                        "No matching MD5 in source folder");
            }

            String sourceRelativePath = md5ToSourcePath.get(matchingMd5.get());
            Path sourceFile = Paths.get(sourcePath, sourceRelativePath);
            String relativeDestPath = biosInfo.getRelativeDestPath(platform);
            Path destFile = Paths.get(destinationPath, relativeDestPath);

            if (!copyFiles) {
                return new BiosEntryResult(system, biosInfo, BiosCopyStatus.FOUND_IN_SOURCE,
                        "Match: " + sourceRelativePath);
            }

            Files.createDirectories(destFile.getParent());
            String expectedName = destFile.getFileName().toString();
            String message = "Copied from " + sourceRelativePath;
            if (!sourceFile.getFileName().toString().equals(expectedName)) {
                message = "Renamed from " + sourceFile.getFileName() + " to " + expectedName + " (" + sourceRelativePath + ")";
            }
            FileUtils.copyFile(sourceFile.toFile(), destFile.toFile());
            return new BiosEntryResult(system, biosInfo, BiosCopyStatus.COPIED, message);
        } catch (IOException e) {
            return new BiosEntryResult(system, biosInfo, BiosCopyStatus.ERROR, e.getMessage());
        }
    }

    private static boolean isPresentWithValidMd5(BiosPlatform platform, BiosInfo biosInfo, String destinationPath)
            throws IOException {
        if (biosInfo.getMd5List().isEmpty()) {
            return false;
        }
        Path destFile = Paths.get(destinationPath, biosInfo.getRelativeDestPath(platform));
        if (!Files.isRegularFile(destFile)) {
            return false;
        }
        try {
            String destMd5 = calculateMd5(destFile).toLowerCase();
            return biosInfo.getMd5List().stream()
                    .map(String::toLowerCase)
                    .anyMatch(destMd5::equals);
        } catch (NoSuchAlgorithmException e) {
            throw new IOException("MD5 algorithm not available", e);
        }
    }

    private static Map<String, String> invertMd5Index(Map<String, String> sourceByRelativePath) {
        Map<String, String> md5ToSourcePath = new HashMap<>();
        for (Map.Entry<String, String> entry : sourceByRelativePath.entrySet()) {
            md5ToSourcePath.putIfAbsent(entry.getValue().toLowerCase(), entry.getKey());
        }
        return md5ToSourcePath;
    }

    private static BiosInfo.Builder flushBuilder(
            Map<String, List<BiosInfo>> systemBiosMap,
            String currentSystem,
            BiosInfo.Builder biosBuilder) {
        if (biosBuilder != null) {
            systemBiosMap.computeIfAbsent(currentSystem, key -> new ArrayList<>()).add(biosBuilder.build());
        }
        return null;
    }

    private static BiosInfo.Builder newBatoceraBuilder(String path) {
        String fileName = path.substring(path.lastIndexOf('/') + 1);
        return new BiosInfo.Builder()
                .setName(fileName)
                .setPath(path)
                .setIsRequired(true);
    }

    private static void flushBatoceraBuilders(
            Map<String, List<BiosInfo>> systemBiosMap,
            String currentSystem,
            Map<String, BiosInfo.Builder> buildersByPath) {
        if (currentSystem == null || buildersByPath.isEmpty()) {
            buildersByPath.clear();
            return;
        }
        List<BiosInfo> biosInfos = new ArrayList<>();
        for (BiosInfo.Builder builder : buildersByPath.values()) {
            biosInfos.add(builder.build());
        }
        systemBiosMap.put(currentSystem, biosInfos);
        buildersByPath.clear();
    }

    private static String calculateMd5(Path file) throws NoSuchAlgorithmException, IOException {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        byte[] hash = digest.digest(Files.readAllBytes(file));
        return bytesToHex(hash);
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
