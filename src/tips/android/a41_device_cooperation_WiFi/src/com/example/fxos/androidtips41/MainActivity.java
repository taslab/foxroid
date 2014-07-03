
package com.example.fxos.androidtips41;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ChannelListener;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class MainActivity extends Activity implements ChannelListener, ConnectionInfoListener {
    private static final String ENCODE = "UTF-8";

    private static final int SOCKET_TIMEOUT = 5000;

    private WifiP2pManager mManager;

    private boolean isWifiP2pEnabled = false;

    private Context mContext;

    Channel mChannel;

    BroadcastReceiver mReceiver;

    private boolean isDiscovering = false;

    private TextView mTextMyDeviceName;

    private Socket mClientSocket;

    private ServerSocket mServerSocket;

    IntentFilter mIntentFilter;

    /**
     * @param isWifiP2pEnabled the isWifiP2pEnabled to set
     */
    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
        this.isWifiP2pEnabled = isWifiP2pEnabled;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        mManager = (WifiP2pManager)getSystemService(WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        mTextMyDeviceName = (TextView)findViewById(R.id.textMyDeviceName);

        // clear previous setting
        mManager.removeGroup(mChannel, new ActionListener() {
            @Override
            public void onFailure(int reasonCode) {
            }

            @Override
            public void onSuccess() {
            }
        });

    }

    /* register the broadcast receiver with the intent values to be matched */
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }

    /* unregister the broadcast receiver */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    public void setMyDeviceInfo(WifiP2pDevice device) {
        mTextMyDeviceName.setText(device.deviceName);
        mTextMyDeviceName.setTag(device);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonDiscoverPeer:
                if (!isWifiP2pEnabled) {
                    // show error dialog
                    AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                    dialog.setTitle(R.string.title_discovery_setting_error)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setMessage(R.string.message_discovery_setting_error)
                            .setPositiveButton(R.string.button_ok,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // move wireless setting
                                            startActivity(new Intent(
                                                    Settings.ACTION_WIRELESS_SETTINGS));
                                        }
                                    }).show();

                    return;
                }
                // show progress
                ProgressUtil.show(mContext, null, getString(R.string.message_discovering), true,
                        true);
                setDiscovering(true);

                mManager.discoverPeers(mChannel, new ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(mContext, R.string.message_discovery_initiated,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int reasonCode) {
                        Toast.makeText(mContext,
                                getString(R.string.message_discovery_failed) + reasonCode,
                                Toast.LENGTH_LONG).show();
                    }
                });
                break;
            case R.id.buttonDisconnect:
                mManager.removeGroup(mChannel, new ActionListener() {

                    @Override
                    public void onFailure(int reasonCode) {
                        Toast.makeText(mContext,
                                getString(R.string.message_disconnect_failed) + reasonCode,
                                Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onSuccess() {
                        setButtonsState(false);
                    }

                });
                break;
        }
    }

    public boolean isDiscovering() {
        return isDiscovering;
    }

    public void setDiscovering(boolean flag) {
        isDiscovering = flag;
    }

    public void setButtonsState(boolean isConnected) {
        if (isConnected) {
            findViewById(R.id.buttonDiscoverPeer).setEnabled(false);
            findViewById(R.id.buttonDisconnect).setEnabled(true);
        } else {
            findViewById(R.id.buttonDiscoverPeer).setEnabled(true);
            findViewById(R.id.buttonDisconnect).setEnabled(false);
        }
    }

    public void connect(WifiP2pConfig config) {
        mManager.connect(mChannel, config, new ActionListener() {

            @Override
            public void onSuccess() {
                // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(mContext, R.string.message_connect_failed, Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    @Override
    public void onChannelDisconnected() {
    }

    @Override
    public void onConnectionInfoAvailable(final WifiP2pInfo info) {
        // hide progress
        ProgressUtil.hideProgress();

        // act as server
        if (info.groupFormed && info.isGroupOwner) {
            ServerTask serverTask = new ServerTask();
            serverTask.execute();
            // act as client
        } else if (info.groupFormed) {
            ClientTask clientTask = new ClientTask();
            clientTask.execute(info.groupOwnerAddress.getHostAddress());
        }
    }

    public void resetData() {
        setButtonsState(false);
        DeviceListFragment fragmentList = (DeviceListFragment)getFragmentManager()
                .findFragmentById(R.id.fragmentList);
        if (fragmentList != null) {
            fragmentList.clearPeers();
        }
    }

    class ClientTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String host = params[0];
            mClientSocket = new Socket();
            int port = 8988;

            try {
                // send message to server
                mClientSocket.bind(null);
                mClientSocket.connect((new InetSocketAddress(host, port)), SOCKET_TIMEOUT);

                OutputStreamWriter osw = new OutputStreamWriter(mClientSocket.getOutputStream());
                osw.write(getString(R.string.message_send_message, mTextMyDeviceName.getText()));
                osw.flush();
                osw.close();
                mClientSocket.close();

                // get server message
                mClientSocket = new Socket();
                mClientSocket.bind(null);
                mClientSocket.connect((new InetSocketAddress(host, port)), SOCKET_TIMEOUT);
                // Thread.sleep(3000);
                //
                InputStream is = mClientSocket.getInputStream();
                int size = 0;
                byte[] buffer = new byte[2000];
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                while ((size = is.read(buffer)) != -1) {
                    bos.write(buffer, 0, size);
                }
                is.close();
                String serverMessage = bos.toString(ENCODE);

                return serverMessage;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (mClientSocket != null) {
                    if (mClientSocket.isConnected()) {
                        try {
                            mClientSocket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String message) {
            if (message == null) {
                Toast.makeText(MainActivity.this, R.string.message_aquistion_of_message_failed,
                        Toast.LENGTH_LONG).show();
            } else {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setMessage(message)
                        .setPositiveButton(R.string.button_ok,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                }).show();
            }
        }

    }

    class ServerTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            try {
                // get client message
                mServerSocket = new ServerSocket(8988);
                Socket client;
                client = mServerSocket.accept();
                InputStream is = client.getInputStream();
                int size = 0;
                byte[] buffer = new byte[2000];
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                while ((size = is.read(buffer)) != -1) {
                    bos.write(buffer, 0, size);
                }
                is.close();
                client.close();

                String clientMessage = bos.toString(ENCODE);

                // send message to client
                client = mServerSocket.accept();
                OutputStreamWriter osw = new OutputStreamWriter(client.getOutputStream());
                osw.write(getString(R.string.message_send_message, mTextMyDeviceName.getText()));
                osw.close();

                client.close();

                mServerSocket.close();

                return clientMessage;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String message) {
            if (message == null) {
                Toast.makeText(MainActivity.this, R.string.message_aquistion_of_message_failed,
                        Toast.LENGTH_LONG).show();
            } else {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setMessage(message)
                        .setPositiveButton(R.string.button_ok,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                }).show();
            }
        }

    }
}
