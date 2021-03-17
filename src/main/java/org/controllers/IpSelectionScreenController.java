package org.controllers;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


public class IpSelectionScreenController implements Initializable {
    public static String previousScreen;
    public static List ips = new ArrayList();

    public boolean checkAll = false;
    ToggleGroup group = new ToggleGroup();
    RadioButton ipRadio[] = new RadioButton[100];

    @FXML
    BorderPane mainPane;

    @FXML
    VBox ipsListVbox;

    @FXML
    Button nextBtn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        for(int i=0; i<ips.size(); i++){
            if(ips.get(i) == null){
                continue;
            }
            ipRadio[i] = new RadioButton((String) ips.get(i)+"*");
            ipRadio[i].setStyle("-fx-font: 15 System");
            ipRadio[i].setStyle("-fx-text-fill: white");

            ipRadio[i].setToggleGroup(group);

            ipsListVbox.getChildren().add(ipRadio[i]);

            if(i==0){
                ipRadio[0].selectedProperty().setValue(true);
            }

        }
    }

    public void verifyAllIpsToggle(ActionEvent actionEvent) {
        if(checkAll == false){
            ipsListVbox.setDisable(true);
            checkAll = true;
        }else{
            ipsListVbox.setDisable(false);
            checkAll = false;
        }



    }




    public void startScanning(ActionEvent actionEvent) throws IOException {


        RadioButton radioSelected = (RadioButton) group.getSelectedToggle();

        String ip = cleanIp(radioSelected.getText());

        AutoScanLoadingController.ipSelected = ip;

        Parent autoScanLoadingRoot = FXMLLoader.load(getClass().getResource("/org/FxmlScreens/autoScanLoadingScreen.fxml"));

        Scene window = ((Node) actionEvent.getSource()).getScene();
        window.setRoot(autoScanLoadingRoot);

    }

    private String cleanIp(String ipDirty) {
        StringBuilder ipReverse;
        ipReverse = new StringBuilder(ipDirty).reverse();
        String ipCutted  = ipReverse.toString();
        ipCutted = ipCutted.substring(1);
        return new StringBuilder(ipCutted).reverse().toString();

    }
    public void backToStartScreen(ActionEvent actionEvent) throws IOException {
        Parent camerasMainScreenRoot = FXMLLoader.load(getClass().getResource(previousScreen));
        //Scene camerasMainScreen = new Scene(camerasMainScreenRoot);
        Scene window = ((Node) actionEvent.getSource()).getScene();
        window.setRoot(camerasMainScreenRoot);
        //Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        //window.setScene(camerasMainScreen);
    }
}
