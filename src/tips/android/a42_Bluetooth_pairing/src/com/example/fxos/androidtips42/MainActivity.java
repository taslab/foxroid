
package com.example.fxos.androidtips42;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends ListActivity implements OnItemClickListener {
    private static final int REQUEST_SET_ENABLE_BLUETOOTH = 0x10;

    private static final int REQUEST_BE_DISCOVERED_BLUETOOTH = 0x20;

    private static final int SEARCH_DURATION = 120;

    private BluetoothAdapter mBluetoothAdapter;

    private BluetoothServerThread mServerThread;

    private BluetoothClientThread mClientThread;

    private TextView txtMessageFromPair;

    private ArrayAdapter<String> mAdapter;

    private ArrayList<BluetoothDevice> deviceArray = new ArrayList<BluetoothDevice>();

    private BroadcastReceiver mReceiver = null;

    private BroadcastReceiver mUpdateMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Const.ACTION_UPDATE_MESSAGE)) {
                // set message from pair
                String message = intent.getStringExtra(Const.EXTRA_UPDATE_MESSAGE);
                txtMessageFromPair.setVisibility(View.VISIBLE);
                txtMessageFromPair.setText(message);

                // set visibility gone to other items
                findViewById(R.id.btn_be_discovered_device).setVisibility(View.GONE);
                findViewById(R.id.btn_discover_device).setVisibility(View.GONE);
                getListView().setVisibility(View.GONE);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // init Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        txtMessageFromPair = (TextView)findViewById(R.id.txt_messasge_from_pair);
        txtMessageFromPair.setVisibility(View.GONE);

        // check Bluetooth is supported
        if (mBluetoothAdapter == null) {
            // finish activity
            Toast.makeText(this, R.string.message_bluetooth_not_supported, Toast.LENGTH_LONG)
                    .show();
            finish();
        }
    }

    protected void onResume() {
        super.onResume();
        // confirm Bluetooth enable
        if (mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQUEST_SET_ENABLE_BLUETOOTH);
        }
        // regist receiver for pair message
        IntentFilter filter = new IntentFilter();
        filter.addAction(Const.ACTION_UPDATE_MESSAGE);
        registerReceiver(mUpdateMessageReceiver, filter);

    }

    protected void onPause() {
        super.onPause();
        unregisterReceiver(mUpdateMessageReceiver);
    }

    protected void onDestroy() {
        super.onDestroy();
        unRegistDiscoverReceiver();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        // set enable Bluetooh
            case REQUEST_SET_ENABLE_BLUETOOTH:
                if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, R.string.message_set_bluetooth_if_canceled,
                            Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
            // set device to be found
            case REQUEST_BE_DISCOVERED_BLUETOOTH:
                // show alert message
                if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, R.string.message_set_ok_if_canceled, Toast.LENGTH_LONG)
                            .show();
                }
                break;
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
        // be found (work as server)
            case R.id.btn_be_discovered_device:
                // be discovered a certain period of time
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, SEARCH_DURATION);
                startActivityForResult(intent, REQUEST_BE_DISCOVERED_BLUETOOTH);

                // click multiple measures
                if (mServerThread != null && mServerThread.isAlive()) {
                    mServerThread.interrupt();
                    mServerThread = null;
                }

                mServerThread = new BluetoothServerThread(this, mBluetoothAdapter);
                mServerThread.start();

                unRegistDiscoverReceiver();
                break;
            // discover device (work as client)
            case R.id.btn_discover_device:
                // set list view
                deviceArray.clear();
                mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
                getListView().setAdapter(mAdapter);
                getListView().setOnItemClickListener(this);

                // unregist bofore regist
                unRegistDiscoverReceiver();

                IntentFilter filter = new IntentFilter();
                filter.addAction(BluetoothDevice.ACTION_FOUND);

                // register receiver
                mReceiver = new DiscoverReceiver();
                registerReceiver(mReceiver, filter);

                // cancel discovery before start
                if (mBluetoothAdapter.isDiscovering()) {
                    mBluetoothAdapter.cancelDiscovery();
                }
                // start discovery
                mBluetoothAdapter.startDiscovery();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
        mClientThread = new BluetoothClientThread(this, deviceArray.get(position),
                mBluetoothAdapter);
        mClientThread.start();
    }

    private void unRegistDiscoverReceiver() {
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    class DiscoverReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // device found
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // add to list and array
                mAdapter.add(device.getName() + "\n" + device.getAddress());
                deviceArray.add(device);
            }
        }
    }
}
