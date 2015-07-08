/**
 * Created by Jaycon Systems on 18/01/15.
 * Copyright @ 2014 Jaycon Systems. All rights reserved.
 */

package com.hiroapp.adapter;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hiroapp.common.BluetoothDeviceActor;
import com.hiroapp.common.Utils;
import com.hiroapp.dbhelper.DBHelper;
import com.hiroapp.font.OpenSansLight;
import com.hiroapp.main.HeroApp_App;
import com.hiroapp.main.MainActivity;
import com.hiroapp.main.MapActivity;
import com.hiroapp.main.R;
import com.hiroapp.main.SettingsActivity;
import com.hiroapp.model.DeviceInfoModel;

public class ConnectedDeviceListAdapter extends BaseAdapter {

	private LayoutInflater layoutInflater;
	private ArrayList<BluetoothDeviceActor> listData;
	private MainActivity mcontext;
	private ListView listview;
	// private ViewHolder holder;
	// private Timer startbeepTimer;
	// private BeepFinishedTask beepfinishedTask;

	// private ArrayList<BluetoothDeviceActor> beepRunningList = null;

	private DBHelper dbhelper;
	private HeroApp_App appStorage;
	private TextView deviceName;
	private int selectedPosition = -1;
	
	public ConnectedDeviceListAdapter(MainActivity context,
			ArrayList<BluetoothDeviceActor> connectedDeviceList, ListView list) {
		this.listData = connectedDeviceList;
		mcontext = context;
		listview = list;
		layoutInflater = LayoutInflater.from(context);
		// beepRunningList = new ArrayList<BluetoothDeviceActor>();
		appStorage = (HeroApp_App) context.getApplicationContext();
		dbhelper = appStorage.getDbhelper();
	}

	public void addDeviceTolist(final BluetoothDeviceActor Device) {
		if (!listData.contains(Device)) {
			if (listData != null && listData.size() > 0) {
				for (int i = 0; i < listData.size(); i++) {
					BluetoothDeviceActor bda = listData.get(i);
					if (bda.getDevmodel()
							.getDeviceMacAddress()
							.equalsIgnoreCase(
									Device.getDevmodel().getDeviceMacAddress())) {
						listData.remove(bda);
						mcontext.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								notifyDataSetChanged();
							}
						});

					}
				}
			}
			listData.add(Device);
			mcontext.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					notifyDataSetChanged();
				}
			});

		} else {

			if (listData.contains(Device)) {
				mcontext.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						View v2 = null;

						// TODO Auto-generated method stub
						int index = listData.indexOf(Device);
						v2 = listview.getChildAt(index
								- listview.getFirstVisiblePosition());
						ImageView i = (ImageView) v2
								.findViewById(R.id.inflate_device_img_sound);

						i.setImageResource(R.drawable.buzzer_n);
						// i.setClickable(true);

						updateBattery(Device);
						updateRssi(Device);

						// new code 6/18
						ImageView l=(ImageView)v2.findViewById(R.id.inflate_device_img_location);
						l.setImageResource(R.drawable.location_i);
						l.setEnabled(false);
						//----
						
						listview.invalidate();

					}
				});

			}

		}
	}

	public void removeDeviceFromlist(BluetoothDeviceActor Device) {
		if (listData.contains(Device)) {
			int index = listData.indexOf(Device);
			listData.remove(index);
		}
	}

	public void updateRssi(BluetoothDeviceActor Device) {

		
		if (listData.contains(Device)) {
			int index = listData.indexOf(Device);
			BluetoothDeviceActor bda = listData.get(index);
			bda.setAverageRSSI(Device.getAverageRSSI());

			View v = null;
			// for (int i = 0; i < listview.getChildCount(); i++) {
			v = listview.getChildAt(index - listview.getFirstVisiblePosition());
			// }

			if (v != null) {
				ImageView rssi = (ImageView) v
						.findViewById(R.id.inflate_device_img_rssi);
				if (bda.getAverageRSSI() > 95)
					rssi.setImageResource(R.drawable.signal_n0);
				else if (bda.getAverageRSSI() > 85
						&& bda.getAverageRSSI() <= 95)
					rssi.setImageResource(R.drawable.signal_n1);
				else if (bda.getAverageRSSI() > 70
						&& bda.getAverageRSSI() <= 85)
					rssi.setImageResource(R.drawable.signal_n2);
				else
					rssi.setImageResource(R.drawable.signal_n3);

			}

		}
	}

	public void updateBattery(BluetoothDeviceActor Device) {
		if (listData.contains(Device)) {
			int index = listData.indexOf(Device);
			BluetoothDeviceActor bda = listData.get(index);
			bda.setBatterylevel(Device.getBatterylevel());

			View v = listview.getChildAt(index
					- listview.getFirstVisiblePosition());

			if (v != null) {

				ImageView battery = (ImageView) v
						.findViewById(R.id.inflate_device_img_battery);

				if (bda.getBatterylevel() > 80 && bda.getBatterylevel() <= 100)
					battery.setImageResource(R.drawable.battery_n4);
				else if (bda.getBatterylevel() > 60
						&& bda.getBatterylevel() <= 80)
					battery.setImageResource(R.drawable.battery_n3);
				else if (bda.getBatterylevel() > 40
						&& bda.getBatterylevel() <= 60)
					battery.setImageResource(R.drawable.battery_n2);
				else if (bda.getBatterylevel() > 20
						&& bda.getBatterylevel() <= 40)
					battery.setImageResource(R.drawable.battery_n1);
				else if (bda.getBatterylevel() > 1
						&& bda.getBatterylevel() <= 20)
					battery.setImageResource(R.drawable.battery_i);
				else
					battery.setImageResource(R.drawable.battery_n4);
			}
		}
	}

	public void updateDisconnectEvent(BluetoothDeviceActor Device) {
		if (listData.contains(Device)) {
			int index = listData.indexOf(Device);
			View v = listview.getChildAt(index
					- listview.getFirstVisiblePosition());

			if (v != null) {
				ImageView battery = (ImageView) v
						.findViewById(R.id.inflate_device_img_battery);
				battery.setImageResource(R.drawable.battery_i);

				ImageView rssi = (ImageView) v
						.findViewById(R.id.inflate_device_img_rssi);
				rssi.setImageResource(R.drawable.signal_i);

				ImageView sound = (ImageView) v
						.findViewById(R.id.inflate_device_img_sound);
				sound.setImageResource(R.drawable.buzzer_i);
				// sound.setClickable(false);

				ImageView settings = (ImageView) v
						.findViewById(R.id.inflate_device_img_settings);
				
				// new code 6-18
				
				ImageView location = (ImageView) v
						.findViewById(R.id.inflate_device_img_location);
				location.setImageResource(R.drawable.location_n);
				location.setEnabled(true);
				
				
				//----

				settings.setImageResource(R.drawable.settings_n);
				settings.setClickable(true);
				listview.invalidate();
			}
		}

	}

	@Override
	public int getCount() {
		return listData.size();
	}

	@Override
	public Object getItem(int position) {
		return listData.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub

		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder holder;
		if (convertView == null) {

			convertView = layoutInflater.inflate(R.layout.inflate_devicelist,
					null);
			holder = new ViewHolder();
			holder.deviceName = (OpenSansLight) convertView
					.findViewById(R.id.inflate_device_txt_Name);
			holder.imgrssi = (ImageView) convertView
					.findViewById(R.id.inflate_device_img_rssi);
			holder.imgmain = (ImageView) convertView
					.findViewById(R.id.inflate_device_imageMain);
			holder.imgsound = (ImageView) convertView
					.findViewById(R.id.inflate_device_img_sound);
			holder.imglocation = (ImageView) convertView
					.findViewById(R.id.inflate_device_img_location);
			holder.imgsettings = (ImageView) convertView
					.findViewById(R.id.inflate_device_img_settings);
			holder.imgbattery = (ImageView) convertView
					.findViewById(R.id.inflate_device_img_battery);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// final BluetoothDeviceActor bda = listData.get(position);

		holder.deviceName.setText(listData.get(position).getDevmodel()
				.getDeviceLogicalName());
		if (listData.get(position).isConnected()) {
			if (listData.get(position).getBatterylevel() > 80
					&& listData.get(position).getBatterylevel() <= 100)
				holder.imgbattery.setImageResource(R.drawable.battery_n4);
			else if (listData.get(position).getBatterylevel() > 60
					&& listData.get(position).getBatterylevel() <= 80)
				holder.imgbattery.setImageResource(R.drawable.battery_n3);
			else if (listData.get(position).getBatterylevel() > 40
					&& listData.get(position).getBatterylevel() <= 60)
				holder.imgbattery.setImageResource(R.drawable.battery_n2);
			else if (listData.get(position).getBatterylevel() > 20
					&& listData.get(position).getBatterylevel() <= 40)
				holder.imgbattery.setImageResource(R.drawable.battery_n1);
			else if (listData.get(position).getBatterylevel() > 1
					&& listData.get(position).getBatterylevel() <= 20)
				holder.imgbattery.setImageResource(R.drawable.battery_i);
			else
				holder.imgbattery.setImageResource(R.drawable.battery_n4);

			if (listData.get(position).getAverageRSSI() > 80
					&& listData.get(position).getAverageRSSI() <= 100)
				holder.imgrssi.setImageResource(R.drawable.signal_i);
			else if (listData.get(position).getAverageRSSI() > 65
					&& listData.get(position).getAverageRSSI() <= 80)
				holder.imgrssi.setImageResource(R.drawable.signal_n0);
			else if (listData.get(position).getAverageRSSI() > 50
					&& listData.get(position).getAverageRSSI() <= 65)
				holder.imgrssi.setImageResource(R.drawable.signal_n1);
			else if (listData.get(position).getAverageRSSI() > 40
					&& listData.get(position).getAverageRSSI() <= 50)
				holder.imgrssi.setImageResource(R.drawable.signal_n2);
			else if (listData.get(position).getAverageRSSI() > 30
					&& listData.get(position).getAverageRSSI() <= 40)
				holder.imgrssi.setImageResource(R.drawable.signal_n3);
			else
				holder.imgrssi.setImageResource(R.drawable.signal_n3);

		}

		if (!listData.get(position).isConnected()) {
			holder.imgsound.setImageResource(R.drawable.buzzer_i);
//			holder.imgsound.setEnabled(false);

			holder.imgbattery.setImageResource(R.drawable.battery_i);

			holder.imgrssi.setImageResource(R.drawable.signal_i);
			// 6/18 enable location
			holder.imglocation.setImageResource(R.drawable.location_n);
			holder.imglocation.setEnabled(true);
			
			holder.imgsettings.setImageResource(R.drawable.settings_n);
//			holder.imgsettings.setEnabled(true);

		} else {
			holder.imgsound.setImageResource(R.drawable.buzzer_n);
//			holder.imgsound.setEnabled(true);
			holder.imgsettings.setImageResource(R.drawable.settings_n);
			
			// 6/18 disable location			
			holder.imglocation.setImageResource(R.drawable.location_i);
			holder.imglocation.setEnabled(false);
//			holder.imgsettings.setEnabled(true);
		}

		if (listData.get(position) != null
				&& listData.get(position).getDevmodel().getDevicePhotoUrl() != null
				&& !listData.get(position).getDevmodel().getDevicePhotoUrl()
						.equalsIgnoreCase("")) {

			final Bitmap bitmap1 = BitmapFactory.decodeFile(listData
					.get(position).getDevmodel().getDevicePhotoUrl());

			if (bitmap1 != null) {
				holder.imgmain.setImageBitmap(bitmap1);
			}

		}
		
		holder.deviceName.setOnClickListener(new OnClickListener(){
			
			@Override
			public void onClick(View v) {
				
				if (listData.get(position).isConnected()) {
				
				// TODO Auto-generated method stubE
				
				changeLogicalName(listData.get(position).getDeviceMacAddress().toString());
				}
			}

		});
		
		
		holder.imgsound.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (listData.get(position).isConnected()) {

					if (!listData.get(position).isBeepRinging) {

						holder.imgsound.setImageResource(R.drawable.buzzer_a2);

						if (getBeepVolume(listData.get(position)
								.getDeviceMacAddress())) {

							listData.get(position)
									.deviceIsReadyForCommunication("HiroAlert",
											2, "");
						} else {
							listData.get(position)
									.deviceIsReadyForCommunication("HiroAlert",
											1, "");
						}
						listData.get(position).isBeepRinging = true;
					} else {
						
						listData.get(position).isBeepRinging = false;
						holder.imgsound.setImageResource(R.drawable.buzzer_n);
						listData.get(position).deviceIsReadyForCommunication(
								"HiroAlert", 0, "");
					}
				}

			}
		});

		holder.imgsettings.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MainActivity.BDA = listData.get(position);
				Intent i = new Intent(mcontext, SettingsActivity.class);
				i.putExtra("position", position);
				mcontext.startActivity(i);

			}
		});

		holder.imglocation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(mcontext, MapActivity.class);
				i.putExtra("latitude", listData.get(position).getLatitude());
				i.putExtra("longitude", listData.get(position).getLongitude());
				i.putExtra("title", listData.get(position).getDevmodel()
						.getDeviceLogicalName());
				mcontext.startActivity(i);

			}
		});

		return convertView;
	}
	
	/**
	 * Changes Hiro's name
	 */
	protected void changeLogicalName(final String mac) {

		// TODO Auto-generated method stub
		
		final Dialog dlg = new Dialog(MainActivity.instance);
		dlg.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		dlg.setContentView(R.layout.dlg_logical_name_layout);
		dlg.setTitle(MainActivity.instance.getResources().getString(R.string.app_name));
		dlg.setCancelable(true);
		
		Button b1 = (Button) dlg.findViewById(R.id.dlg_logical_name_btn_OK);
		
		deviceName = (TextView) MainActivity.instance.findViewById(R.id.inflate_device_txt_Name);
		

		dlg.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				// TODO Auto-generated method stub
				deviceName.clearFocus();
				Utils.hidekeyboard(MainActivity.instance);
			}
		});		
		
		b1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dlg.dismiss();
				EditText et = (EditText) dlg
						.findViewById(R.id.dlg_logical_name_edt_lname);
				if (!et.getText().toString().equals("")) {
					
					dbhelper.updateLogicalName(mac, et.getText().toString());
					MainActivity.instance.initScreen();
					// Utils.hidekeyboard(instance);
				
				} else {
					dlg.dismiss();
					Toast.makeText(MainActivity.instance,
							"Field should not be blank.", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});
		
		deviceName = (TextView) MainActivity.instance.findViewById(R.id.inflate_device_txt_Name);
		
		deviceName.setOnFocusChangeListener(new OnFocusChangeListener() {
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

	static class ViewHolder {
		OpenSansLight deviceName;
		ImageView imgrssi;
		ImageView imgsound;
		ImageView imgsettings;
		ImageView imglocation;
		ImageView imgmain;
		ImageView imgbattery;
	}
	
	
	/**
	 * Changes Hiro's name
	 */
	
	/*
	private void changeLogicalName() {
		
		
		final Dialog dlg = new Dialog(MainActivity.instance);	
	
		dlg.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		
		dlg.setContentView(R.layout.dlg_logical_name_layout);
		
		dlg.setTitle(MainActivity.instance.getResources().getString(R.string.app_name));
		
		dlg.setCancelable(true);
		
		Button b1 = (Button) dlg.findViewById(R.id.dlg_logical_name_btn_OK);
		
		EditText et = (EditText) dlg
				.findViewById(R.id.dlg_logical_name_edt_lname); 

		dlg.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				// TODO Auto-generated method stub
				deviceName.clearFocus();
				Utils.hidekeyboard(MainActivity.instance);
			}
		});		
		
		b1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dlg.dismiss();
				EditText et = (EditText) dlg
						.findViewById(R.id.dlg_logical_name_edt_lname);
				if (!et.getText().toString().equals("")) {
					dbhelper.updateLogicalName( et.getText().toString());
					deviceName.setText(et.getText().toString());
					 Utils.hidekeyboard(MainActivity.instance);
				
				} else {
					dlg.dismiss();
					Toast.makeText(MainActivity.instance,
							"Field should not be blank.", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});
		
		deviceName.setOnFocusChangeListener(new OnFocusChangeListener() {
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
*/
	public boolean getBeepVolume(String mac) {

		boolean isbeepVolume = true;
		DeviceInfoModel model = dbhelper.getdeviceSettings(mac);

		if (model.isHiroBeepVolume() == 1)
			isbeepVolume = true;
		else
			isbeepVolume = false;

		return isbeepVolume;
	}
}
