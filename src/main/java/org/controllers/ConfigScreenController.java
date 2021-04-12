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

    public static HashMap<String, Integer> propertiesSetted = new HashMap<String, Integer>();
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //readConfigFile();
        applyBtn.setVisible(false);
        applyBtn.setDefaultButton(true);


        propertiesSetted.put("ip_start_scan", Config.get("ip_start_scan"));
        propertiesSetted.put("ip_end_scan", Config.get("ip_end_scan"));
        propertiesSetted.put("port_start_scan", Config.get("port_start_scan"));
        propertiesSetted.put("port_end_scan", Config.get("port_end_scan"));
        propertiesSetted.put("number_threads", Config.get("number_threads"));
        propertiesSetted.put("timeout", Config.get("timeout"));
        propertiesSetted.put("advanced_configuration", Config.get("advanced_configuration"));
        propertiesSetted.put("default_scan_config", Config.get("default_scan_config"));
        propertiesSetted.put("silent_mode", Config.get("silent_mode"));
        propertiesSetted.put("active_verification", Config.get("active_verification"));


        defaultCheckbox.selectedProperty().setValue(propertiesSetted.get("default_scan_config") == 1);
        silentModeCheckbox.selectedProperty().setValue(propertiesSetted.get("silent_mode") == 1);
        verificationOnRadio.selectedProperty().setValue(propertiesSetted.get("active_verification") == 1);
        verificationOffRadio.selectedProperty().setValue(!(propertiesSetted.get("active_verification") == 1));


        if (propertiesSetted.get("default_scan_config") == 1){
            //disable all text fields
            startIpSpinner.setDisable(true);
            endIpSpinner.setDisable(true);
            startPortSpinner.setDisable(true);
            endPortSpinner.setDisable(true);
        }else {
            startIpSpinner.setDisable(false);
            endIpSpinner.setDisable(false);
            startPortSpinner.setDisable(false);
            endPortSpinner.setDisable(false);
        }

        if(propertiesSetted.get("advanced_configuration") == 1){
            advancedConfigsCheckbox.selectedProperty().setValue(true);
            threadsSpinner.setDisable(false);
            timeoutSpinner.setDisable(false);
        }

        SpinnerValueFactory<Integer> spinnerStartIpFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 254, propertiesSetted.get("ip_start_scan")); //minimo, maximo, default
        SpinnerValueFactory<Integer> spinnerEndIpFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 254, propertiesSetted.get("ip_end_scan"));
        SpinnerValueFactory<Integer> spinnerStartPortFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 65534, propertiesSetted.get("port_start_scan"));
        SpinnerValueFactory<Integer> spinnerEndPortFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 65535, propertiesSetted.get("port_end_scan"));
        SpinnerValueFactory<Integer> spinnerThreadsFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 2000, propertiesSetted.get("number_threads"));
        SpinnerValueFactory<Integer> spinnerTimeoutFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(10, 5000, propertiesSetted.get("timeout"));

        startIpSpinner.setValueFactory(spinnerStartIpFactory);
        endIpSpinner.setValueFactory(spinnerEndIpFactory);
        startPortSpinner.setValueFactory(spinnerStartPortFactory);
        endPortSpinner.setValueFactory(spinnerEndPortFactory);
        threadsSpinner.setValueFactory(spinnerThreadsFactory);
        timeoutSpinner.setValueFactory(spinnerTimeoutFactory);

        startIpSpinner.valueProperty().addListener((obs, oldValue, newValue) -> showApplyBtn());
        endIpSpinner.valueProperty().addListener((obs, oldValue, newValue) -> showApplyBtn());
        startPortSpinner.valueProperty().addListener((obs, oldValue, newValue) -> showApplyBtn());
        endPortSpinner.valueProperty().addListener((obs, oldValue, newValue) -> showApplyBtn());
        threadsSpinner.valueProperty().addListener((obs, oldValue, newValue) -> showApplyBtn());
        timeoutSpinner.valueProperty().addListener((obs, oldValue, newValue) -> showApplyBtn());
    }


    public void toggleDefault(ActionEvent actionEvent) {
        showApplyBtn();
        if(defaultCheckbox.selectedProperty().getValue() == true){

            startIpSpinner.getValueFactory().setValue(1);
            endIpSpinner.getValueFactory().setValue(254);
            startPortSpinner.getValueFactory().setValue(8081);
            endPortSpinner.getValueFactory().setValue(8086);

            startIpSpinner.setDisable(true);
            endIpSpinner.setDisable(true);
            startPortSpinner.setDisable(true);
            endPortSpinner.setDisable(true);
        }
        else if(defaultCheckbox.selectedProperty().getValue() == false){
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
        propertiesSetted.put("ip_start_scan", startIpSpinner.getValue());
        propertiesSetted.put("ip_end_scan", endIpSpinner.getValue());
        propertiesSetted.put("port_start_scan", startPortSpinner.getValue());
        propertiesSetted.put("port_end_scan", endPortSpinner.getValue());
        propertiesSetted.put("number_threads", threadsSpinner.getValue());
        propertiesSetted.put("timeout", timeoutSpinner.getValue());

        propertiesSetted.put("default_scan_config", defaultCheckbox.selectedProperty().getValue() ? 1 : 0);
        propertiesSetted.put("silent_mode", silentModeCheckbox.selectedProperty().getValue() ? 1 : 0);
        propertiesSetted.put("active_verification", verificationOnRadio.selectedProperty().getValue() ? 1 : 0);
        propertiesSetted.put("advanced_configuration", advancedConfigsCheckbox.selectedProperty().getValue() ? 1 : 0);

        Config.saveToConfigFile(propertiesSetted);

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


//    public void saveToConfigFile(){
//        try (OutputStream output = new FileOutputStream("config.properties")) {
//
//            Properties prop = new Properties();
//
//            // set the properties value
//            prop.setProperty("default_scan_config", String.valueOf(defaultScanConfig));
//            prop.setProperty("ip_start_scan", String.valueOf(startIp));
//            prop.setProperty("ip_end_scan", String.valueOf(endIp));
//            prop.setProperty("port_start_scan", String.valueOf(startPort));
//            prop.setProperty("port_end_scan", String.valueOf(endPort));
//
//            prop.setProperty("advanced_configuration", String.valueOf(advancedConfigs));
//            prop.setProperty("number_threads", String.valueOf(threads));
//            prop.setProperty("timeout", String.valueOf(timeout));
//
//            prop.setProperty("silent_mode", String.valueOf(silentMode));
//            prop.setProperty("active_verification", String.valueOf(verificationMode));
//
//
//            prop.store(output, null);
//
//
//        } catch (IOException io) {
//            io.printStackTrace();
//        }
//
//    }

    public void backToStartScreen(ActionEvent actionEvent) throws IOException {
        Parent startScreenRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(previousScreen)));
        Scene window = ((Node) actionEvent.getSource()).getScene();

        window.setRoot(startScreenRoot);


    }
}
