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

    private static Map<Integer, CamerasConfig> CameraCache = new HashMap<>();
    private static boolean isCameraCached = false;

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
                cameraInstance.setBrightness(results.getFloat("brightness"));
                cameraInstance.setContrast(results.getFloat("contrast"));
                cameraInstance.setGamma(results.getFloat("gamma"));
                cameraInstance.setSaturation(results.getFloat("saturation"));

                CameraCache.put(cameraInstance.getId(), cameraInstance);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    isCameraCached = true;
    }

    public static boolean validadeCamerasCache(){
        if(!isCameraCached){
            getConfigs();
        }
        return true;
    }
    public static int camerasCount() {
        validadeCamerasCache();
        return CameraCache.size();
    }

    public static HashMap getCamerasList() {
        validadeCamerasCache();
        return (HashMap) CameraCache;
    }

    public static CamerasConfig getCamera(Integer id) {
        validadeCamerasCache();
        return CameraCache.get(id);
    }

    public static boolean isCameraAlreadyRegistered(String ip, int port) {
        for (Map.Entry<Integer, CamerasConfig> entry : CameraCache.entrySet()) {
            CamerasConfig camerasObj = entry.getValue();
            if (camerasObj.getAddress().equals(ip) && camerasObj.getPort() == port) {
                return true;
            }
        }
        return false;
    }

    public void delete(){
        if(this.id != null){
//            If the ID is null (IN THEORY) this camera is not registered in the database yet,
//                    so no need to actually run the command
            Database.deleteCamera(this.id);
            CameraCache.remove(this.id);
        }
    }

    public void save() {
//        If the ID of this class is not set, that means the camera is not in the database
        if (this.id == null){
            this.id = Math.toIntExact(Database.insertCamera(this));
            CameraCache.put(this.id,this);
        }else{
//            If the ID is set, that means you want to update a already existing camera
            Database.updateCamera(this);
            CameraCache.replace(this.id,this);
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

    public void setAdjustmentsToDefault() {
        this.gamma = 1;
        this.brightness = 1;
        this.saturation =1;
        this.contrast =1;
    }
}
