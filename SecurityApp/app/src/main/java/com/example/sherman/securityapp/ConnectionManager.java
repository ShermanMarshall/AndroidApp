package com.example.sherman.securityapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ConnectionManager extends Thread {
    WifiManager wm;
    String ip;
    static ConnectionManager connMann;
    BufferManager bufferManager;
    boolean isConnected;
    Handler handler;
    Thread periodicIpChecker;
    int ipv4Bytes;

    private ConnectionManager(WifiManager _wm) {
        wm = _wm;
        bufferManager = new BufferManager();
        verifyIP();
        connMann = this;
        ipv4Bytes = 0;
        verifyIP();
        periodicIpChecker = new Thread(new Runnable() {
            public void run() {
            while (true) {
                int bytes = wm.getConnectionInfo().getIpAddress();
                if (bytes != ipv4Bytes) {
                    ipv4Bytes = bytes;
                    byte tmp;
                    String ipv4 = "";
                    for (int x = 0; x < 4; x++) {
                        tmp = (byte) (bytes & (0xFF));
                        if (tmp < 0)
                            ipv4 += Integer.toString(256 - (tmp * -1)) + ".";
                        else
                            ipv4 += Integer.toString(tmp) + ".";
                        bytes = bytes >>> 8;
                    }
                    ip = ipv4.substring(0, ipv4.length() - 1);
                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    ipv4Bytes = -1;
                    periodicIpChecker.start();
                }
            }
            }
        });
        periodicIpChecker.start();
        isConnected = false;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public void run() {
        try {
            Socket s = connMann.connect();
            if (s != null) {
                Log.i("Init", "Connection established");
                //createParsers();
                isConnected = true;
                DataInputStream dis = new DataInputStream(s.getInputStream());
                while (isConnected) {
                    dis.readFully(bufferManager.getData()); //Block and read the 640*480*3 values
                    bufferManager.dataReady();              //Increment buffer index, get next stream
                    alternate();
                    handler.sendMessage(handler.obtainMessage());
                }
                s.close();
            } else
                Log.i("Error", "Failed to connect");
        } catch (IOException e) {
            Log.e("Error", e.getMessage() + ", " + e.getLocalizedMessage());
        } finally {
            isConnected = false;
        }
    }

    public void alternate() {
        byte[] data = bufferManager.dataForNextBitmap();
        int[] activeBitmap = bufferManager.getBitmap();

        for (int x = 0, r, g, b; x < Constants.CVCOLORCOUNT.n(); x +=3) {
            b = data[x];
            if (b < 0)
                b = 256 - (b * -1);
            g = data[x+1];
            if (g < 0)
                g = 256 - (g * -1);
            r = data[x+2];
            if (r < 0)
                r = 256 - (r * -1);
            activeBitmap[x/3] = Color.argb(0xFF, r, g, b);
            //Log.i("int value", Integer.toString(activeBitmap[x/3]));
        }
        for (int x = 0; x < 3; x++)
            bufferManager.complete[x] = false;

        handler.sendMessage(handler.obtainMessage());
    }

    public static ConnectionManager newInstance(Activity activity) {
        if (connMann == null)
            connMann = new ConnectionManager((WifiManager) activity.getSystemService(Context.WIFI_SERVICE));
        return connMann;
    }

    public void verifyIP() {
        int bytes = wm.getConnectionInfo().getIpAddress();
        byte tmp;
        this.ip = "";
        for (int x = 0; x < 4; x++) {
            tmp = (byte) (bytes & (0xFF));
            if (tmp < 0)
                this.ip += Integer.toString(256 - (tmp * -1)) + ".";
            else
                this.ip += Integer.toString(tmp) + ".";
            bytes = bytes >>> 8;
        }
        this.ip = this.ip.substring(0, this.ip.length() - 1);
    }

    public String getIP() {
        return this.ip;
    }

    public Socket connect() {
        try {
            ServerSocket ss = new ServerSocket(27014, 10, InetAddress.getByName(ip));
            Socket init = new Socket(InetAddress.getByName("192.168.0.5"), 27011);
            OutputStream os = init.getOutputStream();
            os.write((getIP() + ":init").getBytes());
            Socket s = ss.accept();
            os.close();
            init.close();
            return s;
        } catch (IOException e) {
            Log.e("Error", e.getMessage() + ", " + e.getLocalizedMessage());
        }
        return null;
    }

    public void createParsers() {
        for (int x = 0; x < 3; x++) {
            final int val = x;
            Thread t = new Thread(new Runnable() {
                public void run() {
                while (isConnected) {
                    try {
                        Thread.sleep(100);
                        if (bufferManager.hasNext() && !bufferManager.complete[val]) {
                            /*
                            Log.i("Parsers", "Parsing for thread #" + val +
                                    " idx#" + bufferManager.idx[Constants.PARSEIDX.n()] +
                                    " began");
                            */
                            byte[] data = bufferManager.dataForNextBitmap();
                            int[] activeBitmap = bufferManager.getBitmap();

                            for (int x = val, tmp; x < Constants.CVCOLORCOUNT.n(); x+=3) {
                                tmp = data[x];
                                if (tmp < 0)
                                    tmp = 256 - (tmp * -1);
                                activeBitmap[x/3] += (tmp << (8 * val)) + (0x55000000);
                            }
                            if (bufferManager.setComplete(val))
                                handler.sendMessage(handler.obtainMessage());
                        }
                    } catch (InterruptedException e) {
                        Log.e("Error", e.getMessage() + ", " + e.getLocalizedMessage());
                    }
                }
                }
            }); t.start();
            //Log.i("Threaderator", "Thread #" + val + " created");
        }
    }

    public boolean connectionEstablished(String ip) {
        Socket s = null;
        try {
            s = new Socket(InetAddress.getByName("192.168.0.5"), 27011);

            OutputStream os = s.getOutputStream();
            DataInputStream dis = new DataInputStream(s.getInputStream());
            String syn = ip + ":test";
            byte[] ack = new byte[22];
            try {
                for (int x = 0; x < 10; x++) {
                    os.write(syn.getBytes());
                    dis.readFully(ack);
                    if ((new String(ack)).equals("connection_established")) {
                        s.close();
                        return true;
                    }
                }
            } catch (IOException e) {
                Log.i(e.getClass().getName(), e.getMessage());
            }
        } catch (IOException e) {
            Log.i("IOE", e.getMessage());
        } finally {
            try {
                if (s != null)
                    s.close();
            } catch (IOException e) {
                Log.i("IOE", "socket not connected");
            }
        } return false;
    }

    public int[] getReadyBuffer() {
        return bufferManager.getColors();
    }

    public void prepareNext() {
        bufferManager.prepareNext();
    }
}
