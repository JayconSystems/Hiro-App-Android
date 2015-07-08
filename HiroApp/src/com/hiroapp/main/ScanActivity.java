/**
 * Created by Jaycon Systems on 28/12/14.
 * Copyright @ 2014 Jaycon Systems. All rights reserved.
 */
package com.hiroapp.main;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hiroapp.adapter.LeDeviceListAdapter;
import com.hiroapp.common.BluetoothDeviceActor;
import com.hiroapp.common.ScanDev2;
import com.hiroapp.common.ScanDevices;
import com.hiroapp.common.ScanInterface;
import com.hiroapp.common.Utils;
import com.hiroapp.model.DeviceInfoModel;

public class ScanActivity extends Activity implements OnClickListener,
		ScanInterface {

	LeDeviceListAdapter mLeDeviceListAdapter;
	ArrayList<BluetoothDevice> scanlist;
	ListView lv;
	ProgressBar prb;
	TextView txtscanning;
	ScanDevices scan;
	//para volver como antes descomentar esta line y comentar la siguiente
	//ScanDev2 scan;
	Context context;
	ProgressDialog progdialog;
	BluetoothAdapter adapter;
	
	public static BluetoothDeviceActor BDA;
	public static final int REQUEST_ENABLE_BT = 0001;
	private ImageView imgback;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.scanscreen);
		intiScreen();
		setListener();
		if (getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_BLUETOOTH_LE))
			startscanning();
		else
			Toast.makeText(this, "BLE is not supported on this phone",
					Toast.LENGTH_SHORT).show();

		registerReceiver(mGattUpdateReceiver, Utils.makeIntentFilter());
		
	}

	/**
	 * It will start the scanning for near by Hiro.
	 */
	private void startscanning() {
		// TODO Auto-generated method stub
		if (adapter.isEnabled()) {
			prb.setVisibility(View.VISIBLE);
			scan = new ScanDevices (ScanActivity.this);
			//scan = new ScanDev2(ScanActivity.this);
			scan.startScanning();
		} else {
			prb.setVisibility(View.INVISIBLE);
			Intent enableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);

		}

	}

	/**
	 * Handle the result when device Blue tooth is Enable.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case REQUEST_ENABLE_BT:
			if (resultCode == RESULT_OK) {
				prb.setVisibility(View.VISIBLE);
				scan = new ScanDevices (ScanActivity.this);
				//scan = new ScanDev2(ScanActivity.this);
				scan.startScanning();
				txtscanning.setText("Stop");
			} else {
				txtscanning.setText("Scan");
			}
			break;

		default:
			break;
		}
	}

	/**
	 * define click listener of the objects.
	 */
	private void setListener() {
		// TODO Auto-generated method stub
		txtscanning.setOnClickListener(this);
		imgback.setOnClickListener(this);
	}

	/**
	 * Initialization of the objects;
	 */
	private void intiScreen() {
		// TODO Auto-generated method stub
		context = this;
		scanlist = new ArrayList<BluetoothDevice>();
		adapter = BluetoothAdapter.getDefaultAdapter();
		mLeDeviceListAdapter = new LeDeviceListAdapter(this, scanlist);
		prb = (ProgressBar) findViewById(R.id.progressbar);
		lv = (ListView) findViewById(R.id.lstscanning);
		txtscanning = (TextView) findViewById(R.id.txtscan);
		imgback = (ImageView) findViewById(R.id.imageView1);
		
		lv.setAdapter(mLeDeviceListAdapter);
		lv.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				scan.stopScanning();
				prb.setVisibility(View.INVISIBLE);

				BluetoothDevice btdevice = scanlist.get(position);

				showLogicalNameDialog(btdevice);

			}
		});
	}
	


	@Override
	public void connectedDevice(BluetoothDevice device) {
		// TODO Auto-generated method stub

	}

	/**
	 * Handle click event of the objects.
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.txtscan:
			if (txtscanning.getText().toString().equalsIgnoreCase("Stop")) {
				prb.setVisibility(View.INVISIBLE);
				scan.stopScanning();
				txtscanning.setText("Scan");
			} else {

				if (adapter.isEnabled()) {
					if (scanlist != null && scanlist.size() > 0)
						scanlist.clear();
					mLeDeviceListAdapter.notifyDataSetChanged();
					scan.startScanning();
					prb.setVisibility(View.VISIBLE);
					txtscanning.setText("Stop");

				} else {
					prb.setVisibility(View.INVISIBLE);
					Intent enableIntent = new Intent(
							BluetoothAdapter.ACTION_REQUEST_ENABLE);
					startActivityForResult(enableIntent, REQUEST_ENABLE_BT);

				}
			}
			break;
		default:
			break;
		}
		if (v== imgback)
		{
			scan.stopScanning();
			finish();
		}
	}

	/**
	 * Add device into List when it available into scan.
	 */
	@Override
	public void addDevice(final BluetoothDevice scandevices, final String uuid,
			final String transmissionpower) {
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (mLeDeviceListAdapter != null) {
					try {
						// if (scandevices != null
						// && !scandevices.getName().equalsIgnoreCase("")
						// || !scandevices.getName()
						// .equalsIgnoreCase(null)) {
						mLeDeviceListAdapter.addDeviceTolist(scandevices, uuid,
								transmissionpower);
						mLeDeviceListAdapter.notifyDataSetChanged();
						// }
					} catch (NullPointerException ex) {

					} catch (Exception e) {
					}
				}
			}
		});

	}

	/**
	 * BroadcastReceiver to handle the event that come from Hiro.
	 */
	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();

			if (Utils.ACTION_GATT_CONNECTED.equals(action)) {
				// Intent i = new Intent(ScanActivity.this,
				// BeaconConfigurationScreen.class);
				// startActivity(i);

			} else if (Utils.ACTION_GATT_DISCONNECTED.equals(action)) {

				if (progdialog != null && progdialog.isShowing())
					progdialog.dismiss();

				Toast t = Toast.makeText(ScanActivity.this,
						"Error while connecting Hiro", Toast.LENGTH_SHORT);
				t.show();

			} else if (Utils.ACTION_DESCRIPTOR_WRITE.equals(action)) {
				if (progdialog != null && progdialog.isShowing())
					progdialog.dismiss();

				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {
						finish();

					}
				}, 1000);

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
	 * It will display the Dialog to enter the Logical Name of the Hiro.
	 * 
	 * @param btdevice
	 */

	public void showLogicalNameDialog(final BluetoothDevice btdevice) {
		final Dialog dlg = new Dialog(ScanActivity.this);
		dlg.setContentView(R.layout.dlg_logical_name_layout);
		dlg.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		dlg.setTitle(getResources().getString(R.string.app_name));
		dlg.setCancelable(true);
		Button b1 = (Button) dlg.findViewById(R.id.dlg_logical_name_btn_OK);
		

		dlg.setOnDismissListener(new OnDismissListener() {
			
			EditText et = (EditText) dlg
					.findViewById(R.id.dlg_logical_name_edt_lname);
			
			@Override
			public void onDismiss(DialogInterface dialog) {
				
				et.clearFocus();
				Utils.hidekeyboard(ScanActivity.this);
			}
		});
		b1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dlg.dismiss();
				EditText et = (EditText) dlg
						.findViewById(R.id.dlg_logical_name_edt_lname);
				if (!et.getText().toString().equals("")) {
					progdialog = ProgressDialog.show(ScanActivity.this, "",
							"Please Wait Connecting Device", true);
					progdialog.setCancelable(true);
					DeviceInfoModel devinfo = new DeviceInfoModel();
					devinfo.setDeviceSerialNumber(btdevice.getName());
					devinfo.setDeviceMacAddress(btdevice.getAddress());
					devinfo.setDeviceLogicalName(et.getText().toString().trim());
					BDA = new BluetoothDeviceActor();
					BDA.initialization(ScanActivity.this);
					BDA.setContext(context);
					BDA.connectedDevice(btdevice);
					BDA.setDevmodel(devinfo);
					BDA.setBda(BDA);
					Utils.hidekeyboard(ScanActivity.this);
				} else {
					dlg.dismiss();
					Toast.makeText(ScanActivity.this,
							"Field should not be blank.", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});
		
		EditText et = (EditText) dlg
				.findViewById(R.id.dlg_logical_name_edt_lname);
		
		et.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
					
				if (!hasFocus)
				{
					dlg.dismiss();
				}
			}
		});
		dlg.show();
	}


}
