package rommanager.main;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Gamelist.compareGame method
 */
public class GamelistTest {

    @Test
    public void testCompareGame_LocalFavoriteTakesPrecedence() {
        // Create a remote game (from Recalbox)
        Game remoteGame = new Game("/path/to/rom.zip", "hash123", "Remote Name", "Description",
                "/path/to/image.png", "/path/to/video.mp4", "/path/to/thumb.png", 4.5f,
                "1990-01-01", "Developer", "Publisher", "Action", "25", "1-2", 0, "",
                false, 1234567890L, false, false, "4/3", "US");

        // Create a local game (from RomManager) with favorite=true
        Game localGame = new Game("/path/to/rom.zip", "hash123", "Local Name", "Local Description",
                "/path/to/local/image.png", "/path/to/local/video.mp4", "/path/to/local/thumb.png", 3.0f,
                "1991-01-01", "Local Developer", "Local Publisher", "Adventure", "26", "1", 5, "2023-01-01",
                true, 1234567891L, false, false, "16/9", "EU");

        // We don't need the file for this test, just test the compareGame method directly
        Gamelist gamelist = new Gamelist(null);
        Game result = gamelist.compareGame(localGame, remoteGame);

        // Should use remote data as base (more complete metadata) but override favorite with local preference
        assertEquals(remoteGame.getPath(), result.getPath());
        assertEquals(remoteGame.getName(), result.getName()); // Name comes from remote (more complete)
        assertEquals(remoteGame.getDesc(), result.getDesc());
        assertEquals(remoteGame.getImage(), result.getImage());
        assertEquals(remoteGame.getVideo(), result.getVideo());
        assertEquals(remoteGame.getThumbnail(), result.getThumbnail());
        assertEquals(remoteGame.getRating(), result.getRating());
        assertEquals(remoteGame.getReleaseDate(), result.getReleaseDate());
        assertEquals(remoteGame.getDeveloper(), result.getDeveloper());
        assertEquals(remoteGame.getPublisher(), result.getPublisher());
        assertEquals(remoteGame.getGenre(), result.getGenre());
        assertEquals(remoteGame.getGenreId(), result.getGenreId());
        assertEquals(remoteGame.getPlayers(), result.getPlayers());
        assertEquals(5, result.getPlaycount()); // Should take max of local (5) and remote (0)
        assertEquals("2023-01-01", result.getLastplayed()); // Should take most recent date
        assertEquals(1234567891L, result.getTimestamp()); // Should take most recent timestamp
        assertEquals(remoteGame.getRatio(), result.getRatio());
        assertEquals(remoteGame.getRegion(), result.getRegion());

        // Local preferences should override
        assertTrue(result.isFavorite(), "Local favorite should take precedence");
        assertFalse(result.isHidden(), "Hidden should remain false");
        assertFalse(result.isAdult(), "Adult should remain false");
    }

    @Test
    public void testCompareGame_LocalHiddenTakesPrecedence() {
        Game remoteGame = new Game("/path/to/rom.zip", "hash123", "Remote Name", "Description",
                "/path/to/image.png", "/path/to/video.mp4", "/path/to/thumb.png", 4.5f,
                "1990-01-01", "Developer", "Publisher", "Action", "25", "1-2", 0, "",
                false, 1234567890L, false, false, "4/3", "US");

        Game localGame = new Game("/path/to/rom.zip", "hash123", "Local Name", "Local Description",
                "/path/to/local/image.png", "/path/to/local/video.mp4", "/path/to/local/thumb.png", 3.0f,
                "1991-01-01", "Local Developer", "Local Publisher", "Adventure", "26", "1", 5, "2023-01-01",
                false, 1234567891L, true, false, "16/9", "EU"); // hidden=true

        Gamelist gamelist = new Gamelist(null);
        Game result = gamelist.compareGame(localGame, remoteGame);

        assertTrue(result.isHidden(), "Local hidden should take precedence");
        assertFalse(result.isFavorite(), "Favorite should remain false");
        assertFalse(result.isAdult(), "Adult should remain false");
    }

    @Test
    public void testCompareGame_LocalAdultTakesPrecedence() {
        Game remoteGame = new Game("/path/to/rom.zip", "hash123", "Remote Name", "Description",
                "/path/to/image.png", "/path/to/video.mp4", "/path/to/thumb.png", 4.5f,
                "1990-01-01", "Developer", "Publisher", "Action", "25", "1-2", 0, "",
                false, 1234567890L, false, false, "4/3", "US");

        Game localGame = new Game("/path/to/rom.zip", "hash123", "Local Name", "Local Description",
                "/path/to/local/image.png", "/path/to/local/video.mp4", "/path/to/local/thumb.png", 3.0f,
                "1991-01-01", "Local Developer", "Local Publisher", "Adventure", "26", "1", 5, "2023-01-01",
                false, 1234567891L, false, true, "16/9", "EU"); // adult=true

        Gamelist gamelist = new Gamelist(null);
        Game result = gamelist.compareGame(localGame, remoteGame);

        assertTrue(result.isAdult(), "Local adult should take precedence");
        assertFalse(result.isFavorite(), "Favorite should remain false");
        assertFalse(result.isHidden(), "Hidden should remain false");
    }

    @Test
    public void testCompareGame_MultipleLocalPreferences() {
        Game remoteGame = new Game("/path/to/rom.zip", "hash123", "Remote Name", "Description",
                "/path/to/image.png", "/path/to/video.mp4", "/path/to/thumb.png", 4.5f,
                "1990-01-01", "Developer", "Publisher", "Action", "25", "1-2", 0, "",
                false, 1234567890L, false, false, "4/3", "US");

        Game localGame = new Game("/path/to/rom.zip", "hash123", "Local Name", "Local Description",
                "/path/to/local/image.png", "/path/to/local/video.mp4", "/path/to/local/thumb.png", 3.0f,
                "1991-01-01", "Local Developer", "Local Publisher", "Adventure", "26", "1", 5, "2023-01-01",
                true, 1234567891L, true, true, "16/9", "EU"); // favorite, hidden, adult all true

        Gamelist gamelist = new Gamelist(null);
        Game result = gamelist.compareGame(localGame, remoteGame);

        // All local preferences should take precedence
        assertTrue(result.isFavorite(), "Local favorite should take precedence");
        assertTrue(result.isHidden(), "Local hidden should take precedence");
        assertTrue(result.isAdult(), "Local adult should take precedence");

        // Other data should come from remote
        assertEquals(remoteGame.getName(), result.getName());
        assertEquals(remoteGame.getDesc(), result.getDesc());
        assertEquals(remoteGame.getImage(), result.getImage());
    }

    @Test
    public void testCompareGame_NoLocalPreferences() {
        Game remoteGame = new Game("/path/to/rom.zip", "hash123", "Remote Name", "Description",
                "/path/to/image.png", "/path/to/video.mp4", "/path/to/thumb.png", 4.5f,
                "1990-01-01", "Developer", "Publisher", "Action", "25", "1-2", 0, "",
                false, 1234567890L, false, false, "4/3", "US");

        Game localGame = new Game("/path/to/rom.zip", "hash123", "Local Name", "Local Description",
                "/path/to/local/image.png", "/path/to/local/video.mp4", "/path/to/local/thumb.png", 3.0f,
                "1991-01-01", "Local Developer", "Local Publisher", "Adventure", "26", "1", 5, "2023-01-01",
                false, 1234567891L, false, false, "16/9", "EU"); // all preferences false

        Gamelist gamelist = new Gamelist(null);
        Game result = gamelist.compareGame(localGame, remoteGame);

        // Should use remote data as base (more complete metadata) and merge play statistics
        assertEquals(remoteGame.getPath(), result.getPath());
        assertEquals(remoteGame.getName(), result.getName());
        assertEquals(remoteGame.getDesc(), result.getDesc());
        assertEquals(remoteGame.getImage(), result.getImage());
        assertEquals(remoteGame.getVideo(), result.getVideo());
        assertEquals(remoteGame.getThumbnail(), result.getThumbnail());
        assertEquals(remoteGame.getRating(), result.getRating());
        assertEquals(remoteGame.getReleaseDate(), result.getReleaseDate());
        assertEquals(remoteGame.getDeveloper(), result.getDeveloper());
        assertEquals(remoteGame.getPublisher(), result.getPublisher());
        assertEquals(remoteGame.getGenre(), result.getGenre());
        assertEquals(remoteGame.getGenreId(), result.getGenreId());
        assertEquals(remoteGame.getPlayers(), result.getPlayers());
        assertEquals(5, result.getPlaycount()); // Should take max of local (5) and remote (0)
        assertEquals("2023-01-01", result.getLastplayed()); // Should take most recent date
        assertEquals(1234567891L, result.getTimestamp()); // Should take most recent timestamp
        assertEquals(remoteGame.getRatio(), result.getRatio());
        assertEquals(remoteGame.getRegion(), result.getRegion());
        assertEquals(remoteGame.isFavorite(), result.isFavorite());
        assertEquals(remoteGame.isHidden(), result.isHidden());
        assertEquals(remoteGame.isAdult(), result.isAdult());
    }
}