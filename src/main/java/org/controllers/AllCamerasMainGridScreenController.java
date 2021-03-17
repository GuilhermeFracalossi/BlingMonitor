package org.controllers;


import java.io.*;

import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.FontWeight;
import javafx.stage.Screen;
import org.network.GetCameraUrls;
import org.Log;
import org.PlayerInstance;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;

import javafx.fxml.Initializable;
import javafx.geometry.*;
import javafx.scene.Node;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.json.JsonWriter;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import static uk.co.caprica.vlcj.javafx.videosurface.ImageViewVideoSurfaceFactory.videoSurfaceForImageView;

public class AllCamerasMainGridScreenController implements Initializable {

    public static Stage stage;
    Log logger;

    boolean fullScreenStage;


    final List<Future<GetCameraUrls.ScanResult>> camerasScanResult= new ArrayList<>();

    @FXML BorderPane mainBorderPane;
    @FXML HBox topPane;
    @FXML Button gridModeBtn;
    @FXML Button slideModeBtn;
    @FXML HBox topControlsMenu;


    @FXML Button fullScreenToggleBtn;
    @FXML ImageView fullScreenToggleImg;

    @FXML HBox gridSizeContainerHbox;
    @FXML Slider gridSizeSlider;

    private final Image cameraNotFoundImg = new Image(getClass().getResource("/org/images/cameraNotFound.jpg").toString());

    private final Image minimizeFullScreenPlayerImg = new Image(getClass().getResource("/org/images/exitFullScreenCameraLOW.png").toString());
    private final Image fullScreenPlayerImg = new Image(getClass().getResource("/org/images/cameraFullScreenLOW.png").toString());

    JSONObject fullJson;
    static JSONArray camerasList;

    boolean gridScreen = true;


    @FXML HBox playerControlsHbox;
    @FXML TextField cameraNameField;
    @FXML Label addressLabel;
    @FXML Label portLabel;
    @FXML Slider brightnessSlider;
    @FXML Slider contrastSlider;
    @FXML Slider saturationSlider;
    @FXML Slider gammaSlider;
    @FXML Button fullScreenCameraToggleBtn;
    @FXML ImageView fullScreenCameraToggleImg;


    Timeline slideRepeater;
    @FXML VBox centerContainerVbox;
    @FXML ScrollPane camerasScrollContainer = new ScrollPane();
    TilePane cameraViewGrid = new TilePane();
    Text loadingText;
    public final int HGAP_SIZE = 10;
    public final int VGAP_SIZE = 10;
    public final int BORDER_WIDTH = 3;

    double playerSizeSelected;
    StackPane cameraContainer[] = new StackPane[100];
    public MediaPlayerFactory mediaPlayerFactory = new MediaPlayerFactory();
    private final List<PlayerInstance> players = new ArrayList<PlayerInstance>();
    //ImageView cameraView[] = new ImageView[100];
    EmbeddedMediaPlayer playerSetted;


    StackPane alerterStack[] = new StackPane[100];
    HBox alertsContainer[] = new HBox[100];
    boolean fullScreenPlayer = false;
    long currentCameraIndex;
    String currentCameraName;
    String currentAddress;
    long currentPort;
    StackPane cameraContainerSetted;

    int lastFrameCount[] = new int[100];
    int currentFrameCount[] = new int[100];
    int reconeectionTolerance;
    Timeline analyzer[] = new Timeline[100];

    MediaPlayer[] audioPlayer = new MediaPlayer[100];

//    Thread startThread;
//    Timeline taskSlider;

    private boolean silentMode;
    private boolean verificationMode;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

//
        try {
            logger=Log.getInstance();
        } catch (IOException e) {
            e.printStackTrace();
        }
        readConfigFile();
       // String audioPath = new File("src/resources/alarm.mp3").getAbsolutePath();
        Media audioMedia = new Media(getClass().getResource("/org/alarm.mp3").toString());

        //AllCamerasMainGridScreenController camerasMainGridScreenController = new AllCamerasMainGridScreenController();
        //mainBorderPane.setUserData(this);

        playerControlsHboxStartingConfig();

        cameraContainerStartingConfig();

        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader("camerasIndexed.json"))
        {
            Object obj = jsonParser.parse(reader);
            fullJson = (JSONObject) obj;
            camerasList = (JSONArray) fullJson.get("cameras");

            if(camerasList.size() == 1){
                fullScreenPlayer = true;
                fullScreenCameraToggleBtn.setDisable(true);
                gridSizeContainerHbox.setDisable(true);
                gridModeBtn.setDisable(true);
                slideModeBtn.setDisable(true);
                gridModeBtn.setStyle("-fx-background-color: none;");
            }

            for(int i = 0; i < camerasList.size(); i++)
            {
                int cameraIndex = i;
                JSONObject cameraObject = (JSONObject) camerasList.get(i);
                String cameraName = (String) cameraObject.get("cameraName");
                String cameraAddress = (String) cameraObject.get("address");


                long cameraPort = (long) cameraObject.get("port");
                long id = (long) cameraObject.get("id");

                EmbeddedMediaPlayer embeddedMediaPlayer= mediaPlayerFactory.mediaPlayers().newEmbeddedMediaPlayer();
                PlayerInstance playerInstance = new PlayerInstance(embeddedMediaPlayer, cameraAddress, cameraPort);
                players.add(playerInstance);
                cameraContainer[i] = new StackPane();
                cameraContainer[i].setAlignment(Pos.BOTTOM_CENTER);
                cameraContainer[i].setStyle("-fx-border-width:"+BORDER_WIDTH+"px;"+"-fx-border-color: #698cde");
                cameraContainer[i].getChildren().add(playerInstance.videoSurface());
                cameraViewGrid.getChildren().add(cameraContainer[i]);
                }
            }
        catch (ParseException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
            imageControlsListeners();
            startPlayers();

            for (int i = 0; i < players.size(); i++) {
                int cameraIndex = i;
                createAlerts(i);


                audioPlayer[i] = new MediaPlayer(audioMedia);
                cameraContainer[i].setOnMouseEntered(new EventHandler() {
                    @Override
                    public void handle(Event event) {

                        Scene scene = cameraContainer[cameraIndex].getScene();
                        scene.setCursor(Cursor.HAND); //Change cursor to hand
                    }

                });
                cameraContainer[i].setOnMouseExited(new EventHandler() {
                    @Override
                    public void handle(Event event) {

                        Scene scene = cameraContainer[cameraIndex].getScene();
                        scene.setCursor(Cursor.DEFAULT); //Change cursor to hand
                    }

                });

//                if (camerasList.size() != 1) {

                    cameraContainer[i].setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent mouseEvent) {
                            if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {

                                stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
                                playerSetted = players.get(cameraIndex).mediaPlayer();
                                cameraContainerSetted = cameraContainer[cameraIndex];
                                currentCameraIndex = cameraIndex;
                                currentAddress = players.get(cameraIndex).cameraAddress();
                                currentPort = players.get(cameraIndex).cameraPort();
                                playerControls();


                                if (!players.get(cameraIndex).getCameraOpen()) {
                                    playerControlsHbox.setDisable(true);

                                } else {
                                    playerControlsHbox.setDisable(false);
                                }


                                if (mouseEvent.getClickCount() == 2) {
                                    fullScreenCameraToggle();
                                }
                            }
                        }
                    });

//                } else {
//
//
//                    playerSetted = players.get(cameraIndex).mediaPlayer();
//                    cameraContainerSetted = cameraContainer[cameraIndex];
//                    currentCameraIndex = cameraIndex;
//                    currentAddress = players.get(cameraIndex).cameraAddress();
//                    currentPort = players.get(cameraIndex).cameraPort();
//                    playerControls();
//
//                    System.out.println(cameraIndex);
//                    System.out.println(players.get(cameraIndex).getCameraOpen() + " camera");
//                    if (!players.get(cameraIndex).getCameraOpen()) {
//                        playerControlsHbox.setDisable(true);
//
//
//                    } else {
//                        playerControlsHbox.setDisable(false);
//                    }
//                }

        }

    }
//
    private synchronized void startPlayers() {

        Task<Void> start = new Task<Void>() {
            @Override
            protected Void call() throws Exception {

                ExecutorService es = Executors.newFixedThreadPool(10);
                for (int i =0; i< players.size(); i++){
                    camerasScanResult.add(GetCameraUrls.portIsOpen(es, players.get(i).cameraAddress(), (int)players.get(i).cameraPort(), 2000));
                }
                es.shutdown();

//                for (int i = 0; i < players.size(); i++) {
//                    players.get(i).setCameraOpen(true);
//                        players.get(i).mediaPlayer().videoSurface().set(videoSurfaceForImageView(players.get(i).videoSurface()));
//                        players.get(i).mediaPlayer().media().start("http://"+players.get(i).cameraAddress()+":"+players.get(i).cameraPort());
////
//                }

                for (final Future<GetCameraUrls.ScanResult> f : camerasScanResult) {
                    int i = camerasScanResult.indexOf(f);

                    if (f.get().isOpen()) {

                        players.get(i).setCameraOpen(true);
                        players.get(i).mediaPlayer().videoSurface().set(videoSurfaceForImageView(players.get(i).videoSurface()));
                        players.get(i).mediaPlayer().media().start("http://"+players.get(i).cameraAddress()+":"+players.get(i).cameraPort());

                    }else{
                        players.get(i).setCameraOpen(false);
                    }
                }
                int camerasOpened=0;
                for (int i = 0; i < players.size(); i++) {
                    players.get(i).videoSurface().setPreserveRatio(true);
                    if(players.get(i).getCameraOpen()){


                        camerasOpened++;
                        EmbeddedMediaPlayer mediaPlayer = players.get(i).mediaPlayer();
                        mediaPlayer.controls().start();



                        if(verificationMode == true){
                            fpsAnalizer(players.get(i).mediaPlayer(), i, players.get(i).cameraAddress(), (int) players.get(i).cameraPort());
                        }
                    }
                    else{

                        players.get(i).videoSurface().setImage(cameraNotFoundImg);
                    }

                }
                if(camerasOpened<2){

                    topControlsMenu.setDisable(true);
                }
                synchronized (this) {
                    VBox.setVgrow(loadingText, Priority.NEVER);
                    loadingText.setVisible(false);

                    loadingText.setManaged(false);
                    VBox.setVgrow(camerasScrollContainer, Priority.ALWAYS);
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {}


                for (Future<GetCameraUrls.ScanResult> f : camerasScanResult) {
                    int i = camerasScanResult.indexOf(f);

                    if (f.get().isOpen()) {

                        players.get(i).mediaPlayer().video().setAdjustVideo(true);
                    }
                }

                stage.heightProperty().addListener((observable, oldvalue, newvalue) -> {


                    if(fullScreenPlayer){
                        setPlayersSize();
                    }
                });

                stage.widthProperty().addListener((observable, oldvalue, newvalue) -> {

                    if(fullScreenPlayer){
                        setPlayersSize();
                    }

                });

                setPlayersSize();
                setPlayersImageAdjustments();
                gridSizeAdjust();
                camerasScrollContainer.setVisible(true);
                return null;
            };


        };
        Thread startThread = new Thread(start);
        startThread.start();

    }

    private void setPlayersImageAdjustments() {

        for (int i = 0; i < players.size(); i++) {

            JSONObject cameraObject = (JSONObject) camerasList.get(i);


            float gamma = Float.parseFloat(cameraObject.get("gamma").toString());
            float brightness = Float.parseFloat(cameraObject.get("brightness").toString());
            float contrast = Float.parseFloat(cameraObject.get("contrast").toString());
            float saturation = Float.parseFloat(cameraObject.get("saturation").toString());


            players.get(i).mediaPlayer().video().setGamma(gamma);
            players.get(i).mediaPlayer().video().setBrightness(brightness);
            players.get(i).mediaPlayer().video().setContrast(contrast);
            players.get(i).mediaPlayer().video().setSaturation(saturation);
        }
    }

    private void gridSizeAdjust(){
        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        int screenWidth = (int) screenBounds.getMaxX();
        gridSizeSlider.setMax(screenWidth);
        gridSizeContainerHbox.setDisable(false);
        gridSizeSlider.setValue(players.get(0).videoSurface().getFitWidth());
        gridSizeSlider.valueChangingProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> obs, Boolean wasChanging, Boolean isNowChanging) {
                if (!isNowChanging) {
                    playerSizeSelected = gridSizeSlider.getValue();
                    setPlayersSize();
                }
            }
        });
    }

    private void setPlayersSize() {

        if(playerSizeSelected != 0 && fullScreenPlayer!=true){


            for (int i = 0; i < players.size(); i++) {
                players.get(i).videoSurface().setFitWidth(playerSizeSelected);
            }

            return;
        }

        int numberOfcameras = 0;
        for (int i = 0; i < players.size(); i++) {
            if(cameraContainer[i].isVisible()){
                numberOfcameras++;
            }
        }


        final double RATIO = 1.333;
        int cameraColumnCount=  (numberOfcameras<=3 ? numberOfcameras : (int) Math.ceil((double) numberOfcameras/2));
        int cameraRowCount = (int) Math.ceil((double) numberOfcameras/cameraColumnCount);
        int availableHeightTotal = (int) (stage.getHeight()-playerControlsHbox.getHeight()-topPane.getHeight()-30);


        if((cameraColumnCount*RATIO)/cameraRowCount < (mainBorderPane.getWidth()/availableHeightTotal)){
            int heightSubtraction = VGAP_SIZE + BORDER_WIDTH * 2 + 5;
            int availableHeightPerCamera = (int) availableHeightTotal / cameraRowCount;
            int playersHeight = availableHeightPerCamera - heightSubtraction;
            for (int i = 0; i < players.size(); i++) {
                players.get(i).videoSurface().setFitWidth(playersHeight*RATIO);

            }
        }else{
            int widthSubtraction = HGAP_SIZE + BORDER_WIDTH*2 + 5;
            int availableWidthPerCamera = (int)camerasScrollContainer.getWidth()/cameraColumnCount;
            int playersWidth = availableWidthPerCamera - widthSubtraction;
            for (int i = 0; i < players.size(); i++) {
                players.get(i).videoSurface().setFitWidth(playersWidth);
            }
        }

    }
    private void playerControlsHboxStartingConfig() {
        cameraNameField.setStyle("-fx-text-fill: white;"+ "-fx-background-color: #131617;");
        if(playerControlsHbox.isVisible()){
            playerControlsHbox.setVisible(false);
        }
    }

    private void cameraContainerStartingConfig() {
        camerasScrollContainer.setVisible(false);
        camerasScrollContainer.setStyle("-fx-background-color:transparent;"+"-fx-background: #1F3146;");
        cameraViewGrid.setAlignment(Pos.CENTER);
        cameraViewGrid.setHgap(10);
        cameraViewGrid.setVgap(10);
        loadingText = new Text("Carregando visualização...");
        loadingText.setFill(Color.WHITE);
        loadingText.setFont(Font.font("System",FontWeight.BOLD, 20));
        VBox.setVgrow(loadingText, Priority.ALWAYS);
        VBox.setVgrow(camerasScrollContainer, Priority.NEVER);
        centerContainerVbox.getChildren().add(0,loadingText);
        camerasScrollContainer.setContent(cameraViewGrid);
    }
    public void fpsAnalizer(EmbeddedMediaPlayer player, int cameraIndex, String address, int port) {
        reconeectionTolerance = 0;
        lastFrameCount[cameraIndex] = 0;
        analyzer[cameraIndex] = new Timeline(new KeyFrame(Duration.seconds(5), new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                    currentFrameCount[cameraIndex] = players.get(cameraIndex).mediaPlayer().media().info().statistics().picturesDisplayed();
                    if(lastFrameCount[cameraIndex] == currentFrameCount[cameraIndex]){
                        if(!silentMode){
                            audioPlayer[cameraIndex].seek(audioPlayer[cameraIndex].getStartTime());
                            audioPlayer[cameraIndex].play();
                        }
                        logger.setWarning("Camera: "+address+":"+port+ " disconnected");
                        transmissionOffAlert(cameraIndex);
                        reconnectCamera(cameraIndex, address, port);
                        analyzer[cameraIndex].stop();

                    }else{
                        lastFrameCount[cameraIndex] = currentFrameCount[cameraIndex];
                        if(alerterStack[cameraIndex].isVisible()){
                            alerterStack[cameraIndex].setVisible(false);
                        }
                    }
            }

        }));
        analyzer[cameraIndex].setDelay(Duration.seconds(2));
        analyzer[cameraIndex].setCycleCount(Timeline.INDEFINITE);
        analyzer[cameraIndex].play();
    }

    public void reconnectCamera(int cameraIndex,String address, int port) {
        Task<Void> reconnector = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                boolean reconnected = false;
                while (reconnected != true) {
                    try {
                        Socket socket = new Socket();
                        socket.connect(new InetSocketAddress(players.get(cameraIndex).cameraAddress(), (int) players.get(cameraIndex).cameraPort()), 200);
                        socket.close();
                        reconnected = true;

                        Thread.sleep(7000);
                        players.get(cameraIndex).mediaPlayer().media().play("http://" + players.get(cameraIndex).cameraAddress() + ":" + players.get(cameraIndex).cameraPort());
                        succeedReconection(cameraIndex);
                        logger.setWarning("Camera: "+address+":"+port+ " reconnected");
                        lastFrameCount[cameraIndex] = 0;
                        analyzer[cameraIndex].playFromStart();
                    } catch (Exception ex) {

                    }
                }
                return null;
            };

        };
        Thread reconnectThread = new Thread(reconnector);
        reconnectThread.setDaemon(true);
        reconnectThread.start();

    }
    public void createAlerts(int index){
        alertsContainer[index] = new HBox();
        alertsContainer[index].setAlignment(Pos.CENTER);
        alertsContainer[index].setPadding(new Insets(3, 10, 3, 10));
        alertsContainer[index].setSpacing(10);
        alertsContainer[index].setStyle("-fx-background-color: #0E2035");

        Text statusText = new Text();
        statusText.setTextAlignment(TextAlignment.CENTER);
        statusText.setSmooth(true);

        statusText.setFont(Font.font ("System", 16));
        Circle statusCircle = new Circle();
        statusCircle.setStrokeWidth(0);
        statusCircle.setRadius(6);

        alertsContainer[index].getChildren().addAll(statusCircle, statusText);
        alerterStack[index] = new StackPane();
        alerterStack[index].getChildren().add(alertsContainer[index]);
        alerterStack[index].setMaxHeight(40);
        StackPane.setAlignment(alerterStack[index], Pos.BOTTOM_CENTER);
        alerterStack[index].setOpacity(0.8);
        alerterStack[index].setVisible(false);
        cameraContainer[index].getChildren().add(alerterStack[index]);

    }
    private void transmissionOffAlert(int index) {
        Text statusText = (Text) alertsContainer[index].getChildren().get(1);
        statusText.setText("Aviso: Perda de transmissão, tentando reconexão...");
        statusText.setFill(Color.rgb(255, 199, 0));
        Circle statusCircle = (Circle) alertsContainer[index].getChildren().get(0);
        statusCircle.setFill(Color.rgb(255, 199, 0));
        alerterStack[index].setVisible(true);
    }

    public void succeedReconection(int index){
        Text statusText = (Text) alertsContainer[index].getChildren().get(1);
        statusText.setText("Conexão reestabelecida");
        statusText.setFill(Color.rgb(0, 211, 21));
        Circle statusCircle = (Circle) alertsContainer[index].getChildren().get(0);
        statusCircle.setFill(Color.rgb(0, 211, 21));
    }


    private void imageControlsListeners() {
        gammaSlider.valueChangingProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> obs, Boolean wasChanging, Boolean isNowChanging) {
                if (!isNowChanging) {
                    playerSetted.video().setGamma((float) gammaSlider.getValue());
                    saveCameraAdjusts("gamma", gammaSlider.getValue());
                }
            }
        });

        contrastSlider.valueChangingProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> obs, Boolean wasChanging, Boolean isNowChanging) {
                if (!isNowChanging) {

                    playerSetted.video().setContrast((float) contrastSlider.getValue());
                    saveCameraAdjusts("contrast", contrastSlider.getValue());

                }
            }
        });
        saturationSlider.valueChangingProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> obs, Boolean wasChanging, Boolean isNowChanging) {
                if (!isNowChanging) {

                    playerSetted.video().setSaturation((float) saturationSlider.getValue());
                    saveCameraAdjusts("saturation", saturationSlider.getValue());
                }
            }
        });
        brightnessSlider.valueChangingProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> obs, Boolean wasChanging, Boolean isNowChanging) {
                if (!isNowChanging) {
                    playerSetted.video().setBrightness((float) brightnessSlider.getValue());
                    saveCameraAdjusts("brightness", brightnessSlider.getValue());
                }
            }
        });

    }

    public void playerControls(){
        updateCurrentCamera();
        defaultCameraStyles();
        cameraNameField.setText(currentCameraName);
        addressLabel.setText(currentAddress);
        portLabel.setText(String.valueOf(currentPort));

        cameraContainerSetted.setStyle("-fx-border-color: #ffffff;"+ "-fx-border-width:"+BORDER_WIDTH+"px;" +"-fx-background-color: black;");

        brightnessSlider.setValue(playerSetted.video().brightness());
        contrastSlider.setValue(playerSetted.video().contrast());
        saturationSlider.setValue(playerSetted.video().saturation());
        gammaSlider.setValue(playerSetted.video().gamma());

        if(!playerControlsHbox.isVisible()){
            playerControlsHbox.setVisible(true);
        }
    }

    private void defaultCameraStyles() {
        for(int i=0; i<players.size(); i++){
            cameraContainer[i].setStyle("-fx-background-color: black;"+"-fx-border-width:"+BORDER_WIDTH+"px;"+"-fx-border-color: #698cde");
        }
    }

//    private void removeAllBorderSelections() {
//        for(int i=0; i<players.size(); i++){
//            if(cameraContainer[i] == null){
//                break;
//            }
//            cameraContainer[i].setStyle("-fx-border-color: none");
//        }
//    }

    public void snapshotPlayer(ActionEvent actionEvent) {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH.mm.ss");
        String time = (String)(formatter.format(date));
        File file = new File("./BlingMonitor Snapshots");
        boolean dirCreated = file.mkdir();

        File photo = new File("./BlingMonitor Snapshots/"+time+" "+currentCameraName+".png");
        playerSetted.snapshots().save(photo);
    }

    public void confirmEditCameraName(ActionEvent actionEvent) {
        String newName = cameraNameField.getText();
        cameraNameField.setText(newName);
        camerasScrollContainer.requestFocus();

        JSONObject cameraObject = (JSONObject) camerasList.get((int) currentCameraIndex);
        cameraObject.put("cameraName", newName);
        updateJson();
        updateCurrentCamera();

    }
    public void saveCameraAdjusts(String adjustment, double value){
        JSONObject cameraObject = (JSONObject) camerasList.get((int) currentCameraIndex);
        cameraObject.put(adjustment, value);
        updateJson();
        updateCurrentCamera();
    }
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    private void updateCurrentCamera() {
        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader("camerasIndexed.json")) {
            Object obj = jsonParser.parse(reader);
            fullJson = (JSONObject) obj;
            camerasList = (JSONArray) fullJson.get("cameras");
            JSONObject cameraObject = (JSONObject) camerasList.get((int) currentCameraIndex);

            currentCameraName = (String) cameraObject.get("cameraName");
        }  catch (ParseException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void updateJson(){
        JsonWriter.main(fullJson);
    }

    public void resetControls(ActionEvent actionEvent) {
        gammaSlider.setValue(1);
        contrastSlider.setValue(1);
        saturationSlider.setValue(1);
        brightnessSlider.setValue(1);

        playerSetted.video().setGamma(1);
        playerSetted.video().setContrast(1);
        playerSetted.video().setSaturation(1);
        playerSetted.video().setBrightness(1);
        saveCameraAdjusts("gamma", 1);
        saveCameraAdjusts("saturation", 1);
        saveCameraAdjusts("brightness", 1);
        saveCameraAdjusts("contrast", 1);

    }

    public void fullScreenCameraToggle() {
        int indexPlayerFullScreen = (int) currentCameraIndex;
        int playerCollumn = indexPlayerFullScreen % 3;
        int playerRow = indexPlayerFullScreen / 3;

        if (fullScreenPlayer == false){
            fullScreenCameraToggleImg.setImage(minimizeFullScreenPlayerImg);
            for (int i = 0; i < players.size(); i++) {
                if(i != indexPlayerFullScreen){
                    cameraContainer[i].setVisible(false);
                    cameraContainer[i].setManaged(false);
                }
            }
            gridSizeContainerHbox.setDisable(true);
            fullScreenPlayer = true;
            setPlayersSize();

        }
        else if(fullScreenPlayer == true){
            fullScreenCameraToggleImg.setImage(fullScreenPlayerImg);
            for (int i = 0; i < players.size(); i++) {
                    cameraContainer[i].setVisible(true);
                    cameraContainer[i].setManaged(true);
            }
            gridSizeContainerHbox.setDisable(false);
            fullScreenPlayer = false;
            setPlayersSize();
        }

    }

    public void fullScreenToggle(){
        fullScreenStage = stage.isFullScreen();
        if(fullScreenStage){
            stage.setFullScreen(false);
            fullScreenStage = false;
            setPlayersSize();
        }
        else if(fullScreenStage == false){
            stage.setFullScreen(true);

            fullScreenStage = true;
            setPlayersSize();
        }

    }


    public void slideModeScreen(ActionEvent actionEvent) {

        if(gridScreen == true) {
            gridSizeContainerHbox.setDisable(true);

            gridScreen = false;
            HBox intervalContainer = new HBox();
            intervalContainer.setAlignment(Pos.CENTER);
            intervalContainer.setSpacing(5);

            //texto "Intervalo: "
            Text text = new Text();
            text.setText("Intervalo: ");
            text.setFont(Font.font("System", 17));
            text.setFill(Color.WHITE);
            text.setTextAlignment(TextAlignment.CENTER);

            //Spinner do intervalo
            Spinner intervalSpinner = new Spinner();
            SpinnerValueFactory spFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 120, 5);
            intervalSpinner.setPrefWidth(80);
            intervalSpinner.setValueFactory(spFactory);
            intervalSpinner.setEditable(true);

            //botao iniciar
            Button startBtn = new Button();
            startBtn.setText("Iniciar");
            startBtn.setFont(Font.font("System", 15));
            startBtn.setTextFill(Color.WHITE);
            startBtn.setStyle("-fx-background-color: #0092cc;");
            startBtn.setCursor(Cursor.HAND);
           // startBtn.setPrefWidth(60);

            intervalContainer.getChildren().addAll(text, intervalSpinner, startBtn);
            topControlsMenu.getChildren().add(intervalContainer);

            startBtn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    int INTERVAL = (int) intervalSpinner.getValue();

                    topControlsMenu.getChildren().remove(intervalContainer);

                    //removeAllBorderSelections();
                    slideModeBtn.setStyle("-fx-background-color:  #133f78;");
                    gridModeBtn.setStyle("-fx-background-color:  none;");

                    playerControlsHbox.setVisible(false);
                    playerControlsHbox.setManaged(false);

                    currentCameraIndex = 0;

                    //NECESSARIO POR QUE A TIMELINE SO INICIA APOS OS 5 SEGUNDOS DE DELAY
                    for (int i = 0; i < players.size(); i++) {
                        if(players.get((int) currentCameraIndex).getCameraOpen() == false){
                            currentCameraIndex++;
                        }
                        if(i != currentCameraIndex){
                            cameraContainer[i].setVisible(false);
                            cameraContainer[i].setManaged(false);
                        }
                    }
                    setPlayersSize();
                    Timeline taskSlider = new Timeline(new KeyFrame(Duration.seconds(INTERVAL), new EventHandler<ActionEvent>() {
                        int i = (int) currentCameraIndex+1;
                        @Override
                        public void handle(ActionEvent event) {

                            if(cameraContainer[i] == null){
                                i=0;

                            }
                            if(players.get(i).getCameraOpen() == false && cameraContainer[i+1]!= null){
                                i++;
                            }else if(players.get(i).getCameraOpen() == false && cameraContainer[i+1]== null){
                                i=0;
                            }

                            currentCameraIndex = i;
                            playerSetted = players.get(i).mediaPlayer();
                            cameraContainerSetted = cameraContainer[i];
                            currentAddress = players.get(i).cameraAddress();
                            currentPort = players.get(i).cameraPort();

                            for (int i = 0; i < players.size(); i++) {
                                if(i != currentCameraIndex){
                                    cameraContainer[i].setVisible(false);
                                    cameraContainer[i].setManaged(false);
                                }else if(i== currentCameraIndex){
                                    cameraContainer[i].setManaged(true);
                                    cameraContainer[i].setVisible(true);

                                }
                            }
                            setPlayersSize();
                            i++;
                        }
                    }));

                    taskSlider.setCycleCount(Timeline.INDEFINITE);
                    taskSlider.play();

                    slideRepeater = taskSlider;

                }
            });
        }
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
            silentMode = prop.getProperty("silent_mode") == null ? ConfigScreenController.SILENT_MODE_DEFAULT_VALUE : Boolean.parseBoolean(prop.getProperty("silent_mode"));
            verificationMode = prop.getProperty("active_verification") == null ? ConfigScreenController.VERIFICATION_MODE_DEFAULT_VALUE : Boolean.parseBoolean(prop.getProperty("active_verification"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    public void gridScreenMode(ActionEvent actionEvent) {
        //ALTERAR

        playerControlsHbox.setManaged(true);
        if (gridScreen == false) {
            gridScreen = true;
            slideRepeater.stop();
            gridModeBtn.setStyle("-fx-background-color: #133f78;");
            slideModeBtn.setStyle("-fx-background-color: none;");

            fullScreenPlayer = true;
            fullScreenCameraToggle();
            //playerControlsHbox.setVisible(true);
            for(int i=0; i<players.size(); i++){

                cameraContainer[i].setManaged(true);
            }
        }
    }

    public void backToCamerasRegistration(ActionEvent actionEvent) throws IOException {
        for (int i = 0; i <players.size() ; i++) {
            if(analyzer[i] != null){
                analyzer[i].stop();
            }

            audioPlayer[i].dispose();
            players.get(i).mediaPlayer().controls().stop();
            players.get(i).mediaPlayer().release();
        }
        //taskSlider.stop();
        //startThread.interrupt();

        mediaPlayerFactory.release();

        ManualRegisterController.enableScan = true;
        Parent manualRegisterRoot = FXMLLoader.load(getClass().getResource("/org/FxmlScreens/manualRegisterScreen.fxml"));
        Scene window = ((Node) actionEvent.getSource()).getScene();
        window.setRoot(manualRegisterRoot);
    }


}

