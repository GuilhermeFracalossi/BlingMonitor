package org;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
//    Methods to get app configurations
    private static String configFileName = "config.properties";

    private static Properties configFileCache = null;

    public static final int START_IP_DEFAULT_VALUE = 1;
    public static final int END_IP_DEFAULT_VALUE  = 254;
    public static final int START_PORT_DEFAULT_VALUE  = 8081;
    public static final int END_PORT_DEFAULT_VALUE  = 8086;
    public static final int THREADS_DEFAULT_VALUE  = 50;
    public static final int TIMEOUT_DEFAULT_VALUE  = 1500;
    private static final boolean DEFAULT_OPTIONS_SCAN_DEFAULT_VALUE  = true;
    public static final boolean SILENT_MODE_DEFAULT_VALUE  = false;
    public static final boolean VERIFICATION_MODE_DEFAULT_VALUE  = true;
    private static final boolean ADVANCED_CONFIGS_DEFAULT_VALUE  = false;


private static Properties readConfigFile(){

    if(configFileCache != null){
        return configFileCache;
    }

    ConfigFileExists();

    try (InputStream inputConfigFile = new FileInputStream(configFileName)) {
        Properties configuration = new Properties();
        configuration.load(inputConfigFile);

        configFileCache = configuration;
        return configFileCache;

    } catch (IOException ex) {
        ex.printStackTrace();
    }

    return null;
}
private static void ConfigFileExists(){
    File configFile = new File(configFileName);
    if(!configFile.exists()){
        try {
            configFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

public static String get(String property){
    Properties cfgFile = readConfigFile();

    String response = cfgFile.getProperty(property);
    if (response != null){
        return response;
    }

//    if property cant be read, return the default values
    switch (property) {
        case "ip_start_scan":
            return String.valueOf(START_IP_DEFAULT_VALUE);
        case "ip_end_scan":
            return String.valueOf(END_IP_DEFAULT_VALUE);

        case "port_start_scan":
            return String.valueOf(START_PORT_DEFAULT_VALUE);
        case "port_end_scan":
            return String.valueOf(END_PORT_DEFAULT_VALUE);

        case "number_threads":
            return String.valueOf(THREADS_DEFAULT_VALUE);
        case "silent_mode":
            return String.valueOf(SILENT_MODE_DEFAULT_VALUE);

        case "active_verification":
            return String.valueOf(VERIFICATION_MODE_DEFAULT_VALUE);

        case "default_configuration_scan":
            return String.valueOf(DEFAULT_OPTIONS_SCAN_DEFAULT_VALUE);

        case "timeout":
            return String.valueOf(TIMEOUT_DEFAULT_VALUE);

        case "advanced_configuration":
            return String.valueOf(ADVANCED_CONFIGS_DEFAULT_VALUE);

        default:
            return null;
    }
}
}
