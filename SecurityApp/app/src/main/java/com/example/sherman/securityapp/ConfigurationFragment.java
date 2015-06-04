package com.example.sherman.securityapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class ConfigurationFragment extends Fragment {
    public static ConfigurationFragment fragment;
    public ConfigManager configMan;
    private ListAdapter mAdapter;
    private boolean[] itemsSelected;
    private OnFragmentInteractionListener mListener;

    public final Handler handler = new Handler() {
      public void handleMessage(Message message) {
          ((BaseAdapter)mAdapter).notifyDataSetChanged();
      }
    };

    public static ConfigurationFragment newInstance(Context validContext) {
        if (fragment != null)
            return fragment;
        fragment = new ConfigurationFragment();
        fragment.configMan = ConfigManager.newInstance(validContext);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        configMan = ConfigManager.newInstance(this.getActivity());
        setAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final FragmentActivity activity = getActivity();
        final View view =  inflater.inflate(R.layout.fragment_config_menu, container, false);
        Button add = (Button) view.findViewById(R.id.add_button);
            add.setOnClickListener(new Button.OnClickListener(){
                public void onClick(View v) {
                    if (!configMan.hasMinConfig()) {
                        ((TextView) view.findViewById(R.id.config_error)).setText(getString(R.string.error_no_name));
                        return;
                    }
                    ((TextView) view.findViewById(R.id.config_error)).setText("");
                    InputDialog id = new InputDialog();
                    id.setHandler(handler);String log = getString(R.string.new_device) + Integer.toString(configMan.getSize() + 1);
                    id.show(getActivity().getSupportFragmentManager(), log);
                }
            });
        EditText fName = (EditText) view.findViewById(R.id.user_fName);
        EditText lName = (EditText) view.findViewById(R.id.user_lName);

        if (configMan.hasMinConfig()) {
            fName.setText(configMan.config.firstName);
            lName.setText(configMan.config.lastName);
        }

        fName.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId == EditorInfo.IME_NULL) || (actionId == EditorInfo.IME_ACTION_NEXT))
                    if (v.getText().toString().trim().length() > 0)
                        configMan.firstNameSet(v.getText().toString().trim());
                return false;
            }
        });
        fName.setOnFocusChangeListener(new EditText.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus)
                   if  (getText((EditText) v) != null)
                       configMan.firstNameSet(getText((EditText) v).trim());
                   else
                       ((TextView) view.findViewById(R.id.config_error)).setText(getString(R.string.error_fName));
            }
        });
        lName.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId == EditorInfo.IME_NULL) || (actionId == EditorInfo.IME_ACTION_DONE))
                    if (v.getText().toString().trim().length() > 0)
                        configMan.lastNameSet(v.getText().toString().trim());
                return false;
            }
        });
        lName.setOnFocusChangeListener(new EditText.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus)
                    if  (getText((EditText) v) != null)
                        configMan.lastNameSet(getText((EditText) v).trim());
                    else
                        ((TextView) view.findViewById(R.id.config_error)).setText(getString(R.string.error_lName));
            }
        });
        ListView lv = (ListView) view.findViewById(R.id.devices_view);
        lv.setAdapter(mAdapter);
        return view;    }

    public void setAdapter() {
        Activity context = (getActivity() == null) ? new Activity() : getActivity();
        mAdapter = new DeviceAdapter(context, configMan.config.devices);
    }

    public String getText (EditText editText) {
        return editText.getText().toString();
    }

    public void onActivityCreated(Bundle bundle) {
        //init UI per configuration state
        super.onActivityCreated(bundle);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public static class InputDialog extends DialogFragment {
        static Handler mHandler;
        Device device;
        public View reference;
        public static InputDialog newInstance(Device device) {
            Bundle b = new Bundle();
            if (device != null)
                b.putSerializable("device", device);
            //mHandler = handler;
            InputDialog id = new InputDialog();
            id.setArguments(b);
            return id;
        }

        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Activity aContext = getActivity();
            final View v = aContext.getLayoutInflater().inflate(R.layout.new_device, null);
            final ConfigManager inputConfigMan = ConfigManager.newInstance(aContext);
            EditText ipInput = (EditText) v.findViewById(R.id.new_ip);
                ipInput.setOnEditorActionListener(new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if ((actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_NULL))
                            if (inputConfigMan.validateIP(v.getText().toString()))
                                inputConfigMan.testConfig(v.getText().toString(), aContext);
                            else
                                ((TextView) v.findViewById(R.id.error)).setText(getString(R.string.error_conn));
                        return false;
                    }
                });
            final Spinner types = (Spinner) v.findViewById(R.id.new_type);
                types.setAdapter(ArrayAdapter.createFromResource(aContext, R.array.types, android.R.layout.simple_spinner_item));
            final AlertDialog dialog = new AlertDialog.Builder(aContext)
                    .setView(v)
                    .setTitle(R.string.new_device + inputConfigMan.config.devices.size() + 1)
                    .setPositiveButton(R.string.add_button, null)
                    .setNegativeButton(R.string.cancel, null)
                    .create();
            dialog.show();  //y?
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                    ConfigManager configManager = ConfigManager.newInstance(aContext);
                    TextView error = (TextView) v.findViewById(R.id.error);
                    error.setText("");
                    String name = ((EditText) v.findViewById(R.id.new_name)).getText().toString().trim();
                    String ip = ((EditText) v.findViewById(R.id.new_ip)).getText().toString().trim();
                    if (name.equals("") || ip.equals("") || types.getSelectedItemPosition() == 0) {
                        error.setText(getString(R.string.error));
                    } else if (configManager.validateIP(ip)) {
                        if (configManager.hasConnection(ip, aContext)) {
                            if (device == null) {
                                configManager.addDevice(name, ip, types.getSelectedItemPosition());
                                configManager.saveConfiguration();
                                mHandler.sendMessage(mHandler.obtainMessage());
                            } else {
                                device.setName(name);
                                device.setIP(ip);
                                device.setType(types.getSelectedItemPosition()-1);
                                configManager.saveConfiguration();
                                mHandler.sendMessage(mHandler.obtainMessage());
                            }
                            dialog.dismiss();
                        } else {
                            error.setText(getString(R.string.error_conn));
                        }
                    } else {
                        error.setText(getString(R.string.error_ip));
                    }
                }
            });
            reference = v;
            return dialog;
        }

        public void setHandler(Handler handler) {
            mHandler = handler;
        }
        public void onStart() {
            super.onStart();
            Bundle b = this.getArguments();
            if ((b != null) && (!b.isEmpty())) {
                device = (Device) b.getSerializable("device");
                ((EditText) reference.findViewById(R.id.new_name)).setText(device.name);
                ((EditText) reference.findViewById(R.id.new_ip)).setText(device.ip);
                ((Spinner) reference.findViewById(R.id.new_type)).setSelection(device.getType()+1);
            }
        }
    }
    /* More Info: See the Android Training lesson @"http://developer.android.com/training/basics/fragments/communicating.html" */
    public interface OnFragmentInteractionListener {  public void onFragmentInteraction(Uri uri);  }
}
