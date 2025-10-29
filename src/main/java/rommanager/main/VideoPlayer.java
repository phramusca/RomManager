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
package rommanager.main;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import java.io.File;

/**
 * Video player component using JavaFX embedded in Swing
 * @author raph
 */
public class VideoPlayer extends JFXPanel {
    
    private MediaPlayer mediaPlayer;
    private MediaView mediaView;
    private Label statusLabel;
    private Button playButton;
    private Button pauseButton;
    private Button stopButton;
    private CheckBox autoPlayCheckBox;
    private CheckBox muteCheckBox;
    private Slider volumeSlider;
    private String currentVideoPath;
    private boolean autoPlayEnabled = false;
    private boolean muteEnabled = false;
    private double volumeLevel = 0.5;
    private VideoPlayerPreferences preferences;
    
    public VideoPlayer() {
        this.preferences = new VideoPlayerPreferences();
        loadPreferences();
        Platform.runLater(() -> {
            initFX();
        });
    }
    
    private void initFX() {
        // Create JavaFX scene
        BorderPane root = new BorderPane();
        
        // Create media view
            mediaView = new MediaView();
            mediaView.setFitWidth(640);
            mediaView.setFitHeight(360); // 16:9 ratio
            mediaView.setPreserveRatio(true);
            mediaView.setSmooth(true);
        
        // Create control buttons
        playButton = new Button("▶ Play");
        playButton.setFont(Font.font("Arial", 11));
        playButton.setPrefWidth(80);
        playButton.setOnAction(e -> playVideo());
        
        pauseButton = new Button("⏸ Pause");
        pauseButton.setFont(Font.font("Arial", 11));
        pauseButton.setPrefWidth(80);
        pauseButton.setOnAction(e -> pauseVideo());
        pauseButton.setDisable(true);
        
        stopButton = new Button("⏹ Stop");
        stopButton.setFont(Font.font("Arial", 11));
        stopButton.setPrefWidth(80);
        stopButton.setOnAction(e -> stopVideo());
        stopButton.setDisable(true);
        
        // Create auto-play checkbox
        autoPlayCheckBox = new CheckBox("Auto Play");
        autoPlayCheckBox.setFont(Font.font("Arial", 10));
        autoPlayCheckBox.setSelected(autoPlayEnabled);
        autoPlayCheckBox.setOnAction(e -> {
            setAutoPlay(autoPlayCheckBox.isSelected());
        });
        
        // Create mute checkbox
        muteCheckBox = new CheckBox("Mute");
        muteCheckBox.setFont(Font.font("Arial", 10));
        muteCheckBox.setSelected(muteEnabled);
        muteCheckBox.setOnAction(e -> {
            muteEnabled = muteCheckBox.isSelected();
            if (mediaPlayer != null) {
                mediaPlayer.setMute(muteEnabled);
            }
            savePreferences();
        });
        
        // Create volume slider
        volumeSlider = new Slider(0, 1, volumeLevel);
        volumeSlider.setPrefWidth(100);
        volumeSlider.setShowTickLabels(true);
        volumeSlider.setShowTickMarks(true);
        volumeSlider.setMajorTickUnit(0.25);
        volumeSlider.setMinorTickCount(0);
        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            volumeLevel = newVal.doubleValue();
            if (mediaPlayer != null) {
                mediaPlayer.setVolume(volumeLevel);
            }
            savePreferences();
        });
        
        HBox controls = new HBox(8, playButton, pauseButton, stopButton, autoPlayCheckBox, muteCheckBox, volumeSlider);
        controls.setStyle("-fx-padding: 8; -fx-alignment: center; -fx-background-color: #f0f0f0;");
        
        // Create status label
        statusLabel = new Label("No video selected");
        statusLabel.setFont(Font.font("Arial", 10));
        statusLabel.setTextFill(Color.GRAY);
        statusLabel.setStyle("-fx-padding: 5; -fx-background-color: #f8f8f8;");
        
        // Layout
        root.setCenter(mediaView);
        root.setBottom(controls);
        root.setTop(statusLabel);
        
        // Set minimum size and make it resizable
        root.setMinSize(640, 400);
        root.setMaxSize(800, 600);
        
        Scene scene = new Scene(root, 640, 400);
        scene.setFill(Color.BLACK);
        setScene(scene);
    }
     
    /**
     * Load video with caching support
     * @param gameName Game name (used as cache key)
     * @param videoPath Original video path on Recalbox
     */
    public void loadVideoWithCache(String gameName, String videoPath) {
        if (gameName == null || gameName.trim().isEmpty()) {
            loadVideo(videoPath);
            return;
        }
        
        // If no video path provided, clear the player
        if (videoPath == null || videoPath.trim().isEmpty()) {
            loadVideo(null);
            return;
        }
        
        // Check if video is cached, if not copy it from Recalbox
        if (BufferVideo.checkOrGetVideo(gameName, videoPath)) {
            String cachedPath = BufferVideo.getCachedVideoPath(gameName);
            if (cachedPath != null) {
                loadVideo(cachedPath);
                return;
            }
        }
        
        // Fallback to original path
        loadVideo(videoPath);
    }
    
    public void loadVideo(String videoPath) {
        if (videoPath == null || videoPath.trim().isEmpty()) {
            Platform.runLater(() -> {
                // Stop current video if playing
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.dispose();
                    mediaPlayer = null;
                }
                mediaView.setMediaPlayer(null);
                
                statusLabel.setText("No video available");
                playButton.setDisable(true);
                pauseButton.setDisable(true);
                stopButton.setDisable(true);
            });
            return;
        }
        
        File videoFile = new File(videoPath);
        if (!videoFile.exists()) {
            Platform.runLater(() -> {
                statusLabel.setText("Video file not found: " + videoFile.getName());
                playButton.setDisable(true);
                pauseButton.setDisable(true);
                stopButton.setDisable(true);
            });
            return;
        }
        
        currentVideoPath = videoPath;
        Platform.runLater(() -> {
            try {
                // Stop current video if playing
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.dispose();
                }
                
                // Create new media player
                        Media media = new Media(videoFile.toURI().toString());
                        mediaPlayer = new MediaPlayer(media);
                        mediaView.setMediaPlayer(mediaPlayer);
                        
                        // Apply audio settings
                        mediaPlayer.setVolume(volumeLevel);
                        mediaPlayer.setMute(muteEnabled);
                
                // Set up event handlers
                        mediaPlayer.setOnReady(() -> {
                            statusLabel.setText("Ready: " + videoFile.getName());
                            playButton.setDisable(false);
                            pauseButton.setDisable(true);
                            stopButton.setDisable(true);
                            
                            // Adjust video size based on actual video dimensions
                            adjustVideoSize();
                            
                            // Auto-play if enabled
                            if (autoPlayEnabled) {
                                playVideo();
                            }
                        });
                
                mediaPlayer.setOnPlaying(() -> {
                    statusLabel.setText("Playing: " + videoFile.getName());
                    playButton.setDisable(true);
                    pauseButton.setDisable(false);
                    stopButton.setDisable(false);
                });
                
                mediaPlayer.setOnPaused(() -> {
                    statusLabel.setText("Paused: " + videoFile.getName());
                    playButton.setDisable(false);
                    pauseButton.setDisable(true);
                    stopButton.setDisable(false);
                });
                
                mediaPlayer.setOnStopped(() -> {
                    statusLabel.setText("Stopped: " + videoFile.getName());
                    playButton.setDisable(false);
                    pauseButton.setDisable(true);
                    stopButton.setDisable(true);
                });
                
                        mediaPlayer.setOnError(() -> {
                            statusLabel.setText("Error playing video: " + videoFile.getName() + " - Attempting conversion...");
                            playButton.setDisable(true);
                            pauseButton.setDisable(true);
                            stopButton.setDisable(true);
                            
                            // Try to convert the video
                            convertVideo(videoFile);
                        });
                
            } catch (Exception e) {
                statusLabel.setText("Error loading video: " + e.getMessage());
                playButton.setDisable(true);
                pauseButton.setDisable(true);
                stopButton.setDisable(true);
            }
        });
    }
    
    private void playVideo() {
        if (mediaPlayer != null) {
            if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                mediaPlayer.pause();
            } else {
                mediaPlayer.play();
            }
        }
    }
    
    private void pauseVideo() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }
    
    private void stopVideo() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }
    
    public void dispose() {
        Platform.runLater(() -> {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.dispose();
                mediaPlayer = null;
            }
        });
    }
    
    public boolean hasVideo() {
        return currentVideoPath != null && !currentVideoPath.trim().isEmpty();
    }
    
    public void setAutoPlay(boolean enabled) {
        this.autoPlayEnabled = enabled;
        if (autoPlayCheckBox != null) {
            Platform.runLater(() -> autoPlayCheckBox.setSelected(enabled));
        }
        
        // If auto-play is enabled and we have a loaded video, start playing
        if (enabled && mediaPlayer != null) {
            MediaPlayer.Status status = mediaPlayer.getStatus();
            if (status == MediaPlayer.Status.READY || status == MediaPlayer.Status.PAUSED) {
                Platform.runLater(() -> playVideo());
            }
        }
        // If auto-play is disabled and we're playing, stop
        else if (!enabled && mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            Platform.runLater(() -> pauseVideo());
        }
    }
    
    public boolean isAutoPlayEnabled() {
        return autoPlayEnabled;
    }
    
    public void setMute(boolean enabled) {
        this.muteEnabled = enabled;
        if (muteCheckBox != null) {
            Platform.runLater(() -> muteCheckBox.setSelected(enabled));
        }
        if (mediaPlayer != null) {
            mediaPlayer.setMute(enabled);
        }
    }
    
    public boolean isMuteEnabled() {
        return muteEnabled;
    }
    
    public void setVolume(double volume) {
        this.volumeLevel = Math.max(0.0, Math.min(1.0, volume));
        if (volumeSlider != null) {
            Platform.runLater(() -> volumeSlider.setValue(volumeLevel));
        }
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volumeLevel);
        }
    }
    
    public double getVolume() {
        return volumeLevel;
    }
    
    private void loadPreferences() {
        autoPlayEnabled = preferences.isAutoPlayEnabled();
        muteEnabled = preferences.isMuteEnabled();
        volumeLevel = preferences.getVolume();
    }
    
    private void savePreferences() {
        preferences.setAutoPlayEnabled(autoPlayEnabled);
        preferences.setMuteEnabled(muteEnabled);
        preferences.setVolume(volumeLevel);
        preferences.savePreferences();
    }
    
    private void adjustVideoSize() {
        if (mediaPlayer != null && mediaPlayer.getMedia() != null) {
            double videoWidth = mediaPlayer.getMedia().getWidth();
            double videoHeight = mediaPlayer.getMedia().getHeight();
            
            if (videoWidth > 0 && videoHeight > 0) {
                // Calculate aspect ratio
                double aspectRatio = videoWidth / videoHeight;
                
                // Set maximum dimensions
                double maxWidth = 640;
                double maxHeight = 360;
                
                double newWidth = maxWidth;
                double newHeight = maxWidth / aspectRatio;
                
                // If height exceeds max, scale down
                if (newHeight > maxHeight) {
                    newHeight = maxHeight;
                    newWidth = maxHeight * aspectRatio;
                }
                
                mediaView.setFitWidth(newWidth);
                mediaView.setFitHeight(newHeight);
                
                // Update status with video info
                String currentStatus = statusLabel.getText();
                statusLabel.setText(currentStatus + String.format(" (%dx%d)", (int)videoWidth, (int)videoHeight));
            }
        }
    }
    
    private void convertVideo(File originalFile) {
        // Check if ffmpeg is available
        if (!isFFmpegAvailable()) {
            Platform.runLater(() -> {
                statusLabel.setText("FFmpeg not available - cannot convert video");
                playButton.setDisable(false);
                pauseButton.setDisable(true);
                stopButton.setDisable(true);
            });
            return;
        }
        
        // Create converted file path in cache directory (same as images)
        String originalPath = originalFile.getAbsolutePath();
        String cacheDir = "cache/videos";
        new File(cacheDir).mkdirs();
        
        String fileName = originalFile.getName();
        String baseName = fileName.replaceFirst("\\.[^.]+$", "");
        String convertedPath = cacheDir + "/" + baseName + "_converted.mp4";
        File convertedFile = new File(convertedPath);
        
        // Check if converted file already exists and is newer than original
        if (convertedFile.exists() && convertedFile.lastModified() > originalFile.lastModified()) {
            Platform.runLater(() -> {
                statusLabel.setText("Using cached converted video: " + convertedFile.getName());
                loadVideo(convertedPath);
            });
            return;
        }
        
        statusLabel.setText("Converting video: " + originalFile.getName() + "...");
        
        // Run conversion in background thread
        new Thread(() -> {
            try {
                // Use ffmpeg to convert video to a compatible format
                ProcessBuilder pb = new ProcessBuilder(
                    "ffmpeg", "-i", originalPath,
                    "-c:v", "libx264", "-c:a", "aac", "-b:a", "128k",
                    "-movflags", "+faststart",
                    "-y", // Overwrite output file
                    convertedPath
                );
                
                Process process = pb.start();
                int exitCode = process.waitFor();
                
                Platform.runLater(() -> {
                    if (exitCode == 0 && convertedFile.exists()) {
                        statusLabel.setText("Conversion successful! Loading converted video...");
                        // Delete original file since we have the converted version
                        try {
                            originalFile.delete();
                        } catch (Exception e) {
                            // Ignore deletion errors
                        }
                        // Update cache to point to converted file
                        String gameName = getGameNameFromPath(originalPath);
                        if (gameName != null) {
                            BufferVideo.VIDEO_CACHE.put(gameName, convertedFile.getAbsolutePath());
                        }
                        // Load the converted video
                        loadVideo(convertedPath);
                    } else {
                        statusLabel.setText("Conversion failed: " + originalFile.getName());
                        playButton.setDisable(false);
                        pauseButton.setDisable(true);
                        stopButton.setDisable(true);
                    }
                });
                
            } catch (Exception e) {
                Platform.runLater(() -> {
                    statusLabel.setText("Conversion error: " + e.getMessage());
                    playButton.setDisable(false);
                    pauseButton.setDisable(true);
                    stopButton.setDisable(true);
                });
            }
        }).start();
    }
    
    private boolean isFFmpegAvailable() {
        try {
            ProcessBuilder pb = new ProcessBuilder("ffmpeg", "-version");
            Process process = pb.start();
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (Exception e) {
            return false;
        }
    }
    
    private String getGameNameFromPath(String videoPath) {
        if (videoPath == null) return null;
        File file = new File(videoPath);
        String fileName = file.getName();
        // Remove extension to get game name
        return fileName.replaceFirst("\\.[^.]+$", "");
    }
}
