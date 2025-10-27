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
        GameComparisonResult result = gamelist.compareGame(localGame, remoteGame);
        Game game = result.getGame();

        // Should use remote data as base (more complete metadata) but override favorite with local preference
        assertEquals(remoteGame.getPath(), game.getPath());
        assertEquals(remoteGame.getName(), game.getName()); // Name follows "Plus récent" rule - but in fallback mode (lastModifiedDate=0), recalbox takes precedence
        assertEquals(remoteGame.getDesc(), game.getDesc());
        assertEquals(remoteGame.getImage(), game.getImage());
        assertEquals(remoteGame.getVideo(), game.getVideo());
        assertEquals(remoteGame.getThumbnail(), game.getThumbnail());
        assertEquals(remoteGame.getRating(), game.getRating());
        assertEquals(remoteGame.getReleaseDate(), game.getReleaseDate());
        assertEquals(remoteGame.getDeveloper(), game.getDeveloper());
        assertEquals(remoteGame.getPublisher(), game.getPublisher());
        assertEquals(remoteGame.getGenre(), game.getGenre());
        assertEquals(remoteGame.getGenreId(), game.getGenreId());
        assertEquals(remoteGame.getPlayers(), game.getPlayers());
        assertEquals(0, game.getPlaycount()); // Should take from Recalbox (0)
        assertEquals("", game.getLastplayed()); // Should take from Recalbox (empty)
        assertEquals(1234567890L, game.getTimestamp()); // Should take from Recalbox (1234567890L)
        assertEquals(remoteGame.getRatio(), game.getRatio());
        assertEquals(remoteGame.getRegion(), game.getRegion());

        // In fallback mode (lastModifiedDate=0), recalbox takes precedence
        assertFalse(game.isFavorite(), "In fallback mode, recalbox takes precedence");
        assertFalse(game.isHidden(), "Hidden should remain false");
        assertFalse(game.isAdult(), "Adult should remain false");
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
        GameComparisonResult result = gamelist.compareGame(localGame, remoteGame);
        Game game = result.getGame();

        assertFalse(game.isHidden(), "In fallback mode, recalbox takes precedence");
        assertFalse(game.isFavorite(), "Favorite should remain false");
        assertFalse(game.isAdult(), "Adult should remain false");
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
        GameComparisonResult result = gamelist.compareGame(localGame, remoteGame);
        Game game = result.getGame();

        assertFalse(game.isAdult(), "In fallback mode, recalbox takes precedence");
        assertFalse(game.isFavorite(), "Favorite should remain false");
        assertFalse(game.isHidden(), "Hidden should remain false");
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
        GameComparisonResult result = gamelist.compareGame(localGame, remoteGame);
        Game game = result.getGame();

        // All local preferences should take precedence
        assertFalse(game.isFavorite(), "In fallback mode, recalbox takes precedence");
        assertFalse(game.isHidden(), "In fallback mode, recalbox takes precedence");
        assertFalse(game.isAdult(), "In fallback mode, recalbox takes precedence");

        // Other data should come from remote, but name follows "Plus récent" rule
        assertEquals(remoteGame.getName(), game.getName()); // Name follows "Plus récent" rule - but in fallback mode (lastModifiedDate=0), recalbox takes precedence
        assertEquals(remoteGame.getDesc(), game.getDesc());
        assertEquals(remoteGame.getImage(), game.getImage());
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
        GameComparisonResult result = gamelist.compareGame(localGame, remoteGame);
        Game game = result.getGame();

        // Should use remote data as base (more complete metadata) and merge play statistics
        assertEquals(remoteGame.getPath(), game.getPath());
        assertEquals(remoteGame.getName(), game.getName()); // Name follows "Plus récent" rule - but in fallback mode (lastModifiedDate=0), recalbox takes precedence
        assertEquals(remoteGame.getDesc(), game.getDesc());
        assertEquals(remoteGame.getImage(), game.getImage());
        assertEquals(remoteGame.getVideo(), game.getVideo());
        assertEquals(remoteGame.getThumbnail(), game.getThumbnail());
        assertEquals(remoteGame.getRating(), game.getRating());
        assertEquals(remoteGame.getReleaseDate(), game.getReleaseDate());
        assertEquals(remoteGame.getDeveloper(), game.getDeveloper());
        assertEquals(remoteGame.getPublisher(), game.getPublisher());
        assertEquals(remoteGame.getGenre(), game.getGenre());
        assertEquals(remoteGame.getGenreId(), game.getGenreId());
        assertEquals(remoteGame.getPlayers(), game.getPlayers());
        assertEquals(0, game.getPlaycount()); // Should take from Recalbox (0)
        assertEquals("", game.getLastplayed()); // Should take from Recalbox (empty)
        assertEquals(1234567890L, game.getTimestamp()); // Should take from Recalbox (1234567890L)
        assertEquals(remoteGame.getRatio(), game.getRatio());
        assertEquals(remoteGame.getRegion(), game.getRegion());
        assertEquals(remoteGame.isFavorite(), game.isFavorite());
        assertEquals(remoteGame.isHidden(), game.isHidden());
        assertEquals(remoteGame.isAdult(), game.isAdult());
    }
    @Test
    public void testCompareGame_TimeplayedAndLastModifiedDate() {
        // Create a remote game (from Recalbox) with timeplayed=3600 (1 hour)
        Game remoteGame = new Game("/path/to/rom.zip", "hash123", "Remote Name", "Description",
                "/path/to/image.png", "/path/to/video.mp4", "/path/to/thumb.png", 4.5f,
                "1990-01-01", "Developer", "Publisher", "Action", 
                "25", "1-2", 0, "", false, 
                1234567890L, false, false, "4/3", "US", 
                3600, 1234567890L);

        // Create a local game (from RomManager) with timeplayed=7200 (2 hours) and lastModifiedDate=1234567891L
        Game localGame = new Game("/path/to/rom.zip", "hash123", "Local Name", "Local Description",
                "/path/to/local/image.png", "/path/to/local/video.mp4", "/path/to/local/thumb.png", 3.0f,
                "1991-01-01", "Local Developer", "Local Publisher", "Adventure", 
                "26", "1", 5, "2023-01-01", false, 
                1234567891L, false, false, "16/9", "EU", 
                7200, 1234567891L);

        Gamelist gamelist = new Gamelist(null);
        GameComparisonResult result = gamelist.compareGame(localGame, remoteGame);
        Game game = result.getGame();

        // Should use remote data as base (more complete metadata) but override timeplayed with local value
        assertEquals(remoteGame.getPath(), game.getPath());
        assertEquals(localGame.getName(), game.getName()); // Name follows "Plus récent" rule - local is newer (1234567891L > 1234567890L)
        assertEquals(remoteGame.getDesc(), game.getDesc());
        assertEquals(remoteGame.getImage(), game.getImage());
        assertEquals(remoteGame.getVideo(), game.getVideo());
        assertEquals(remoteGame.getThumbnail(), game.getThumbnail());
        assertEquals(remoteGame.getRating(), game.getRating());
        assertEquals(remoteGame.getReleaseDate(), game.getReleaseDate());
        assertEquals(remoteGame.getDeveloper(), game.getDeveloper());
        assertEquals(remoteGame.getPublisher(), game.getPublisher());
        assertEquals(remoteGame.getGenre(), game.getGenre());
        assertEquals(remoteGame.getGenreId(), game.getGenreId());
        assertEquals(remoteGame.getPlayers(), game.getPlayers());
        assertEquals(0, game.getPlaycount()); // Should take from Recalbox (0)
        assertEquals("", game.getLastplayed()); // Should take from Recalbox (empty)
        assertEquals(1234567890L, game.getTimestamp()); // Should take from Recalbox (1234567890L)
        assertEquals(remoteGame.getRatio(), game.getRatio());
        assertEquals(remoteGame.getRegion(), game.getRegion());

        // Timeplayed should come from remote (not modifiable locally)
        assertEquals(3600, game.getTimeplayed(), "Timeplayed should come from remote");

        // LastModifiedDate should come from local (1234567891L) since local has lastModifiedDate > 0
        assertEquals(1234567891L, game.getLastModifiedDate(), "LastModifiedDate should come from local");

        // Local preferences should remain unchanged
        assertFalse(game.isFavorite(), "Favorite should remain false");
        assertFalse(game.isHidden(), "Hidden should remain false");
        assertFalse(game.isAdult(), "Adult should remain false");
    }
}