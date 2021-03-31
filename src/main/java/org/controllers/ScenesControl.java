package org.controllers;

import org.PlayerInstance;
import org.controllers.AllCamerasMainGridScreenController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.Log;
import org.MySql;


public class ScenesControl extends Application {

    Log logger;
    @Override
    public void start(Stage stage) throws Exception{
        logger = Log.getInstance();
        logger.setInfoLog("BlingMonitor opened");

//        Instanciate MySQL and create the admin entry on the users table if its empty
//        if the table doesn't exists, then creates it
        MySql connection = new MySql();
        connection.prepareAll();


        Parent root = FXMLLoader.load(getClass().getResource("/org/FxmlScreens/loginScreen.fxml"));//Gets the frontend page
        Scene startingScene = new Scene(root);//Create a scene with the loginscreen

        //Opens a window, set the title,icon and set the first scene to show
        stage.setTitle("BlingMonitor");
        stage.getIcons().add(new Image(String.valueOf(getClass().getResource("/org/images/brand.png"))));
        stage.setScene(startingScene);
        stage.show();

        AllCamerasMainGridScreenController.stage = stage;

    }
    @Override
    public void stop() {
//        for (int i = 0; i < 100; i++) {
//
//
//            if(AllCamerasMainGridScreenController.audioPlayer[i] == null){
//                break;
//            }
//
//        }
//        System.out.println(players);
        logger.setInfoLog("BlingMonitor closed");
        Log.handler.close();
        MySql.FecharConexao();
        PlayerInstance.releaseAll();
        //System.exit(0);

    }
    public static void main(String[] args) {
        launch(args);
    }

}


