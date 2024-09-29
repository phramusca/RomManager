/*
 * Copyright (C) 2024 raph
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
package rommanager.romM.models;

import lombok.Data;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

@Data
public class Rom {
  private int id;
  private int igdb_id;
  private int sgdb_id;
  private int moby_id;
  private int platform_id;
  private String platform_slug;
  private String platform_name;
  private String file_name;
  private String file_name_no_tags;
  private String file_name_no_ext;
  private String file_extension;
  private String file_path;
  private long file_size_bytes;
  private String name;
  private String slug;
  private String summary;
  private long first_release_date;
  private List<String> alternative_names;
  private List<String> genres;
  private List<String> franchises;
  private List<String> collections;
  private List<String> companies;
  private List<String> game_modes;
  private IgdbMetadata igdb_metadata;
  private MobyMetadata moby_metadata;
  private String path_cover_s;
  private String path_cover_l;
  private boolean has_cover;
  private String url_cover;
  private String revision;
  private List<String> regions;
  private List<String> languages; 
  private List<String> tags;
  private boolean multi;
  private List<File> files; 
  private String crc_hash;
  private String md5_hash;
  private String sha1_hash;
  private String full_path;
  private String created_at;
  private String updated_at;
  private List<String> merged_screenshots;
  private List<SiblingRom> sibling_roms;
  private RomUser rom_user;
  private List<UserSave> user_saves;
  private List<UserState> user_states;
  private List<UserScreenshot> user_screenshots;
  private List<UserNote> user_notes;
  private List<UserCollection> user_collections;
  private String sort_comparator;

  @Override
  public String toString() {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    return gson.toJson(this);
  }

  @Data
  public static class IgdbMetadata {
    private String total_rating;
    private String aggregated_rating;
    private long first_release_date;
    private List<String> genres;
    private List<String> franchises;
    private List<String> alternative_names;
    private List<String> collections;
    private List<String> companies;
    private List<String> game_modes;
    private List<IgdbPlatform> platforms;
    private List<IgdbExpansion> expansions;
    private List<IgdbDlc> dlcs;
    private List<IgdbRemaster> remasters;
    private List<IgdbRemake> remakes;
    private List<IgdbExpandedGame> expanded_games;
    private List<IgdbPort> ports;
    private List<IgdbSimilarGame> similar_games;

    @Override
    public String toString() {
      Gson gson = new GsonBuilder().setPrettyPrinting().create();
      return gson.toJson(this);
    }
  }

  @Data
  public static class IgdbPlatform {
    private int igdb_id;
    private String name;

    @Override
    public String toString() {
      Gson gson = new GsonBuilder().setPrettyPrinting().create();
      return gson.toJson(this);
    }
  }

  @Data
  public static class IgdbExpansion {
    private int id;
    private String name;
    private String slug;
    private String type;
    private String cover_url;

    @Override
    public String toString() {
      Gson gson = new GsonBuilder().setPrettyPrinting().create();
      return gson.toJson(this);
    }
  }

  @Data
  public static class IgdbDlc {
    private int id;
    private String name;
    private String slug;
    private String type;
    private String cover_url;

    @Override
    public String toString() {
      Gson gson = new GsonBuilder().setPrettyPrinting().create();
      return gson.toJson(this);
    }
  }

  @Data
  public static class IgdbRemaster {
    private int id;
    private String name;
    private String slug;
    private String type;
    private String cover_url;

    @Override
    public String toString() {
      Gson gson = new GsonBuilder().setPrettyPrinting().create();
      return gson.toJson(this);
    }
  }

  @Data
  public static class IgdbRemake {
    private int id;
    private String name;
    private String slug;
    private String type;
    private String cover_url;

    @Override
    public String toString() {
      Gson gson = new GsonBuilder().setPrettyPrinting().create();
      return gson.toJson(this);
    }
  }

  @Data
  public static class IgdbExpandedGame {
    private int id;
    private String name;
    private String slug;
    private String type;
    private String cover_url;

    @Override
    public String toString() {
      Gson gson = new GsonBuilder().setPrettyPrinting().create();
      return gson.toJson(this);
    }
  }

  @Data
  public static class IgdbPort {
    private int id;
    private String name;
    private String slug;
    private String type;
    private String cover_url;

    @Override
    public String toString() {
      Gson gson = new GsonBuilder().setPrettyPrinting().create();
      return gson.toJson(this);
    }
  }

  @Data
  public static class IgdbSimilarGame {
    private int id;
    private String name;
    private String slug;
    private String type;
    private String cover_url;

    @Override
    public String toString() {
      Gson gson = new GsonBuilder().setPrettyPrinting().create();
      return gson.toJson(this);
    }
  }

  @Data
  public static class MobyMetadata {
    private String moby_score;
    private List<String> genres;
    private List<String> alternate_titles;
    private List<MobyPlatform> platforms;

    @Override
    public String toString() {
      Gson gson = new GsonBuilder().setPrettyPrinting().create();
      return gson.toJson(this);
    }
  }

  @Data
  public static class MobyPlatform {
    private int moby_id;
    private String name;

    @Override
    public String toString() {
      Gson gson = new GsonBuilder().setPrettyPrinting().create();
      return gson.toJson(this);
    }
  }

  @Data
  public static class File {
    private String filename;
    private long size;
    private long last_modified;

    @Override
    public String toString() {
      Gson gson = new GsonBuilder().setPrettyPrinting().create();
      return gson.toJson(this);
    }
  }

  @Data
  public static class SiblingRom {
    private int id;
    private int igdb_id;
    private int sgdb_id;
    private int moby_id;
    private int platform_id;
    private String platform_slug;
    private String platform_name;
    private String file_name;
    private String file_name_no_tags;
    private String file_name_no_ext;
    private String file_extension;
    private String file_path;
    private long file_size_bytes;
    private String name;
    private String slug;
    private String summary;
    private long first_release_date;
    private List<String> alternative_names;
    private List<String> genres;
    private List<String> franchises;
    private List<String> collections;
    private List<String> companies;
    private List<String> game_modes;
    private IgdbMetadata igdb_metadata;
    private MobyMetadata moby_metadata;
    private String path_cover_s;
    private String path_cover_l;
    private boolean has_cover;
    private String url_cover;
    private String revision;
    private List<String> regions;
    private List<String> languages;
    private List<String> tags;
    private boolean multi;
    private List<File> files;
    private String crc_hash;
    private String md5_hash;
    private String sha1_hash;
    private String full_path;
    private String created_at;
    private String updated_at;
    private String sort_comparator;

    @Override
    public String toString() {
      Gson gson = new GsonBuilder().setPrettyPrinting().create();
      return gson.toJson(this);
    }
  }

  @Data
  public static class RomUser {
    private int id;
    private int user_id;
    private int rom_id;
    private String created_at;
    private String updated_at;
    private String note_raw_markdown;
    private boolean note_is_public;
    private boolean is_main_sibling;
    private String user__username;

    @Override
    public String toString() {
      Gson gson = new GsonBuilder().setPrettyPrinting().create();
      return gson.toJson(this);
    }
  }

  @Data
  public static class UserSave {
    private int id;
    private int rom_id;
    private int user_id;
    private String file_name;
    private String file_name_no_tags;
    private String file_name_no_ext;
    private String file_extension;
    private String file_path;
    private long file_size_bytes;
    private String full_path;
    private String download_path;
    private String created_at;
    private String updated_at;
    private String emulator;
    private Screenshot screenshot;

    @Override
    public String toString() {
      Gson gson = new GsonBuilder().setPrettyPrinting().create();
      return gson.toJson(this);
    }

    @Data
    public static class Screenshot {
      private int id;
      private int rom_id;
      private int user_id;
      private String file_name;
      private String file_name_no_tags;
      private String file_name_no_ext;
      private String file_extension;
      private String file_path;
      private long file_size_bytes;
      private String full_path;
      private String download_path;
      private String created_at;
      private String updated_at;

      @Override
      public String toString() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
      }
    }
  }

  @Data
  public static class UserState {
    private int id;
    private int rom_id;
    private int user_id;
    private String file_name;
    private String file_name_no_tags;
    private String file_name_no_ext;
    private String file_extension;
    private String file_path;
    private long file_size_bytes;
    private String full_path;
    private String download_path;
    private String created_at;
    private String updated_at;
    private String emulator;
    private Screenshot screenshot;

    @Override
    public String toString() {
      Gson gson = new GsonBuilder().setPrettyPrinting().create();
      return gson.toJson(this);
    }

    @Data
    public static class Screenshot {
      private int id;
      private int rom_id;
      private int user_id;
      private String file_name;
      private String file_name_no_tags;
      private String file_name_no_ext;
      private String file_extension;
      private String file_path;
      private long file_size_bytes;
      private String full_path;
      private String download_path;
      private String created_at;
      private String updated_at;

      @Override
      public String toString() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
      }
    }
  }

  @Data
  public static class UserScreenshot {
    private int id;
    private int rom_id;
    private int user_id;
    private String file_name;
    private String file_name_no_tags;
    private String file_name_no_ext;
    private String file_extension;
    private String file_path;
    private long file_size_bytes;
    private String full_path;
    private String download_path;
    private String created_at;
    private String updated_at;

    @Override
    public String toString() {
      Gson gson = new GsonBuilder().setPrettyPrinting().create();
      return gson.toJson(this);
    }
  }

  @Data
  public static class UserNote {
    private int user_id;
    private String username;
    private String note_raw_markdown;

    @Override
    public String toString() {
      Gson gson = new GsonBuilder().setPrettyPrinting().create();
      return gson.toJson(this);
    }
  }

  @Data
  public static class UserCollection {
    private int id;
    private String name;
    private String description;
    private String path_cover_l;
    private String path_cover_s;
    private boolean has_cover;
    private String url_cover;
    private List<Integer> roms;
    private int rom_count;
    private int user_id;
    private String user__username;
    private boolean is_public;
    private String created_at;
    private String updated_at;

    @Override
    public String toString() {
      Gson gson = new GsonBuilder().setPrettyPrinting().create();
      return gson.toJson(this);
    }
  }
}

