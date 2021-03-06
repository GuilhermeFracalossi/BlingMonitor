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
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.CamerasConfig;
import org.Config;
import org.network.GetCameraUrls;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;


public class AutoScanLoadingController implements Initializable {
    public static String previousScreen;
    public static String ipSelected;
    List<Object[]> camerasFound;

    @FXML HBox topPane;

    @FXML Button nextBtn;
    @FXML HBox progressHbox;

    @FXML
    VBox startInfoVbox;

    @FXML ImageView tickImg;
    @FXML Text statusScanningLabel;

    @FXML ProgressBar progressBarScanning;

    private int startIp;
    private int endIp;
    private int startPort;
    private int endPort;
    private int threads;
    private int timeout;

    int newCameras = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        progressHbox.setVisible(false);
        progressHbox.setManaged(false);
        startInfoVbox.setManaged(true);
        startInfoVbox.setVisible(true);

    }
    public void startScan(ActionEvent actionEvent) {
        progressHbox.setManaged(true);
        progressHbox.setVisible(true);
        startInfoVbox.setVisible(false);
        startInfoVbox.setManaged(false);
        GetCameraUrls getCameras = new GetCameraUrls();

        startIp = Config.get("ip_start_scan");
        endIp= Config.get("ip_end_scan");
        startPort = Config.get("port_start_scan");
        endPort = Config.get("port_end_scan");
        threads = Config.get("number_threads");
        timeout = Config.get("timeout");

        try {
            Task<Void> scanning = new Task<>() {
                @Override public Void call() throws Exception {
                    camerasFound = getCameras.main(startIp, endIp, startPort, endPort, threads, timeout);

                    for (int index = 0; index < camerasFound.size(); index++) {
                        String ip = (String) camerasFound.get(index)[0];
                        int port = (int) camerasFound.get(index)[1];
                        if (!CamerasConfig.isCameraAlreadyRegistered("http://"+ip+":"+port)){
                            newCameras++;
                            CamerasConfig  cameraObj = new CamerasConfig();
                            cameraObj.setName("Camera " +(index + 1));
                            cameraObj.setAddress("http://"+ip+":"+port);
                            cameraObj.setAdjustmentsToDefault();
//                         TODO change this to save on the END of the loop
                            cameraObj.save();
                        }

                    }
                    scanFinished();
                    return null;

                }
            };
            Thread scanningThread = new Thread(scanning);
            scanningThread.start();

            progressBarScanning.progressProperty().bind(getCameras.progressProperty());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void scanFinished(){
        statusScanningLabel.setText("Concluído");
        nextBtn.setVisible(true);
        tickImg.setVisible(true);
    }

    public void showCamerasRegisteredPage(ActionEvent actionEvent) throws IOException {

        ManualRegisterController.enableScan = true;
        ManualRegisterController.previousScreenScan = true;
        ManualRegisterController.newCameras = newCameras;
        Parent camerasRegisteredRoot= FXMLLoader.load(getClass().getResource("/org/FxmlScreens/manualRegisterScreen.fxml"));
        Scene window = ((Node) actionEvent.getSource()).getScene();
        //Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        window.setRoot(camerasRegisteredRoot);
    }


    public void back(ActionEvent actionEvent) throws IOException {
        Parent startScreenRoot = FXMLLoader.load(getClass().getResource(previousScreen));
        Scene window =  ((Node) actionEvent.getSource()).getScene();
        window.setRoot(startScreenRoot);
    }
}
