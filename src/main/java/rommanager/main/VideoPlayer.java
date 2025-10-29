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
import javafx.scene.control.Label;
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
    private String currentVideoPath;
    
    public VideoPlayer() {
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
        
        HBox controls = new HBox(8, playButton, pauseButton, stopButton);
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
    
    public void loadVideo(String videoPath) {
        if (videoPath == null || videoPath.trim().isEmpty()) {
            Platform.runLater(() -> {
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
                
                // Set up event handlers
                        mediaPlayer.setOnReady(() -> {
                            statusLabel.setText("Ready: " + videoFile.getName());
                            playButton.setDisable(false);
                            pauseButton.setDisable(true);
                            stopButton.setDisable(true);
                            
                            // Adjust video size based on actual video dimensions
                            adjustVideoSize();
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
                    statusLabel.setText("Error playing video: " + videoFile.getName());
                    playButton.setDisable(true);
                    pauseButton.setDisable(true);
                    stopButton.setDisable(true);
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
}
