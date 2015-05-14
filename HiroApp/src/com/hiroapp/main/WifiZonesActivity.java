/**
 * Created by Jaycon Systems on 17/01/15.
 * Copyright @ 2014 Jaycon Systems. All rights reserved.
 */
package com.hiroapp.main;

import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.hiroapp.adapter.WifissidAdapter;
import com.hiroapp.common.Utils;
import com.hiroapp.dbhelper.DBHelper;
import com.hiroapp.font.OpenSansLightItalic;

public class WifiZonesActivity extends Activity implements OnClickListener {

	private ImageView img_back, imgwifiinfo;
	Context context;
	private OpenSansLightItalic txtcurrentssid;
	private ImageView imgaddwifi, imgwifistrength;
	private String ssid = "";
	private String macAddress;
	private DBHelper dbhelper;
	private HeroApp_App appStorage;
	private ArrayList<String> ssidlist;
	private ListView listview;

	WifissidAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.wifisafezones);
		context = WifiZonesActivity.this;
		initscreen();
		setListener();
		setSSID();
		registerReceiver(mWifiUpdateReceiver, Utils.makeIntentFilter());
	}

	private void setSSID() {
		// TODO Auto-generated method stub
		String currssid = getWifiSSIDName();
		if (currssid == null || currssid.equalsIgnoreCase(""))
			txtcurrentssid.setText("Wi-Fi N/A");
		else
			txtcurrentssid.setText(currssid);
	}

	private void setListener() {
		// TODO Auto-generated method stub
		img_back.setOnClickListener(this);
		imgwifiinfo.setOnClickListener(this);
		imgaddwifi.setOnClickListener(this);
	}

	private void initscreen() {
		// TODO Auto-generated method stub
		img_back = (ImageView) findViewById(R.id.imgback);
		imgwifiinfo = (ImageView) findViewById(R.id.wifisafezones_imgwifiinfo);
		txtcurrentssid = (OpenSansLightItalic) findViewById(R.id.wifisafezones_txt_currentwifi);
		imgaddwifi = (ImageView) findViewById(R.id.wifisafezones_img_addwifi);
		imgwifistrength = (ImageView) findViewById(R.id.wifisafezones_img_wifistregnth);
		appStorage = (HeroApp_App) context.getApplicationContext();
		dbhelper = appStorage.getDbhelper();
		listview = (ListView) findViewById(R.id.wifisafezones_lst_listwifi);
		ssidlist = new ArrayList<String>();

		ssidlist = dbhelper.getWifiSSID();

		adapter = new WifissidAdapter(WifiZonesActivity.this, ssidlist,
				dbhelper);
		listview.setAdapter(adapter);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.imgback:
			finish();
			break;
		case R.id.wifisafezones_imgwifiinfo:
			Intent wifiinfo = new Intent(WifiZonesActivity.this,
					WifiInfoActivity.class);
			startActivity(wifiinfo);
			break;
		case R.id.wifisafezones_img_addwifi:

			if (ssid != null && !ssid.equalsIgnoreCase("")) {

				boolean isAvailabel = dbhelper.ssidAvailable(ssid);

				if (!isAvailabel) {

					dbhelper.addWifiSSID(ssid, macAddress);
					ssidlist.add(ssid);
					adapter.notifyDataSetChanged();

				} else {
					Toast t = Toast
							.makeText(WifiZonesActivity.this,
									"Wi-fi safe zone Already added",
									Toast.LENGTH_LONG);
					t.show();
				}
			}

		default:
			break;
		}
	}

	private String getWifiSSIDName() {
		WifiManager wifiManager;
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo Info = cm.getActiveNetworkInfo();
		if (Info == null || !Info.isConnectedOrConnecting()) {
		} else {
			int netType = Info.getType();
			int netSubtype = Info.getSubtype();

			if (netType == ConnectivityManager.TYPE_WIFI) {
				wifiManager = (WifiManager) context
						.getSystemService(Context.WIFI_SERVICE);
				WifiInfo info = wifiManager.getConnectionInfo();
				ssid = info.getSSID();
				macAddress = info.getMacAddress();
			} else if (netType == ConnectivityManager.TYPE_MOBILE) {
				Log.i("WifiZones", "GPRS/3G connection");
			}
		}
		return ssid;
	}

	private BroadcastReceiver mWifiUpdateReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();

			if (action.equalsIgnoreCase(Utils.ACTION_WIFI_CHANGE)) {

				if (intent != null && intent.hasExtra("ssid")) {
					ssid = intent.getStringExtra("ssid");
					macAddress = intent.getStringExtra("macaddress");
					if (ssid.equalsIgnoreCase("")) {
						txtcurrentssid.setText("Wi-Fi N/A");
						imgwifistrength.setImageResource(R.drawable.signal_i);
					} else
						txtcurrentssid.setText(ssid);
				}
			} else if (action.equalsIgnoreCase(Utils.ACTION_WIFI_RSSI_CHANGE)) {
				if (intent != null && intent.hasExtra("rssi")) {
					int rssi = intent.getIntExtra("rssi", 5);
					if (rssi > 3) {
						imgwifistrength.setImageResource(R.drawable.signal_n3);
					} else if (rssi == 3) {
						imgwifistrength.setImageResource(R.drawable.signal_n2);
					} else if (rssi == 2) {
						imgwifistrength.setImageResource(R.drawable.signal_n1);
					} else if (rssi <= 1) {
						imgwifistrength.setImageResource(R.drawable.signal_n0);
					}
				}
			}
		}

	};

	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mWifiUpdateReceiver);
	};

}
