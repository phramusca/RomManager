/*
 * Copyright (C) 2026 phramusca ( https://github.com/phramusca/RomManager/ )
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
import java.util.concurrent.CountDownLatch;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javax.swing.JFrame;
import rommanager.utils.LogManager;

public class DialogBios {

    private DialogBios() {
    }

    public static void main(JFrame parent) {
        final CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(DialogBios.class.getResource("/rommanager/main/DialogBios.fxml"));
                BorderPane root = loader.load();
                DialogBiosController controller = loader.getController();

                Stage dialogStage = new Stage();
                if (parent != null) {
                    dialogStage.initOwner(null);
                }
                dialogStage.initModality(Modality.APPLICATION_MODAL);
                dialogStage.setTitle("BIOS Manager");
                Scene scene = new Scene(root, 980, 720);
                scene.getStylesheets().add(DialogBios.class.getResource("/rommanager/main/DialogBios.css").toExternalForm());
                dialogStage.setScene(scene);
                dialogStage.setMinWidth(860);
                dialogStage.setMinHeight(640);
                controller.setStage(dialogStage);
                controller.loadPaths();
                dialogStage.showAndWait();
            } catch (IOException ex) {
                LogManager.getInstance().error(DialogBios.class, "Error loading DialogBios FXML", ex);
            } finally {
                latch.countDown();
            }
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
