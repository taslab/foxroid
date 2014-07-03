
package com.example.fxos.androidtips41;

import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class DeviceListFragment extends ListFragment implements PeerListListener {
    private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();

    View mContentView = null;

    private WiFiPeerListAdapter mWiFiPeerListAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mWiFiPeerListAdapter = new WiFiPeerListAdapter(getActivity(),
                android.R.layout.simple_list_item_1, peers);
        this.setListAdapter(mWiFiPeerListAdapter);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.device_list, null);
        return mContentView;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        final WifiP2pDevice device = (WifiP2pDevice)getListAdapter().getItem(position);

        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle(R.string.title_connect_confirm)
                .setMessage(R.string.message_connect_confirm)
                .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // connect device
                        WifiP2pConfig config = new WifiP2pConfig();
                        config.deviceAddress = device.deviceAddress;
                        config.wps.setup = WpsInfo.PBC;

                        ProgressUtil.show(getActivity(), null,
                                getString(R.string.message_connecting_to, device.deviceAddress),
                                true, true);
                        ((MainActivity)getActivity()).connect(config);

                    }
                }).setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList) {
        peers.clear();
        peers.addAll(peerList.getDeviceList());
        mWiFiPeerListAdapter.notifyDataSetChanged();
        if (peers.size() == 0) {
            return;
        }
    }

    private class WiFiPeerListAdapter extends ArrayAdapter<WifiP2pDevice> {

        private int textViewResourceId;

        private List<WifiP2pDevice> items;

        /**
         * @param context
         * @param textViewResourceId
         * @param objects
         */
        public WiFiPeerListAdapter(Context context, int textViewResourceId,
                List<WifiP2pDevice> objects) {
            super(context, textViewResourceId, objects);
            this.textViewResourceId = textViewResourceId;
            items = objects;

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater)getActivity().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(textViewResourceId, null);
            }
            WifiP2pDevice device = items.get(position);
            if (device != null) {
                TextView top = (TextView)v.findViewById(android.R.id.text1);
                if (top != null) {
                    top.setText(device.deviceName);
                }
            }

            return v;

        }
    }

    public void clearPeers() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                peers.clear();
                mWiFiPeerListAdapter.notifyDataSetChanged();
            }
        }, 100);
    }

}
