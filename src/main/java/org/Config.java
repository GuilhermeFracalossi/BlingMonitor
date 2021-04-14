package org;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Config {
//    Methods to get app configurations
    private static final String CONFIG_FILE_NAME = "config.properties";

    private static Properties configFileCache = null;

    public static final HashMap<String, Integer> DEFAULT_VALUES = new HashMap<String, Integer>();
    public static HashMap<String, Integer> propertiesSetted = new HashMap<String, Integer>();

    public Config(){
        DEFAULT_VALUES.put("ip_start_scan", 2);
        DEFAULT_VALUES.put("ip_end_scan", 254);
        DEFAULT_VALUES.put("port_start_scan", 8081);
        DEFAULT_VALUES.put("port_end_scan", 8086);
        DEFAULT_VALUES.put("default_scan_config", 1);
        DEFAULT_VALUES.put("advanced_configuration", 0);
        DEFAULT_VALUES.put("number_threads", 50);
        DEFAULT_VALUES.put("timeout", 1500);
        DEFAULT_VALUES.put("silent_mode", 0);
        DEFAULT_VALUES.put("active_verification", 1);

    }

    private static Properties readConfigFile(){

        if(configFileCache != null){
            return configFileCache;
        }

        configFileExists();

        try (InputStream inputConfigFile = new FileInputStream(CONFIG_FILE_NAME)) {
            Properties configuration = new Properties();
            configuration.load(inputConfigFile);
            propertiesSetted = (HashMap<String, Integer>) DEFAULT_VALUES.clone();
            configFileCache = configuration;
            return configFileCache;

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return null;
    }
    private static void configFileExists(){
        File configFile = new File(CONFIG_FILE_NAME);
        if(!configFile.exists()){
            try {
                configFile.createNewFile();
                saveToConfigFile(DEFAULT_VALUES);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveToConfigFile(Map<String, Integer> configValues) {
        try (OutputStream output = new FileOutputStream(CONFIG_FILE_NAME)) {

            Properties prop = new Properties();

            // set the properties value
            prop.setProperty("ip_start_scan", String.valueOf(configValues.get("ip_start_scan")));
            prop.setProperty("ip_end_scan", String.valueOf(configValues.get("ip_end_scan")));
            prop.setProperty("port_start_scan", String.valueOf(configValues.get("port_start_scan")));
            prop.setProperty("port_end_scan", String.valueOf(configValues.get("port_end_scan")));
            prop.setProperty("default_scan_config", String.valueOf(configValues.get("default_scan_config")));
            prop.setProperty("advanced_configuration", String.valueOf(configValues.get("advanced_configuration")));
            prop.setProperty("number_threads", String.valueOf(configValues.get("number_threads")));
            prop.setProperty("timeout", String.valueOf(configValues.get("timeout")));
            prop.setProperty("silent_mode", String.valueOf(configValues.get("silent_mode")));
            prop.setProperty("active_verification", String.valueOf(configValues.get("active_verification")));

            prop.store(output, null);
            configFileCache = null;
        } catch (IOException io) {
            io.printStackTrace();
        }
    }
    public static HashMap<String, Integer> getAllProperties(){

        Properties properties = readConfigFile();
        //update properties
        for (String key : properties.stringPropertyNames()) {
            String value = properties.getProperty(key);
            propertiesSetted.put(key, Integer.valueOf(value));
        }
        return propertiesSetted;

    }

    public static int get(String property){
        Properties cfgFile = readConfigFile();

        String response = cfgFile.getProperty(property);
        if (response != null){
            return Integer.parseInt(response);
        }

        return DEFAULT_VALUES.get(property);

    }
    public static void setProperty(String propertyName, Integer value){

        readConfigFile();
        propertiesSetted.put(propertyName, value);
        saveToConfigFile(propertiesSetted);

    }
}
