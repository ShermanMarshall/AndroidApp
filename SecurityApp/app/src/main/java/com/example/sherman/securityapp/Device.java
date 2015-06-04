package com.example.sherman.securityapp;

import java.io.Serializable;

/**
 * Created by sherman on 5/7/2015.
 */
public class Device implements Serializable {
    public String name, ip;//v4, ipv6;
    public int id;
    public Constants type;
    public Device(String[] data) {
        //this.id = Integer.parseInt(data[0]);
        this.name = data[0];
        this.ip = data[1];
        setType(Integer.parseInt(data[2]));
    }
    public Device (String name, String ip, int type, int id) {
        this.name = name;
        this.ip = ip;
        this.id = id;
        setType(type-1);
    }
    public void setType(int type){
        Constants[] device_types = {Constants.DEVICE_CAMERA};
        this.type = device_types[type];
    }
    public void setName(String name) { this.name = name; }
    public void setIP(String ip) { this.ip = ip; }
    public String get(String s) {
        switch(s.toLowerCase()) {
            case "name":
                return this.name;
            case "ip":
                return this.ip;
            case "type":
                return type.str();
        }
        return "";
    }
    public int getType() { return type.n(); }
    public String toString() {
        return name + "|" + ip + "|" + type.n() + "\n";
    }
}
