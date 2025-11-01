package rommanager.main;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import rommanager.utils.LogManager;
import org.apache.commons.io.FilenameUtils;
import rommanager.utils.StringManager;

/**
 * Manages video file caching similar to BufferIcon
 * Copies videos from Recalbox to local cache for faster access
 * @author raph
 */
public class BufferVideo {
    
    public static final Map<String, String> VIDEO_CACHE = new HashMap<>();
    
    /**
     * Check if video is cached, if not copy it from Recalbox to cache
     * @param key Game name (used as cache key)
     * @param originalPath Original video path on Recalbox
     * @return true if video is available (cached or original), false otherwise
     */
    public static boolean checkOrGetVideo(String key, String originalPath) {
        if (VIDEO_CACHE.containsKey(key)) {
            return true;
        }
        
        File cacheFile = getCacheFile(key);
        if (cacheFile.exists()) {
            VIDEO_CACHE.put(key, cacheFile.getAbsolutePath());
            return true;
        }
        
        // Check for converted file
        File convertedFile = getConvertedFile(key);
        if (convertedFile.exists()) {
            VIDEO_CACHE.put(key, convertedFile.getAbsolutePath());
            return true;
        }
        
        if (originalPath != null && !originalPath.trim().isEmpty()) {
            File originalFile = new File(originalPath);
            if (originalFile.exists()) {
                try {
                    // Copy video to cache
                    Files.copy(originalFile.toPath(), cacheFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    VIDEO_CACHE.put(key, cacheFile.getAbsolutePath());
                    return true;
                } catch (IOException ex) {
                    LogManager.getInstance().error(BufferVideo.class, 
                        "Failed to copy video to cache: " + originalPath, ex);
                    return false;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Get cached video path
     * @param key Game name
     * @return Cached video path or null if not cached
     */
    public static String getCachedVideoPath(String key) {
        return VIDEO_CACHE.get(key);
    }
    
    /**
     * Get cache file path for a given key
     * @param key Game name
     * @return Cache file path
     */
    public static File getCacheFile(String key) {
        String safeKey = StringManager.removeIllegal(key);
        return new File(FilenameUtils.concat(FilenameUtils.concat("cache", "videos"), safeKey + ".mp4"));
    }
    
    /**
     * Get converted file path for a given key
     * @param key Game name
     * @return Converted file path
     */
    public static File getConvertedFile(String key) {
        String safeKey = StringManager.removeIllegal(key);
        return new File(FilenameUtils.concat(FilenameUtils.concat("cache", "videos"), safeKey + "_converted.mp4"));
    }
    
    /**
     * Clear video cache (for memory management)
     */
    public static void clearCache() {
        VIDEO_CACHE.clear();
    }
    
    /**
     * Get cache directory
     * @return Cache directory path
     */
    public static String getCacheDirectory() {
        return "cache/videos";
    }
}
