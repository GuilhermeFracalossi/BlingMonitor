package org.controllers;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.CamerasConfig;
import org.Config;
import org.network.GetAvailableIps;
import org.database.Database;


public class ManualRegisterController implements Initializable {
    FadeTransition fade;
    public static boolean enableScan = false;
    public static boolean previousScreenScan = false;
    public static int newCameras;
    @FXML Button userBtn;
    @FXML Button backBtn;
    @FXML Button reescanBtn;
    @FXML HBox  registerMessageHbox;

    @FXML VBox camerasRegisteredList;

    @FXML private TextField cameraAddress;
    @FXML private TextField name;
    @FXML private Text registerText;

    @FXML Text camerasFoundText;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        if(enableScan){
            reescanBtn.setVisible(true);
            reescanBtn.setManaged(true);
            backBtn.setVisible(false);
            backBtn.setManaged(false);
        }
        if(previousScreenScan){
            camerasFoundText.setVisible(true);
            if (newCameras == 1) {
                camerasFoundText.setText(newCameras + " câmera nova encontrada");
            } else {
                camerasFoundText.setText(newCameras + " câmeras novas encontradas");
            }
            previousScreenScan = false;
        }

        fillCamerasRegistered();
        try {
            userBtn.setText(getUserName());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public String getUserName() throws SQLException {
        return Database.getUsers().getString(2);
    }

    public void manualRegisterAction(ActionEvent actionEvent) throws Exception {

        String cameraName = name.getText();
        String addressCamera = cameraAddress.getText();

        if(cameraName.isEmpty() | addressCamera.isEmpty()){
            registerMessage("Preencha todos os campos");
            return;
        }

        String regex = ".*[/@]((25[0-5]|(2[0-4]|1[0-9]|[1-9]|)[0-9])(\\.(?!$)|$)){3}(25[0-5]|(2[0-4]|1[0-9]|[1-9]|)[0-9])(:|/|$).*";

        boolean matches = Pattern.matches(regex, addressCamera);

        if(!matches){
            registerMessage("Insira um endereço válido");
            return;
        }

        CamerasConfig cameraObj = new CamerasConfig();

        cameraObj.setName(cameraName);
        cameraObj.setAddress(addressCamera);
        cameraObj.setAdjustmentsToDefault();
        cameraObj.save();

        resetTextFields();

        addCamera(cameraName, addressCamera, cameraObj.getId());
        registerMessage("Câmera registrada com sucesso");
    }

    public static boolean isInteger(String s) {
        try
        {
            Integer.parseInt(s);

            return true;
        }
        catch (NumberFormatException ex)
        {
            return false;
        }
    }
    private void fillCamerasRegistered() {
        CamerasConfig.getCamerasList().forEach((id,v) ->{
            CamerasConfig cameraObj = (CamerasConfig) v;

            String name = cameraObj.getName();
            String address = cameraObj.getAddress();

            addCamera(name, address, cameraObj.getId());
        });
    }

    public void addCamera(String cameraName, String address, long id)  {

        cameraName = cameraName.trim();
        address = address.trim();

        HBox cameraContainer = new HBox();
        cameraContainer.setAlignment(Pos.CENTER_LEFT);
        cameraContainer.setPrefHeight(75);
        cameraContainer.setStyle( "-fx-border-color: #9e9e9e;");
        cameraContainer.setPadding(new Insets(0,0,0,15));

        Image cameraImg = new Image(getClass().getResource("/org/images/camera-icon-low.png").toString());

        ImageView imageView = new ImageView();
        imageView.setImage(cameraImg);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setCache(true);
        imageView.setFitHeight(50);
        imageView.setFitWidth(50);
        cameraContainer.getChildren().add(imageView);


        Separator separador = new Separator();
        separador.setOrientation(Orientation.VERTICAL);
        separador.setPrefWidth(50);
        cameraContainer.getChildren().add(separador);

        //identificacoes
        Text nomeDaCamera = new Text();
        nomeDaCamera.setText(cameraName);
        nomeDaCamera.setFont(Font.font ("System", 20));
        nomeDaCamera.setFill(Color.WHITE);
        nomeDaCamera.setStyle("-fx-alignment: center");


        Text enderecoCamera = new Text();
        enderecoCamera.setText(address);
        enderecoCamera.setFont(Font.font ("System", 12));
        enderecoCamera.setFill(Color.WHITE);
        enderecoCamera.setStyle("-fx-alignment: center");

        //Containers
        VBox informationContainer = new VBox();
        cameraContainer.setHgrow(informationContainer, Priority.ALWAYS);
        informationContainer.setSpacing(10);
        informationContainer.setAlignment(Pos.CENTER);

        HBox addressContainer = new HBox();
        addressContainer.setAlignment(Pos.CENTER);
        //Region regiaoEspacadora = new Region();

        //container do botao de excluir
        HBox deleteContainer = new HBox();
        deleteContainer.setAlignment(Pos.TOP_RIGHT);
        deleteContainer.setPadding(new Insets(5,5,0,0));


        //botao de excluir
        //File deleteFile = new File("src/images/deleteIconLOW.png");
        Image deleteIconIMG = new Image(getClass().getResource("/org/images/delete-icon-low.png").toString());

        ImageView deleteImageView = new ImageView();
        deleteImageView.setImage(deleteIconIMG);
        deleteImageView.setPreserveRatio(true);
        deleteImageView.setSmooth(true);
        deleteImageView.setCache(true);
        deleteImageView.setFitHeight(20);
        deleteImageView.setFitWidth(20);
        Button deleteBtn = new Button();
        deleteBtn.setGraphic(deleteImageView);
        deleteBtn.setStyle("-fx-background-color: none");
        deleteBtn.setCursor(Cursor.HAND);

        deleteBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                camerasRegisteredList.getChildren().remove(cameraContainer);
                removeCamera(id);
            }
        });


        deleteContainer.getChildren().add(deleteBtn);

        addressContainer.getChildren().addAll(enderecoCamera);

        informationContainer.getChildren().addAll(nomeDaCamera, addressContainer);
        cameraContainer.getChildren().addAll(informationContainer, deleteContainer);

        camerasRegisteredList.getChildren().add(cameraContainer);

    }

    public void removeCamera(long ID) {
        CamerasConfig cameraObj = CamerasConfig.getCamera((int) ID);
        cameraObj.delete();
    }

    private void resetTextFields() {
        name.clear();
        cameraAddress.clear();
    }

    private void registerMessage(String message) {

        registerText.setText(message);
        registerMessageHbox.setVisible(true);
        registerMessageHbox.setOpacity(1);

        if(fade != null){
            fade.stop();
        }
        fade = new FadeTransition();
        fade.setDelay(Duration.seconds(2));
        fade.setDuration(Duration.millis(1000));
        fade.setFromValue(1);
        fade.setToValue(0);
        fade.setNode(registerMessageHbox);
        fade.play();

    }

    public void visualizeCameras(ActionEvent actionEvent) throws IOException {

        if(CamerasConfig.camerasCount() == 0){
            registerMessage("Nenhuma câmera cadastrada");
        }else{
            Parent camerasMainScreenRoot = FXMLLoader.load(getClass().getResource("/org/FxmlScreens/allCamerasMainGridScreen.fxml"));
            //Parent camerasMainScreenRoot = FXMLLoader.load(getClass().getResource("/FxmlScreens/teste.fxml"));
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = ((Node) actionEvent.getSource()).getScene();
            boolean maximized = Config.get("maximized") == 1;
            if(maximized){
                stage.setMaximized(true);
            }
            scene.setRoot(camerasMainScreenRoot);
        }


    }

    public void scanAgain(ActionEvent actionEvent) throws IOException {

        ArrayList availableIps = new GetAvailableIps().main();
        IpSelectionScreenController.ips = availableIps;
        IpSelectionScreenController.previousScreen = "/org/FxmlScreens/manualRegisterScreen.fxml";
        AutoScanLoadingController.previousScreen = "/org/FxmlScreens/manualRegisterScreen.fxml";
        Parent camerasMainScreenRoot = FXMLLoader.load(getClass().getResource("/org/FxmlScreens/ipSelectionScreen.fxml"));
        //Scene camerasMainScreen = new Scene(camerasMainScreenRoot);
        //Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = ((Node) actionEvent.getSource()).getScene();
        scene.setRoot(camerasMainScreenRoot);
       // window.setScene(camerasMainScreen);
    }
    public void configScreen(ActionEvent actionEvent) throws IOException {
        ConfigScreenController.previousScreen = "/org/FxmlScreens/manualRegisterScreen.fxml";
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

        UserScreenController.previousScreen = "/org/FxmlScreens/manualRegisterScreen.fxml";
        Parent loginScreenRoot = FXMLLoader.load(getClass().getResource("/org/FxmlScreens/userScreen.fxml"));
        Scene window = ((Node) actionEvent.getSource()).getScene();
        window.setRoot(loginScreenRoot);

    }

    public void backToStartScreen(ActionEvent actionEvent) throws IOException {
        Parent startScreenRoot = FXMLLoader.load(getClass().getResource("/org/FxmlScreens/startScreen.fxml"));
        //Scene camerasMainScreen = new Scene(camerasMainScreenRoot);
        Scene window =  ((Node) actionEvent.getSource()).getScene();
        window.setRoot(startScreenRoot);
    }
}