package org;

import org.database.Database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class CamerasConfig {
    private Integer id = null;
    private String name = "camera";
    private String address;

    private int port = 8081;

    private float brightness = 1;
    private float gamma = 1;
    private float saturation = 1;
    private float contrast = 1;

    private static Map<Integer, CamerasConfig> cameras = new HashMap<>();
    private static boolean isCamerasReaded = false;

    public CamerasConfig(int id){
        this.id = id;
    }
    public CamerasConfig(){

    }

    public static void getConfigs(){
        try {

            ResultSet results = Database.execute("SELECT * FROM cameras", true);

            while (results.next()) {
//                Get all the camera info
                CamerasConfig cameraInstance = new CamerasConfig(results.getInt("id"));
                cameraInstance.setName(results.getString("name"));
                cameraInstance.setAddress(results.getString("address"));
                cameraInstance.setPort(results.getInt("port"));
                cameraInstance.setBrightness(results.getInt("brightness"));
                cameraInstance.setContrast(results.getInt("contrast"));
                cameraInstance.setGamma(results.getInt("gamma"));
                cameraInstance.setSaturation(results.getInt("saturation"));

                cameras.put(cameraInstance.getId(), cameraInstance);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    isCamerasReaded = true;
    }

    public static int camerasCount() {
        if(!isCamerasReaded){
            getConfigs();
        }
        return cameras.size();
    }

    public static HashMap getCamerasList() {
        if(!isCamerasReaded){
            getConfigs();
        }
        return (HashMap) cameras;
    }

    public static CamerasConfig getCamera(Integer id) {
        if (!isCamerasReaded){
            getConfigs();
        }
        return cameras.get(id);
    }

    public void delete(){
        if(this.id != null){
//            If the ID is null (IN THEORY) this camera is not registered in the database yet,
//                    so no need to actually run the command
            Database.deleteCamera(this.id);
            cameras.remove(this.id);
        }
    }
    public int getId(){
        return id;
    }
    public float getContrast() {
        return contrast;
    }

    public void setContrast(float contrast) {
        this.contrast = contrast;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public float getBrightness() {
        return brightness;
    }

    public void setBrightness(float brightness) {
        this.brightness = brightness;
    }

    public float getGamma() {
        return gamma;
    }

    public void setGamma(float gamma) {
        this.gamma = gamma;
    }

    public float getSaturation() {
        return saturation;
    }

    public void setSaturation(float saturation) {
        this.saturation = saturation;
    }

    public void save() {
//        If the ID of this class is not set, that means the camera is not in the database
        if (this.id == null){
            this.id = Math.toIntExact(Database.insertCamera(this));
        }else{
//            If the ID is set, that means you want to update a already existing camera
            System.out.println("Updating camera with id: "+this.id);
            Database.updateCamera(this);
        }
        cameras.put(this.id,this);
    }

    public void setAdjustmentsToDefault() {
        this.gamma = 1;
        this.brightness = 1;
        this.saturation =1;
        this.contrast =1;
    }
}
