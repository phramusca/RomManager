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

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import rommanager.bios.BatoceraMode;
import rommanager.bios.BiosEntryResult;
import rommanager.bios.BiosPlatform;
import rommanager.bios.ICallBackBios;
import rommanager.bios.ProcessBios;
import rommanager.utils.Popup;

public class DialogBiosController implements Initializable {

    @FXML
    private TextField textFieldSourcePath;
    @FXML
    private Button buttonBrowseSource;
    @FXML
    private TabPane tabPanePlatform;
    @FXML
    private TextField textFieldRecalboxReport;
    @FXML
    private TextField textFieldRecalboxDestination;
    @FXML
    private TextField textFieldBatoceraReadme;
    @FXML
    private TextField textFieldBatoceraDestination;
    @FXML
    private RadioButton radioButtonBatoceraMissing;
    @FXML
    private RadioButton radioButtonBatoceraFull;
    @FXML
    private ToggleGroup toggleGroupBatoceraMode;
    @FXML
    private TableView<BiosEntryResult> tableViewResults;
    @FXML
    private TableColumn<BiosEntryResult, String> columnSystem;
    @FXML
    private TableColumn<BiosEntryResult, String> columnFile;
    @FXML
    private TableColumn<BiosEntryResult, String> columnPath;
    @FXML
    private TableColumn<BiosEntryResult, String> columnStatus;
    @FXML
    private TableColumn<BiosEntryResult, String> columnMessage;
    @FXML
    private TextArea textAreaLog;
    @FXML
    private Button buttonSavePaths;
    @FXML
    private Button buttonAnalyze;
    @FXML
    private Button buttonCopy;
    @FXML
    private Button buttonAbort;
    @FXML
    private Button buttonClose;

    private Stage stage;
    private ProcessBios processBios;
    private final ObservableList<BiosEntryResult> results = FXCollections.observableArrayList();
    private BiosPlatform activePlatform = BiosPlatform.RECALBOX;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tableViewResults.setItems(results);
        columnSystem.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getSystem()));
        columnFile.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getFileName()));
        columnPath.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getRelativePath(activePlatform)));
        columnStatus.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getStatus().getLabel()));
        columnMessage.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getMessage()));

        tabPanePlatform.getSelectionModel().selectedIndexProperty().addListener(
                (obs, oldIndex, newIndex) -> activePlatform = newIndex.intValue() == 0
                        ? BiosPlatform.RECALBOX
                        : BiosPlatform.BATOCERA);

        bindPathTooltip(textFieldSourcePath);
        bindPathTooltip(textFieldRecalboxReport);
        bindPathTooltip(textFieldRecalboxDestination);
        bindPathTooltip(textFieldBatoceraReadme);
        bindPathTooltip(textFieldBatoceraDestination);
    }

    private static void bindPathTooltip(TextField field) {
        field.textProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue == null || newValue.isBlank()) {
                field.setTooltip(null);
            } else {
                field.setTooltip(new javafx.scene.control.Tooltip(newValue));
            }
        });
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void loadPaths() {
        textFieldSourcePath.setText(option(BiosPlatform.SOURCE_OPTIONS_KEY));
        textFieldRecalboxReport.setText(option(BiosPlatform.RECALBOX.getReferenceOptionsKey()));
        textFieldRecalboxDestination.setText(option(BiosPlatform.RECALBOX.getDestinationOptionsKey()));
        textFieldBatoceraReadme.setText(option(BiosPlatform.BATOCERA.getReferenceOptionsKey()));
        textFieldBatoceraDestination.setText(option(BiosPlatform.BATOCERA.getDestinationOptionsKey()));
    }

    @FXML
    private void handleBrowseSource() {
        browseDirectory(textFieldSourcePath, "Select BIOS source folder");
    }

    @FXML
    private void handleBrowseRecalboxReport() {
        browseFile(textFieldRecalboxReport, "Select Recalbox missing_bios_report.txt");
    }

    @FXML
    private void handleBrowseRecalboxDestination() {
        browseDirectory(textFieldRecalboxDestination, "Select Recalbox BIOS destination folder");
    }

    @FXML
    private void handleBrowseBatoceraReadme() {
        browseFile(textFieldBatoceraReadme, "Select Batocera readme.txt");
    }

    @FXML
    private void handleBrowseBatoceraDestination() {
        browseDirectory(textFieldBatoceraDestination, "Select Batocera BIOS destination folder");
    }

    @FXML
    private void handleSavePaths() {
        savePaths();
        appendLog("Paths saved.");
    }

    @FXML
    private void handleAnalyze() {
        startProcess(false);
    }

    @FXML
    private void handleCopy() {
        startProcess(true);
    }

    @FXML
    private void handleAbort() {
        if (processBios != null) {
            processBios.abort();
        }
    }

    @FXML
    private void handleClose() {
        if (processBios != null && processBios.isAlive()) {
            processBios.abort();
        }
        if (stage != null) {
            stage.close();
        }
    }

    private void startProcess(boolean copyFiles) {
        if (processBios != null && processBios.isAlive()) {
            return;
        }
        if (!validatePaths()) {
            return;
        }
        savePaths();

        BiosPlatform platform = tabPanePlatform.getSelectionModel().getSelectedIndex() == 0
                ? BiosPlatform.RECALBOX
                : BiosPlatform.BATOCERA;
        activePlatform = platform;
        String referencePath = platform == BiosPlatform.RECALBOX
                ? textFieldRecalboxReport.getText().trim()
                : textFieldBatoceraReadme.getText().trim();
        String destinationPath = platform == BiosPlatform.RECALBOX
                ? textFieldRecalboxDestination.getText().trim()
                : textFieldBatoceraDestination.getText().trim();
        BatoceraMode batoceraMode = radioButtonBatoceraFull.isSelected()
                ? BatoceraMode.FULL_CATALOG
                : BatoceraMode.MISSING_ONLY;

        results.clear();
        textAreaLog.clear();
        setProcessingState(true);

        processBios = new ProcessBios(
                platform,
                referencePath,
                textFieldSourcePath.getText().trim(),
                destinationPath,
                batoceraMode,
                copyFiles,
                new ICallBackBios() {
                    @Override
                    public void onStarted(int total) {
                        Platform.runLater(() -> appendLog("Entries to process: " + total));
                    }

                    @Override
                    public void onResult(BiosEntryResult result) {
                        Platform.runLater(() -> results.add(result));
                    }

                    @Override
                    public void onLog(String message) {
                        Platform.runLater(() -> appendLog(message));
                    }

                    @Override
                    public void onCompleted() {
                        Platform.runLater(() -> {
                            appendLog("Done.");
                            setProcessingState(false);
                            processBios = null;
                        });
                    }

                    @Override
                    public void onError(Exception exception) {
                        Platform.runLater(() -> {
                            appendLog("Error: " + exception.getMessage());
                            Popup.error(exception);
                            setProcessingState(false);
                            processBios = null;
                        });
                    }
                });
        processBios.start();
    }

    private boolean validatePaths() {
        if (isMissing(textFieldSourcePath.getText())) {
            Popup.warning("BIOS source folder is not set.");
            return false;
        }
        if (tabPanePlatform.getSelectionModel().getSelectedIndex() == 0) {
            if (isMissing(textFieldRecalboxReport.getText())) {
                Popup.warning("Recalbox missing report path is not set.");
                return false;
            }
            if (isMissing(textFieldRecalboxDestination.getText())) {
                Popup.warning("Recalbox destination folder is not set.");
                return false;
            }
        } else {
            if (isMissing(textFieldBatoceraReadme.getText())) {
                Popup.warning("Batocera readme path is not set.");
                return false;
            }
            if (isMissing(textFieldBatoceraDestination.getText())) {
                Popup.warning("Batocera destination folder is not set.");
                return false;
            }
        }
        return true;
    }

    private void savePaths() {
        RomManager.options.set(BiosPlatform.SOURCE_OPTIONS_KEY, textFieldSourcePath.getText().trim());
        RomManager.options.set(BiosPlatform.RECALBOX.getReferenceOptionsKey(), textFieldRecalboxReport.getText().trim());
        RomManager.options.set(BiosPlatform.RECALBOX.getDestinationOptionsKey(), textFieldRecalboxDestination.getText().trim());
        RomManager.options.set(BiosPlatform.BATOCERA.getReferenceOptionsKey(), textFieldBatoceraReadme.getText().trim());
        RomManager.options.set(BiosPlatform.BATOCERA.getDestinationOptionsKey(), textFieldBatoceraDestination.getText().trim());
        RomManager.options.save();
    }

    private void browseDirectory(TextField target, String title) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle(title);
        if (!isMissing(target.getText())) {
            File current = new File(target.getText().trim());
            if (current.isDirectory()) {
                chooser.setInitialDirectory(current);
            } else if (current.getParentFile() != null && current.getParentFile().isDirectory()) {
                chooser.setInitialDirectory(current.getParentFile());
            }
        }
        File selected = chooser.showDialog(stage);
        if (selected != null) {
            target.setText(selected.getAbsolutePath());
        }
    }

    private void browseFile(TextField target, String title) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(title);
        if (!isMissing(target.getText())) {
            File current = new File(target.getText().trim());
            if (current.getParentFile() != null && current.getParentFile().isDirectory()) {
                chooser.setInitialDirectory(current.getParentFile());
            }
            if (current.isFile()) {
                chooser.setInitialFileName(current.getName());
            }
        }
        File selected = chooser.showOpenDialog(stage);
        if (selected != null) {
            target.setText(selected.getAbsolutePath());
        }
    }

    private void appendLog(String message) {
        textAreaLog.appendText(message + System.lineSeparator());
    }

    private void setProcessingState(boolean processing) {
        buttonAnalyze.setDisable(processing);
        buttonCopy.setDisable(processing);
        buttonSavePaths.setDisable(processing);
        buttonClose.setDisable(processing);
        buttonAbort.setDisable(!processing);
        tabPanePlatform.setDisable(processing);
        buttonBrowseSource.setDisable(processing);
    }

    private static String option(String key) {
        String value = RomManager.options.get(key);
        return "{Missing}".equals(value) ? "" : value;
    }

    private static boolean isMissing(String value) {
        return value == null || value.isBlank() || "{Missing}".equals(value);
    }
}
