package org.controllers;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import org.network.GetAvailableIps;
import org.json.JsonReader;
import org.json.JsonWriter;
import org.MySql;


public class ManualRegisterController implements Initializable {
    FadeTransition fade;
    public static boolean enableScan = false;
    @FXML
    Button userBtn;
    @FXML
    Button backBtn;

    @FXML
    Button reescanBtn;
    @FXML
    HBox  registerMessageHbox;

    @FXML
    VBox camerasRegisteredList;

    @FXML
    private TextField cameraAddress;

    @FXML
    private TextField port;

    @FXML
    private TextField name;

    @FXML
    private Text registerText;

    JSONObject fullJson;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        if(enableScan == true){
            reescanBtn.setVisible(true);
            reescanBtn.setManaged(true);
            backBtn.setVisible(false);
            backBtn.setManaged(false);
        }
        fullJson = readJson();
        fillCamerasRegistered(fullJson);
        try {
            userBtn.setText(getUserName());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    private JSONObject readJson(){
        File arquivo =  new File("camerasIndexed.json");

        if(!arquivo.exists()){

            //cria arquivo com nenhuma camera listada

            JSONObject rootJson = new JSONObject();
            JSONArray allCameras = new JSONArray();

            rootJson.put("cameras", allCameras);

            JsonWriter.main(rootJson);
        }
        JsonReader reader = new JsonReader();

        return reader.main(null);
    }


    public String getUserName() throws SQLException {
        String userName = MySql.getData().getString(2);
        return userName;
    }
    public void manualRegisterAction(ActionEvent actionEvent) throws Exception {
        if(!isInteger(port.getText())){
            registerMessage("Insira apenas números no campo: Porta");
            return;
        }
         //Pega todos dados de camerasIndexed.json
        JsonReader reader = new JsonReader();
        JSONObject fullJson = reader.main(null);


        JSONArray camerasList = (JSONArray) fullJson.get("cameras");
        JSONObject cameraUnique = new JSONObject();

        String cameraName = name.getText();
        String enderecoCamera = cameraAddress.getText();
        int cameraID = camerasList.size()+1;
        int portNumber = Integer.parseInt(port.getText());


        cameraUnique.put("id", (camerasList.size()+1));
        cameraUnique.put("cameraName", cameraName);
        cameraUnique.put("address", enderecoCamera);
        cameraUnique.put("port", portNumber);

        cameraUnique.put("gamma", 1);
        cameraUnique.put("brightness", 1);
        cameraUnique.put("contrast", 1);
        cameraUnique.put("saturation", 1);
        cameraUnique.get(0);
        camerasList.add(cameraUnique);

        //Escreve no arquivo
        JsonWriter.main(fullJson);



       resetTextFields();

       addCamera(cameraName, enderecoCamera, portNumber, cameraID);
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
    private void fillCamerasRegistered(JSONObject fullJson) {

        JSONArray camerasList = (JSONArray) fullJson.get("cameras");

        for(int i=0; i<camerasList.size(); i++){
            JSONObject cameraObject = (JSONObject) camerasList.get(i);
            String name = (String) cameraObject.get("cameraName");
            String address = (String) cameraObject.get("address");
            long port = (long) cameraObject.get("port");
            long id = (long) cameraObject.get("id");
            addCamera(name, address, port, id);

        }
    }

    public void addCamera(String cameraName, String address, long port, long id)  {


        HBox cameraContainer = new HBox();
        cameraContainer.setAlignment(Pos.CENTER_LEFT);
        cameraContainer.setPrefHeight(75);
        cameraContainer.setStyle( "-fx-border-color: #9e9e9e;");
        cameraContainer.setPadding(new Insets(0,0,0,15));

        //imagem de camera

        //File cameraFile = new File("src/images/cameraIconLow.png");
        Image cameraImg = new Image(getClass().getResource("/org/images/camera-icon-low.png").toString());

        ImageView imageView = new ImageView();
        imageView.setImage(cameraImg);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setCache(true);
        imageView.setFitHeight(50);
        imageView.setFitWidth(50);
        cameraContainer.getChildren().add(imageView);

        //separador
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


        Text porta = new Text();
        porta.setText(String.valueOf(port));
        porta.setFont(Font.font ("System", 12));
        porta.setFill(Color.WHITE);
        porta.setStyle("-fx-alignment: center");

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
        Region regiaoEspacadora = new Region();

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

        addressContainer.getChildren().addAll(enderecoCamera,  regiaoEspacadora,porta);
        addressContainer.setHgrow(regiaoEspacadora, Priority.ALWAYS);

        informationContainer.getChildren().addAll(nomeDaCamera, addressContainer);
        cameraContainer.getChildren().addAll(informationContainer, deleteContainer);

        camerasRegisteredList.getChildren().add(cameraContainer);

    }

    public void removeCamera(long ID) {
        JsonReader reader = new JsonReader();
        JSONObject fullJson = reader.main(null);
        JSONArray camerasList = (JSONArray) fullJson.get("cameras");

        for(int i=0; i<camerasList.size(); i++){
            JSONObject cameraObject = (JSONObject) camerasList.get(i);
            long idTested = (long) cameraObject.get("id");
            if(idTested == ID){
               camerasList.remove(i);
            }
        }
        JsonWriter.main(fullJson);

    }

    private void resetTextFields() {
        name.clear();
        cameraAddress.clear();
        port.clear();
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
        fullJson = readJson();
        JSONArray camerasList = (JSONArray) fullJson.get("cameras");


        if(camerasList.size() == 0){
            registerMessage("Nenhuma câmera cadastrada");
        }else{
            Parent camerasMainScreenRoot = FXMLLoader.load(getClass().getResource("/org/FxmlScreens/allCamerasMainGridScreen.fxml"));
            //Parent camerasMainScreenRoot = FXMLLoader.load(getClass().getResource("/FxmlScreens/teste.fxml"));
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = ((Node) actionEvent.getSource()).getScene();
            stage.setMaximized(true);
            scene.setRoot(camerasMainScreenRoot);
        }


    }

    public void scanAgain(ActionEvent actionEvent) throws IOException {

        ArrayList availableIps = new GetAvailableIps().main();
        IpSelectionScreenController.ips = availableIps;
        IpSelectionScreenController.previousScreen = "/org/FxmlScreens/manualRegisterScreen.fxml";
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