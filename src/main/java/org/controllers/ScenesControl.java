package org.controllers;

import org.Config;
import org.PlayerInstance;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.Log;
import org.database.Database;
import org.network.GetCameraUrls;

import java.util.Objects;
import java.util.concurrent.Future;


public class ScenesControl extends Application {

    String LOGIN_SCREEN = "/org/FxmlScreens/loginScreen.fxml";
    String SIGN_SCREEN = "/org/FxmlScreens/signInScreen.fxml";
    Log logger;
    @Override
    public void start(Stage stage) throws Exception{
        logger = Log.getInstance();
        logger.setInfoLog("BlingMonitor opened");

//        Instanciate MySQL and create the admin entry on the users table if its empty
//        if the table doesn't exists, then creates it
        Database connection = new Database();
        connection.prepareDatabase();

        Config config = new Config();

        String screen;
        if(connection.userExists()){
            screen = LOGIN_SCREEN;
        }else{
            screen = SIGN_SCREEN;
        }
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(screen)));
        //Gets the frontend page
//        Parent root = FXMLLoader.load(getClass().getResource("/org/FxmlScreens/allCamerasMainGridScreen.fxml"));//Gets the frontend page

        Scene startingScene = new Scene(root);//Create a scene with the loginscreen

        //Opens a window, set the title,icon and set the first scene to show
        stage.setTitle("BlingMonitor");
        stage.getIcons().add(new Image(String.valueOf(getClass().getResource("/org/images/brand.png"))));
        stage.setScene(startingScene);
        stage.show();

        AllCamerasMainGridScreenController.stage = stage;

    }
    @Override
    public void stop(){
        logger.setInfoLog("BlingMonitor closed");
        Log.handler.close();
        Database.closeConnection();
        PlayerInstance.releaseAll();

        for (Future<GetCameraUrls.ScanResult> futureTask : GetCameraUrls.futures) {
            futureTask.cancel(true);
        }

    }
    public static void main(String[] args) {
        launch(args);
    }

}


