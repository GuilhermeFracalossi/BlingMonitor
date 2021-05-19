package org.controllers;


import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import org.CamerasConfig;
import org.Config;
import org.database.Database;
import static org.database.Database.encrypt;


public class LoginScreenController {
    public static boolean logged = false;
    
    @FXML private HBox loginErrorMessage;

    @FXML private PasswordField passwordField;
    @FXML private TextField login;
    @FXML private Button loginBtn;

    ResultSet rs;

    public void logIn(ActionEvent event) throws IOException {

        String user = login.getText();
        String password = encrypt(passwordField.getText());
            try {
                if(Database.isClosed()) {
                    new Database();
                }
                rs = Database.login(user, password);

            if (rs.next()) {

                logged = true;
                if(CamerasConfig.camerasCount() > 0){
                    ManualRegisterController.enableScan = true;
                    Parent camerasMainGridScreenRoot = FXMLLoader.load(getClass().getResource("/org/FxmlScreens/allCamerasMainGridScreen.fxml"));
                    Scene window = ((Node) event.getSource()).getScene();
                    Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
                    boolean maximized = Config.get("maximized") == 1;
                    if(maximized){
                        stage.setMaximized(true);
                    }

                    window.setRoot(camerasMainGridScreenRoot);

                }else{
                    Parent startScreenRoot = FXMLLoader.load(getClass().getResource("/org/FxmlScreens/startScreen.fxml"));
                    Scene window = ((Node) event.getSource()).getScene();
                    window.setRoot(startScreenRoot);
                }
                
            } else {
                loginErrorMessage.setVisible(true);

                FadeTransition fade = new FadeTransition();
                fade.setDuration(Duration.millis(2000));
                fade.setFromValue(1);
                fade.setToValue(0);
                fade.setNode(loginErrorMessage);
                //playing the transition
                fade.play();
            }
                
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }         
    }


}
