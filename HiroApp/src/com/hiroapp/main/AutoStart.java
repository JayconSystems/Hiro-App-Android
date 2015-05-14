/**
 * Created by Jaycon Systems on 24/12/14.
 * Copyright @ 2014 Jaycon Systems. All rights reserved.
 */

package com.hiroapp.main;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.hiroapp.common.BluetoothDeviceActor;
import com.hiroapp.common.Utils;
import com.hiroapp.dbhelper.DBHelper;
import com.hiroapp.model.DeviceInfoModel;
import com.hiroapp.scanservice.ScanBGService;

public class AutoStart extends BroadcastReceiver {

	private DBHelper dbhelper;
	private HeroApp_App appStorage;
	LinkedHashMap<String, DeviceInfoModel> device_list;
	private Context mcontext;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();
//		Log.e(":::::::::::::::Inside Receiver for auto start ", ":::::::"
//				+ action);

		appStorage = (HeroApp_App) context.getApplicationContext();
		dbhelper = appStorage.getDbhelper();
		device_list = new LinkedHashMap<String, DeviceInfoModel>();
		mcontext = context;

		if (intent != null
				&& intent.getAction().equals(
						"android.intent.action.BOOT_COMPLETED")) {
//			Log.e("Auto start service started ", "");
			// SecuRemote s = new SecuRemote(context);

			Intent startServiceIntent = new Intent(context, ScanBGService.class);
			context.startService(startServiceIntent);
		} else if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {

			if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_OFF) {

			} else {
				ScanBGService.isScanRunning = false;
			}

		} else if (action.equals(WifiManager.RSSI_CHANGED_ACTION)) {
			WifiManager wifiManager;
			wifiManager = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			int numberOfLevels = 5;
			WifiInfo wifiInfo = wifiManager.getConnectionInfo();
			int level = WifiManager.calculateSignalLevel(wifiInfo.getRssi(),
					numberOfLevels);
			System.out.println("Bars =" + level);
			Intent i = new Intent(Utils.ACTION_WIFI_RSSI_CHANGE);
			i.putExtra("rssi", level);
			context.sendBroadcast(i);

		} else if (action.equals("android.net.wifi.WIFI_STATE_CHANGED")) {
			WifiManager wifiManager;
			wifiManager = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			ConnectivityManager cm = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo Info = cm.getActiveNetworkInfo();
			if (Info == null || !Info.isConnectedOrConnecting()) {
				Intent i = new Intent(Utils.ACTION_WIFI_CHANGE);
				i.putExtra("ssid", "");
				i.putExtra("macaddress", "");
				context.sendBroadcast(i);

				if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLED) {
					WifiInfo info = wifiManager.getConnectionInfo();
					String mac = info.getMacAddress();
					boolean isAvailable = dbhelper.macAvailable(mac);
					if (isAvailable) {
						checkBDA();
					}
				}

			} else {
				int netType = Info.getType();
				if (netType == ConnectivityManager.TYPE_WIFI) {

					WifiInfo info = wifiManager.getConnectionInfo();

					Intent i = new Intent(Utils.ACTION_WIFI_CHANGE);
					i.putExtra("ssid", info.getSSID());
					i.putExtra("macaddress", info.getMacAddress());
					context.sendBroadcast(i);
				} else {

					
					if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLED) {
						WifiInfo info = wifiManager.getConnectionInfo();
						String mac = info.getMacAddress();
						boolean isAvailable = dbhelper.macAvailable(mac);
						if (isAvailable) {
							checkBDA();
						}
					}
				}
			}
		}
	}

	private void checkBDA() {

		// TODO Auto-generated method stub
		if (dbhelper.checkIfDeviceCount()) {
			// device Found then verify from service that this device is
			// connected or not
			device_list = dbhelper.getDeviceListFromDB();

			Set<String> e = device_list.keySet();

			Iterator e1 = e.iterator();

			while (e1.hasNext()) {
				String mac_add = (String) e1.next();
				if (ScanBGService.hash_service_connected_device_list != null
						& ScanBGService.hash_service_connected_device_list
								.size() > 0) {

					if (ScanBGService.hash_service_connected_device_list
							.containsKey(mac_add)) {
						// verify the bda is conneted or not
						DeviceInfoModel model = device_list.get(mac_add);
						BluetoothDeviceActor bda = ScanBGService.hash_service_connected_device_list
								.get(mac_add);
						if (!bda.isConnected()) {
							playDisconnectSoundAndNotification(mac_add, model);
						}

					} else {
						DeviceInfoModel model = device_list.get(mac_add);
						BluetoothDeviceActor bda = new BluetoothDeviceActor();
						if (!bda.isConnected()) {
							playDisconnectSoundAndNotification(mac_add, model);
						}
					}

				} else {

					DeviceInfoModel model = device_list.get(mac_add);
					BluetoothDeviceActor bda = new BluetoothDeviceActor();
					if (!bda.isConnected()) {
						playDisconnectSoundAndNotification(mac_add, model);
					}
				}
			}

		} else {
			Log.e("MainActivity", "No device found from DB");
		}
	}

	private void playDisconnectSoundAndNotification(String mac_add,
			DeviceInfoModel model) {

		boolean isDisSound = getPreference(mac_add,
				"NotificationDisconnectAlert");

		if (getPreference(mac_add, "NotificationIndication")) {

			int id = dbhelper.getnotificationId(mac_add);

			Utils.generateNotification(mcontext, mcontext.getResources()
					.getString(R.string.app_name), model.getDeviceLogicalName()
					+ " getting away!", id, isDisSound);
		}

		if (isDisSound) {
			String path = dbhelper.getRing(mac_add, "DisconnectRing");
			// String path = getRingtone("DisconnectRing");
			try {
				if (path != null && !path.equalsIgnoreCase("")) {
					Uri uri = Uri.parse(path);
					Ringtone r = RingtoneManager.getRingtone(
							mcontext.getApplicationContext(), uri);
					r.play();
				} else {
					MediaPlayer mp = MediaPlayer.create(
							mcontext.getApplicationContext(), R.raw.disconnect);

					mp.setOnCompletionListener(new OnCompletionListener() {

						@Override
						public void onCompletion(MediaPlayer mp) {
							mp.release();
						}

					});
					mp.start();
				}

			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public boolean getPreference(String deviceMacAddress, String key) {

		boolean istrue = dbhelper.getColumnValue(deviceMacAddress, key);

		return istrue;
	}

}
