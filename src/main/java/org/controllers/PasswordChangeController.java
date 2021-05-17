package org.controllers;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.database.Database;
import java.io.IOException;
import java.sql.SQLException;

public class PasswordChangeController {

    @FXML PasswordField oldPassField;
    @FXML PasswordField newPassField;
    @FXML PasswordField confirmPassField;

    @FXML Text messageText;
    @FXML HBox messageHbox;

    public void updatePassword(ActionEvent actionEvent) throws SQLException, IOException {

        String oldPass = oldPassField.getText();
        String newPass = newPassField.getText();

        String confirmPass = confirmPassField.getText();
        int id = Integer.parseInt(Database.getInfoUser("id"));
        String user = Database.getInfoUser("user");


        if(Database.login(user, Database.encrypt(oldPass)).next()){
            if(newPass.length()<4){
                setMessageText("A nova senha deve possuir no mínimo 4 dígitos");
                return;
            }
            if(newPass.equals(oldPass)){
                setMessageText("A nova senha não pode ser igual a velha");
                return;
            }
            if(newPass.equals(confirmPass)){
                Database.updatePassword(Database.encrypt(newPass), id);
                UserScreenController.passwordUpdated = true;
                Parent startScreenRoot = FXMLLoader.load(getClass().getResource("/org/FxmlScreens/userScreen.fxml"));
                Scene window =  ((Node) actionEvent.getSource()).getScene();
                window.setRoot(startScreenRoot);
            }else{
                setMessageText("Senhas não correspondem");
            }

        }else{
            setMessageText("Senha atual incorreta");
            oldPassField.setStyle("-fx-background-color: #ffabab");
        }
    }

    public void cancelChanges(ActionEvent actionEvent) throws IOException {
        Parent startScreenRoot = FXMLLoader.load(getClass().getResource("/org/FxmlScreens/userScreen.fxml"));
        Scene window =  ((Node) actionEvent.getSource()).getScene();
        window.setRoot(startScreenRoot);
    }


    public void confirmPassVerification() {
        String newPass = newPassField.getText();
        String confirmPass = confirmPassField.getText();

        if(confirmPass.length() <4) return;

        if(!newPass.equals(confirmPass)){
            confirmPassField.setStyle("-fx-background-color: #ffabab");
        }else{
            confirmPassField.setStyle("-fx-background-color: #a6ffc9");
        }
    }
    private void setMessageText(String text){
        messageText.setText(text);
        messageHbox.setVisible(true);

        FadeTransition fade = new FadeTransition();

        fade.setDuration(Duration.millis(2500));
        fade.setFromValue(1);
        fade.setToValue(0);
        fade.setNode(messageHbox);

        fade.play();
    }

    public void whiteField(KeyEvent keyEvent) {
        oldPassField.setStyle("-fx-background-color: white");
    }
}
