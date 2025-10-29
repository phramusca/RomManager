package rommanager.main;

import rommanager.utils.Options;

/**
 * Manages video player preferences (auto-play, mute, volume, etc.)
 * Uses the existing Options system for consistency
 * @author raph
 */
public class VideoPlayerPreferences {
    
    private Options options;
    
    public VideoPlayerPreferences() {
        this.options = new Options("RomManager.properties");
        this.options.read();
    }
    
    public void savePreferences() {
        options.save();
    }
    
    public boolean isAutoPlayEnabled() {
        String value = options.get("video.autoplay");
        if ("{Missing}".equals(value)) {
            return false; // Default value
        }
        return Boolean.parseBoolean(value);
    }
    
    public void setAutoPlayEnabled(boolean enabled) {
        options.set("video.autoplay", String.valueOf(enabled));
    }
    
    public boolean isMuteEnabled() {
        String value = options.get("video.mute");
        if ("{Missing}".equals(value)) {
            return false; // Default value
        }
        return Boolean.parseBoolean(value);
    }
    
    public void setMuteEnabled(boolean enabled) {
        options.set("video.mute", String.valueOf(enabled));
    }
    
    public double getVolume() {
        String value = options.get("video.volume");
        if ("{Missing}".equals(value)) {
            return 0.5; // Default value
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return 0.5; // Default value
        }
    }
    
    public void setVolume(double volume) {
        options.set("video.volume", String.valueOf(volume));
    }
}
