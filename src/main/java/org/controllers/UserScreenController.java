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
import org.database.Database;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import static org.database.Database.encrypt;

public class UserScreenController implements Initializable {
    public static String previousScreen;
    public static boolean passwordUpdated = false;
    @FXML
    private HBox messageTextHbox;

    @FXML
    private Text messageText;

    @FXML
    private ImageView messageImage;

    @FXML
    Button changePassBtn;

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

    String name;
    String user;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ResultSet data= Database.getUsers();

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

        nameField.setText(name);
        userField.setText(user);

        if(passwordUpdated){
            messageImage.setImage(new Image(getClass().getResource("/org/images/tick.png").toString()));
            showMessage("Senha alterada com sucesso");
            passwordUpdated = false;
        }

    }

    public void changeUserData(ActionEvent actionEvent)  {

        nameField.setEditable(true);
        userField.setEditable(true);
        nameField.requestFocus();

        cancelBtn.setManaged(true);
        cancelBtn.setVisible(true);
        saveBtn.setVisible(true);
        saveBtn.setManaged(true);
        
        changeDataBtn.setManaged(false);
        changeDataBtn.setVisible(false);

    }

    public void update(ActionEvent event) throws InterruptedException {

        String user = userField.getText().trim();
        String name = nameField.getText().trim();

        if(user.length() == 0 ||  name.length() == 0 ){
            messageImage.setImage(new Image(getClass().getResource("/org/images/cancel-low.png").toString()));
            showMessage("Todos os campos devem estar preenchidos");
            return;

        }else{
            Database.updateUserInfo(LoginScreenController.id, name, user);
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

        nameField.setEditable(false);
        userField.setEditable(false);

    }

    public void showMessage(String text){
        messageText.setText(text);
        messageTextHbox.setVisible(true);

        FadeTransition fade = new FadeTransition();
        fade.setDuration(Duration.millis(2500));
        fade.setFromValue(1);
        fade.setToValue(0);
        fade.setNode(messageTextHbox);

        fade.play();
    }

    public void changePassword(ActionEvent actionEvent) throws IOException {
        Parent startScreenRoot = FXMLLoader.load(getClass().getResource("/org/FxmlScreens/passwordChangeScreen.fxml"));
        Scene window =  ((Node) actionEvent.getSource()).getScene();
        window.setRoot(startScreenRoot);
    }
}
