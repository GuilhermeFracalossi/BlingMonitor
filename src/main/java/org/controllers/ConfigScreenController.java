package org.controllers;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.Config;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Objects;
import java.util.ResourceBundle;

public class ConfigScreenController implements Initializable {
    public static String previousScreen;

    @FXML
    BorderPane mainPane;
    
    @FXML
    Spinner<Integer> startIpSpinner;

    @FXML
    Spinner<Integer> endIpSpinner;

    @FXML
    Spinner<Integer> startPortSpinner;

    @FXML
    Spinner<Integer> endPortSpinner;

    @FXML
    Spinner<Integer> threadsSpinner;

    @FXML
    Spinner<Integer> timeoutSpinner;

    @FXML
    CheckBox defaultCheckbox;

    @FXML
    CheckBox silentModeCheckbox;

    @FXML
    RadioButton verificationOnRadio;

    @FXML
    RadioButton verificationOffRadio;

    @FXML
    HBox advancedConfigsHbox;

    @FXML
    CheckBox advancedConfigsCheckbox;

    @FXML
    Button applyBtn;

    public HashMap<String, Integer> properties;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        applyBtn.setVisible(false);
        applyBtn.setDefaultButton(true);

        properties = Config.getAllProperties();
        setStartingFieldsValues();


        //If any change occurs, show the Apply Button
        startIpSpinner.valueProperty().addListener((obs, oldValue, newValue) -> showApplyBtn());
        endIpSpinner.valueProperty().addListener((obs, oldValue, newValue) -> showApplyBtn());
        startPortSpinner.valueProperty().addListener((obs, oldValue, newValue) -> showApplyBtn());
        endPortSpinner.valueProperty().addListener((obs, oldValue, newValue) -> showApplyBtn());
        threadsSpinner.valueProperty().addListener((obs, oldValue, newValue) -> showApplyBtn());
        timeoutSpinner.valueProperty().addListener((obs, oldValue, newValue) -> showApplyBtn());
    }

    private void setStartingFieldsValues(){
        SpinnerValueFactory<Integer> spinnerStartIpFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 254, properties.get("ip_start_scan")); //minimo, maximo, default
        SpinnerValueFactory<Integer> spinnerEndIpFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 254, properties.get("ip_end_scan"));
        SpinnerValueFactory<Integer> spinnerStartPortFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 65534, properties.get("port_start_scan"));
        SpinnerValueFactory<Integer> spinnerEndPortFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 65535, properties.get("port_end_scan"));
        SpinnerValueFactory<Integer> spinnerThreadsFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 2000, properties.get("number_threads"));
        SpinnerValueFactory<Integer> spinnerTimeoutFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(10, 5000, properties.get("timeout"));

        startIpSpinner.setValueFactory(spinnerStartIpFactory);
        endIpSpinner.setValueFactory(spinnerEndIpFactory);
        startPortSpinner.setValueFactory(spinnerStartPortFactory);
        endPortSpinner.setValueFactory(spinnerEndPortFactory);
        threadsSpinner.setValueFactory(spinnerThreadsFactory);
        timeoutSpinner.setValueFactory(spinnerTimeoutFactory);

        startIpSpinner.setDisable(properties.get("default_scan_config") == 1);
        endIpSpinner.setDisable(properties.get("default_scan_config") == 1);
        startPortSpinner.setDisable(properties.get("default_scan_config") == 1);
        endPortSpinner.setDisable(properties.get("default_scan_config") == 1);

        advancedConfigsCheckbox.selectedProperty().setValue(properties.get("advanced_configuration") == 1);
        threadsSpinner.setDisable(properties.get("advanced_configuration") == 0);
        timeoutSpinner.setDisable(properties.get("advanced_configuration") == 0);

        defaultCheckbox.selectedProperty().setValue(properties.get("default_scan_config") == 1);
        silentModeCheckbox.selectedProperty().setValue(properties.get("silent_mode") == 1);
        verificationOnRadio.selectedProperty().setValue(properties.get("active_verification") == 1);
        verificationOffRadio.selectedProperty().setValue(!(properties.get("active_verification") == 1));
    }

    public void toggleDefault(ActionEvent actionEvent) {
        showApplyBtn();
        if(defaultCheckbox.selectedProperty().getValue() == true){

            startIpSpinner.getValueFactory().setValue(Config.DEFAULT_VALUES.get("ip_start_scan"));
            endIpSpinner.getValueFactory().setValue(Config.DEFAULT_VALUES.get("ip_end_scan"));
            startPortSpinner.getValueFactory().setValue(Config.DEFAULT_VALUES.get("port_start_scan"));
            endPortSpinner.getValueFactory().setValue(Config.DEFAULT_VALUES.get("port_end_scan"));

            startIpSpinner.setDisable(true);
            endIpSpinner.setDisable(true);
            startPortSpinner.setDisable(true);
            endPortSpinner.setDisable(true);
        }
        else{
            startIpSpinner.setDisable(false);
            endIpSpinner.setDisable(false);
            startPortSpinner.setDisable(false);
            endPortSpinner.setDisable(false);
        }
    }

    public void advancedConfigsToggle(ActionEvent actionEvent) {
        showApplyBtn();
        if(advancedConfigsCheckbox.selectedProperty().getValue() == true){
            threadsSpinner.setDisable(false);
            timeoutSpinner.setDisable(false);
        }else{
            // applyBtn.setVisible(true);
            threadsSpinner.getValueFactory().setValue(50);
            timeoutSpinner.getValueFactory().setValue(1500);
            threadsSpinner.setDisable(true);
            timeoutSpinner.setDisable(true);
        }

    }

    public void showApplyBtn() {

        mainPane.setBottom(applyBtn);

        applyBtn.setVisible(true);

    }

    public void appliedConfigs(ActionEvent actionEvent) {

        properties.put("ip_start_scan", startIpSpinner.getValue());
        properties.put("ip_end_scan", endIpSpinner.getValue());
        properties.put("port_start_scan", startPortSpinner.getValue());
        properties.put("port_end_scan", endPortSpinner.getValue());
        properties.put("number_threads", threadsSpinner.getValue());
        properties.put("timeout", timeoutSpinner.getValue());

        properties.put("default_scan_config", defaultCheckbox.selectedProperty().getValue() ? 1 : 0);
        properties.put("silent_mode", silentModeCheckbox.selectedProperty().getValue() ? 1 : 0);
        properties.put("active_verification", verificationOnRadio.selectedProperty().getValue() ? 1 : 0);
        properties.put("advanced_configuration", advancedConfigsCheckbox.selectedProperty().getValue() ? 1 : 0);

        Config.saveToConfigFile(properties);

        mainPane.setBottom(null);
        HBox statusContainer = new HBox();
        statusContainer.setPrefHeight(50);
        statusContainer.setAlignment(Pos.TOP_CENTER);

        Text saveStatus = new Text();
        saveStatus.setText("Alterações salvas com sucesso");
        saveStatus.setFill(Color.WHITE);
        saveStatus.setFont(Font.font("System", FontWeight.BOLD,22));

        statusContainer.getChildren().add(saveStatus);
        mainPane.setBottom(statusContainer);

        FadeTransition fade = new FadeTransition();
        fade.setDuration(Duration.millis(1500));
        fade.setFromValue(1);
        fade.setToValue(0);
        fade.setNode(statusContainer);
        fade.play();

    }

    public void backToStartScreen(ActionEvent actionEvent) throws IOException {
        Parent startScreenRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(previousScreen)));
        Scene window = ((Node) actionEvent.getSource()).getScene();
        window.setRoot(startScreenRoot);

    }
}
