package org.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import javafx.stage.Stage;
import org.network.GetAvailableIps;
import org.MySql;


import java.io.IOException;
import java.net.*;
import java.sql.SQLException;
import java.util.*;


public class StartScreenController implements Initializable {


    @FXML
    BorderPane mainPane;

    @FXML
    HBox topPane;

    @FXML
    Button userBtn;
    public static Stage window;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {

            userBtn.setText(getUserName());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


    }


    public String getUserName() throws SQLException {
        String userName = MySql.getData().getString(2);
        return userName;
    }


    public void autoScan(ActionEvent actionEvent) throws IOException {

        ArrayList availableIps = new GetAvailableIps().main();


        if(availableIps.size() == 1){

            AutoScanLoadingController.ipSelected = (String) availableIps.get(0);
            Parent autoScanLoadingRoot = FXMLLoader.load(getClass().getResource("/org/FxmlScreens/autoScanLoadingScreen.fxml"));

            Scene window =  ((Node) actionEvent.getSource()).getScene();
            window.setRoot(autoScanLoadingRoot);

        }else{
            IpSelectionScreenController.ips = availableIps;
            IpSelectionScreenController.previousScreen = "/org/FxmlScreens/startScreen.fxml";
            Parent ipSelectionRoot = FXMLLoader.load(getClass().getResource("/org/FxmlScreens/ipSelectionScreen.fxml"));

            Scene window =  ((Node) actionEvent.getSource()).getScene();
            window.setRoot(ipSelectionRoot);
        }



    }


    public void manualRegister(ActionEvent actionEvent) throws IOException {

        ManualRegisterController.enableScan = false;
        Parent manualRegisterRoot = FXMLLoader.load(getClass().getResource("/org/FxmlScreens/manualRegisterScreen.fxml"));
        Scene window = ((Node) actionEvent.getSource()).getScene();
        //Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        window.setRoot(manualRegisterRoot);

    }

    public void configScreen(ActionEvent actionEvent) throws IOException {
        ConfigScreenController.previousScreen = "/org/FxmlScreens/startScreen.fxml";
        Parent configScreenRoot = FXMLLoader.load(getClass().getResource("/org/FxmlScreens/configScreen.fxml"));
        Scene window = ((Node) actionEvent.getSource()).getScene();
        window.setRoot(configScreenRoot);
    }

    public void logout(ActionEvent actionEvent) throws IOException {

        LoginScreenController.logged = false;
        Parent loginScreenRoot = FXMLLoader.load(getClass().getResource("/org/FxmlScreens/loginScreen.fxml"));
        Scene window = ((Node) actionEvent.getSource()).getScene();
        window.setRoot(loginScreenRoot);

    }

    public void userScreen(ActionEvent actionEvent) throws IOException {
        UserScreenController.previousScreen = "/org/FxmlScreens/startScreen.fxml";
        Parent loginScreenRoot = FXMLLoader.load(getClass().getResource("/org/FxmlScreens/userScreen.fxml"));
        Scene window = ((Node) actionEvent.getSource()).getScene();
        window.setRoot(loginScreenRoot);

    }
}
