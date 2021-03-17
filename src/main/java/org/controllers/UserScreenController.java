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
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.MySql;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import static org.MySql.encrypt;

public class UserScreenController implements Initializable {
    public static String previousScreen;
    @FXML
    private HBox loginErrorMessage;

    @FXML
    private Text messageText;

    @FXML
    private ImageView messageImage;



    @FXML
    Button changeDataBtn;
    @FXML
    Button saveBtn;

    @FXML
    Button cancelBtn;
    @FXML
    TextField nameField;
    @FXML
    TextField userField;

    @FXML
    PasswordField passwordField;

    String name;
    String user;

    String password = LoginScreenController.passwordTyped;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        ResultSet data= MySql.getData();

        try {

             name= data.getString(2);
             user= data.getString(3);



        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        cancelBtn.setVisible(false);
        saveBtn.setVisible(false);
        cancelBtn.setManaged(false);
        saveBtn.setManaged(false);

        nameField.setText(name);
        userField.setText(user);
        passwordField.setText(password);

        nameField.setText(name);
        userField.setText(user);
        passwordField.setText(password);

    }

    public void changeUserData(ActionEvent actionEvent)  {

        nameField.setEditable(true);
        userField.setEditable(true);
        passwordField.setEditable(true);

        cancelBtn.setManaged(true);
        cancelBtn.setVisible(true);
        saveBtn.setVisible(true);
        saveBtn.setManaged(true);

        changeDataBtn.setManaged(false);
        changeDataBtn.setVisible(false);

    }

    public void update(ActionEvent event) throws IOException, InterruptedException {

        String user = userField.getText().trim();
        String passwordString = passwordField.getText().trim();
        String name = nameField.getText().trim();



        if(user.length() == 0 | passwordString.length() == 0 | name.length() == 0 ){
            showMessage("Todos os campos devem ser preenchidos");

        }else{

            MySql.update(name, user, encrypt(passwordString), LoginScreenController.id);

            LoginScreenController.passwordTyped = passwordString;
            messageImage.setImage(new Image(getClass().getResource("/org/images/tick.png").toString()));
            showMessage("Dados alterados com sucesso");
        }
        cancelBtn.setVisible(false);
        saveBtn.setVisible(false);
        cancelBtn.setManaged(false);
        saveBtn.setManaged(false);

        changeDataBtn.setManaged(true);
        changeDataBtn.setVisible(true);

        nameField.setEditable(false);
        userField.setEditable(false);
        passwordField.setEditable(false);


    }
    public void backToStartScreen(ActionEvent actionEvent) throws IOException {

        Parent startScreenRoot = FXMLLoader.load(getClass().getResource(previousScreen));

        Scene window =  ((Node) actionEvent.getSource()).getScene();
        window.setRoot(startScreenRoot);
    }


    public void cancelChanges(ActionEvent actionEvent) {
        cancelBtn.setVisible(false);
        saveBtn.setVisible(false);
        cancelBtn.setManaged(false);
        saveBtn.setManaged(false);

        changeDataBtn.setManaged(true);
        changeDataBtn.setVisible(true);

        nameField.setText(name);
        userField.setText(user);
        passwordField.setText(password);

        nameField.setEditable(false);
        userField.setEditable(false);
        passwordField.setEditable(false);

    }

    public void showMessage(String text) throws InterruptedException{
        messageText.setText(text);
        loginErrorMessage.setVisible(true);

        FadeTransition fade = new FadeTransition();
        fade.setDuration(Duration.millis(2500));
        fade.setFromValue(1);
        fade.setToValue(0);
        fade.setNode(loginErrorMessage);

        fade.play();
    }
}
