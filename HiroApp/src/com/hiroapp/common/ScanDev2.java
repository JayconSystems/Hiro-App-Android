package com.hiroapp.common;

import android.bluetooth.BluetoothAdapter;
import android.net.wifi.ScanResult;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.hiroapp.model.DeviceInfoModel;
import com.hiroapp.scanservice.ScanBGService;


public class ScanDev2 extends Thread {
	   public Handler mHandler;
		private final static int SCAN_INTERVAL=200;
		private boolean isScanning = false;
		private static final UUID IMMEDIATE_ALERT_SERVICE = UUID
				.fromString("00001802-0000-1000-8000-00805f9b34fb");
		private volatile Looper mMyLooper;
		private ArrayList<String> scanDeviceNameList;
		private Context context;
		public ScanInterface scaninterface;
		String uuid;
		Handler scanHandler;
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		public static ArrayList<String> a= new ArrayList<String>();
	
		public ScanDev2(Context mcontext) {
			final BluetoothManager bluetoothManager = (BluetoothManager) mcontext
					.getSystemService(Context.BLUETOOTH_SERVICE);
			adapter = BluetoothAdapter.getDefaultAdapter();
			context = mcontext;
			scaninterface = (ScanInterface) mcontext;
			Log.w("ScanDev2 -44-","Activity interface.");
		}
	    
		@SuppressLint("NewApi")
		public ScanDev2(Context mcontext,
				LinkedHashMap<String, DeviceInfoModel> devlist) {
			final BluetoothManager bluetoothManager = (BluetoothManager) mcontext
					.getSystemService(Context.BLUETOOTH_SERVICE);
			 adapter = BluetoothAdapter.getDefaultAdapter();
			
			context = mcontext;
			//startScanning();
			Log.e("scanDevices","begginig scan");
			//beginScanning();
			Log.w("ScanDevice","78");
			Thread t = new Thread(this);
			t.start();
			
		}
		
		
		
		@Override 
	    
	    
	    
	    
	    public void run() {
	        // Initialize the current thread as a Looper
	        // (this thread can have a MessageQueue now)
	        Looper.prepare();
	        scanHandler = new Handler(Looper.getMainLooper());
	        beginScanning();
	      
			/*scanHandler.postDelayed(new Runnable(){
			public void run() {
	            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
	            scanDeviceNameList = new ArrayList<String>();
	            Log.e("aaa","bbbbbbbbbbbbbbb");

	            if (isScanning) {
	                adapter.stopLeScan(mLeScanCallback);
	            } else if (!adapter.startLeScan(mLeScanCallback)) {
	                // an error occurred during startLeScan
	            }

	            isScanning = !isScanning;
			
			
								}
													}, SCAN_INTERVAL);*/
			
			mMyLooper = Looper.myLooper();

		    Looper.loop();
	    							}
	 
	        
	    
	    private Runnable scanRunnable = new Runnable() {
	        @Override
	        public void run() {
	        	// log give me startscan: null becuse adapter.StartLeScan(uuid,mcalladapter)
	        	// the uuid = null
	        	
	        	//Log.i("BlutuoothAdapter adaper ",String.valueOf(adapter));
	            // Log.e("aa","s-110-");
	            //Log.e("ScanDev2 -109-", "valor = " + ScanBGService.hash_service_connected_device_list.size());
	        	try{
	            if (isScanning) {
	                adapter.stopLeScan(mLeScanCallback);
	            } else if (!adapter.startLeScan(mLeScanCallback)) {
	                Log.e("ScanDev2", "Error StarLeScan --------- 82------");
	                
	                //TEst 6/29
	                
	            }
	        	}catch (Exception e){
	        		Log.e("ScanDev2 -120-", "exception ");
	        		adapter.startLeScan(mLeScanCallback);
	        	}
	            
	            isScanning = !isScanning;
			    //Log.e("IsScanning ", String.valueOf(isScanning));
	            scanHandler.postDelayed(this, SCAN_INTERVAL);
	            
	            
	          		
	        }
			};
			
			public void beginScanning() {
			    scanDeviceNameList = new ArrayList<String>();
				scanHandler.post(scanRunnable);
		    }
			
			
			
			
	
		
													
													
													
	    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
	    	    	
	        @Override
	        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
	            // use the RSSI value
	        	//Log.w("ScanDev2 ","Sline 154");
	        	//Log.e("ScanDev2 ","Sline 155 device "+ device);
	        	
	        	
	        	// if para filtrar dispositivos."nexus".
	        /*	if (a.contains(device.getAddress())){
	        		Log.e("ScanDev2","salto: ");
	        		BluetoothDeviceActor bda = ScanBGService.hash_service_connected_device_list
							.get(device.getAddress());
	        		if (bda.isConnected()) {
						
					}else{a.remove(device.getAddress());
					Log.e("ScanDev2", "S-165- borro");}
	        		
	        	}else{
	        	a.add(device.getAddress());
	        	Log.e("ScanDev2","Sline 162 add "+ device.getAddress());*/
	        	String deviceName = device.getAddress();

				if (scanDeviceNameList != null
						&& scanDeviceNameList.contains(device.getAddress())) {
					Log.e("ScanDev2","Scan-178-");
					if (ScanBGService.device_list != null
							&& ScanBGService.device_list.size() > 0
							&& ScanBGService.device_list.containsKey(deviceName)) {
						if (ScanBGService.hash_service_connected_device_list != null
								&& ScanBGService.hash_service_connected_device_list
										.size() > 0
								&& ScanBGService.hash_service_connected_device_list
										.containsKey(deviceName)) {
							Log.e("ScanDev2","Scan-187-");
							BluetoothDeviceActor bda = ScanBGService.hash_service_connected_device_list
									.get(deviceName);
							if (!bda.isConnected()) {
								Log.e("ScanDev2","conectando");
								scanDeviceNameList.add(device.getAddress());
								DeviceInfoModel devmodel = ScanBGService.device_list
										.get(deviceName);
								//ScanBGService.device_list.remove(deviceName);
								if (ScanBGService.device_list.size() == 0)
									//ScanBGService.isScanRunning = false;
								makeandconnectBDA(device, devmodel);
							}
						} else {
							Log.e("ScanDev2","Scan-201-");
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
							//ScanBGService.isScanRunning = false;
						makeandconnectBDA(device, devmodel);
					} else if (device != null && device.getName() != null
							&& !device.getName().toString().equalsIgnoreCase("")) {
						//if (device.getName().toString().equalsIgnoreCase("hiro")) {
							if(device.getName().toString().contains("hiro")){
							scanDeviceNameList.add(device.getAddress());
							if (scaninterface != null) {
								//scaninterface.addDevice(device, uuid,
								//		String.valueOf(transmissionpower));
							}

						}
					}

				}
	      //  }
				
	        }
	        
	        
	     
	        
	    };
	    
	    
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
	    
	    
	    class ScheduleTask extends TimerTask {
			@Override
			public void run() {

				broadcastUpdate(Utils.ACTION_DEVICE_NOT_FOUND);
				//mBluetoothAdapter.stopLeScan(mLeScanCallback);
			}
		}

		private void broadcastUpdate(final String action) {

			final Intent intent = new Intent(action);
			context.sendBroadcast(intent);
		}
	    
	    
		
		//  ------- 6/1 new test------ 
		
		public void startScanning() {
			scanDeviceNameList = new ArrayList<String>();
			adapter.startLeScan(mLeScanCallback);
		}

		public void stopScanning() {
			try {
				adapter.stopLeScan(mLeScanCallback);
			} catch (Exception e) {
				// Log.e("StopScanning", e.getMessage());
			}
		}

		
		
		// ------- 6/1-------
		
		
		// ---- 6/15-----
		// make an array one time.
		public void makeanarray(){
			// change boolean position 
			boolean aux1 =true;
			if (aux1 == true){
			scanDeviceNameList = new ArrayList<String>();
			}else {
				Log.i("ScanDev2 -273-", " aux1 = false");
			}
			
			
		}
		
		
		
		// ----- 6/15 ------ 
		
	}




