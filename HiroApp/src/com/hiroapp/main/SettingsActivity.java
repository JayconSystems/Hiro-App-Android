/**
 * Created by Jaycon Systems on 31/12/14.
 * Copyright @ 2014 Jaycon Systems. All rights reserved.
 */

package com.hiroapp.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.hiroapp.common.BluetoothDeviceActor;
import com.hiroapp.common.Utils;
import com.hiroapp.dbhelper.DBHelper;
import com.hiroapp.font.OpenSansLight;
import com.hiroapp.model.DeviceInfoModel;
import com.hiroapp.scanservice.ScanBGService;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.BLUE;
import static android.graphics.Color.GRAY;
import static android.graphics.Color.GREEN;
import static android.widget.Toast.*;

public class SettingsActivity extends Activity implements OnClickListener {

	private ImageView swbattery;
	private ImageView swHeroAlert;
	private ImageView swBeep;
	// private ImageView swWifi;
	private CheckBox chkNotification;
	private CheckBox chkSoundAlert;
	private CheckBox chkherobeepAlert;

	private ImageView imgback;

	private ImageView imginfo;
	private SharedPreferences preferences;

	private OpenSansLight txtDisconnectRingtone;
	private OpenSansLight txtPhoneRing;
	private OpenSansLight txtDelete;
	// private OpenSansLight txtWifiName;
	// private OpenSansLight txtSave;

	private static DBHelper dbhelper;
	private HeroApp_App appStorage;

	private String macAddress;

	private DeviceInfoModel model;

	private int position;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);

		initialization();
		setListener();

		if (MainActivity.BDA != null)
			macAddress = MainActivity.BDA.getDeviceMacAddress();

		position = getIntent().getIntExtra("position", 0);

		// method is used to update the UI.
		UpdateUi();

		swBeep.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (model.isHiroBeepVolume() == 1) {
					if (MainActivity.BDA.isConnected()) {
						dbhelper.updateDeviceInfo(macAddress, "HiroBeepVolume",
								0);
						model.setHiroBeepVolume(0);
						swBeep.setImageResource(R.drawable.sw_mild);
					}
				} else {
					if (MainActivity.BDA.isConnected()) {
						dbhelper.updateDeviceInfo(macAddress, "HiroBeepVolume",
								1);
						model.setHiroBeepVolume(1);
						swBeep.setImageResource(R.drawable.sw_high);
					}
				}
			}
		});

		swbattery.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (model.isBatteryIndication() == 1) {
					dbhelper.updateDeviceInfo(macAddress, "BatteryIndication",
							0);
					model.setBatteryIndication(0);
					swbattery.setImageResource(R.drawable.sw_disable);
				} else {
					dbhelper.updateDeviceInfo(macAddress, "BatteryIndication",
							1);
					model.setBatteryIndication(1);
					swbattery.setImageResource(R.drawable.sw_enable);
				}
			}
		});

		swHeroAlert.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (model.isHiroDisconnectBeep() == 1) {
					if (MainActivity.BDA.isConnected()) {
						MainActivity.BDA.deviceIsReadyForCommunication(
								"LinkLossAlert", 1, "Write");

						dbhelper.updateDeviceInfo(macAddress,
								"HiroDisconnectBeep", 0);
						model.setHiroDisconnectBeep(0);
						swHeroAlert.setImageResource(R.drawable.sw_mild);
					}
				} else if(model.isHiroDisconnectBeep() == 0
						|| model.isHiroDisconnectBeep() == -1) {
					if (MainActivity.BDA.isConnected()) {
						MainActivity.BDA.deviceIsReadyForCommunication(
								"LinkLossAlert", 2, "Write");
						dbhelper.updateDeviceInfo(macAddress,
								"HiroDisconnectBeep", 1);
						model.setHiroDisconnectBeep(1);
						swHeroAlert.setImageResource(R.drawable.sw_high);
					}

				}
			}
		});

		chkNotification
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							dbhelper.updateDeviceInfo(macAddress,
                                    "NotificationIndication", 1);
                        } else {
							dbhelper.updateDeviceInfo(macAddress,
                                    "NotificationIndication", 0);
													}
					}
				});

		chkSoundAlert.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					dbhelper.updateDeviceInfo(macAddress,
							"NotificationDisconnectAlert", 1);
                    txtDisconnectRingtone.setTextColor(BLACK);
				} else {
					dbhelper.updateDeviceInfo(macAddress,
							"NotificationDisconnectAlert", 0);
                    txtDisconnectRingtone.setTextColor(GRAY);
				}
			}
		});

		chkherobeepAlert
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							dbhelper.updateDeviceInfo(macAddress,
									"HiroDisBeepAlert", 1);
							swHeroAlert.setEnabled(true);

							if (MainActivity.BDA.isConnected()) {

								if (model.isHiroDisconnectBeep() == 1) {
									swHeroAlert
											.setImageResource(R.drawable.sw_high);
									MainActivity.BDA
											.deviceIsReadyForCommunication(
													"LinkLossAlert", 2, "Write");
								} else if(model.isHiroDisconnectBeep() == 0 || model.isHiroDisconnectBeep() == -1){
									swHeroAlert
											.setImageResource(R.drawable.sw_mild);
									MainActivity.BDA
											.deviceIsReadyForCommunication(
													"LinkLossAlert", 1, "Write");
								}
							}
						} else {
							 dbhelper.updateDeviceInfo(macAddress,
							 "HiroDisBeepAlert", 0);
							swHeroAlert.setEnabled(false);

							if (MainActivity.BDA.isConnected()) {
								MainActivity.BDA.deviceIsReadyForCommunication(
										"LinkLossAlert", 0, "Write");
							}

						}
					}
				});
	}

	/**
	 * Method is used to update the Setting UI.
	 */
	private void UpdateUi() {
		// TODO Auto-generated method stub

		if (macAddress != null && !macAddress.equalsIgnoreCase("")) {

			model = dbhelper.getdeviceSettings(macAddress);

			if (model != null) {

				if (model.isBatteryIndication() == 1)
					swbattery.setImageResource(R.drawable.sw_enable);
				else
					swbattery.setImageResource(R.drawable.sw_disable);

				if (model.isNotificationIndication() == 1)
					chkNotification.setChecked(true);

				else
					chkNotification.setChecked(false);

				if (model.isNotificationDisconnectAlert() == 1)
                {
                    chkSoundAlert.setChecked(true);
                txtDisconnectRingtone.setTextColor(BLACK);
                }
				else
                {
					chkSoundAlert.setChecked(false);
                txtDisconnectRingtone.setTextColor(GRAY);
                }

                if (model.isHiroDisBeepAlert() != 1) {
                    chkherobeepAlert.setChecked(false);
                    swHeroAlert.setEnabled(false);
                } else {
					chkherobeepAlert.setChecked(true);
					swHeroAlert.setEnabled(true);
				}
                if (model.isHiroDisconnectBeep() == 1) {
					swHeroAlert.setImageResource(R.drawable.sw_high);
					if (MainActivity.BDA.isConnected()) {
						MainActivity.BDA.deviceIsReadyForCommunication(
								"LinkLossAlert", 2, "Write");
					}
				} else if (model.isHiroDisconnectBeep() == 0) {
					swHeroAlert.setImageResource(R.drawable.sw_mild);
					if (MainActivity.BDA.isConnected()) {
						MainActivity.BDA.deviceIsReadyForCommunication(
								"LinkLossAlert", 1, "Write");
					}
				} else if (model.isHiroDisconnectBeep() == -1) {
					swHeroAlert.setImageResource(R.drawable.sw_mild);
				}

				if (model.isHiroBeepVolume() == 1)
                    swBeep.setImageResource(R.drawable.sw_high);
				else
					swBeep.setImageResource(R.drawable.sw_mild);

				setRingname(model);

			}
		}

		// String ssid = getCurrentSsid(SettingsActivity.this);
		// txtWifiName.setText(ssid);

	}

	/**
	 * Initialization of the objects;
	 */
	private void initialization() {

		imginfo = (ImageView) findViewById(R.id.Settings_img_info);
		imgback = (ImageView) findViewById(R.id.imageView1);

		swbattery = (ImageView) findViewById(R.id.settings_sw_battery);
		swHeroAlert = (ImageView) findViewById(R.id.settings_sw_heroalert);
		swBeep = (ImageView) findViewById(R.id.settings_sw_beepvolume);
		// swWifi = (ImageView) findViewById(R.id.settings_sw_wifimode);

		chkNotification = (CheckBox) findViewById(R.id.settings_chk_pushnotification);
		chkSoundAlert = (CheckBox) findViewById(R.id.settings_chk_phonesound);
		chkherobeepAlert = (CheckBox) findViewById(R.id.settings_chk_heroalert);
		preferences = this.getSharedPreferences(getString(R.string.app_name),
				Activity.MODE_WORLD_WRITEABLE);

		txtDisconnectRingtone = (OpenSansLight) findViewById(R.id.Settings_txt_defaultring);

		txtPhoneRing = (OpenSansLight) findViewById(R.id.Settings_txt_defaultring_for_phone);
		txtDelete = (OpenSansLight) findViewById(R.id.delete);
		// txtWifiName = (OpenSansLight)
		// findViewById(R.id.settings_txt_wifiName);
		// txtSave = (OpenSansLight) findViewById(R.id.settings_txt_Save);

		appStorage = (HeroApp_App) this.getApplicationContext();
		dbhelper = appStorage.getDbhelper();

	}


    /**
	 * define click listener of the objects.
	 */
	private void setListener() {
		// TODO Auto-generated method stub

		imginfo.setOnClickListener(this);
		txtDisconnectRingtone.setOnClickListener(this);
		txtPhoneRing.setOnClickListener(this);
		imgback.setOnClickListener(this);
		txtDelete.setOnClickListener(this);
	}

	/**
	 * Handle click event of the objects.
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		if (v == imginfo) {

			Intent i = new Intent(SettingsActivity.this, InfoActivity.class);
			startActivity(i);

		} else {
            if (v == txtDisconnectRingtone && chkSoundAlert.isChecked()) {

                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE,
                        RingtoneManager.TYPE_NOTIFICATION);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
                String disRing = model.getDisconnectRing();
                if (disRing == null || disRing.equalsIgnoreCase("")) {
                    intent.putExtra(
                            RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,
                            RingtoneManager
                                    .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                } else {
                    Uri uri = Uri.parse(model.getDisconnectRing());
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,
                            uri);
                }
                this.startActivityForResult(intent, 5);
            }else if (v == txtPhoneRing) {
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE,
                        RingtoneManager.TYPE_ALL);
                String phoneRing = model.getPhoneRing();
                if (phoneRing == null || phoneRing.equalsIgnoreCase("")) {
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,
                            RingtoneManager
                                    .getDefaultUri(RingtoneManager.TYPE_RINGTONE));
                } else {
                    Uri uri = Uri.parse(model.getPhoneRing());
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,
                            uri);
                }
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
                this.startActivityForResult(intent, 6);
            } else if (v == imgback) {
                finish();
            } else if (v == txtDelete) {
                openWarningDialog(position);
            }
        }

	}

	/**
	 * Set the Ring tone that is selected for the disconnect Hiro or Find phone
	 * through.
	 * 
	 * @param model
	 */
	private void setRingname(DeviceInfoModel model) {

		String phoneRing = preferences.getString("PhoneRing", "");

		String disRing = model.getDisconnectRing();

		if (disRing != null && !disRing.equalsIgnoreCase("")) {
			Uri uri = Uri.parse(disRing);
			Ringtone ringtone = RingtoneManager.getRingtone(
					SettingsActivity.this, uri);
			String name = ringtone.getTitle(SettingsActivity.this);
			if (name != null && !name.equalsIgnoreCase(""))
				txtDisconnectRingtone.setText(name);
		}

		if (phoneRing != null && !phoneRing.equalsIgnoreCase("")) {
			Uri uri1 = Uri.parse(phoneRing);
			Ringtone ringtone1 = RingtoneManager.getRingtone(
					SettingsActivity.this, uri1);
			String name1 = ringtone1.getTitle(SettingsActivity.this);
			txtPhoneRing.setText(name1);
		}

	}

	/**
	 * Get the Result of selected ring tone for disconnect Hiro or find phone
	 * through.
	 */
	@Override
	protected void onActivityResult(final int requestCode,
			final int resultCode, final Intent intent) {
		if (resultCode == Activity.RESULT_OK && requestCode == 5) {
			Uri uri = intent
					.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

			if (uri != null) {
				String chosenRingtone = uri.toString();
				dbhelper.updateDeviceRingInfo(macAddress, "DisconnectRing",
						chosenRingtone);
				model.setDisconnectRing(chosenRingtone);
			}

			setRingname(model);

		} else if (resultCode == Activity.RESULT_OK && requestCode == 6) {
			Uri uri = intent
					.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

			if (uri != null) {
				String chosenRingtone = uri.toString();

				SharedPreferences.Editor editor1 = preferences.edit();
				editor1.putString("PhoneRing", chosenRingtone);
				editor1.commit();
			}
			setRingname(model);
		}
	}

	/**
	 * Open warning dialog while delete the Hiro.
	 * 
	 * @param position
	 */
	private void openWarningDialog(final int position) {
		// TODO Auto-generated method stub
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();

						int id = dbhelper.getnotificationId(macAddress);
						Utils.removeNotification(MainActivity.mcontext, id);
						dbhelper.removeDevice(macAddress);

						if (ScanBGService.hash_service_connected_device_list != null
								& ScanBGService.hash_service_connected_device_list
										.size() > 0) {

							if (ScanBGService.hash_service_connected_device_list
									.containsKey(macAddress)) {

								BluetoothDeviceActor bda = ScanBGService.hash_service_connected_device_list
										.get(macAddress);

								if (bda.isConnected())
									bda.getmBluetoothGatt().disconnect();

								ScanBGService.hash_service_connected_device_list
										.remove(macAddress);
							}
						}

						if (ScanBGService.device_list != null
								&& ScanBGService.device_list
										.containsKey(macAddress))
							ScanBGService.device_list.remove(macAddress);

						if (MainActivity.BDA != null
								&& MainActivity.BDA.isConnected())
							MainActivity.BDA.getmBluetoothGatt().disconnect();

						MainActivity.connectedDeviceList.remove(position);

						MainActivity.mLeDeviceListAdapter
								.notifyDataSetChanged();

						finish();
					}
				});
		dialogBuilder.setNegativeButton("No",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		dialogBuilder
				.setTitle(this.getResources().getString(R.string.app_name));
		dialogBuilder.setMessage("Are you sure you want to delete?");
		dialogBuilder.show();
	}

	// get the current wifi SSID

	// public static String getCurrentSsid(Context context) {
	// String ssid = null;
	// ConnectivityManager connManager = (ConnectivityManager) context
	// .getSystemService(Context.CONNECTIVITY_SERVICE);
	// NetworkInfo networkInfo = connManager
	// .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
	// if (networkInfo.isConnected()) {
	// final WifiManager wifiManager = (WifiManager) context
	// .getSystemService(Context.WIFI_SERVICE);
	// final WifiInfo connectionInfo = wifiManager.getConnectionInfo();
	// if (connectionInfo != null
	// && !TextUtils.isEmpty(connectionInfo.getSSID())) {
	// ssid = connectionInfo.getSSID();
	// }
	// }
	//
	// Log.e("SSID", "SSID = " + ssid);
	//
	// return ssid;
	// }

}
