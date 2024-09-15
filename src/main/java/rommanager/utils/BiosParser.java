package rommanager.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

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
                    if (biosBuilder.getMd5List() != null) {
                        biosBuilder.getMd5List().add(line.trim());
                    }
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
            Map<String, List<BiosInfo>> biosMap = parseBiosFile("recalbox/bios/missing_bios_report.txt");
            biosMap.forEach((system, biosInfos) -> {
                System.out.println("System: " + system);
                biosInfos.forEach(System.out::println);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
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
