package com.example.sherman.securityapp;

public enum Constants {
    CVIMGSIZE(640*480),
    CVCOLORCOUNT(640*480*3),
    BUFFERMAX(10),
    DATAIDX(0),
    BMPIDX(1),
    PARSEIDX(2),
    CVIMGWIDTH(640),
    CVIMGHEIGHT(480),
    BUFFERIDXES(3),
    CONFIGMENU("ConfigMenu"),
    DEVICE_CAMERA(0, "Camera"),
    CODEFORCONFIG(1),
    CODEFORAPPMENU(2);

    private final int size;
    private final String name;
    private Constants(int val) { this.size = val; name = null; }
    private Constants(String string) { this.name = string; size = 0; }
    private Constants (int val, String name) { this.size = val; this.name = name; }
    public int n() { return this.size; }
    public String str() { return this.name; }
}
