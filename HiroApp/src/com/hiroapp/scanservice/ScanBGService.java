/**
 * Created by Jaycon Systems on 29/12/14.
 * Copyright @ 2014 Jaycon Systems. All rights reserved.
 */
package com.hiroapp.scanservice;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.hiroapp.common.BluetoothDeviceActor;
import com.hiroapp.common.ScanDev2;
import com.hiroapp.common.ScanDevices;
import com.hiroapp.dbhelper.DBHelper;
import com.hiroapp.main.HeroApp_App;
import com.hiroapp.model.DeviceInfoModel;

public class ScanBGService extends Service {

	public static Context context;
	private Timer timer;
	private ScheduleTask scheduleTask;
	public static Intent intentService = null;
	public static LinkedHashMap<String, BluetoothDeviceActor> hash_service_connected_device_list = new LinkedHashMap<String, BluetoothDeviceActor>();
	public static DBHelper dbhelper;
	private static HeroApp_App appStorage;
	public static LinkedHashMap<String, DeviceInfoModel> device_list;
	public static LinkedHashMap<String, DeviceInfoModel> notConnected_device_list;
	public static boolean isScanRunning = false;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		// Log.e("onStartCommand", "called");
		context = this;
		appStorage = (HeroApp_App) context.getApplicationContext();
		dbhelper = appStorage.getDbhelper();
		timer = new Timer();
		scheduleTask = new ScheduleTask();
		timer.schedule(scheduleTask, 2000, 25000);

		return START_STICKY;
	}

	class ScheduleTask extends TimerTask {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			checkBDAConnected();
		}

	}

	private void checkBDAConnected() {
		// TODO Auto-generated method stub
		if (dbhelper.checkIfDeviceCount()) {
			// device Found then verify from service that this device is
			// connected or not

			notConnected_device_list = new LinkedHashMap<String, DeviceInfoModel>();

			device_list = dbhelper.getDeviceListFromDB();

			// Enumeration<String> e = device_list.keys();

			Set<String> e = device_list.keySet();

			Iterator e1 = e.iterator();

			if (hash_service_connected_device_list == null)
				hash_service_connected_device_list = new LinkedHashMap<String, BluetoothDeviceActor>();

			while (e1.hasNext()) {
				String mac_add = (String) e1.next();
				if (hash_service_connected_device_list != null
						&& hash_service_connected_device_list.size() > 0) {

					if (hash_service_connected_device_list.containsKey(mac_add)) {
						// verify the bda is conneted or not
						BluetoothDeviceActor bda = hash_service_connected_device_list
								.get(mac_add);
						if (bda != null && bda.isConnected()) {
							DeviceInfoModel model = device_list.get(mac_add);
							bda.setPhotoUrl(model.getDevicePhotoUrl());
							bda.setDevmodel(model);
							// Log.e("ScanBGService", "checkBDA :" + mac_add
							// + " Already Connected");
							// device_list.remove(mac_add); // if device
							// connected
							// then remove from
							// the device list
						} else {
							// Log.e("ScanBGService",
							// "checkBDA :"
							// + mac_add
							// +
							// " not Connected. connect it from background service");
							// device_list.remove(mac_add);
							// hash_service_connected_device_list.remove(mac_add);
							DeviceInfoModel model = device_list.get(mac_add);
							notConnected_device_list.put(mac_add, model);
						}
					}

				} else {

					if (hash_service_connected_device_list != null
							&& hash_service_connected_device_list.size() == 0) {
						DeviceInfoModel model = device_list.get(mac_add);
						notConnected_device_list.put(mac_add, model);
					}

					// Log.e("checkBDAConnected",
					// "Device is pending for connection");
				}
			}
			if (notConnected_device_list != null
					&& notConnected_device_list.size() > 0) {
				if (!isScanRunning) {
					isScanRunning = true;
					ScanDev2 scanDevices = new ScanDev2(context,
							notConnected_device_list);
					Log.e("ScanBGService","start scan line -138-");
				} else {
					 //Log.e("ScanBGService",
					// "scanning is already running for pending connection");
				}
				// device_list.clear();
			}

		} else {
			 Log.e("ScanBGService", "No device found in DB");
		}
	}

}
