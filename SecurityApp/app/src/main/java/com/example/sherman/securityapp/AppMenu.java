package com.example.sherman.securityapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;


public class AppMenu extends ActionBarActivity {
    ConfigManager configMan;
    ListView lv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_menu);
        configMan = ConfigManager.newInstance(this);
        ((TextView) findViewById(R.id.user_name)).setText(configMan.showName());
        lv = (ListView) findViewById(R.id.app_devices);
        DeviceAdapter da = new DeviceAdapter(this, configMan.config.devices);
        lv.setAdapter(new DeviceAdapter(this, configMan.config.devices));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_app_menu, menu);
        MenuItem settings = menu.getItem(0);
        final Context c = this;
        settings.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(c, ConfigMenu.class);
                c.startActivity(intent);
                return false;
            }
        });
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
