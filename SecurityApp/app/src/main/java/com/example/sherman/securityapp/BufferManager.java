package com.example.sherman.securityapp;

import android.util.Log;

public class BufferManager {
    int[][]bitmaps;
    byte[][] data;
    boolean[] complete = {false, false, false};
    boolean[] containsData;
    int idx[];

    public BufferManager() {
        bitmaps = new int[Constants.BUFFERMAX.n()][Constants.CVIMGSIZE.n()];
        data = new byte[Constants.BUFFERMAX.n()][Constants.CVCOLORCOUNT.n()];
        idx = new int[Constants.BUFFERIDXES.n()];
        containsData = new boolean[Constants.BUFFERMAX.n()];    }

    public byte[] getData() {
        byte[] reference = data[idx[Constants.DATAIDX.n()]];
        if (idx[Constants.DATAIDX.n()] < Constants.BUFFERMAX.n() - 1)
            idx[Constants.DATAIDX.n()]++;
        else idx[Constants.DATAIDX.n()] = 0;
        return reference; }

    public byte[] dataForNextBitmap() {
        return data[idx[Constants.PARSEIDX.n()]];
    }

    public void dataReady() {
        containsData[idx[Constants.BMPIDX.n()]] = true;
        if (idx[Constants.BMPIDX.n()] < Constants.BUFFERMAX.n() - 1)
            idx[Constants.BMPIDX.n()]++;
        else
            idx[Constants.BMPIDX.n()] = 0;  }

    public int[] getBitmap() {
        return bitmaps[idx[Constants.PARSEIDX.n()]]; }

    public boolean hasNext() {
        if (containsData[idx[Constants.PARSEIDX.n()]]) {
            return true;
        } else
            return false;    }

    public int[] getColors() {
        //containsData[idx[Constants.BMPIDX.n()]] = false;
        return bitmaps[idx[Constants.PARSEIDX.n()]];
    }

    public boolean setComplete(int index) {
        complete[index] = true;
        if (complete[0] && complete[1] && complete[2]) {
            containsData[Constants.PARSEIDX.n()] = false;

            setIncomplete();
            return true;
        } else
            return false;
    }

    public void setIncomplete() {
        for (int x = 0; x < 3; x++)
            complete[x] = false;
    }

    public void next() {
        if (idx[Constants.PARSEIDX.n()] < Constants.BUFFERMAX.n() - 1)
            idx[Constants.PARSEIDX.n()]++;
        else
            idx[Constants.PARSEIDX.n()] = 0;
    }

    public void prepareNext() {
        bitmaps[idx[Constants.PARSEIDX.n()]] = new int[Constants.CVIMGSIZE.n()];
        if (idx[Constants.PARSEIDX.n()] < Constants.BUFFERMAX.n() - 1)
            idx[Constants.PARSEIDX.n()]++;
        else
            idx[Constants.PARSEIDX.n()] = 0;
    }
}
