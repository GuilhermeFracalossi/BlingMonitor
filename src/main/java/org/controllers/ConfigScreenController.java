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
import java.util.Properties;
import java.util.ResourceBundle;

public class ConfigScreenController implements Initializable {
    public static String previousScreen;

    @FXML
    BorderPane mainPane;
    
    @FXML
    Spinner startIpSpinner;

    @FXML
    Spinner endIpSpinner;

    @FXML
    Spinner startPortSpinner;

    @FXML
    Spinner endPortSpinner;

    @FXML
    Spinner threadsSpinner;

    @FXML
    Spinner timeoutSpinner;

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

    public static int startIp;
    public static int endIp;
    public static int startPort;
    public static int endPort;

    public static int threads;
    public static int timeout ;


    public static boolean defaultOptionsScan;
    public static boolean silentMode;
    public static boolean verificationMode;
    public static boolean advancedConfigs;

//    Map<Boolean, Object> map = new HashMap<Boolean, Object>();
//    final Object[] defaultOptions = new Object[]{true, 1, 254, 8081, 8086, 50, 2000, false, true};
    //default options address, Ip start, ip end, port start, port end, number of threds, timeout, silent mode, active verification


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //readConfigFile();
        applyBtn.setVisible(false);
        applyBtn.setDefaultButton(true);

//        default options address, Ip start, ip end, port start, port end, number of threds, timeout, silent mode, active verification
        threads = Integer.parseInt(Config.get("number_threads"));
        timeout = Integer.parseInt(Config.get("timeout"));

        advancedConfigs = Boolean.parseBoolean(Config.get("advanced_configuration"));
        defaultOptionsScan = Boolean.parseBoolean(Config.get("default_configuration_scan"));

        silentMode = Boolean.parseBoolean(Config.get("silent_mode"));
        verificationMode = Boolean.parseBoolean(Config.get("active_verification"));

        startIp =  Integer.parseInt(Config.get("ip_start_scan"));
        endIp = Integer.parseInt(Config.get("ip_end_scan"));
        startPort = Integer.parseInt(Config.get("port_start_scan"));
        endPort = Integer.parseInt(Config.get("port_end_scan"));




        defaultCheckbox.selectedProperty().setValue(defaultOptionsScan);
        silentModeCheckbox.selectedProperty().setValue(silentMode);
        verificationOnRadio.selectedProperty().setValue(verificationMode);
        verificationOffRadio.selectedProperty().setValue(!verificationMode);


        if (defaultOptionsScan == true){
            //disable all text fields
            startIpSpinner.setDisable(true);
            endIpSpinner.setDisable(true);
            startPortSpinner.setDisable(true);
            endPortSpinner.setDisable(true);
        }else if(defaultOptionsScan == false){
            startIpSpinner.setDisable(false);
            endIpSpinner.setDisable(false);
            startPortSpinner.setDisable(false);
            endPortSpinner.setDisable(false);
        }

        if(advancedConfigs == true){
            advancedConfigsCheckbox.selectedProperty().setValue(true);
            threadsSpinner.setDisable(false);
            timeoutSpinner.setDisable(false);
        }

        SpinnerValueFactory spinnerStartIpFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 254, startIp); //minimo, maximo, default
        SpinnerValueFactory spinnerEndIpFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 254, endIp);
        SpinnerValueFactory spinnerStartPortFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 65534, startPort);
        SpinnerValueFactory spinnerEndPortFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 65535, endPort);

        SpinnerValueFactory spinnerThreadsFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 2000, threads);
        SpinnerValueFactory spinnerTimeoutFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(10, 5000, timeout);

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


    public void appliedConfigs(ActionEvent actionEvent) {
        startIp = (int) startIpSpinner.getValue();
        endIp = (int) endIpSpinner.getValue();
        startPort = (int) startPortSpinner.getValue();
        endPort = (int) endPortSpinner.getValue();
        threads = (int) threadsSpinner.getValue();
        timeout = (int) timeoutSpinner.getValue();

        defaultOptionsScan = defaultCheckbox.selectedProperty().getValue();
        silentMode = silentModeCheckbox.selectedProperty().getValue();
        verificationMode = verificationOnRadio.selectedProperty().getValue();

        advancedConfigs = advancedConfigsCheckbox.selectedProperty().getValue();

        saveToConfigFile();

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

    public void saveToConfigFile(){
        try (OutputStream output = new FileOutputStream("config.properties")) {

            Properties prop = new Properties();

            // set the properties value
            prop.setProperty("default_configuration_scan", String.valueOf(defaultOptionsScan));
            prop.setProperty("ip_start_scan", String.valueOf(startIp));
            prop.setProperty("ip_end_scan", String.valueOf(endIp));
            prop.setProperty("port_start_scan", String.valueOf(startPort));
            prop.setProperty("port_end_scan", String.valueOf(endPort));

            prop.setProperty("advanced_configuration", String.valueOf(advancedConfigs));
            prop.setProperty("number_threads", String.valueOf(threads));
            prop.setProperty("timeout", String.valueOf(timeout));

            prop.setProperty("silent_mode", String.valueOf(silentMode));
            prop.setProperty("active_verification", String.valueOf(verificationMode));


            prop.store(output, null);


        } catch (IOException io) {
            io.printStackTrace();
        }

    }

    public void backToStartScreen(ActionEvent actionEvent) throws IOException {
        Parent startScreenRoot = FXMLLoader.load(getClass().getResource(previousScreen));
        Scene window = ((Node) actionEvent.getSource()).getScene();

        window.setRoot(startScreenRoot);


    }
}
