/**
 * Created by Jaycon Systems on 26/12/14.
 * Copyright @ 2014 Jaycon Systems. All rights reserved.
 */

package com.hiroapp.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.hiroapp.adapter.ConnectedDeviceListAdapter;
import com.hiroapp.adapter.CropOptionAdapter;
import com.hiroapp.common.BluetoothDeviceActor;
import com.hiroapp.common.DisconnectInterface;
import com.hiroapp.common.GPSTracker;
import com.hiroapp.common.Utils;
import com.hiroapp.dbhelper.DBHelper;
import com.hiroapp.model.CropOption;
import com.hiroapp.model.DeviceInfoModel;
import com.hiroapp.scanservice.ScanBGService;

public class MainActivity extends Activity implements OnClickListener,
		DisconnectInterface {
//	static Context context;
	private DBHelper dbhelper;
	private HeroApp_App appStorage;
	LinkedHashMap<String, DeviceInfoModel> device_list;
	private ListView list;
	public static ConnectedDeviceListAdapter mLeDeviceListAdapter;
	public static ArrayList<BluetoothDeviceActor> connectedDeviceList;

	public static BluetoothDeviceActor BDA = null;
	// private View footerView;
	// private RelativeLayout relfooterMain;
	private ArrayAdapter<String> adapter = null;
	private File file = null;
	private Uri mImageCaptureUri = null;
	private static final int PICK_FROM_CAMERA = 1;
	private static final int CROP_FROM_CAMERA = 2;
	private static final int PICK_FROM_FILE = 3;
	private String strImagePath = null;
	private Bitmap bitmap = null, photo = null;

	private int selectedPosition = -1;
	private ImageView image;
	private ImageView imgadd_device;
	// public static BluetoothDeviceActor lastConnectedBDA = null;

	public static Context mcontext;
	private ImageView imgaddwifi;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.mainscreen);

		registerReceiver(mGattUpdateReceiver, Utils.makeIntentFilter());

		initScreen();
		setListener();

		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					final int position, long id) {
				// TODO Auto-generated method stub

				image = (ImageView) view
						.findViewById(R.id.inflate_device_imageMain);

				image.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub

						selectedPosition = position;
						chooseImage();
					}
				});

			}
		});

	}

	/**
	 * define click listener of the objects.
	 */
	private void setListener() {
		// TODO Auto-generated method stub
		imgadd_device.setOnClickListener(this);
		imgaddwifi.setOnClickListener(this);
	}

	/**
	 * Initialization of the objects;
	 */
	private void initScreen() {
		// TODO Auto-generated method stub
//		context = this;
		mcontext = this;
		appStorage = (HeroApp_App) mcontext.getApplicationContext();
		dbhelper = appStorage.getDbhelper();
		device_list = new LinkedHashMap<String, DeviceInfoModel>();
		imgadd_device = (ImageView) findViewById(R.id.imgaddnewdevice);
		list = (ListView) findViewById(R.id.listView1);
		imgaddwifi = (ImageView) findViewById(R.id.imgaddwifizone);
		connectedDeviceList = new ArrayList<BluetoothDeviceActor>();

		mLeDeviceListAdapter = new ConnectedDeviceListAdapter(this,
				connectedDeviceList, list);
		list.setAdapter(mLeDeviceListAdapter);

		final String[] items = getResources().getStringArray(
				R.array.ImageSelection);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.select_dialog_item, items);
		checkBDA();
		startBGService();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}

	/**
	 * Method is used to check if Hiro connected or not. if connected then
	 * display UI.
	 */
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
						bda.setContext(MainActivity.this);
						bda.setNotificationId(model.getId());
						bda.setPhotoUrl(model.getDevicePhotoUrl());
						bda.setDevmodel(model);
						bda.setLatitude(model.getLatitude());
						bda.setLongitude(model.getLongitude());
						bda.setDeviceMacAddress(model.getDeviceMacAddress());
						mLeDeviceListAdapter.addDeviceTolist(bda);
						mLeDeviceListAdapter.notifyDataSetChanged();

						try {
							if (bda.getmPlayer() != null
									&& bda.getmPlayer().isPlaying()) {
								try {
									bda.getmPlayer().stop();
									bda.getmPlayer().release();

									Utils.removeNotification(mcontext, 500);
								} catch (Exception e2) {
									// TODO: handle exception
								}
							}
						} catch (Exception e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}

					} else {
						DeviceInfoModel model = device_list.get(mac_add);
						BluetoothDeviceActor bda = new BluetoothDeviceActor();
						bda.setContext(MainActivity.this);
						bda.setNotificationId(model.getId());
						bda.setPhotoUrl(model.getDevicePhotoUrl());
						bda.setLatitude(model.getLatitude());
						bda.setLongitude(model.getLongitude());
						bda.setDeviceMacAddress(model.getDeviceMacAddress());
						bda.setDevmodel(model);
						mLeDeviceListAdapter.addDeviceTolist(bda);
						mLeDeviceListAdapter.notifyDataSetChanged();
					}

				} else {

					DeviceInfoModel model = device_list.get(mac_add);
					BluetoothDeviceActor bda = new BluetoothDeviceActor();
					bda.setContext(MainActivity.this);
					bda.setNotificationId(model.getId());
					bda.setPhotoUrl(model.getDevicePhotoUrl());
					bda.setLatitude(model.getLatitude());
					bda.setLongitude(model.getLongitude());
					bda.setDeviceMacAddress(model.getDeviceMacAddress());
					bda.setDevmodel(model);
					mLeDeviceListAdapter.addDeviceTolist(bda);
					mLeDeviceListAdapter.notifyDataSetChanged();
				}
			}

		} else {
			Log.e("MainActivity", "No device found from DB");
		}
	}

	/**
	 * Handle click event of the objects.
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		if (v == imgadd_device) {
			Intent i = new Intent(MainActivity.this, ScanActivity.class);
			startActivity(i);
		} else if (v == imgaddwifi) {
			Intent i = new Intent(MainActivity.this, WifiZonesActivity.class);
			startActivity(i);
		}

	}

	/**
	 * Start Background service of the app. that scan Hiro in background if not
	 * connected.
	 */
	public static void startBGService() {

		if (!isMyServiceRunning()) {
			ScanBGService.intentService = new Intent(mcontext,
					ScanBGService.class);
			mcontext.startService(ScanBGService.intentService);

		} else {
			Log.e(" service is already running ", "");
		}

	}

	/**
	 * Check if Background service is already running. if not then start
	 * background service again.
	 * 
	 * @return
	 */
	static boolean isMyServiceRunning() {
		try {
			final ActivityManager manager;
			if (mcontext != null) {
				manager = (ActivityManager) mcontext
						.getSystemService(ACTIVITY_SERVICE);
				for (ActivityManager.RunningServiceInfo service : manager
						.getRunningServices(Integer.MAX_VALUE)) {
					if ("com.heroapp.scanservice.ScanBGService"
							.equalsIgnoreCase(service.service.getClassName())) {
						Log.e("com.heroapp.scanservice.ScanBGService", "");
						return true;
					}
				}
			}
		} catch (NullPointerException ex) {
		} catch (Exception e) {
		}
		return false;
	}

	/**
	 * BroadcastReceiver to handle the event that come from Hiro.
	 */
	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();

			if (Utils.ACTION_GATT_CONNECTED.equals(action)) {

			} else if (Utils.ACTION_GATT_DISCONNECTED.equals(action)) {

				// getAndStoreLocation();

			} else if (Utils.ACTION_DESCRIPTOR_WRITE.equals(action)) {
			} else if (Utils.ACTION_BATTERY_LEVEL.equals(action)) {

				// mLeDeviceListAdapter.updateBattery(Utils.getBdaForbattery());

			} else if (Utils.ACTION_RSSI.equals(action)) {
				int rssi = intent.getIntExtra("Rssi", 0);
				// Log.e("Average RSSI", "RSSI = " + rssi);
				mLeDeviceListAdapter.updateRssi(Utils.getBda());
			}

		}
	};

	/**
	 * called when Activity is destroy and unregister the broadcast receiver.
	 */
	protected void onDestroy() {
		super.onDestroy();
		try {
			unregisterReceiver(mGattUpdateReceiver);
		} catch (IllegalArgumentException ex) {

		} catch (Exception e) {

		}
	};

	/**
	 * Method is used to pick the Image from File or Capture the image from
	 * camera.
	 */
	public void chooseImage() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Choose Image");
		builder.setAdapter(adapter, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int item) {
				if (item == 0) {
					Utils.makeDirectory(MainActivity.this);
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

					Random random = new Random();
					int no = random.nextInt();

					file = new File(Environment.getExternalStorageDirectory()
							+ File.separator + getString(R.string.FolderPath)
							+ File.separator + no + ".jpg");
					mImageCaptureUri = Uri.fromFile(file);
					intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
							mImageCaptureUri);

					try {
						intent.putExtra("return-data", true);
						startActivityForResult(intent, PICK_FROM_CAMERA);
					} catch (ActivityNotFoundException e) {
						e.printStackTrace();
					}
				} else {
					Utils.makeDirectory(MainActivity.this);

					Random random = new Random();
					int no = random.nextInt();

					file = new File(Environment.getExternalStorageDirectory()
							+ File.separator + getString(R.string.FolderPath)
							+ File.separator + no + ".jpg");

					Intent intent = new Intent();
					intent.setType("image/*");
					intent.setAction(Intent.ACTION_GET_CONTENT);
					startActivityForResult(Intent.createChooser(intent,
							"Complete action using"), PICK_FROM_FILE);
				}
			}
		});

		final AlertDialog dialog_profile = builder.create();
		dialog_profile.show();
	}

	/**
	 * Get the Result of the Pick Image from camera or Gallery.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode != RESULT_OK)
			return;

		switch (requestCode) {
		case PICK_FROM_CAMERA:
			doCrop();
			break;

		case PICK_FROM_FILE:
			mImageCaptureUri = data.getData();

			try {
				if (bitmap != null) {
					bitmap.recycle();
					bitmap = null;
				}
				bitmap = Utils.decodeUri(MainActivity.this, mImageCaptureUri);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			Random random = new Random();
			int no = random.nextInt();

			strImagePath = Environment.getExternalStorageDirectory()
					+ File.separator + getString(R.string.FolderPath)
					+ File.separator + no + ".jpg";

			Utils.saveImageFile(bitmap, strImagePath);

			if (bitmap != null) {
				image.setImageBitmap(bitmap);
				String mac = connectedDeviceList.get(selectedPosition)
						.getDeviceMacAddress();
				connectedDeviceList.get(selectedPosition).setPhotoUrl(
						strImagePath);
				dbhelper.updatePhotoURL(mac, strImagePath);
			}

			break;

		case CROP_FROM_CAMERA:
			Bundle extras = data.getExtras();
			Utils.makeDirectory(MainActivity.this);

			if (extras != null) {
				if (photo != null) {
					photo.recycle();
					photo = null;
				}
				photo = extras.getParcelable("data");

				strImagePath = file.getAbsolutePath();

				Utils.saveImageFile(photo, strImagePath);

				if (photo != null) {
					String mac = connectedDeviceList.get(selectedPosition)
							.getDeviceMacAddress();
					if (mac != null) {
						connectedDeviceList.get(selectedPosition).setPhotoUrl(
								strImagePath);
						ScanBGService.hash_service_connected_device_list.get(
								mac).setPhotoUrl(strImagePath);
						dbhelper.updatePhotoURL(mac, strImagePath);
						image.setImageBitmap(photo);
					}

				}
			}
			break;

		}
	}

	/**
	 * Is used to store the last Location of the Hiro when it disconnect with
	 * phone.
	 * 
	 * @param bluetoothDeviceActor
	 */
	private void getAndStoreLocation(BluetoothDeviceActor lastConnectedBDA) {

		Log.e("MainActivity", "getAndStoreLocation = "
				+ lastConnectedBDA.getDevmodel().getDeviceLogicalName());

		GPSTracker gps = new GPSTracker(MainActivity.this);

		// check if GPS enabled
		if (gps.canGetLocation()) {
			double latitude = gps.getLatitude();
			double longitude = gps.getLongitude();

			// Log.e("LatLong", "Latitude = " + latitude + " And Longitude = "
			// + longitude);

			dbhelper.updateLocation(lastConnectedBDA.getDeviceMacAddress(),
					latitude, longitude);

			lastConnectedBDA.setLatitude(latitude);
			lastConnectedBDA.setLongitude(longitude);
		}

		// checkBDA();

		mLeDeviceListAdapter.updateDisconnectEvent(lastConnectedBDA);

	}

	/**
	 * This Method is used to Crop the Image. If image is large in size then
	 * this is used to crop that image. Image taken from file or from Camera
	 * that will be croped by this method.
	 */
	private void doCrop() {
		final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setType("image/*");
		List<ResolveInfo> list = getPackageManager().queryIntentActivities(
				intent, 0);

		int size = list.size();

		if (size == 0) {
			Utils.showOKAlertMsg(getString(R.string.app_name),
					getString(R.string.Crop_error), MainActivity.this);

			return;
		} else {
			intent.setData(mImageCaptureUri);
			intent.putExtra("outputX", 150);
			intent.putExtra("outputY", 150);
			intent.putExtra("aspectX", 1);
			intent.putExtra("aspectY", 1);
			intent.putExtra("scale", true);
			intent.putExtra("return-data", true);

			if (size == 1) {
				Intent i = new Intent(intent);
				ResolveInfo res = list.get(0);

				i.setComponent(new ComponentName(res.activityInfo.packageName,
						res.activityInfo.name));

				startActivityForResult(i, CROP_FROM_CAMERA);
			} else {
				for (ResolveInfo res : list) {
					final CropOption co = new CropOption();

					co.title = getPackageManager().getApplicationLabel(
							res.activityInfo.applicationInfo);
					co.icon = getPackageManager().getApplicationIcon(
							res.activityInfo.applicationInfo);
					co.appIntent = new Intent(intent);

					co.appIntent
							.setComponent(new ComponentName(
									res.activityInfo.packageName,
									res.activityInfo.name));
					cropOptions.add(co);
				}
				CropOptionAdapter adapter = new CropOptionAdapter(
						getApplicationContext(), cropOptions);

				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("Choose Crop App");
				builder.setAdapter(adapter,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int item) {
								startActivityForResult(
										cropOptions.get(item).appIntent,
										CROP_FROM_CAMERA);
							}
						});

				builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {

						if (mImageCaptureUri != null) {
							getContentResolver().delete(mImageCaptureUri, null,
									null);
							mImageCaptureUri = null;
						}
					}
				});

				AlertDialog alert = builder.create();

				alert.show();
			}
		}

	}

	@Override
	public void disconnectBda(final BluetoothDeviceActor bluetoothDeviceActor) {
		// TODO Auto-generated method stub

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				getAndStoreLocation(bluetoothDeviceActor);
			}
		});

	}

	@Override
	public void updateRSSI(final BluetoothDeviceActor bluetoothDeviceActor) {
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				// mLeDeviceListAdapter.updateRssi(bluetoothDeviceActor);
			}
		});

	}

	@Override
	public void updateBattery(final BluetoothDeviceActor bluetoothDeviceActor) {
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				mLeDeviceListAdapter.updateBattery(bluetoothDeviceActor);
			}
		});

	}

}
