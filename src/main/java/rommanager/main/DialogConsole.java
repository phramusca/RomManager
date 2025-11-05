/*
 * Copyright (C) 2012 phramusca ( https://github.com/phramusca/JaMuz/ )
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

import java.io.IOException;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javax.swing.JFrame;
import rommanager.utils.LogManager;

/**
 * JavaFX dialog for console selection
 * @author phramusca ( https://github.com/phramusca/JaMuz/ )
 */
public class DialogConsole {
    
    private static DialogConsoleController controller;
    private static Stage dialogStage;
    private static boolean manualExit = true;
    
    /**
     * Open the console selection dialog
     * @param parent Parent window (Swing JFrame, can be null)
     * @param callback Callback interface
     * @param displayRefresh Whether to show refresh options
     * @param buttonString Text for the action button
     * @param displayFilter Whether to show filter options
     */
    public static void main(JFrame parent, ICallBackConsole callback, boolean displayRefresh, String buttonString, boolean displayFilter) {
        Platform.runLater(() -> {
            try {
                // Load FXML
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(DialogConsole.class.getResource("/rommanager/main/DialogConsole.fxml"));
                loader.setResources(ResourceBundle.getBundle("rommanager/main/Bundle"));
                
                BorderPane root = loader.load();
                controller = loader.getController();
                
                // Configure controller
                controller.setCallback(callback);
                controller.setDisplayRefresh(displayRefresh);
                controller.setDisplayFilter(displayFilter);
                controller.setButtonText(buttonString);
                controller.setManualExit(manualExit);
                
                // Create stage
                dialogStage = new Stage();
                if (parent != null) {
                    // Try to get JavaFX window from Swing parent (if embedded)
                    dialogStage.initOwner(null); // Could be improved to get actual JavaFX parent
                }
                dialogStage.initModality(Modality.APPLICATION_MODAL);
                dialogStage.setTitle("Console Selection");
                dialogStage.setScene(new Scene(root));
                dialogStage.setResizable(false);
                
                // Handle window close
                dialogStage.setOnCloseRequest((WindowEvent event) -> {
                    manualExit = true;
                    if (controller != null) {
                        controller.handleWindowClose();
                    }
                });
                
                // Show dialog
                dialogStage.showAndWait();
                
            } catch (IOException ex) {
                LogManager.getInstance().error(DialogConsole.class, "Error loading DialogConsole FXML", ex);
            }
        });
    }
}
