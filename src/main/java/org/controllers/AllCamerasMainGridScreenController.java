package org.controllers;


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
import org.CamerasConfig;
import org.Config;
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
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.base.VideoApi;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import static uk.co.caprica.vlcj.javafx.videosurface.ImageViewVideoSurfaceFactory.videoSurfaceForImageView;

public class AllCamerasMainGridScreenController implements Initializable {

    public static Stage stage;
    public boolean inCamerasScreen;
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
    @FXML Button snapshotBtn;
    @FXML Button resetControlsBtn;
    @FXML ImageView alarmToggleImg;
    @FXML Button alarmToggleBtn;


    private final Image cameraNotFoundImg = new Image(getClass().getResource("/org/images/camera-not-found.jpg").toString());

    private final Image minimizeFullScreenPlayerImg = new Image(getClass().getResource("/org/images/exit-full-screen-camera-low.png").toString());
    private final Image fullScreenPlayerImg = new Image(getClass().getResource("/org/images/camera-full-screen-low.png").toString());
    private final Image alarmOnImg = new Image(getClass().getResource("/org/images/bell-icon.png").toString());
    private final Image alarmOffImg = new Image(getClass().getResource("/org/images/bell-silent-icon.png").toString());


    boolean gridScreen = true;

    Timeline taskSlider;
    @FXML HBox intervalContainer;
    @FXML Spinner intervalSpinner = new Spinner();

   // Timeline slideRepeater;
    @FXML VBox centerContainerVbox;
    @FXML ScrollPane camerasScrollContainer = new ScrollPane();
    TilePane cameraViewGrid = new TilePane();
    Text loadingText;

//    Horizontal and vertical gap in the grid layout
    public final int HGAP_SIZE = 10;
    public final int VGAP_SIZE = 10;
    public final int BORDER_WIDTH = 3;


    double playerSizeSelected;
    StackPane[] cameraContainer = new StackPane[20];

    MediaPlayerFactory mediaPlayerFactory = new MediaPlayerFactory();

    EmbeddedMediaPlayer playerSetted;


    StackPane[] alerterStack = new StackPane[20];
    HBox[] alertsContainer = new HBox[20];
    boolean fullScreenPlayer = false;
    long currentCameraIndex;
    long currentCameraId;
    CamerasConfig currentCameraObj;


    String currentCameraName;
    String currentAddress;
    long currentPort;
    StackPane cameraContainerSetted;

//    Frame analyzer variables
    int[] lastFrameCount = new int[20];
    int[] currentFrameCount = new int[20];
    int reconnectionTolerance;
    Timeline[] analyzer = new Timeline[20];

    static MediaPlayer[] audioPlayer = new MediaPlayer[20];

    private boolean silentMode;
    private boolean verificationMode;
    private int index = 0;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        inCamerasScreen = true;
        setTooltips();
        intervalContainer.setManaged(false);
        intervalContainer.setVisible(false);

        try {
            logger=Log.getInstance();
        } catch (IOException e) {
            e.printStackTrace();
        }

        silentMode = Config.get("silent_mode") == 1;
        verificationMode = Config.get("active_verification") == 1;
        setAlarmIcon();

        //Clears the players array
        PlayerInstance.players.clear();

        //Gets warning sound that plays when connection to the camera is lost
        Media audioMedia = new Media(getClass().getResource("/org/alarm.mp3").toString());

//        Containers start configuration sequence
        playerControlsHboxStartingConfig();
        cameraContainerStartingConfig();

//        Logic to set to full screen if only one camera is registered
            if(CamerasConfig.camerasCount()== 1){
                fullScreenPlayer = true;
                fullScreenCameraToggleBtn.setDisable(true);
                gridSizeContainerHbox.setDisable(true);
                gridModeBtn.setDisable(true);
                slideModeBtn.setDisable(true);
                gridModeBtn.setStyle("-fx-background-color: none;");
            }

//            Logic to instantiate all the camera players and UI elements for it
//            In the end, all camera players are stored in the players property
//            Even though im using a foreach loop, i still need a index to put in the camera container
//            because im not sure if i can use the cameraID as a index to do this.
            index = 0;
            CamerasConfig.getCamerasList().forEach((id,v) ->{

                CamerasConfig cameraObj = (CamerasConfig) v;

                String cameraAddress = cameraObj.getAddress();
                int cameraId = cameraObj.getId();
                float gamma = cameraObj.getGamma();
                float brightness = cameraObj.getBrightness();
                float saturation = cameraObj.getSaturation();
                float contrast = cameraObj.getContrast();

//                Creates an VLC player instance and put it into the players instance list
                EmbeddedMediaPlayer embeddedMediaPlayer= mediaPlayerFactory.mediaPlayers().newEmbeddedMediaPlayer();

                PlayerInstance playerInstance = new PlayerInstance(
                    cameraId,embeddedMediaPlayer,cameraAddress,
                    gamma,brightness,saturation,contrast);


                cameraContainer[index] = new StackPane();
                cameraContainer[index].setAlignment(Pos.CENTER);
                cameraContainer[index].setStyle("-fx-border-width:"+BORDER_WIDTH+"px;"+"-fx-border-color: #698cde");
                cameraContainer[index].getChildren().add(playerInstance.videoSurface());
                cameraViewGrid.getChildren().add(cameraContainer[index]);

                index = index + 1;
            });

        imageControlsListeners();//Event listener for the image controls
        startPlayers();


//      Create the audio player to play the warning audio when the alert is triggered
        for (int i = 0; i < PlayerInstance.players.size(); i++) {
            int cameraIndex = i;
            createAlerts(i);//Create all the styles for the alerts

            audioPlayer[i] = new MediaPlayer(audioMedia);

//          Event handlers for mouse enter and leave and click
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
                    scene.setCursor(Cursor.DEFAULT); //Change cursor to default
                }
            });

            cameraContainer[i].setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                        if(gridScreen != false){
                            stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
                            playerSetted = PlayerInstance.players.get(cameraIndex).mediaPlayer();
                            cameraContainerSetted = cameraContainer[cameraIndex];

                            currentCameraIndex = cameraIndex;
                            currentCameraId = PlayerInstance.players.get(cameraIndex).getId();
                            currentCameraObj = CamerasConfig.getCamera((int) currentCameraId);
                            currentCameraName = currentCameraObj.getName();

                            currentAddress = currentCameraObj.getAddress();

                            playerControls();

                            //if the camera is disconnected, doesn't allow to change it's configuration like saturation or even take a snapshot
                            playerControlsHbox.setDisable(!PlayerInstance.players.get(cameraIndex).getCameraOpen());

                            if (mouseEvent.getClickCount() == 2 && CamerasConfig.camerasCount()>1) {
                                fullScreenCameraToggle();
                            }
                        }
                    }
                }
            });
        }
    }

    private void setTooltips() {
        gridModeBtn.setTooltip(new Tooltip("Modo grade"));
        slideModeBtn.setTooltip(new Tooltip("Modo slide"));
        alarmToggleBtn.setTooltip(new Tooltip("Ativar/desativar alertas sonoros"));
        fullScreenToggleBtn.setTooltip(new Tooltip("Ativar/desativar tela cheia"));
        snapshotBtn.setTooltip(new Tooltip("Tirar print da câmera"));
        resetControlsBtn.setTooltip(new Tooltip("Resetar todos ajustes de imagem"));
        fullScreenCameraToggleBtn.setTooltip(new Tooltip("Ativar/desativar câmera em tela cheia"));
        cameraNameField.setTooltip(new Tooltip("Alterar nome da câmera"));
    }

    private synchronized void startPlayers() {
        Task<Void> start = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                ExecutorService es = Executors.newFixedThreadPool(10);
                for (int i =0; i< PlayerInstance.players.size(); i++){
                    camerasScanResult.add(GetCameraUrls.portIsOpen(es, PlayerInstance.players.get(i).cameraAddress(), (int)PlayerInstance.players.get(i).cameraPort(), 3000));
                }
                es.shutdown();

                for (final Future<GetCameraUrls.ScanResult> f : camerasScanResult) {
                    int i = camerasScanResult.indexOf(f);

                    if (f.get().isOpen()) {
//                    if(true){
                        PlayerInstance.players.get(i).setCameraOpen(true);
                        PlayerInstance.players.get(i).mediaPlayer().videoSurface().set(videoSurfaceForImageView(PlayerInstance.players.get(i).videoSurface()));
                        PlayerInstance.players.get(i).mediaPlayer().media().start("http://"+PlayerInstance.players.get(i).cameraAddress()+":"+PlayerInstance.players.get(i).cameraPort());
//                        PlayerInstance.players.get(i).mediaPlayer().media().start(PlayerInstance.players.get(i).cameraAddress());
                    }else{
                        PlayerInstance.players.get(i).setCameraOpen(false);
                    }
                }
                int camerasOpened=0;
                for (int i = 0; i < PlayerInstance.players.size(); i++) {
                    PlayerInstance.players.get(i).videoSurface().setPreserveRatio(true);
                    if(PlayerInstance.players.get(i).getCameraOpen()){
                        camerasOpened++;
                        EmbeddedMediaPlayer mediaPlayer = PlayerInstance.players.get(i).mediaPlayer();
                        mediaPlayer.controls().start();
                        if(verificationMode == true){
                            fpsAnalyzer(i, PlayerInstance.players.get(i).cameraAddress(), (int) PlayerInstance.players.get(i).cameraPort());
                        }
                    }
                    else{
                        PlayerInstance.players.get(i).videoSurface().setImage(cameraNotFoundImg);
                    }
                }
                topControlsMenu.setDisable(camerasOpened < 2);
                synchronized (this) {
                    VBox.setVgrow(loadingText, Priority.NEVER);
                    loadingText.setVisible(false);
                    loadingText.setManaged(false);
                    VBox.setVgrow(camerasScrollContainer, Priority.ALWAYS);
                }

                Thread.sleep(1000);

                for (Future<GetCameraUrls.ScanResult> f : camerasScanResult) {
                    int i = camerasScanResult.indexOf(f);
                    if (f.get().isOpen()) {
                        PlayerInstance.players.get(i).mediaPlayer().video().setAdjustVideo(true);
                    }
                }
                stage.heightProperty().addListener((observable, oldvalue, newvalue) -> {
                    if(fullScreenPlayer && inCamerasScreen){
                        setPlayersSize();
                    }
                });
                stage.widthProperty().addListener((observable, oldvalue, newvalue) -> {
                    if(fullScreenPlayer && inCamerasScreen){
                        setPlayersSize();
                    }
                });

                setPlayersSize();
                setPlayersImageAdjustments();
                if (CamerasConfig.camerasCount() > 1) {
                    gridSizeAdjust();
                }
                defaultCameraStyles();
                camerasScrollContainer.setVisible(true);

                return null;
            }
        };
        Thread startThread = new Thread(start);
        startThread.setDaemon(true);
        startThread.start();
    }

    private void setPlayersImageAdjustments() {
        //Done
        for (int i = 0; i < PlayerInstance.players.size(); i++) {
            PlayerInstance player = PlayerInstance.players.get(i);
            VideoApi playerVideo = player.mediaPlayer().video();

            playerVideo.setGamma(player.getGamma());
            playerVideo.setBrightness(player.getBrightness());
            playerVideo.setContrast(player.getContrast());
            playerVideo.setSaturation(player.getSaturation());
        }
    }

    private void gridSizeAdjust(){
        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        int screenWidth = (int) screenBounds.getMaxX();
        gridSizeSlider.setMax(screenWidth);
        gridSizeContainerHbox.setDisable(false);
        gridSizeSlider.setValue(PlayerInstance.players.get(0).videoSurface().getFitWidth());
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
        double videoWidths = 0;
        double videoHeights = 0;
        int numberOfcameras = 0;
        for (int i = 0; i < PlayerInstance.players.size(); i++) {
            if(cameraContainer[i].isVisible()){
                numberOfcameras++;
                videoWidths += PlayerInstance.players.get(i).videoSurface().getBoundsInLocal().getWidth();
                videoHeights += PlayerInstance.players.get(i).videoSurface().getBoundsInLocal().getHeight();
            }
        }
        final double RATIO = (videoWidths/numberOfcameras) / (videoHeights/numberOfcameras);
        int cameraColumnCount=  (numberOfcameras<=3 ? numberOfcameras : (int) Math.ceil((double) numberOfcameras/2));
        int cameraRowCount = (int) Math.ceil((double) numberOfcameras/cameraColumnCount);
        int availableHeightTotal = (int) (stage.getHeight()-playerControlsHbox.getHeight()-topPane.getHeight()-30);

        if(playerSizeSelected != 0 && fullScreenPlayer!=true && gridScreen == true){
            for (int i = 0; i < PlayerInstance.players.size(); i++) {
                PlayerInstance.players.get(i).videoSurface().setFitWidth(playerSizeSelected);
                PlayerInstance.players.get(i).videoSurface().setFitHeight(playerSizeSelected/RATIO);
            }
            return;
        }

        if((cameraColumnCount*RATIO)/cameraRowCount < (mainBorderPane.getWidth()/availableHeightTotal)){
            int heightSubtraction = VGAP_SIZE + BORDER_WIDTH * 2 + 5;
            int availableHeightPerCamera = availableHeightTotal / cameraRowCount;
            int playersHeight = availableHeightPerCamera - heightSubtraction;
            for (int i = 0; i < PlayerInstance.players.size(); i++) {
                PlayerInstance.players.get(i).videoSurface().setFitWidth(playersHeight*RATIO);
                PlayerInstance.players.get(i).videoSurface().setFitHeight(playersHeight);
            }
        }else{
            int widthSubtraction = HGAP_SIZE + BORDER_WIDTH*2 + 5;
            int availableWidthPerCamera = (int)camerasScrollContainer.getWidth()/cameraColumnCount;
            int playersWidth = availableWidthPerCamera - widthSubtraction;
            for (int i = 0; i < PlayerInstance.players.size(); i++) {
                PlayerInstance.players.get(i).videoSurface().setFitWidth(playersWidth);
                PlayerInstance.players.get(i).videoSurface().setFitHeight(playersWidth/RATIO);
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

    public void fpsAnalyzer(int cameraIndex, String address, int port) {
        reconnectionTolerance = 0;
        lastFrameCount[cameraIndex] = 0;

        analyzer[cameraIndex] = new Timeline(new KeyFrame(Duration.seconds(5), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                currentFrameCount[cameraIndex] = PlayerInstance.players.get(cameraIndex).mediaPlayer().media().info().statistics().picturesDisplayed();
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
        analyzer[cameraIndex].setDelay(Duration.seconds(4));
        analyzer[cameraIndex].setCycleCount(Timeline.INDEFINITE);
        analyzer[cameraIndex].play();
    }

    public void reconnectCamera(int cameraIndex,String address, int port) {
        Task<Void> reconnector = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                boolean reconnected = false;
                while (reconnected != true) {
                    //if the socket.connect goes wrong, lands on catch and it loops again, that's why it works,
                    try {
                        Socket socket = new Socket();
                        socket.connect(new InetSocketAddress(PlayerInstance.players.get(cameraIndex).cameraAddress(), (int) PlayerInstance.players.get(cameraIndex).cameraPort()), 1500);
                        socket.close();

                        reconnected = true;
                        Thread.sleep(7000);
                        PlayerInstance.players.get(cameraIndex).mediaPlayer().media().play("http://" + PlayerInstance.players.get(cameraIndex).cameraAddress() + ":" + PlayerInstance.players.get(cameraIndex).cameraPort());
                        succeedReconnection(cameraIndex);
                        logger.setWarning("Camera: " + address + ":" + port + " reconnected");
                        lastFrameCount[cameraIndex] = 0;
                        analyzer[cameraIndex].playFromStart();
                    } catch (Exception ignored) { }
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

    public void succeedReconnection(int index){
        Text statusText = (Text) alertsContainer[index].getChildren().get(1);
        statusText.setText("Conexão reestabelecida");
        statusText.setFill(Color.rgb(0, 211, 21));
        Circle statusCircle = (Circle) alertsContainer[index].getChildren().get(0);
        statusCircle.setFill(Color.rgb(0, 211, 21));
    }

    private void imageControlsListeners() {
        gammaSlider.valueChangingProperty().addListener((obs, wasChanging, isNowChanging) -> {
            if (!isNowChanging) {
                playerSetted.video().setGamma((float) gammaSlider.getValue());
                currentCameraObj.setGamma((float) gammaSlider.getValue());
                currentCameraObj.save();
            }
        });

        contrastSlider.valueChangingProperty().addListener((obs, wasChanging, isNowChanging) -> {
            if (!isNowChanging) {
                playerSetted.video().setContrast((float) contrastSlider.getValue());
                currentCameraObj.setContrast((float) contrastSlider.getValue());
                currentCameraObj.save();
            }
        });
        saturationSlider.valueChangingProperty().addListener((obs, wasChanging, isNowChanging) -> {
            if (!isNowChanging) {
                playerSetted.video().setSaturation((float) saturationSlider.getValue());
                currentCameraObj.setSaturation((float) saturationSlider.getValue());
                currentCameraObj.save();
            }
        });
        brightnessSlider.valueChangingProperty().addListener((obs, wasChanging, isNowChanging) -> {
            if (!isNowChanging) {
                playerSetted.video().setBrightness((float) brightnessSlider.getValue());
                currentCameraObj.setBrightness((float) brightnessSlider.getValue());
                currentCameraObj.save();
            }
        });
    }

    public void playerControls(){
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
        for(int i=0; i<PlayerInstance.players.size(); i++){
            cameraContainer[i].setStyle("-fx-background-color: black;"+"-fx-border-width:"+BORDER_WIDTH+"px;"+"-fx-border-color: #698cde");
        }
    }

    public void snapshotPlayer(ActionEvent actionEvent) {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH.mm.ss");
        String time = formatter.format(date);
        File file = new File("./BlingMonitor Snapshots");
        file.mkdir();
        File photo = new File("./BlingMonitor Snapshots/"+time+" "+currentCameraName+".png");
        playerSetted.snapshots().save(photo);
    }

    public void confirmEditCameraName(ActionEvent actionEvent) {
        String newName = cameraNameField.getText();
        cameraNameField.setText(newName);
        camerasScrollContainer.requestFocus();

        CamerasConfig cameraObj = CamerasConfig.getCamera((int) currentCameraId);
        cameraObj.setName(newName);
        cameraObj.save();
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

        currentCameraObj.setAdjustmentsToDefault();
        currentCameraObj.save();
    }

    public void fullScreenCameraToggle() {
        int indexPlayerFullScreen = (int) currentCameraIndex;

        if (fullScreenPlayer == false){
            fullScreenCameraToggleImg.setImage(minimizeFullScreenPlayerImg);
            for (int i = 0; i < PlayerInstance.players.size(); i++) {
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
            for (int i = 0; i < PlayerInstance.players.size(); i++) {
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
    public void alarmToggle(ActionEvent actionEvent) {
        silentMode =! silentMode;

        for (int i = 0; i < PlayerInstance.players.size(); i++) {
            audioPlayer[i].setVolume(silentMode ? 0 : 1);
        }
        Config.setProperty("silent_mode", silentMode ? 1 : 0);
        setAlarmIcon();
    }

    private void setAlarmIcon(){
        if(silentMode == true){
            alarmToggleImg.setImage(alarmOffImg);
        }
        else{
            alarmToggleImg.setImage(alarmOnImg);
        }
    }

    public void slideModeScreen(ActionEvent actionEvent) {
        if(gridScreen == true) {
            if(!intervalContainer.isManaged()){
                SpinnerValueFactory<Integer> spFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 120, 5);
                intervalSpinner.setValueFactory(spFactory);

                intervalContainer.setManaged(true);
                intervalContainer.setVisible(true);
            }
        }
    }
    public void startSlideMode(ActionEvent actionEvent) {
        currentCameraIndex = fullScreenPlayer ? currentCameraIndex : 0;
        fullScreenCameraToggle();
        //if(fullScreenPlayer) fullScreenCameraToggle();
        gridScreen = false;
        int INTERVAL = (int) intervalSpinner.getValue();
        gridSizeContainerHbox.setDisable(true);
        intervalContainer.setManaged(false);
        intervalContainer.setVisible(false);
        slideModeBtn.setStyle("-fx-background-color:  #133f78;");
        gridModeBtn.setStyle("-fx-background-color:  none;");
        playerControlsHbox.setVisible(false);
        playerControlsHbox.setManaged(false);



        //NECESSARIO POR QUE A TIMELINE SO INICIA APOS OS 5 SEGUNDOS DE DELAY
        for (int i = 0; i < PlayerInstance.players.size(); i++) {
            if(PlayerInstance.players.get((int) currentCameraIndex).getCameraOpen() == false){
                currentCameraIndex++;
                continue;
            }
            for(int j = 0; j < PlayerInstance.players.size(); j++){
                if(j != currentCameraIndex){
                    cameraContainer[j].setVisible(false);
                    cameraContainer[j].setManaged(false);
                }else if(j == currentCameraIndex){

                    cameraContainer[j].setManaged(true);
                    cameraContainer[j].setVisible(true);
                }
            }

        }
        setPlayersSize();
        taskSlider = new Timeline(new KeyFrame(Duration.seconds(INTERVAL), new EventHandler<ActionEvent>() {
            int i = (int) currentCameraIndex+1;
            @Override
            public void handle(ActionEvent event) {

                if(cameraContainer[i] == null){
                    i=0;

                }
                while(!PlayerInstance.players.get(i).getCameraOpen()){
                    i++;
                    if(cameraContainer[i]== null){
                        i=0;

                    }
                }
                currentCameraIndex = i;
                playerSetted = PlayerInstance.players.get(i).mediaPlayer();
                cameraContainerSetted = cameraContainer[i];
                currentAddress = PlayerInstance.players.get(i).cameraAddress();
                currentPort = PlayerInstance.players.get(i).cameraPort();

                for (int i = 0; i < PlayerInstance.players.size(); i++) {
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
    }

    public void gridScreenMode(ActionEvent actionEvent) {
        intervalContainer.setVisible(false);
        intervalContainer.setManaged(false);

        playerControlsHbox.setManaged(true);
        if (gridScreen == false) {
            gridScreen = true;
            taskSlider.stop();

            gridModeBtn.setStyle("-fx-background-color: #133f78;");
            slideModeBtn.setStyle("-fx-background-color: none;");

            fullScreenPlayer = true;
            fullScreenCameraToggle();
            for(int i=0; i<PlayerInstance.players.size(); i++){
                cameraContainer[i].setManaged(true);
            }
        }
    }

    private void stopAllRunningTasks() {
        for (int i = 0; i <PlayerInstance.players.size() ; i++) {
            if(analyzer[i] != null){
                analyzer[i].stop();
                analyzer[i] = null;

            }
            audioPlayer[i].stop();
            audioPlayer[i].dispose();
        }
        PlayerInstance.releaseAll();

        if(mediaPlayerFactory != null){
            mediaPlayerFactory.release();
        }
        if(taskSlider != null){
            taskSlider.stop();
            taskSlider = null;
        }
    }
    public void backToCamerasRegistration(ActionEvent actionEvent) throws IOException {
        inCamerasScreen = false;
        stopAllRunningTasks();
        ManualRegisterController.enableScan = true;
        Parent manualRegisterRoot = FXMLLoader.load(getClass().getResource("/org/FxmlScreens/manualRegisterScreen.fxml"));
        Scene window = ((Node) actionEvent.getSource()).getScene();
        window.setRoot(manualRegisterRoot);
    }

    public void logout(ActionEvent actionEvent) throws IOException {
        stopAllRunningTasks();

        LoginScreenController.logged = false;
        Parent loginScreenRoot = FXMLLoader.load(getClass().getResource("/org/FxmlScreens/loginScreen.fxml"));
        Scene window = ((Node) actionEvent.getSource()).getScene();
        window.setRoot(loginScreenRoot);
    }
}