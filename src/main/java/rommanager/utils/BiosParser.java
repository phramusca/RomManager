package rommanager.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import org.apache.commons.io.FileUtils;

public class BiosParser {

    private static final String SYSTEM_PREFIX = "SYSTEM:";
    private static final String MISSING_PREFIX = "MISSING";
    private static final String PATH_PREFIX = "Path:";
    private static final String NOTES_PREFIX = "Notes:";
    private static final String FOR_PREFIX = "For:";
    private static final String MD5_LIST_PREFIX = "Possible MD5 List:";

    public static Map<String, List<BiosInfo>> parseBiosFile(String filePath) throws IOException {
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
                    if (biosBuilder != null) {
                        systemBiosMap.computeIfAbsent(currentSystem, k -> new ArrayList<>())
                                .add(biosBuilder.build());
                        biosBuilder = null;
                    }
                    currentSystem = line.substring(SYSTEM_PREFIX.length()).trim();
                } else if (line.startsWith(MISSING_PREFIX)) {
                    if (biosBuilder != null) {
                        systemBiosMap.computeIfAbsent(currentSystem, k -> new ArrayList<>())
                                .add(biosBuilder.build());
                    }
                    biosBuilder = new BiosInfo.Builder()
                            .setName(line.substring(MISSING_PREFIX.length() + 1).trim())
                            .setIsRequired(line.contains("REQUIRED"));
                } else if (line.startsWith(PATH_PREFIX)) {
                    biosBuilder.setPath(line.substring(PATH_PREFIX.length()).trim());
                } else if (line.startsWith(NOTES_PREFIX)) {
                    biosBuilder.setNotes(line.substring(NOTES_PREFIX.length()).trim());
                } else if (line.startsWith(FOR_PREFIX)) {
                    biosBuilder.setForSystems(line.substring(FOR_PREFIX.length()).trim());
                } else if (line.startsWith(MD5_LIST_PREFIX)) {
                    biosBuilder.setMd5List(new ArrayList<>());
                } else if (line.matches("[A-F0-9]{32}")) {
                    biosBuilder.getMd5List().add(line.trim());
                }
            }

            // Handle the last BIOS
            if (biosBuilder != null) {
                systemBiosMap.computeIfAbsent(currentSystem, k -> new ArrayList<>())
                        .add(biosBuilder.build());
            }
        }
        return systemBiosMap;
    }

    public static void main(String[] args) {
        try {
            Map<String, List<BiosInfo>> biosMap = parseBiosFile("/media/recalbox/bios/missing_bios_report.txt");

            // Define your source and destination folders
            String sourceFolder = "/home/Documents/06-Jeux/Emulation/Bios/Source/";
            String destinationFolder = "/home/Documents/06-Jeux/Emulation/Bios/Destination/";

            // Create a map of files and their MD5 hashes in the source folder
            Map<String, String> sourceFiles = listFilesAndMD5(sourceFolder);

            List<String> notCopiedBios = new ArrayList<>(); // List to store not copied BIOS names

            biosMap.forEach((system, biosInfos) -> {
                System.out.println("System: " + system);
                biosInfos.forEach(biosInfo -> {
                    System.out.println(biosInfo);
                    // Check if the BIOS exists and has the correct MD5
                    if (biosInfo.getMd5List().stream().anyMatch(md5 -> sourceFiles.values().stream().anyMatch(sourceMd5 -> {
                        // Define sourceMd5 here
                        if (sourceMd5.equalsIgnoreCase(md5)) {
                            // Get the correct path from the BIOS entry
                            String path = biosInfo.getPath();
                            String relativePath = path.substring(path.indexOf("/recalbox/share/bios/") + "/recalbox/share/bios/".length());
                            String destFilePath = destinationFolder + relativePath;
                            // Copy the file to the destination, renaming it if necessary
                            try {
                                String fileName = new File(path).getName();
                                // Find the source file path based on the matching MD5
                                Optional<String> sourceFilePath = sourceFiles.entrySet().stream()
                                        .filter(entry -> entry.getValue().equalsIgnoreCase(sourceMd5))
                                        .map(Map.Entry::getKey)
                                        .findFirst();
                                if (sourceFilePath.isPresent()) {
                                    Path sourceFile = Paths.get(sourceFolder, sourceFilePath.get());
                                    if (!sourceFile.getFileName().toString().equals(fileName)) {
                                        System.out.println("Warning: " + sourceFile.getFileName().toString() + " filename mismatch for " + biosInfo.getName() + ". Renaming to " + fileName);
                                    }
                                    FileUtils.copyFile(sourceFile.toFile(), Paths.get(destFilePath).toFile());
//                                    FileUtils.moveFile(sourceFile.toFile(), Paths.get(destFilePath).toFile());
                                    System.out.println("Copied " + biosInfo.getName() + " to " + destFilePath);
                                } else {
                                    System.out.println("Warning: File with matching MD5 not found in source folder: " + biosInfo.getName());
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return true; // Stop the inner stream once a match is found
                        }
                        return false; // Continue searching if no match
                    }))) {
                        // Already treated
                    } else {
                        // Add to not copied list
                        notCopiedBios.add(biosInfo.getName() + " (" + biosInfo.getPath() + ") for system " + system + " with MD5: " + biosInfo.getMd5List());
                    }
                });
            });

            // Display the list of not copied BIOS
            if (!notCopiedBios.isEmpty()) {
                System.out.println("\nBIOS not copied:");
                notCopiedBios.forEach(System.out::println);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Map<String, String> listFilesAndMD5(String folderPath) throws IOException {
        Map<String, String> fileMD5Map = new HashMap<>();
        Files.walk(Paths.get(folderPath))
                .filter(Files::isRegularFile)
                .forEach(file -> {
                    try {
                        String relativePath = file.toString().substring(folderPath.length());
                        System.out.println(relativePath);
                        String md5 = calculateMD5(file);
                        System.out.println(md5);
                        fileMD5Map.put(relativePath, md5);
                    } catch (NoSuchAlgorithmException | IOException e) {
                        e.printStackTrace();
                    }
                });
        return fileMD5Map;
    }

    private static String calculateMD5(Path file) throws NoSuchAlgorithmException, IOException {
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

class BiosInfo {

    private final String name;
    private final String path;
    private final String notes;
    private final String forSystems;
    private final List<String> md5List;
    private final boolean isRequired;

    private BiosInfo(String name, String path, String notes, String forSystems, List<String> md5List, boolean isRequired) {
        this.name = name;
        this.path = path;
        this.notes = notes;
        this.forSystems = forSystems;
        this.md5List = md5List;
        this.isRequired = isRequired;
    }

    public List<String> getMd5List() {
        return md5List;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    // Builder pattern for creating BiosInfo objects
    public static class Builder {

        private String name;
        private String path;
        private String notes;
        private String forSystems;
        private List<String> md5List;
        private boolean isRequired;

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setPath(String path) {
            this.path = path;
            return this;
        }

        public Builder setNotes(String notes) {
            this.notes = notes;
            return this;
        }

        public Builder setForSystems(String forSystems) {
            this.forSystems = forSystems;
            return this;
        }

        public Builder setMd5List(List<String> md5List) {
            this.md5List = md5List;
            return this;
        }

        public Builder setIsRequired(boolean isRequired) {
            this.isRequired = isRequired;
            return this;
        }

        public BiosInfo build() {
            return new BiosInfo(name, path, notes, forSystems, md5List, isRequired);
        }

        public List<String> getMd5List() {
            return md5List;
        }

        public String getName() {
            return name;
        }

        public String getPath() {
            return path;
        }
    }

    @Override
    public String toString() {
        return "BiosInfo{"
                + "name='" + name + '\''
                + ", path='" + path + '\''
                + ", notes='" + notes + '\''
                + ", forSystems='" + forSystems + '\''
                + ", md5List=" + md5List
                + ", isRequired=" + isRequired
                + '}';
    }
}
