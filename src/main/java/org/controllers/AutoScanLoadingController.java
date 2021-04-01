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
import org.Config;
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

        startIp = Integer.parseInt(Config.get("ip_start_scan"));
        endIp= Integer.parseInt(Config.get("ip_end_scan"));
        startPort = Integer.parseInt(Config.get("port_start_scan"));
        endPort = Integer.parseInt(Config.get("port_end_scan"));
        threads = Integer.parseInt(Config.get("number_threads"));
        timeout = Integer.parseInt(Config.get("timeout"));

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

    public void showCamerasRegisteredPage(ActionEvent actionEvent) throws IOException {

        ManualRegisterController.enableScan = true;
        Parent camerasRegisteredRoot= FXMLLoader.load(getClass().getResource("/org/FxmlScreens/manualRegisterScreen.fxml"));
        Scene window = ((Node) actionEvent.getSource()).getScene();
        //Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        window.setRoot(camerasRegisteredRoot);
    }

}
