package org.controllers;


import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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
import org.database.Database;
import static org.database.Database.encrypt;


public class LoginScreenController implements Initializable {

    public static String name;
    public static String passwordTyped;
    public static String user;
    public static int id;

    public static boolean logged = false;
    
    @FXML
    private HBox loginErrorMessage;

    @FXML
    private PasswordField passwordField;


    @FXML
    private TextField login;

    @FXML
    private Button loginBtn;

    ResultSet rs;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        loginBtn.setDefaultButton(true); //makes the enter key activate the button
    }
    public void logIn(ActionEvent event) throws IOException {

        String usuario = login.getText();
        String senha = encrypt(passwordField.getText());
        
            try {
//                if(Database.conn.isClosed()){
                if(Database.isClosed()){
                    Database connection = new Database();
                     rs = connection.login(usuario, senha);

                }else{
                    rs = Database.login(usuario, senha);
                }

            if (rs.next()) { 
                id = rs.getInt("id");

                name = rs.getString("nome");
                user = rs.getString("login");
                passwordTyped = passwordField.getText();

                logged = true;
                if(CamerasConfig.camerasCount() > 0){
//                    if(false){
                    ManualRegisterController.enableScan = true;
                    Parent camerasMainGridScreenRoot = FXMLLoader.load(getClass().getResource("/org/FxmlScreens/allCamerasMainGridScreen.fxml"));
                    Scene window = ((Node) event.getSource()).getScene();
                    Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
                    stage.setMaximized(true);
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
