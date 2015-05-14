/**
 * Created by Jaycon Systems on 11/01/15.
 * Copyright @ 2014 Jaycon Systems. All rights reserved.
 */

package com.hiroapp.common;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.view.inputmethod.CorrectionInfo;

import com.hiroapp.dbhelper.DBHelper;
import com.hiroapp.main.HeroApp_App;
import com.hiroapp.main.MainActivity;
import com.hiroapp.main.R;
import com.hiroapp.model.CharacteristicsModel;
import com.hiroapp.model.DeviceInfoModel;
import com.hiroapp.model.OperationModel;
import com.hiroapp.scanservice.ScanBGService;

@SuppressLint("NewApi")
public class BluetoothDeviceActor implements Runnable, ScanInterface {

	private final static String TAG = BluetoothDeviceActor.class
			.getSimpleName();
	private Context mContext;
	private BluetoothManager mBluetoothManager;
	private BluetoothAdapter mBluetoothAdapter;
	public int deviceId = 0;
	public BluetoothGatt mBluetoothGatt;
	public String commandname;
	public int commandValue;
	private String operationType;
	private BluetoothDevice mDevice;
	public String deviceMacAddress;
	private boolean isConnected = false;
	private Thread thread;
	private Thread thread1;

	public ArrayList<BDACommand> bdaCommandQueue;
	private Queue<BluetoothGattDescriptor> descriptorWriteQueue = new LinkedList<BluetoothGattDescriptor>();
	private Timer failTimer;
	private ConnectionFailerTask failerTask;
	private DBHelper dbhelper;
	private HeroApp_App appStorage;
	public BluetoothDeviceActor bda;
	private DisconnectInterface disInterface;

	public byte[] uuidMajorMinor = null;
	public byte[] name = null;
	public byte[] txPower = null;

	private int Batterylevel;

	private int notificationId = 0;

	private DeviceInfoModel devmodel;

	private Timer timer;
	private ScheduleTask scheduleTask;

	// private Timer timer1;
	// private ScheduleTask1 scheduleTaskForBattery;

	private int alertlevel = 0;
	private int linkLossAlert = 0;

	private MediaPlayer mPlayer;

	private static final UUID IMMEDIATE_ALERT_SERVICE = UUID
			.fromString("00001802-0000-1000-8000-00805f9b34fb");
	private static final UUID ALERT_LEVEL_CHAR = UUID
			.fromString("00002A06-0000-1000-8000-00805f9b34fb");

	private static final int ALERT_LEVEL_CHARACTERISTIC_VALUE = 2;
	private static final int ALERT_LEVEL_CHARACTERISTIC_FORMATTYPE = 17;
	private static final int ALERT_LEVEL_CHARACTERISTIC_OFFSET = 0;

	private ArrayList<Integer> rssiCountList = new ArrayList<Integer>();
	private int averageRSSI = 0;

	private boolean isRssiget = false;

	private String photoUrl;

	private double latitude;
	private double longitude;

	public boolean isBeepRinging = false;
	// public boolean isSoundRinging = false;

	private Timer soundTimer;
	private SoundTask SoundTask;

	SharedPreferences preferences;

	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
			Locale.getDefault());

	public double getLatitude() {
		return latitude;
	}

	public MediaPlayer getmPlayer() {
		return mPlayer;
	}

	public void setmPlayer(MediaPlayer mPlayer) {
		this.mPlayer = mPlayer;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}

	public int getAverageRSSI() {
		return averageRSSI;
	}

	public void setAverageRSSI(int averageRSSI) {
		this.averageRSSI = averageRSSI;
	}

	public int getLinkLossAlert() {
		return linkLossAlert;
	}

	public void setLinkLossAlert(int linkLossAlert) {
		this.linkLossAlert = linkLossAlert;
	}

	public int getAlertlevel() {
		return alertlevel;
	}

	public void setAlertlevel(int alertlevel) {
		this.alertlevel = alertlevel;
	}

	public int getNotificationId() {
		return notificationId;
	}

	public void setNotificationId(int notificationId) {
		this.notificationId = notificationId;
	}

	public int getBatterylevel() {
		return Batterylevel;
	}

	public void setBatterylevel(int batterylevel) {
		Batterylevel = batterylevel;
	}

	public DeviceInfoModel getDevmodel() {
		return devmodel;
	}

	public void setDevmodel(DeviceInfoModel devmodel) {
		this.devmodel = devmodel;
	}

	public byte[] getUuidMajorMinor() {
		return uuidMajorMinor;
	}

	public void setUuidMajorMinor(byte[] uuidMajorMinor) {
		this.uuidMajorMinor = uuidMajorMinor;
	}

	public byte[] getName() {
		return name;
	}

	public void setName(byte[] name) {
		this.name = name;
	}

	public byte[] getTxPower() {
		return txPower;
	}

	public void setTxPower(byte[] txPower) {
		this.txPower = txPower;
	}

	public int getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(int deviceId) {
		this.deviceId = deviceId;
	}

	public BluetoothGatt getmBluetoothGatt() {
		return mBluetoothGatt;
	}

	public void setmBluetoothGatt(BluetoothGatt mBluetoothGatt) {
		this.mBluetoothGatt = mBluetoothGatt;
	}

	public BluetoothDevice getmDevice() {
		return mDevice;
	}

	public void setmDevice(BluetoothDevice mDevice) {
		this.mDevice = mDevice;
	}

	public String getDeviceMacAddress() {
		return deviceMacAddress;
	}

	public void setDeviceMacAddress(String deviceMacAddress) {
		this.deviceMacAddress = deviceMacAddress;
	}

	public boolean isConnected() {
		return isConnected;
	}

	public void setConnected(boolean isConnected) {
		this.isConnected = isConnected;
	}

	public BluetoothDeviceActor getBda() {
		return bda;
	}

	public void setBda(BluetoothDeviceActor bda) {
		this.bda = bda;
	}

	@SuppressLint("NewApi")
	public void initialization(Context context) {

		mContext = context;
		appStorage = (HeroApp_App) context.getApplicationContext();
		dbhelper = appStorage.getDbhelper();
		mBluetoothManager = (BluetoothManager) context
				.getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = mBluetoothManager.getAdapter();
		bdaCommandQueue = new ArrayList<BDACommand>();
		if (MainActivity.mcontext != null)
			disInterface = (DisconnectInterface) MainActivity.mcontext;
	}

	public void setContext(Context context) {
		mContext = context;
		if (MainActivity.mcontext != null)
			disInterface = (DisconnectInterface) MainActivity.mcontext;
	}

	@Override
	public void connectedDevice(BluetoothDevice device) {

		mDevice = device;
		setDeviceMacAddress(device.getAddress());
		startThread();
	}

	@Override
	public void run() {
		connectDevice(mDevice);
	}

	public void startThread() {
		thread = new Thread(this);
		thread.start();
	}

	public void stopThread() {
		if (thread != null) {
			final Thread tempThread = thread;
			thread = null;
			tempThread.interrupt();
		}
	}

	/**
	 * Connects to the GATT server hosted on the Bluetooth LE device.
	 * 
	 * @param device
	 *            The device address of the destination device.
	 * 
	 * @return Return true if the connection is initiated successfully. The
	 *         connection result is reported asynchronously through the
	 *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
	 *         callback.
	 */
	public boolean connectDevice(final BluetoothDevice device) {

		if (device == null) {
			return false;
		}

		failTimer = new Timer();
		failerTask = new ConnectionFailerTask();
		failTimer.schedule(failerTask, 15000, 50000);

		try {

			if (mContext != null) {

				mBluetoothGatt = device.connectGatt(mContext, true,
						mGattCallback);

				setmBluetoothGatt(mBluetoothGatt);

				setupGattServer();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return true;
	}

	// Implements callback methods for GATT events that the app cares about. For
	// example,
	// connection change and services discovered.
	public final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

		public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {

			if (rssiCountList != null && rssiCountList.size() < 20) {

				rssiCountList.add(rssi);
				if (rssiCountList.size() == 20) {
					int count = 0;
					for (int i = 0; i < rssiCountList.size(); i++) {

						count = count - rssiCountList.get(i);
					}
					final int posCount = Math.abs(count);
					final int averageRSSI = posCount / 20;

					setAverageRSSI(averageRSSI);

					// if (disInterface != null)
					// disInterface.updateRSSI(BluetoothDeviceActor.this);

					Utils.setBda(BluetoothDeviceActor.this);
					broadcastUpdateRSSI(Utils.ACTION_RSSI, averageRSSI);
					rssiCountList.clear();

					// Log.e("updateRssi", "updateRssi called for "
					// + getDevmodel().getDeviceLogicalName());

				} else {
					if (getmBluetoothGatt() != null)
						getmBluetoothGatt().readRemoteRssi();
				}

			}

		};

		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status,
				int newState) {
			super.onConnectionStateChange(gatt, status, newState);

			String intentAction;

			if (newState == BluetoothProfile.STATE_CONNECTED) {
				try {
					setConnected(true);
					if (failerTask != null && failTimer != null) {
						failerTask.cancel();
						failTimer.cancel();
					}

					if (soundTimer != null && SoundTask != null) {
						soundTimer.cancel();
						SoundTask.cancel();
					}

					// Log.e(TAG, "Connect from GATT server.");
					Log.e(TAG, "Connect BDA = "
							+ getDevmodel().getDeviceLogicalName());
					stopThread();

					if (getmBluetoothGatt() != null) {
						boolean discover = getmBluetoothGatt()
								.discoverServices();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
				// Log.e(TAG, "Disconnected from GATT server.");
				setConnected(false);
				intentAction = Utils.ACTION_GATT_DISCONNECTED;
				try {
					if (mBluetoothGatt != null)
						refreshDeviceCache(mBluetoothGatt);
					getmBluetoothGatt().close();
					setmBluetoothGatt(null);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				isBeepRinging = false;
				// MainActivity.lastConnectedBDA = BluetoothDeviceActor.this;

				Log.e(TAG, "Disconnect BDA = "
						+ getDevmodel().getDeviceLogicalName());
				Log.e(TAG, "Disconnect disInterface = " + disInterface);

				if (disInterface != null)
					disInterface.disconnectBda(BluetoothDeviceActor.this);

				if (bdaCommandQueue != null && bdaCommandQueue.size() > 0)
					bdaCommandQueue.clear();
				if (descriptorWriteQueue != null
						&& descriptorWriteQueue.size() > 0)
					descriptorWriteQueue.clear();

				if (timer != null && scheduleTask != null) {
					scheduleTask.cancel();
					timer.cancel();
				}
				isRssiget = false;
				commandname = "";
				broadcastUpdate(intentAction, -1);

				if (soundTimer != null && SoundTask != null) {
					soundTimer.cancel();
					SoundTask.cancel();
				}

				soundTimer = new Timer();
				SoundTask = new SoundTask();
				soundTimer.schedule(SoundTask, 20000, 50000000);

			}

		}

		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			super.onServicesDiscovered(gatt, status);

			String intentAction;
			intentAction = Utils.ACTION_GATT_CONNECTED;
			broadcastUpdate(intentAction, -1);
			discoverServices(gatt.getServices());

			Log.e(TAG, "onServicesDiscovered called");

		}

		@Override
		public void onCharacteristicRead(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			// TODO Auto-generated method stub
			super.onCharacteristicRead(gatt, characteristic, status);
			// Log.e(TAG, "onCharacteristicRead called");
			BDACommand bdacommand = null;
			if (bdaCommandQueue != null && bdaCommandQueue.size() > 0) {
				bdacommand = bdaCommandQueue.get(0);
				bdaCommandQueue.remove(0);
			}
			byte[] data = characteristic.getValue();
			if (data != null) {

				convertCharacteristicsValue(characteristic, bdacommand);
			}

			if (bdacommand.commandName.equalsIgnoreCase("Batterylevel")) {

				// if (timer1 != null && scheduleTaskForBattery != null) {
				// scheduleTaskForBattery.cancel();
				// timer1.cancel();
				// }

				deviceIsReadyForCommunication("LinkLossAlert", -1, "Read");

				// int id = dbhelper.getnotificationId(deviceMacAddress);
				//
				// Utils.generateNotificationForConnect(mContext, mContext
				// .getResources().getString(R.string.app_name),
				// getDevmodel().getDeviceLogicalName() + " is Connected",
				// id);
				String intentAction;

				if (MainActivity.mLeDeviceListAdapter != null) {
					MainActivity.mLeDeviceListAdapter
							.addDeviceTolist(BluetoothDeviceActor.this);
				}

				intentAction = Utils.ACTION_DESCRIPTOR_WRITE;
				broadcastUpdate(intentAction, -1);

			} else if (bdacommand.commandName.equalsIgnoreCase("LinkLossAlert")
					&& operationType != null
					&& operationType.equalsIgnoreCase("Read")) {

				if (!isRssiget) {

					isRssiget = true;

					DeviceInfoModel model = dbhelper
							.getdeviceSettings(deviceMacAddress);

					if (model != null) {
						if (model.isHiroDisBeepAlert() == 0) {
							deviceIsReadyForCommunication("LinkLossAlert", 0,
									"Write");
							Log.e(TAG, "send command LinkLossAlert with 0");
						} else {
							if (model.isHiroDisconnectBeep() == 1) {
								MainActivity.BDA.deviceIsReadyForCommunication(
										"LinkLossAlert", 2, "Write");
								Log.e(TAG, "send command LinkLossAlert with 2");
							} else if (model.isHiroDisconnectBeep() == 0
									|| model.isHiroDisconnectBeep() == -1) {
								MainActivity.BDA.deviceIsReadyForCommunication(
										"LinkLossAlert", 1, "Write");
								Log.e(TAG, "send command LinkLossAlert with 1");
							}
						}
					}

					if (timer != null && scheduleTask != null) {
						scheduleTask.cancel();
						timer.cancel();
					}

					timer = new Timer();
					scheduleTask = new ScheduleTask();
					timer.schedule(scheduleTask, 1000, 3000);
				}

			}
		}

		@Override
		public void onCharacteristicWrite(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			// TODO Auto-generated method stub
			super.onCharacteristicWrite(gatt, characteristic, status);
			// Log.e(TAG, "onCharacteristicWrite called");

			BDACommand bdacommand = null;

			byte[] data = characteristic.getValue();
			if (data != null) {

				if (bdaCommandQueue != null && bdaCommandQueue.size() > 0) {
					bdacommand = bdaCommandQueue.get(0);
					bdaCommandQueue.remove(0);

					if (bdacommand.commandName.equalsIgnoreCase("HeroAlert")) {

						String value = convertCharacteristicsValue(
								characteristic, bdacommand);
						int val = Integer.parseInt(value, 16);

						broadcastUpdate(Utils.ACTION_ALERT, val);
					}
				}

			}
		}

		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic) {
			// TODO Auto-generated method stub
			super.onCharacteristicChanged(gatt, characteristic);

			// Log.e(TAG, "onCharacteristicChanged called");

			if (characteristic.getUuid().equals(
					"00002a19-0000-1000-8000-00805f9b34fb")) {

				// if (timer1 != null && scheduleTaskForBattery != null) {
				// scheduleTaskForBattery.cancel();
				// timer1.cancel();
				// }

				getBatteryUpdate(characteristic);
			}

		}

		@Override
		public void onDescriptorRead(BluetoothGatt gatt,
				BluetoothGattDescriptor descriptor, int status) {
			// TODO Auto-generated method stub
			super.onDescriptorRead(gatt, descriptor, status);
		}

		@Override
		public void onDescriptorWrite(BluetoothGatt gatt,
				BluetoothGattDescriptor descriptor, int status) {
			super.onDescriptorWrite(gatt, descriptor, status);

			descriptorWriteQueue.remove(); // pop the item that we just

			// finishing writing
			// if there is more to write, do it!
			if (descriptorWriteQueue.size() > 0)
				writeGattDescriptor(descriptorWriteQueue.element());
			else {

				if (!dbhelper.checkIfDeviceExists(getDevmodel())) {
					setNotificationId(dbhelper.insertDeviceInfo(getDevmodel()));
				}
				// if service hash already contain the mac address the remove
				// the old with fresh bda
				if (ScanBGService.hash_service_connected_device_list != null
						&& ScanBGService.hash_service_connected_device_list
								.containsKey(getDeviceMacAddress()))
					ScanBGService.hash_service_connected_device_list
							.remove(getDeviceMacAddress());
				ScanBGService.hash_service_connected_device_list.put(
						getDeviceMacAddress(), getBda());

				// timer1 = new Timer();
				// scheduleTaskForBattery = new ScheduleTask1();
				// timer1.schedule(scheduleTaskForBattery, 2000, 50000);
				deviceIsReadyForCommunication("Batterylevel", 0, "");

			}

		}

	};

	private void discoverServices(List<BluetoothGattService> list) {

		// for (int i = 0; i < list.size(); i++) {
		// Log.e("serviceUUID", " " + list.get(i).getUuid());
		// }

		for (int i = 0; i < list.size(); i++) {

			final int serviceId = dbhelper.getServiceIdFromUUIDAndDeviceId(list
					.get(i).getUuid(), getDeviceId());

			if (serviceId == -1) {
				continue;
			}

			Hashtable<String, CharacteristicsModel> charHas = dbhelper
					.getCharacteristicsOfService(serviceId, getDeviceId());

			ArrayList<BluetoothGattCharacteristic> gattCharacteristics = (ArrayList<BluetoothGattCharacteristic>) list
					.get(i).getCharacteristics();

			for (int j = 0; j < gattCharacteristics.size(); j++) {

				Object name = gattCharacteristics.get(j).getUuid().toString()
						.trim();

				if (charHas != null && charHas.containsKey(name)) {

					CharacteristicsModel model = charHas
							.get(gattCharacteristics.get(j).getUuid()
									.toString().trim());

					if (model.isObservable() == 1) {

						boolean isNotify = getmBluetoothGatt()
								.setCharacteristicNotification(
										gattCharacteristics.get(j), true);

						ArrayList<BluetoothGattDescriptor> gattdescriptor = (ArrayList<BluetoothGattDescriptor>) gattCharacteristics
								.get(j).getDescriptors();

						for (int k = 0; k < gattdescriptor.size(); k++) {

							BluetoothGattDescriptor descriptor = gattdescriptor
									.get(k);
							descriptorWriteQueue.add(descriptor);

						}
					}
				}
			}
		}

		if (descriptorWriteQueue.size() > 0) {
			writeGattDescriptor(descriptorWriteQueue.element());
		} else {
			// store data here
			if (!dbhelper.checkIfDeviceExists(getDevmodel())) {
				setNotificationId(dbhelper.insertDeviceInfo(getDevmodel()));
			}
			// if service hash already contain the mac address the remove
			// the old with fresh bda
			if (ScanBGService.hash_service_connected_device_list != null
					&& ScanBGService.hash_service_connected_device_list
							.containsKey(getDeviceMacAddress()))
				ScanBGService.hash_service_connected_device_list
						.remove(getDeviceMacAddress());
			ScanBGService.hash_service_connected_device_list.put(
					getDeviceMacAddress(), getBda());

			// timer1 = new Timer();
			// scheduleTaskForBattery = new ScheduleTask1();
			// timer1.schedule(scheduleTaskForBattery, 2000, 50000);

			deviceIsReadyForCommunication("Batterylevel", 0, "");

		}

	}

	class ScheduleTask extends TimerTask {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (mBluetoothGatt != null)
				mBluetoothGatt.readRemoteRssi();
		}

	}

	// class ScheduleTask1 extends TimerTask {
	//
	// @Override
	// public void run() {
	// // TODO Auto-generated method stub
	// deviceIsReadyForCommunication("Batterylevel", 0, "");
	//
	// if (timer1 != null && scheduleTaskForBattery != null) {
	// scheduleTaskForBattery.cancel();
	// timer1.cancel();
	// }
	// }
	//
	// }

	// Write gatt descriptor
	public void writeGattDescriptor(BluetoothGattDescriptor d) {
		d.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
		getmBluetoothGatt().writeDescriptor(d);
	}

	class ConnectionFailerTask extends TimerTask {
		@Override
		public void run() {
			broadcastUpdate(Utils.ACTION_CONNECT_FAIL, -1);
			failerTask.cancel();
			failTimer.cancel();
		}
	}

	private void broadcastUpdate(final String action, int val) {
		final Intent intent = new Intent(action);
		intent.putExtra("value", val);
		mContext.sendBroadcast(intent);
	}

	private void broadcastUpdateRSSI(final String action, int val) {
		final Intent intent = new Intent(action);
		intent.putExtra("Rssi", val);
		mContext.sendBroadcast(intent);
	}

	// device is ready to for communition
	public void deviceIsReadyForCommunication(String name, int value,
			String Type) {

		// Log.e(TAG, "commandname in deviceIsReadyForCommunication = " + name);
		commandname = name;
		commandValue = value;
		operationType = Type;
		if (bdaCommandQueue != null && bdaCommandQueue.size() > 0) {
			bdaCommandQueue.remove(0);
		}

		if (name != null && !name.equalsIgnoreCase("")) {
			checkAndPerformCommand(name, commandValue, Type);
		}
	}

	// check if device is null or its bluetooth gatt is null.
	public void checkAndPerformCommand(String command, int value,
			String operationType) {

		if (mDevice == null && deviceMacAddress != null) {
			mDevice = mBluetoothAdapter.getRemoteDevice(deviceMacAddress);
		} else {

			if (mDevice == null && deviceMacAddress == null) {
				ScanDevices scan = new ScanDevices(mContext);
				return;
			}
		}
		if (getmBluetoothGatt() == null) {
			// ScanDevices scan = new ScanDevices(mContext);
		} else {
			performCommand(command, value, operationType);
		}
	}

	public void performCommand(String command, int value, String operationType) {

		// Log.e(TAG, "Command Name in performCommand ====== " + command
		// + " =======  " + bdaCommandQueue);

		if (bdaCommandQueue == null || bdaCommandQueue.size() == 0) {
			BDACommand bdc;
			bdc = new BDACommand(mContext, command);
			bdaCommandQueue.add(bdc);

			if (bdaCommandQueue.size() == 1)
				processCommand(bdc, value, operationType);
		}

	}

	private void processCommand(BDACommand bdc, int value, String operationType) {
		// Log.e("processCommand()", " " + bdc.operationSequenceList.size());
		if (bdc.operationSequenceList.size() > 0) {
			OperationModel model = bdc.operationSequenceList.get(0);
			bdc.operationSequenceList.remove(0);
			processCurrentModel(model, value, operationType);
		} else {
			// Log.e("processCommand()",
			// " else  " + bdc.operationSequenceList.size());
			if (bdaCommandQueue != null && bdaCommandQueue.size() > 0)
				bdaCommandQueue.clear();
		}

	}

	private void processCurrentModel(OperationModel model, int value,
			String operationType) {

		UUID serviceuid = UUID.fromString(model.getServiceUUID());
		BluetoothGattService service = getmBluetoothGatt().getService(
				serviceuid);
		UUID characteristicuid = UUID.fromString(model.getCharUUID());
		BluetoothGattCharacteristic characteristic = null;
		if (service != null) {
			characteristic = service.getCharacteristic(characteristicuid);
		}
		if (characteristic != null) {
			if (model.getOperation().equalsIgnoreCase("Read")) {
				boolean read = getmBluetoothGatt().readCharacteristic(
						characteristic);
				// Log.e(TAG, "Read fire = " + read);
			} else if (model.getOperation().equalsIgnoreCase("Write")) {
				byte[] byt = null;
				byt = makeRequestByte(value);
				characteristic.setValue(byt);
				boolean write = getmBluetoothGatt().writeCharacteristic(
						characteristic);
				// Log.e(TAG, "write fire = " + write);
				if (!write) {
					if (bdaCommandQueue != null && bdaCommandQueue.size() > 0)
						bdaCommandQueue.remove(0);
				}

			} else if (model.getOperation().equalsIgnoreCase("ReadWrite")) {

				if (operationType.equalsIgnoreCase("Read")) {
					boolean read = getmBluetoothGatt().readCharacteristic(
							characteristic);
					// Log.e(TAG, "Read fire = " + read);
				} else {
					byte[] byt = null;
					byt = makeRequestByte(value);
					characteristic.setValue(byt);
					boolean write = getmBluetoothGatt().writeCharacteristic(
							characteristic);
					// Log.e(TAG, "write fire = " + write);
					if (!write) {
						if (bdaCommandQueue != null
								&& bdaCommandQueue.size() > 0)
							bdaCommandQueue.remove(0);
					}
				}

			}
		}

	}

	private byte[] makeRequestByte(int value) {

		byte[] byt = new byte[1];

		byt[0] = (byte) convertIntToHex(value);

		return byt;
	}

	public static int convertIntToHex(int n) {
		return Integer.valueOf(String.valueOf(n), 16);

	}

	public String convertCharacteristicsValue(
			BluetoothGattCharacteristic characteristic, BDACommand bdaCommand) {

		// For all other profiles, writes the data formatted in HEX.
		final byte[] data = characteristic.getValue();
		StringBuilder stringBuilder = null;
		if (data != null && data.length > 0) {
			stringBuilder = new StringBuilder(data.length);
			for (byte byteChar : data)
				stringBuilder.append(String.format("%02X ", byteChar));
		}
		String value = null;
		if (bdaCommand != null
				&& bdaCommand.commandName.equalsIgnoreCase("Batterylevel")) {
			value = stringBuilder.toString().trim();
			Batterylevel = Integer.parseInt(value, 16);

			if (disInterface != null)
				disInterface.updateBattery(BluetoothDeviceActor.this);

			// Utils.setBdaForbattery(BluetoothDeviceActor.this);
			// broadcastUpdate(Utils.ACTION_BATTERY_LEVEL, -1);
			if (Batterylevel < 20) {
				if (getPreference("BatteryIndication")) {

					String currentDate = getDateTime();
					String storeDate = dbhelper
							.getDateTimeValue(deviceMacAddress);

					if (storeDate != null && !storeDate.equalsIgnoreCase("")) {

						int diff = getDaysDiff(currentDate, storeDate);

						if (diff > 1) {

							int id = dbhelper
									.getnotificationId(deviceMacAddress);

							Utils.generateNotification(mContext, mContext
									.getResources()
									.getString(R.string.app_name),
									"Battery is getting low for "
											+ getDevmodel()
													.getDeviceLogicalName(),
									id + 100, true);
						}

					} else {

						int id = dbhelper.getnotificationId(deviceMacAddress);
						Utils.generateNotification(mContext, mContext
								.getResources().getString(R.string.app_name),
								"Battery is getting low for "
										+ getDevmodel().getDeviceLogicalName(),
								id + 100, true);

					}

					dbhelper.updateDeviceDateTime(deviceMacAddress,
							"BatteryNotificationDate", currentDate);
					// playLowBatterySound();
				}
			}

			// Log.e(TAG,
			// "convertCharacteristicsValue  Batterylevel ========== "
			// + Batterylevel);
		} else if (bdaCommand != null
				&& bdaCommand.commandName.equalsIgnoreCase("LinkLossAlert")) {
			value = stringBuilder.toString().trim();
			linkLossAlert = Integer.parseInt(value, 16);
		} else {
			value = stringBuilder.toString().trim();
		}

		// Log.e(TAG, "convertCharacteristicsValue " + value);
		return value;
	}

	private int getDaysDiff(String currentDate, String storeDate) {

		long diff = -1;

		try {
			Date chooseDateFrom = dateFormat.parse(storeDate);
			Date choosenDateTo = dateFormat.parse(currentDate);
			diff = choosenDateTo.getTime() - chooseDateFrom.getTime();
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int diffDays = (int) ((diff / (24 * 60 * 60 * 1000)) + 1);

		return diffDays;
	}

	private void playLowBatterySound() {
		MediaPlayer mp = MediaPlayer.create(mContext, R.raw.error);
		mp.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				mp.release();
			}

		});
		mp.start();

	}

	public void getBatteryUpdate(BluetoothGattCharacteristic characteristic) {
		// For all other profiles, writes the data formatted in HEX.
		final byte[] data = characteristic.getValue();
		StringBuilder stringBuilder = null;
		if (data != null && data.length > 0) {
			stringBuilder = new StringBuilder(data.length);
			for (byte byteChar : data)
				stringBuilder.append(String.format("%02X ", byteChar));
		}
		String value = null;
		value = stringBuilder.toString().trim();
		Batterylevel = Integer.parseInt(value, 16);
		if (Batterylevel < 20) {
			if (getPreference("BatteryIndication")) {
				// playLowBatterySound();

				String currentDate = getDateTime();
				String storeDate = dbhelper.getDateTimeValue(deviceMacAddress);

				if (storeDate != null && !storeDate.equalsIgnoreCase("")) {

					int diff = getDaysDiff(currentDate, storeDate);

					if (diff > 1) {

						int id = dbhelper.getnotificationId(deviceMacAddress);
						Utils.generateNotification(mContext, mContext
								.getResources().getString(R.string.app_name),
								"Battery is getting low for "
										+ getDevmodel().getDeviceLogicalName(),
								id + 100, true);

					}

				} else {

					int id = dbhelper.getnotificationId(deviceMacAddress);
					Utils.generateNotification(mContext, mContext
							.getResources().getString(R.string.app_name),
							"Battery is getting low for "
									+ getDevmodel().getDeviceLogicalName(),
							id + 100, true);

				}

				dbhelper.updateDeviceDateTime(deviceMacAddress,
						"BatteryNotificationDate", currentDate);
				// playLowBatterySound();

			}
		}
		// Utils.setBdaForbattery(BluetoothDeviceActor.this);
		// broadcastUpdate(Utils.ACTION_BATTERY_LEVEL, -1);
		if (disInterface != null)
			disInterface.updateBattery(BluetoothDeviceActor.this);
	}

	@Override
	public void addDevice(BluetoothDevice scandevices, String uuid,
			String string) {
	}

	public void setupGattServer() {

		final BluetoothManager bluetoothManager = (BluetoothManager) mContext
				.getSystemService(Context.BLUETOOTH_SERVICE);
		BluetoothGattServerCallback mBluetoothGattServerCallback = new BluetoothGattServerCallback() {
			@Override
			public void onConnectionStateChange(BluetoothDevice device,
					int status, int newState) {
				// Log.e("BLEMainActivity:onConnectionStateChange", "device : "
				// + device + " status : " + status + " new state : "
				// + newState);
			}

			@Override
			public void onServiceAdded(int status, BluetoothGattService service) {
				// Log.e("BLEMainActivity:onServiceAdded",
				// "service : " + service.getUuid() + " status = "
				// + status);
			}

			@Override
			public void onCharacteristicReadRequest(BluetoothDevice device,
					int requestId, int offset,
					BluetoothGattCharacteristic characteristic) {
				// Log.e("BLEMainActivity:onCharacteristicReadRequest",
				// "device : " + device.getAddress() + " request = "
				// + requestId + " offset = " + offset
				// + " characteristic = "
				// + characteristic.getUuid());

			}

			@Override
			public void onCharacteristicWriteRequest(BluetoothDevice device,
					int requestId, BluetoothGattCharacteristic characteristic,
					boolean preparedWrite, boolean responseNeeded, int offset,
					byte[] value) {
				super.onCharacteristicWriteRequest(device, requestId,
						characteristic, preparedWrite, responseNeeded, offset,
						value);
				int val = 0;
				if (value != null) {
					val = value[0];
				}

				// Log.e(TAG, "value == " + val);

				// Log.e("BLEMainActivity", "device : " + device.getAddress()
				// + " characteristic : " + characteristic.getUuid()
				// + "Value = " + value.toString() + "requestId = "
				// + requestId + " Service == "
				// + characteristic.getService().getUuid());

				playSound(val);

			}

			@Override
			public void onDescriptorReadRequest(BluetoothDevice device,
					int requestId, int offset,
					BluetoothGattDescriptor descriptor) {

			}

			@Override
			public void onDescriptorWriteRequest(BluetoothDevice device,
					int requestId, BluetoothGattDescriptor descriptor,
					boolean preparedWrite, boolean responseNeeded, int offset,
					byte[] value) {

			}

			@Override
			public void onExecuteWrite(BluetoothDevice device, int requestId,
					boolean execute) {

			}
		};
		BluetoothGattServer gattServer = bluetoothManager.openGattServer(
				mContext.getApplicationContext(), mBluetoothGattServerCallback);
		BluetoothGattService service = new BluetoothGattService(
				IMMEDIATE_ALERT_SERVICE,
				BluetoothGattService.SERVICE_TYPE_PRIMARY);
		BluetoothGattCharacteristic characteristic = new BluetoothGattCharacteristic(
				ALERT_LEVEL_CHAR,
				BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE,
				BluetoothGattCharacteristic.PERMISSION_WRITE);
		characteristic.setValue(ALERT_LEVEL_CHARACTERISTIC_VALUE,
				ALERT_LEVEL_CHARACTERISTIC_FORMATTYPE,
				ALERT_LEVEL_CHARACTERISTIC_OFFSET);
		service.addCharacteristic(characteristic);
		gattServer.addService(service);
		Log.e("BLEMainActivity:setupGattServer",
				"Gatt server setup complete : " + gattServer.toString());

	}

	public boolean getPreference(String key) {

		boolean istrue = dbhelper.getColumnValue(deviceMacAddress, key);

		return istrue;
	}

	private void playSound(int val) {

		Log.e(TAG, "playSound called");

		preferences = mContext.getSharedPreferences(
				mContext.getString(R.string.app_name),
				Activity.MODE_WORLD_WRITEABLE);

		String path = preferences.getString("PhoneRing", "");

		if (val == 0) {
			// isSoundRinging = false;
			try {
				if (mPlayer != null && mPlayer.isPlaying()) {
					mPlayer.stop();
					mPlayer.release();
				}
			} catch (Exception e) {
				// TODO: handle exception
			}

		} else {

			Utils.generateNotificationForFindPhone(mContext, mContext
					.getResources().getString(R.string.app_name),
					"Hiro wants to find your phone", 500);

			AudioManager audioManager = (AudioManager) mContext
					.getSystemService(Context.AUDIO_SERVICE);

			int currentVolume = audioManager
					.getStreamVolume(AudioManager.STREAM_MUSIC);

			int maxvolume = audioManager
					.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

			if (currentVolume < maxvolume) {

				audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
						audioManager
								.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
						AudioManager.FLAG_SHOW_UI);
			}
			mPlayer = new MediaPlayer();
			try {

				if (!path.equalsIgnoreCase("")) {
					Uri uri = Uri.parse(path);
					mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
					mPlayer.setDataSource(mContext.getApplicationContext(), uri);
					mPlayer.prepare();
					mPlayer.setLooping(false);
					mPlayer.start();
					setmPlayer(mPlayer);

					mPlayer.setOnCompletionListener(new OnCompletionListener() {

						@Override
						public void onCompletion(MediaPlayer mp) {
							// TODO Auto-generated method stub
							try {
								mPlayer.stop();
								mPlayer.release();
							} catch (IllegalStateException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							// isSoundRinging = false;

						}
					});
				} else {
					Uri notification = RingtoneManager
							.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
					mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
					mPlayer.setDataSource(mContext.getApplicationContext(),
							notification);
					mPlayer.prepare();
					mPlayer.setLooping(false);
					mPlayer.start();

					setmPlayer(mPlayer);

					mPlayer.setOnCompletionListener(new OnCompletionListener() {

						@Override
						public void onCompletion(MediaPlayer mp) {
							// TODO Auto-generated method stub
							try {
								mPlayer.stop();
								mPlayer.release();
							} catch (IllegalStateException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							// isSoundRinging = false;
						}
					});
				}
			} catch (Exception e) {

			}
		}
	}

	private String getWifiSSIDName() {
		String ssid = "";
		WifiManager wifiManager;
		ConnectivityManager cm = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo Info = cm.getActiveNetworkInfo();
		if (Info == null || !Info.isConnectedOrConnecting()) {
			ssid = "";
		} else {
			int netType = Info.getType();
			if (netType == ConnectivityManager.TYPE_WIFI) {
				Log.i("WifiZones", "Wifi connection");
				wifiManager = (WifiManager) mContext
						.getSystemService(Context.WIFI_SERVICE);
				WifiInfo info = wifiManager.getConnectionInfo();
				ssid = info.getSSID();
			}
		}
		return ssid;
	}

	class SoundTask extends TimerTask {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			// Log.e(TAG, "Sound Timer called for " +
			// getDevmodel().getDeviceLogicalName() + " " + isConnected);
			if (!isConnected) {

				String ssid = getWifiSSIDName();

				if (ssid != null && !ssid.equalsIgnoreCase("")) {
					boolean isSsidExist = dbhelper.ssidAvailable(ssid);

					if (!isSsidExist) {
						playDisconnectSoundAndNotification();
					}

				} else {

					playDisconnectSoundAndNotification();

				}

			}

			if (soundTimer != null && SoundTask != null) {
				soundTimer.cancel();
				SoundTask.cancel();
			}
		}
	}

	private void playDisconnectSoundAndNotification() {

		boolean isDisSound = getPreference("NotificationDisconnectAlert");

		if (getPreference("NotificationIndication")) {

			int id = dbhelper.getnotificationId(deviceMacAddress);

			// Log.e(TAG, "DisNotifID = " + id + " "
			// + getDevmodel().getDeviceLogicalName());

			Utils.generateNotification(mContext, mContext.getResources()
					.getString(R.string.app_name), getDevmodel()
					.getDeviceLogicalName() + " getting away!", id, isDisSound);
		}

		if (isDisSound) {
			String path = dbhelper.getRing(deviceMacAddress, "DisconnectRing");
			// String path = getRingtone("DisconnectRing");
			try {
				if (path != null && !path.equalsIgnoreCase("")) {
					Uri uri = Uri.parse(path);
					Ringtone r = RingtoneManager.getRingtone(
							mContext.getApplicationContext(), uri);
					r.play();
				} else {
					MediaPlayer mp = MediaPlayer.create(
							mContext.getApplicationContext(), R.raw.disconnect);

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
				if (soundTimer != null && SoundTask != null) {
					soundTimer.cancel();
					SoundTask.cancel();
				}
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				if (soundTimer != null && SoundTask != null) {
					soundTimer.cancel();
					SoundTask.cancel();
				}
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				if (soundTimer != null && SoundTask != null) {
					soundTimer.cancel();
					SoundTask.cancel();
				}
				e.printStackTrace();
			}
		}

	}

	private String getDateTime() {
		Date date = new Date();
		return dateFormat.format(date);
	}

	private boolean refreshDeviceCache(BluetoothGatt gatt) {
		try {
			BluetoothGatt localBluetoothGatt = gatt;
			Method localMethod = localBluetoothGatt.getClass().getMethod(
					"refresh", new Class[0]);
			if (localMethod != null) {
				boolean bool = ((Boolean) localMethod.invoke(
						localBluetoothGatt, new Object[0])).booleanValue();
				return bool;
			}
		} catch (Exception localException) {
			Log.e(TAG, "An exception occured while refreshing device");
		}
		return false;
	}

}
