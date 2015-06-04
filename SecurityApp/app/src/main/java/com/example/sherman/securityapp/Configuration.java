package com.example.sherman.securityapp;

import android.content.Context;
import android.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Configuration {
    Context c;
    String firstName, lastName;
    ArrayList<Device> devices;
    boolean hasMinConfig, isConfigured;

    public Configuration(Context _c) {
        getConfiguration(_c);
    }

    public boolean validateIP(String ip) {
        String[] values = ip.split("\\.");
        if (values.length != 4)
            return false;

        for (String s : values)
            try {
                int num = Integer.parseInt(s);
                if (num > 255 || num < 0)
                    return false;
            } catch (NumberFormatException e) {
                return false;
            }
        return true;
    }

    public void addDevice(String _name, String _ip, int _type) {
        devices.add( new Device(_name, _ip, _type, devices.size() + 1));
    }

    public void getConfiguration(Context _c) {
        this.c = _c.getApplicationContext();
        File file = c.getFileStreamPath("config.txt");
        if (file.exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                ArrayList<String> theData = new ArrayList<>();

                String data;
                while ((data = br.readLine()) != null) {
                    theData.add(data);
                    Log.i("config.txt", data);
                }

                firstName = theData.get(0);
                lastName = theData.get(1);
                devices = new ArrayList<>(Integer.parseInt(theData.get(2)));
                hasMinConfig = true;

                for (int x = 3; x < theData.size(); x++)
                    devices.add(new Device(theData.get(x).split("\\|")));

                isConfigured = true;
                br.close();
            } catch (IOException e) {
                Log.e("IOE", e.getMessage());
            }
        } else {
            hasMinConfig = isConfigured = false;
            devices = new ArrayList<>();
        }
    }

    public String saveConfiguration() {
        String status;
        try {
            FileOutputStream fos = c.openFileOutput("config.txt", c.MODE_PRIVATE);
            PrintWriter pw = new PrintWriter(new FileWriter(fos.getFD()));

            pw.write(firstName + "\n");
            pw.write(lastName + "\n");
            pw.write(devices.size() + "\n");

            for (Device d : devices)
                pw.write(d.toString());

            pw.close();
            status = "Save successful";
        } catch (IOException e) {
            Log.e("IOE", e.getMessage());
            status = "Save unsuccessful";
        }
        return status;
    }
}