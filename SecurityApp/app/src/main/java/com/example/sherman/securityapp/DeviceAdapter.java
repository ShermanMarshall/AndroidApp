package com.example.sherman.securityapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;

public class DeviceAdapter extends ArrayAdapter {
    private class ViewHolder { TextView deviceId, deviceName, deviceIP;  }
    Activity activity;
    ConfigManager configMan;

    public DeviceAdapter (Activity activity, List theList) {
        super(activity, android.R.layout.simple_list_item_1, theList);
        this.activity = activity;
        configMan = ConfigManager.newInstance(activity);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final Device device = (Device) getItem(position);
        convertView = activity.getLayoutInflater().inflate(R.layout.device, null);
        ViewHolder holder = new ViewHolder();

        holder.deviceId = (TextView) convertView.findViewById(R.id.dev_id);
        holder.deviceName = (TextView) convertView.findViewById(R.id.dev_name);
        holder.deviceIP = (TextView) convertView.findViewById(R.id.dev_ip);

        holder.deviceId.setText(Integer.toString(position + 1));
        holder.deviceName.setText(device.get("name"));
        holder.deviceIP.setText(device.get("ip"));

        convertView.setTag(holder);
        final DeviceAdapter reference = this;

        convertView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            PopupMenu popup = new PopupMenu(activity, view);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.menu_device_config, popup.getMenu());

            if (activity.getClass() == AppMenu.class)
                popup.getMenu().removeItem(R.id.menu_remove);

            if (activity.getClass() == ConfigMenu.class)
                popup.getMenu().removeItem(R.id.menu_view);

            final int device_number = Integer.parseInt(((TextView)view.findViewById(R.id.dev_id)).getText().toString()) - 1;
            popup.setOnMenuItemClickListener(new
                PopupMenu.OnMenuItemClickListener() {
                 @Override
                 public boolean onMenuItemClick(MenuItem item) {
                     Intent showVideo;
                     switch (item.getItemId()) {
                         case R.id.menu_view:
                             if (activity.getClass() != ConfigMenu.class) {
                                 showVideo = new Intent(activity, ShowVideo.class);
                                 showVideo.putExtra("device_number", device_number);
                                 activity.startActivity(showVideo);
                             }
                             return true;
                         case R.id.menu_edit:
                             if (device_number >= 0) {
                                 ConfigurationFragment.InputDialog id = ConfigurationFragment.InputDialog.newInstance(configMan.config.devices.get(device_number));
                                 Handler handler = new Handler() {
                                     public void handleMessage(Message message) {
                                         ((BaseAdapter) reference).notifyDataSetChanged();
                                     }
                                 };
                                 id.setHandler(handler);
                                 id.show(((FragmentActivity) activity).getSupportFragmentManager(),
                                         activity.getString(R.string.new_device) + Integer.toString(device_number + 1));
                             } else Log.i("Menu edit", "invalid index");
                             return true;
                         case R.id.menu_remove:
                             if (activity.getClass() != AppMenu.class) {
                                 configMan.config.devices.remove(device_number);
                                 ((BaseAdapter) reference).notifyDataSetChanged();
                             }
                             Log.i("Remove pressed", "remove device");
                             return true;
                     }
                     Log.e("popup menuitemclicklistener", "invalid menu item selected");
                     return false;
                    }
                });
            popup.show();
            }
        });
        return convertView;
    }
}
