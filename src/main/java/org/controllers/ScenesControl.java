package org.controllers;

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
        MySql connection = new MySql();
        connection.prepareAll();

        Parent root = FXMLLoader.load(getClass().getResource("/org/FxmlScreens/loginScreen.fxml"));
        Scene startingScene = new Scene(root);
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
        logger.setInfoLog("BlingMonitor closed");
        Log.handler.close();
        MySql.FecharConexao();
        //System.exit(0);

    }
    public static void main(String[] args) {
        launch(args);
    }

}


