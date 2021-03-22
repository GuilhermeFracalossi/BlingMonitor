package org.controllers;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.network.GetCameraUrls;
import org.json.JsonWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;


public class AutoScanLoadingController implements Initializable {
    public static String ipSelected;
    List<Object[]> camerasFound;

    @FXML
    HBox topPane;

    @FXML
    Button nextBtn;

    @FXML
    Text statusTxt;

    @FXML
    ImageView tickImg;
    @FXML
    Text statusScanningLabel;

    @FXML
    ProgressBar progressBarScanning;

    private int startIp;
    private int endIp;
    private int startPort;
    private int endPort;
    private int threads;
    private int timeout;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        GetCameraUrls getCameras = new GetCameraUrls();
        readConfigFile();
        try {

            Task scanning = new Task<Void>() {
                @Override public Void call() throws Exception {

                    camerasFound = getCameras.main(startIp, endIp, startPort, endPort, threads, timeout);

                    JSONObject rootJson = new JSONObject();
                    JSONArray allCameras = new JSONArray();

                    for (int index = 0; index < camerasFound.size(); index++) {
                        JSONObject cameraUnique = new JSONObject();

                        //ID START AT 1
                        cameraUnique.put("id", (index + 1));
                        cameraUnique.put("cameraName", "Camera " + (index + 1));
                        cameraUnique.put("address", camerasFound.get(index)[0]);
                        cameraUnique.put("port", camerasFound.get(index)[1]);

                        //1 == default
                        cameraUnique.put("brightness", 1);
                        cameraUnique.put("contrast", 1);
                        cameraUnique.put("saturation", 1);
                        cameraUnique.put("gamma", 1);

                        allCameras.add(cameraUnique);

                    }
                    rootJson.put("cameras", allCameras);

                    JsonWriter.main(rootJson);

                    scanFinished();
                    return null;

                }
            };
            new Thread(scanning).start();

            progressBarScanning.progressProperty().bind(getCameras.progressProperty());



            } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void scanFinished() throws IOException {

        statusScanningLabel.setText("Concluído");
        nextBtn.setVisible(true);
        tickImg.setVisible(true);
    }

    public void readConfigFile(){
        File configFile = new File("config.properties");
        if(!configFile.exists()){
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try (InputStream input = new FileInputStream("config.properties")) {

            Properties prop = new Properties();
            prop.load(input);



            startIp = prop.getProperty("ip_start_scan") == null ? ConfigScreenController.START_IP_DEFAULT_VALUE : Integer.parseInt(prop.getProperty("ip_start_scan"));
            endIp = prop.getProperty("ip_end_scan") == null ? ConfigScreenController.END_IP_DEFAULT_VALUE : Integer.parseInt(prop.getProperty("ip_end_scan"));

            startPort = prop.getProperty("port_start_scan") == null ? ConfigScreenController.START_PORT_DEFAULT_VALUE : Integer.parseInt(prop.getProperty("port_start_scan"));
            endPort = prop.getProperty("port_end_scan") == null ? ConfigScreenController.END_PORT_DEFAULT_VALUE : Integer.parseInt(prop.getProperty("port_end_scan"));

            threads = prop.getProperty("number_threads") == null ? ConfigScreenController.THREADS_DEFAULT_VALUE : Integer.parseInt(prop.getProperty("number_threads"));
            timeout = prop.getProperty("timeout") == null ? ConfigScreenController.TIMEOUT_DEFAULT_VALUE : Integer.parseInt(prop.getProperty("timeout"));




        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void showCamerasRegisteredPage(ActionEvent actionEvent) throws IOException {

        ManualRegisterController.enableScan = true;
        Parent camerasRegisteredRoot= FXMLLoader.load(getClass().getResource("/org/FxmlScreens/manualRegisterScreen.fxml"));
        Scene window = ((Node) actionEvent.getSource()).getScene();
        //Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        window.setRoot(camerasRegisteredRoot);
    }

}
