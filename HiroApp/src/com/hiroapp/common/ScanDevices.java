/**
 * Created by Jaycon Systems on 30/12/14.
 * Copyright @ 2014 Jaycon Systems. All rights reserved.
 */

package com.hiroapp.common;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.hiroapp.model.DeviceInfoModel;
import com.hiroapp.scanservice.ScanBGService;
import com.hiroapp.common.ScanDev2;

@SuppressLint("NewApi")
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class ScanDevices {

	private BluetoothAdapter mBluetoothAdapter;
	private ArrayList<String> scanDeviceNameList;
	private Context context;
	public ScanInterface scaninterface;
	public boolean isDeviceConnected = false;
	private Timer timer;
	//private ScheduleTask scheduleTask;
	int transmissionpower = 0;
	String devicename = null;
	String comp_spe = "";
	String uuid;
	protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
	String mac_address = "";
	
	
	//-------
	private final static int SCAN_INTERVAL=250;
	
	private boolean isScanning = false;
	
	int aux=0;
	//-----
	
	

	@SuppressLint("NewApi")
	public ScanDevices(Context mcontext) {
		final BluetoothManager bluetoothManager = (BluetoothManager) mcontext
				.getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
		context = mcontext;
		scaninterface = (ScanInterface) mcontext;
		Log.w("----ScanDevices-----","line ------------- 65");
	}

	@SuppressLint("NewApi")
	/*public ScanDevices(Context mcontext,
			LinkedHashMap<String, DeviceInfoModel> devlist) {
		final BluetoothManager bluetoothManager = (BluetoothManager) mcontext
				.getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
		context = mcontext;
		//startScanning();
		Log.e("scanDevices","begginig scan");
		//beginScanning();
		Log.w("ScanDevice","78");
		ScanDev2 s= new ScanDev2();
		Thread t = new Thread(s);
		t.start();
		
	}*/

	public void startScanning() {
		scanDeviceNameList = new ArrayList<String>();
		mBluetoothAdapter.startLeScan(mLeScanCallback);
	}

	public void stopScanning() {
		try {
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
		} catch (Exception e) {
			// Log.e("StopScanning", e.getMessage());
		}
	}
	
/*	public void beginScanning() { 
		Handler scanHandler = new Handler();
		scanHandler.postDelayed(new Runnable(){
		public void run() {
			aux=1;
			Looper.prepare();
            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            Log.e("aaa","bbbbbbbbbbbbbbb");

            if (isScanning) {
                adapter.stopLeScan(mLeScanCallback);
            } else if (!adapter.startLeScan(mLeScanCallback)) {
                // an error occurred during startLeScan
            }

            isScanning = !isScanning;
		
		
							}
												}, SCAN_INTERVAL);
    							}
    							
   */

   /* private Runnable scanRunnable = new Runnable() {
        @Override
        public void run() {
            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            Looper.prepare();
            Log.e("aaa","bbbbbbbbbbbbbbb");

            if (isScanning) {
                adapter.stopLeScan(mLeScanCallback);
            } else if (!adapter.startLeScan(mLeScanCallback)) {
                // an error occurred during startLeScan
            }

            isScanning = !isScanning;

            scanHandler.postDelayed(this, SCAN_INTERVAL);
            
        }
    };*/

	/*class ScheduleTask extends TimerTask {
		@Override
		public void run() {

			broadcastUpdate(Utils.ACTION_DEVICE_NOT_FOUND);
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
		}
	}

	private void broadcastUpdate(final String action) {

		final Intent intent = new Intent(action);
		context.sendBroadcast(intent);
	}*/

	// Device scan callback.
	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

		@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
		@Override
		public void onLeScan(final BluetoothDevice device, int rssi,
				final byte[] scanRecord) {
			if (aux==1){
				Log.e("Aux"," -165-Aux = 1");
			}
			Log.e("Aux","-167-Aux = 1");
			String deviceName = device.getAddress();

			if (scanDeviceNameList != null
					&& scanDeviceNameList.contains(device.getAddress())) {
				// ScanBGService.device_list is the database content.
				if (ScanBGService.device_list != null
						&& ScanBGService.device_list.size() > 0
						&& ScanBGService.device_list.containsKey(deviceName)) {
					if (ScanBGService.hash_service_connected_device_list != null
							&& ScanBGService.hash_service_connected_device_list
									.size() > 0
							&& ScanBGService.hash_service_connected_device_list
									.containsKey(deviceName)) {
						BluetoothDeviceActor bda = ScanBGService.hash_service_connected_device_list
								.get(deviceName);
						if (!bda.isConnected()) {
							scanDeviceNameList.add(device.getAddress());
							DeviceInfoModel devmodel = ScanBGService.device_list
									.get(deviceName);
							ScanBGService.device_list.remove(deviceName);
							if (ScanBGService.device_list.size() == 0)
								ScanBGService.isScanRunning = false;
							makeandconnectBDA(device, devmodel);
						}
					} else {
						scanDeviceNameList.remove(device.getAddress());
					}
				}

			} else {
				StringBuffer sb = new StringBuffer();
				for (byte b : scanRecord) {
					sb.append(String.format("%02X", b));
				}
				// decodescan(sb.toString());
				if (ScanBGService.device_list != null
						&& ScanBGService.device_list.size() > 0
						&& ScanBGService.device_list.containsKey(deviceName)) {
					DeviceInfoModel devmodel = ScanBGService.device_list
							.get(deviceName);
					ScanBGService.device_list.remove(deviceName);
					if (ScanBGService.device_list.size() == 0)
						ScanBGService.isScanRunning = false;
					makeandconnectBDA(device, devmodel);
				} else if (device != null && device.getName() != null
						&& !device.getName().toString().equalsIgnoreCase("")) {
					//if to add all different hiros version 'hiro14' 'hiro15' 'hiroxx'
					if ((device.getName().toString()).substring(0, 4).equalsIgnoreCase("hiro")) {
						scanDeviceNameList.add(device.getAddress());
						if (scaninterface != null) {
							scaninterface.addDevice(device, uuid,
									String.valueOf(transmissionpower));
						}

					}
				}

			}
		}

	};

	public void decodescan(String data) {
		// byte[] s = data.getBytes();
		int datalength = 0;
		int length;
		int type;
		int i = 0;
		int j = 2;
		String deviceType;
		while (datalength != data.length()) {
			length = Integer.parseInt(data.substring(i, i + j), 16);
			type = Integer.parseInt(data.substring((i + j), i + j + 2), 16);
			switch (type) {
			case 1:
				break;
			case 2:
				uuid = data.substring((i + j + 2), i + j + (length * 2));
				break;
			case 3:
				uuid = data.substring((i + j + 2), i + j + (length * 2));
				break;
			case 4:
				uuid = data.substring((i + j + 2), i + j + (length * 2));
				break;
			case 5:
				uuid = data.substring((i + j + 2), i + j + (length * 2));
				break;
			case 6:
				uuid = data.substring((i + j + 2), i + j + (length * 2));
				break;
			case 7:
				uuid = data.substring((i + j + 2), i + j + (length * 2));
				break;
			case 9:
				devicename = hextoString(data.substring((i + j + 2), i + j
						+ (length * 2)));
				// Log.e("deviceName==", devicename);
				break;
			case 10: // for the transmission power
				transmissionpower = Integer.parseInt(
						data.substring((i + j + 2), i + j + (length * 2)), 16);
				Log.e("transmissionpower ", " " + transmissionpower);
				break;
			case 255:
				comp_spe = data.substring((i + j + 2), i + j + (length * 2));
				break;
			}
			// i = i + j + 2 + length;
			Log.e("manufacuterID uuid-->", " " + uuid);
			i = i + j + (length * 2);
			datalength = i;
			if (length == 0)
				break;
		}
	}

	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		int v;
		for (int j = 0; j < bytes.length; j++) {
			v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	public String hextoString(String hex) {

		StringBuilder output = new StringBuilder();
		for (int j = 0; j < hex.length(); j += 2) {
			String str = hex.substring(j, j + 2);
			output.append((char) Integer.parseInt(str, 16));
		}
		return output.toString();
	}

	public void makeandconnectBDA(BluetoothDevice device,
			DeviceInfoModel devmodel) {

		if (ScanBGService.hash_service_connected_device_list != null
				&& ScanBGService.hash_service_connected_device_list.size() > 0
				&& ScanBGService.hash_service_connected_device_list
						.containsKey(device.getAddress())) {

			BluetoothDeviceActor bda = ScanBGService.hash_service_connected_device_list
					.get(device.getAddress());
			bda.setDevmodel(devmodel);
			scaninterface = bda;
			bda.connectedDevice(device);
		} else {

			BluetoothDeviceActor serviceBDA = new BluetoothDeviceActor();

			serviceBDA.initialization(context);
			serviceBDA.setmDevice(device);
			serviceBDA.setDeviceMacAddress(device.getAddress());
			serviceBDA.setContext(context);
			serviceBDA.setBda(serviceBDA);
			serviceBDA.setDevmodel(devmodel);
			scaninterface = serviceBDA;
			serviceBDA.connectedDevice(device);
		}
	}

}
