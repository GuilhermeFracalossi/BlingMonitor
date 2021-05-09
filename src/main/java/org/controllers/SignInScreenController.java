package org.controllers;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.Config;
import org.database.Database;
import org.database.MySQL;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class SignInScreenController{

    @FXML TextField nameField;
    @FXML TextField userField;
    @FXML TextField passwordField;
    @FXML Button signBtn;
    @FXML HBox loginErrorMessage;

    public void registerUser(ActionEvent event) throws IOException {
        String name = nameField.getText();
        String user  = userField.getText();
        String pass = passwordField.getText();

        //if any field is empty
        if(isEmpty(name) || isEmpty(user) || isEmpty(pass)){
            loginErrorMessage.setVisible(true);

            FadeTransition fade = new FadeTransition();
            fade.setDuration(Duration.millis(2000));
            fade.setFromValue(1);
            fade.setToValue(0);
            fade.setNode(loginErrorMessage);
            fade.play();

        }else{
            String encryptedPass = Database.encrypt(pass);
            Database.insertUser(name, user, encryptedPass);

            Parent startScreenRoot = FXMLLoader.load(getClass().getResource("/org/FxmlScreens/startScreen.fxml"));
            Scene window = ((Node) event.getSource()).getScene();
            window.setRoot(startScreenRoot);
        }

    }
    private boolean isEmpty(String str){
        return str == null || str.trim().isEmpty();
    }
}
