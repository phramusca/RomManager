/*
 * Copyright (C) 2025 raph
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

import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

/**
 * Controller for DialogConsole FXML
 * @author raph
 */
public class DialogConsoleController implements Initializable {
    
    @FXML
    private ListView<Console> listViewConsoles;
    
    @FXML
    private Button buttonAction;
    
    @FXML
    private RadioButton radioButtonOnlyNew;
    
    @FXML
    private RadioButton radioButtonRefreshSelected;
    
    @FXML
    private CheckBox checkBoxOnlyCultes;
    
    @FXML
    private ToggleGroup toggleGroupListType;
    
    private ICallBackConsole callback;
    private boolean displayRefresh;
    private boolean displayFilter;
    private boolean manualExit = true;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize list view with consoles
        ObservableList<Console> consolesList = FXCollections.observableArrayList();
        Console[] consoles = Console.values();
        Arrays.sort(consoles, Comparator.comparing(Console::getName));
        for (Console console : consoles) {
            if (console.getNbFiles() > 0) {
                consolesList.add(console);
            }
        }
        listViewConsoles.setItems(consolesList);
        listViewConsoles.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.MULTIPLE);

        buttonAction.setText("Export");
        buttonAction.setMinWidth(140);
        buttonAction.setPrefWidth(160);
        buttonAction.setWrapText(true);
        buttonAction.setDefaultButton(true);

        checkBoxOnlyCultes.setWrapText(true);
        checkBoxOnlyCultes.setMaxWidth(Double.MAX_VALUE);
 
        // Reset console selections
        Arrays.asList(Console.values()).forEach(console -> console.setSelected(false));
        
        // Set checkbox text from resources
        if (resources != null) {
            checkBoxOnlyCultes.setText(resources.getString("DialogConsole.jCheckBoxOnlyCultes.text"));
        }
    }
    
    public void setCallback(ICallBackConsole callback) {
        this.callback = callback;
    }
    
    public void setDisplayRefresh(boolean displayRefresh) {
        this.displayRefresh = displayRefresh;
        if (!displayRefresh) {
            radioButtonOnlyNew.setVisible(false);
            radioButtonRefreshSelected.setVisible(false);
        }
    }
    
    public void setDisplayFilter(boolean displayFilter) {
        this.displayFilter = displayFilter;
        if (!displayFilter) {
            checkBoxOnlyCultes.setVisible(false);
        }
    }
    
    public void setButtonText(String text) {
        buttonAction.setText(text);
    }
    
    public void setManualExit(boolean manualExit) {
        this.manualExit = manualExit;
    }
    
    @FXML
    private void handleActionButton() {
        boolean refresh = radioButtonRefreshSelected.isSelected();
        boolean onlyCultes = checkBoxOnlyCultes.isSelected();
        
        List<Console> selectedConsoles = listViewConsoles.getSelectionModel().getSelectedItems();
        selectedConsoles.forEach(console -> console.setSelected(true));
        
        manualExit = false;
        
        // Close the window
        javafx.scene.Node source = (javafx.scene.Node) buttonAction;
        javafx.stage.Stage stage = (javafx.stage.Stage) source.getScene().getWindow();
        stage.close();
        
        if (callback != null) {
            callback.completed(refresh, onlyCultes);
        }
    }
    
    public void handleWindowClose() {
        if (manualExit && callback != null) {
            callback.cancelled();
        }
    }
}

