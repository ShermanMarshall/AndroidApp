package com.example.sherman.securityapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

public class ShowVideo extends ActionBarActivity {
    ConnectionManager connMann;
    ConfigManager configMann;
    ImageView imageView;

    Handler handler = new Handler() {
        public void handleMessage(Message message) {
            imageView.setImageBitmap(
                Bitmap.createBitmap(
                connMann.getReadyBuffer(),
                Constants.CVIMGWIDTH.n(),
                Constants.CVIMGHEIGHT.n(),
                Bitmap.Config.ARGB_8888));
            connMann.prepareNext();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setContentView(R.layout.activity_show_video);
        imageView = (ImageView) findViewById(R.id.video);
        connMann = ConnectionManager.newInstance(this);
        configMann = ConfigManager.newInstance(this);
        Bundle extras = this.getIntent().getExtras();
        int deviceNum = extras.getInt("device_number");
        final Device device = configMann.config.devices.get(deviceNum);
        Thread t = new Thread(new Runnable() {
            public void run() {
                if (connMann.connectionEstablished(device.get("ip"))) {
                    connMann.setHandler(handler);
                    Log.i("threadstate", connMann.getState().toString());
                    while ((connMann.getState() != Thread.State.TERMINATED) &&
                            (connMann.getState() != Thread.State.NEW)) {
                        try {
                            Thread.sleep(1000);
                            Log.i("stuck", "here");
                        } catch (InterruptedException e) { }
                    }
                    connMann.start();
                }
            }
        });
        t.start();
    }

    protected void onStop() {
        super.onStop();
        connMann.isConnected = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_video, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
