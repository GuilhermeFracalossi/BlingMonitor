package org.json;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JsonReader
{
    public static JSONObject main(String[] args){
        
        File arquivo =  new File("src/main/resources/camerasIndexed.json");
        JSONObject fullJson = new JSONObject();


        if(arquivo.exists()){

            try (FileReader reader = new FileReader("src/main/resources/camerasIndexed.json")){
                
                JSONParser jsonParser = new JSONParser();
                Object obj = jsonParser.parse(reader);
                fullJson = (JSONObject) obj;


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        }




        return fullJson;
    }

//    public void parseCameraObject(JSONObject camera){
//        //Pega o objeto na lista
//        JSONObject cameraObject = (JSONObject) camera.get("camera");
//
//        //Pega o nome da camera
//        String cameraName = (String) cameraObject.get("CameraName");
//        System.out.println(cameraName);
//
//        //Pega o id
//        long id = (long) cameraObject.get("id");
//        System.out.println(id);
//
//        //Pega o endereço
//        String url = (String) cameraObject.get("URL");
//        System.out.println(url);
//    }
    
}
