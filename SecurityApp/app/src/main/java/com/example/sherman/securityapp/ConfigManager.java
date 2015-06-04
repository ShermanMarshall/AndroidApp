package com.example.sherman.securityapp;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Created by sherman on 5/3/2015.
 */
public class ConfigManager {
    Configuration config;
    static ConfigManager configManager;
    boolean isConnected = false;     //0: connected 1: timeout

    public static ConfigManager newInstance(Context c) {
        if (configManager != null)
            return configManager;

        configManager = new ConfigManager(c);
        return configManager;
    }

    public ConfigManager(Context c) {
        //this.context = c;
        config = new Configuration(c);
    }

    public void testConfig (String ip, Activity activity) {
        final Activity activityName = activity;
        final String ipName = ip;
        Thread t = new Thread(new Runnable() {
            public void run() {
                ConnectionManager connMann = ConnectionManager.newInstance(activityName);
                isConnected = (connMann != null) ? connMann.connectionEstablished(ipName) : false;
            }
        }); t.start();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Log.e("testConfig", e.getMessage());
        }
    }

    public boolean hasConnection(String ip, Activity activity) {
        Log.i("hasConnection", "0: " + isConnected);
        if (isConnected)
            return true;
        else {
            testConfig(ip, activity);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Log.i("hasConn", e.getMessage());
            }
            return isConnected;
        }
    }

    public void addDevice(String name, String ip, int type) {
        Log.i("Add Device", name + ", " + ip);
        config.addDevice(name, ip, type);
    }

    public boolean validateIP(String ip) {  return config.validateIP(ip);   }

    public int getSize() {
        if (this.config.devices == null)
            this.config.devices = new ArrayList<>(0);
        return this.config.devices.size();
    }
    public void saveConfiguration() { Log.i("saveconfig", config.saveConfiguration()); }
    public boolean hasMinConfig() {
        config.hasMinConfig = (firstNameSet(config.firstName) && lastNameSet(config.lastName)) ? true : false;
        return config.hasMinConfig;
    }
    public boolean isConfigured() { return config.isConfigured; }
    public boolean firstNameSet(String fName) {
        config.firstName = fName;
        Log.i("fName set as:", config.firstName);
        return (config.firstName == null) ? false : true;
    }
    public boolean lastNameSet(String lName) {
        config.lastName = lName;
        Log.i("lName set as:", config.lastName);
        return (config.lastName == null) ? false : true;
    }
    public String showName() { return config.firstName + " " + config.lastName; }
    public void updateDevice(String name, String ip, int type, int id) {
        Device device = (config.devices.get(id));
        device.setType(type);
        device.name = name;
        device.ip = ip;
    }
}
