/**
 * Created by Jaycon Systems on 04/01/15.
 * Copyright @ 2014 Jaycon Systems. All rights reserved.
 */

package com.hiroapp.dbhelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.UUID;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.gms.internal.is;
import com.hiroapp.main.R;
import com.hiroapp.model.CharacteristicsModel;
import com.hiroapp.model.DeviceInfoModel;
import com.hiroapp.model.OperationModel;

public class DBHelper extends SQLiteOpenHelper {

	private static Context context;
	private static SQLiteDatabase db;
	private final static int DB_VERSION = 1;
	private static String tbl_deviceinfo = "DeviceInfo";
	private static String tbl_services = "Services";
	private static String tbl_characteristic = "Characteristics";
	private static String tbl_operationType = "OperationType";

	private final static String TAG = DBHelper.class.getSimpleName();

	public DBHelper(Context mcontext) {
		super(mcontext, mcontext.getResources().getString(R.string.DB_NAME),
				null, DB_VERSION);
		context = mcontext;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	/**
	 * Function is used to create a Database.
	 * 
	 * @throws java.io.IOException
	 */
	public void createDataBase() throws IOException {
		// ---Check whether database is already created or not---
		boolean dbExist = checkDataBase();

		if (!dbExist) {
			this.getReadableDatabase();
			try {
				// ---If not created then copy the database---
				copyDataBase();
				this.close();
			} catch (IOException e) {
				throw new Error("Error copying database");
			}
			this.getWritableDatabase();
		} else {
			this.getWritableDatabase();
		}
	}

	/**
	 * Copy the database to the output stream
	 * 
	 * @throws java.io.IOException
	 */
	private void copyDataBase() throws IOException {
		InputStream myInput = context.getAssets().open(
				context.getString(R.string.DB_NAME));
		String outFileName = context.getString(R.string.DB_PATH)
				+ context.getString(R.string.DB_NAME);
		OutputStream myOutput = new FileOutputStream(outFileName);
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}
		myOutput.flush();
		myOutput.close();
		myInput.close();
	}

	/**
	 * Open the database
	 * 
	 * @throws android.database.SQLException
	 */
	public void openDataBase() throws SQLException {
		// --- Open the database---
		String myPath = context.getString(R.string.DB_PATH)
				+ context.getString(R.string.DB_NAME);
		db = SQLiteDatabase.openDatabase(myPath, null,
				SQLiteDatabase.OPEN_READWRITE);

	}

	/**
	 * Check whether database already created or not
	 * 
	 * @return boolean
	 */
	private boolean checkDataBase() {
		try {
			String myPath = context.getString(R.string.DB_PATH)
					+ context.getString(R.string.DB_NAME);
			File f = new File(myPath);
			if (f.exists())
				return true;
			else
				return false;
		} catch (SQLiteException e) {
			e.printStackTrace();
			return false;
		}
	}

	// get Service UUID
	public int getServiceIdFromUUIDAndDeviceId(UUID uuid, int deviceId) {

		int id = -1;
		Cursor cursor = null;

		cursor = db.query(tbl_services, null, "ServiceUUID='" + uuid + "'",
				null, null, null, null);

		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			id = cursor.getInt(cursor.getColumnIndex("ID"));

		}

		if (cursor != null)
			cursor.close();

		return id;
	}

	// get Characteristic of service.
	public Hashtable<String, CharacteristicsModel> getCharacteristicsOfService(
			int serviceId, int deviceId) {

		Hashtable<String, CharacteristicsModel> table = new Hashtable<String, CharacteristicsModel>();

		CharacteristicsModel model = null;
		Cursor cursor = null;

		cursor = db.query(tbl_characteristic, null, "ServiceId=" + serviceId,
				null, null, null, null);

		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();

			for (int i = 0; i < cursor.getCount(); i++) {

				model = new CharacteristicsModel();
				model.setId(cursor.getInt(cursor.getColumnIndex("ID")));
				model.setCharName(cursor.getString(cursor
						.getColumnIndex("CharName")));
				model.setCharUUID(cursor.getString(
						cursor.getColumnIndex("CharUUID")).trim());
				model.setServiceId(cursor.getInt(cursor
						.getColumnIndex("ServiceId")));
				model.setDeviceId(cursor.getInt(cursor
						.getColumnIndex("DeviceId")));
				model.setObservable(cursor.getInt(cursor
						.getColumnIndex("IsObservable")));

				table.put(model.getCharUUID(), model);
				cursor.moveToNext();

			}

		}

		if (cursor != null)
			cursor.close();

		return table;
	}

	public ArrayList<OperationModel> performOperation(String command) {

		ArrayList<OperationModel> list = new ArrayList<OperationModel>();

		int operationId = -1;
		Cursor cursor = null;

		cursor = db.query(tbl_operationType, null, "OperationName='" + command
				+ "' AND DeviceId=1", null, null, null, null);

		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			operationId = cursor.getInt(cursor.getColumnIndex("ID"));

		}
		if (cursor != null)
			cursor.close();

		list = getOperationSequenceData(operationId, command);

		return list;

	}

	public ArrayList<OperationModel> getOperationSequenceData(int operationId,
			String command) {

		OperationModel model = null;
		ArrayList<OperationModel> list = new ArrayList<OperationModel>();

		String query = "select OS.ID,OS.OperationId,OS.CharId,OS.Operation,OS.Value,OS.ExpectedValue,CH.CharUUID,"
				+ "CH.ServiceId,CH.CharName,CH.CharReturnType,SE.ServiceUUID,OT.OperationName from OperationSequence OS, Characteristics CH, Services SE,"
				+ "OperationType OT where OS.OperationId="
				+ operationId
				+ " and CH.ID=OS.CharId and SE.ID=CH.ServiceId and OS.OperationId = OT.ID";

		// Log.e("Query", "Query == " + query);

		Cursor cursor = db.rawQuery(query, null);

		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();

			for (int i = 0; i < cursor.getCount(); i++) {

				model = new OperationModel();
				model.setId(cursor.getInt(cursor.getColumnIndex("ID")));
				model.setCharRefId(cursor.getInt(cursor
						.getColumnIndex("CharId")));
				model.setCharUUID(cursor.getString(cursor
						.getColumnIndex("CharUUID")));
				model.setOperation(cursor.getString(cursor
						.getColumnIndex("Operation")));
				model.setOperationRefId(cursor.getInt(cursor
						.getColumnIndex("OperationId")));
				model.setServiceUUID(cursor.getString(cursor
						.getColumnIndex("ServiceUUID")));
				model.setOperationName(cursor.getString(cursor
						.getColumnIndex("OperationName")));
				model.setCharName(cursor.getString(cursor
						.getColumnIndex("CharName")));
				model.setCharReturnType(cursor.getString(cursor
						.getColumnIndex("CharReturnType")));
				model.setExpectedValue(cursor.getString(cursor
						.getColumnIndex("ExpectedValue")));
				model.setValue(cursor.getString(cursor.getColumnIndex("Value")));

				list.add(model);
				cursor.moveToNext();

			}
		}

		if (cursor != null)
			cursor.close();

		return list;

	}

	public int insertDeviceInfo(DeviceInfoModel deviceInfo) {
		int id = 0;
		ContentValues devval = new ContentValues();
		devval.put("DeviceSerialNumber", deviceInfo.getDeviceSerialNumber());
		devval.put("DeviceMacAddress", deviceInfo.getDeviceMacAddress());
		devval.put("DeviceLogicalName", deviceInfo.getDeviceLogicalName());
		// Log.e("insertCommandInfo", "Insert Fresh Command to DB");
		id = (int) db.insert(tbl_deviceinfo, null, devval);
		return id;
	}

	public boolean checkIfDeviceExists(DeviceInfoModel model) {
		Cursor cursor = null;

		cursor = db.query(tbl_deviceinfo, null,
				"DeviceMacAddress='" + model.getDeviceMacAddress() + "'", null,
				null, null, null);

		if (cursor != null && cursor.getCount() > 0) {
			if (cursor != null)
				cursor.close();
			return true;
		}
		if (cursor != null)
			cursor.close();

		return false;
	}

	public boolean checkIfDeviceCount() {
		Cursor cursor = null;

		cursor = db.query(tbl_deviceinfo, null, null, null, null, null, null);

		if (cursor != null && cursor.getCount() > 0) {
			if (cursor != null)
				cursor.close();
			return true;
		}
		if (cursor != null)
			cursor.close();

		return false;
	}

	public LinkedHashMap<String, DeviceInfoModel> getDeviceListFromDB() {
		LinkedHashMap<String, DeviceInfoModel> table = new LinkedHashMap<String, DeviceInfoModel>();

		DeviceInfoModel model = null;
		Cursor cursor = null;

		cursor = db.query(tbl_deviceinfo, null, null, null, null, null, null);

		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();

			for (int i = 0; i < cursor.getCount(); i++) {

				model = new DeviceInfoModel();
				model.setDeviceSerialNumber(cursor.getString(cursor
						.getColumnIndex("DeviceSerialNumber")));
				model.setDeviceMacAddress(cursor.getString(cursor
						.getColumnIndex("DeviceMacAddress")));
				model.setDeviceLogicalName(cursor.getString(cursor
						.getColumnIndex("DeviceLogicalName")));
				model.setDevicePhotoUrl(cursor.getString(cursor
						.getColumnIndex("DevicePhotoUrl")));
				model.setId(cursor.getInt(cursor.getColumnIndex("ID")));
				model.setLatitude(cursor.getDouble(cursor
						.getColumnIndex("Latitude")));
				model.setLongitude(cursor.getDouble(cursor
						.getColumnIndex("Longitude")));
				model.setBatteryIndication(cursor.getInt(cursor
						.getColumnIndex("BatteryIndication")));
				model.setDisconnectRing(cursor.getString(cursor
						.getColumnIndex("DisconnectRing")));
				model.setHiroBeepVolume(cursor.getInt(cursor
						.getColumnIndex("HiroBeepVolume")));
				model.setHiroDisBeepAlert(cursor.getInt(cursor
						.getColumnIndex("HiroDisBeepAlert")));
				model.setHiroDisconnectBeep(cursor.getInt(cursor
						.getColumnIndex("HiroDisconnectBeep")));
				model.setNotificationDisconnectAlert(cursor.getInt(cursor
						.getColumnIndex("NotificationDisconnectAlert")));
				model.setNotificationIndication(cursor.getInt(cursor
						.getColumnIndex("NotificationIndication")));
				model.setPhoneRing(cursor.getString(cursor
						.getColumnIndex("PhoneRing")));

				table.put(model.getDeviceMacAddress(), model);
				cursor.moveToNext();
			}
		}

		if (cursor != null)
			cursor.close();

		return table;

	}

	public void updatePhotoURL(String mac, String strImagePath) {

		ContentValues value = new ContentValues();
		value.put("DevicePhotoUrl", strImagePath);

		db.update(tbl_deviceinfo, value, "DeviceMacAddress = '" + mac + "'",
				null);

	}

	public void updateLocation(String deviceMacAddress, double latitude,
			double longitude) {
		ContentValues value = new ContentValues();
		value.put("Latitude", latitude);
		value.put("Longitude", longitude);
		db.update(tbl_deviceinfo, value, "DeviceMacAddress = '"
				+ deviceMacAddress + "'", null);

	}

	public void removeDevice(String deviceMacAddress) {

		db.delete(tbl_deviceinfo, "DeviceMacAddress = '" + deviceMacAddress
				+ "'", null);

	}

	public DeviceInfoModel getdeviceSettings(String mac) {

		DeviceInfoModel model = null;
		Cursor cursor = null;

		cursor = db.query(tbl_deviceinfo, null, "DeviceMacAddress='" + mac
				+ "'", null, null, null, null);

		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();

			for (int i = 0; i < cursor.getCount(); i++) {

				model = new DeviceInfoModel();
				model.setDeviceSerialNumber(cursor.getString(cursor
						.getColumnIndex("DeviceSerialNumber")));
				model.setDeviceMacAddress(cursor.getString(cursor
						.getColumnIndex("DeviceMacAddress")));
				model.setDeviceLogicalName(cursor.getString(cursor
						.getColumnIndex("DeviceLogicalName")));
				model.setDevicePhotoUrl(cursor.getString(cursor
						.getColumnIndex("DevicePhotoUrl")));
				model.setId(cursor.getInt(cursor.getColumnIndex("ID")));
				model.setLatitude(cursor.getDouble(cursor
						.getColumnIndex("Latitude")));
				model.setLongitude(cursor.getDouble(cursor
						.getColumnIndex("Longitude")));
				model.setBatteryIndication(cursor.getInt(cursor
						.getColumnIndex("BatteryIndication")));
				model.setDisconnectRing(cursor.getString(cursor
						.getColumnIndex("DisconnectRing")));
				model.setHiroBeepVolume(cursor.getInt(cursor
						.getColumnIndex("HiroBeepVolume")));
				model.setHiroDisBeepAlert(cursor.getInt(cursor
						.getColumnIndex("HiroDisBeepAlert")));
				model.setHiroDisconnectBeep(cursor.getInt(cursor
						.getColumnIndex("HiroDisconnectBeep")));
				model.setNotificationDisconnectAlert(cursor.getInt(cursor
						.getColumnIndex("NotificationDisconnectAlert")));
				model.setNotificationIndication(cursor.getInt(cursor
						.getColumnIndex("NotificationIndication")));
				model.setPhoneRing(cursor.getString(cursor
						.getColumnIndex("PhoneRing")));

			}

		}

		if (cursor != null)
			cursor.close();

		return model;
	}

	public void updateDeviceInfo(String macAddress, String column, int values) {
		// TODO Auto-generated method stub
		ContentValues value = new ContentValues();
		value.put(column, values);
		db.update(tbl_deviceinfo, value, "DeviceMacAddress = '" + macAddress
				+ "'", null);
	}

	public void updateDeviceDateTime(String macAddress, String column,
			String values) {
		// TODO Auto-generated method stub
		ContentValues value = new ContentValues();
		value.put(column, values);
		db.update(tbl_deviceinfo, value, "DeviceMacAddress = '" + macAddress
				+ "'", null);
	}

	public void updateDeviceRingInfo(String macAddress, String column,
			String values) {
		// TODO Auto-generated method stub
		ContentValues value = new ContentValues();
		value.put(column, values);
		db.update(tbl_deviceinfo, value, "DeviceMacAddress = '" + macAddress
				+ "'", null);
	}

	public int getDeviceLinkLossValue(String macAddress) {

		Cursor cursor = null;
		int value = 0;
		cursor = db
				.query(tbl_deviceinfo, new String[] { "HiroDisBeepAlert" },
						"DeviceMacAddress='" + macAddress + "'", null, null,
						null, null);

		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			value = cursor.getInt(cursor.getColumnIndex("HiroDisBeepAlert"));

			if (cursor != null)
				cursor.close();

			return value;
		}

		if (cursor != null)
			cursor.close();

		return 0;
	}

	public boolean getColumnValue(String deviceMacAddress, String key) {

		Cursor cursor = null;
		boolean isTrue = false;
		int value = 1;
		cursor = db.query(tbl_deviceinfo, null, "DeviceMacAddress='"
				+ deviceMacAddress + "'", null, null, null, null);

		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();

			value = cursor.getInt(cursor.getColumnIndex(key));

			if (value == 1)
				isTrue = true;
			else
				isTrue = false;
		}

		if (cursor != null)
			cursor.close();

		return isTrue;
	}

	public String getDateTimeValue(String deviceMacAddress) {

		Cursor cursor = null;
		String date = "";
		cursor = db.query(tbl_deviceinfo, null, "DeviceMacAddress='"
				+ deviceMacAddress + "'", null, null, null, null);

		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();

			date = cursor.getString(cursor
					.getColumnIndex("BatteryNotificationDate"));

		}
		if (cursor != null)
			cursor.close();

		return date;
	}

	public String getRing(String deviceMacAddress, String key) {
		Cursor cursor = null;
		String path = "";
		cursor = db.query(tbl_deviceinfo, null, "DeviceMacAddress='"
				+ deviceMacAddress + "'", null, null, null, null);

		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			path = cursor.getString(cursor.getColumnIndex(key));
		}
		if (cursor != null)
			cursor.close();
		return path;
	}

	public int getnotificationId(String deviceMacAddress) {
		// TODO Auto-generated method stub

		Cursor cursor = null;
		int id = 1;
		cursor = db.query(tbl_deviceinfo, null, "DeviceMacAddress='"
				+ deviceMacAddress + "'", null, null, null, null);

		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			id = cursor.getInt(cursor.getColumnIndex("ID"));
		}
		if (cursor != null)
			cursor.close();

		return id;
	}

	public boolean ssidAvailable(String ssid) {
		// TODO Auto-generated method stub
		Cursor cursor = null;
		cursor = db.query("WifiInfo", null, "WifiSSID='" + ssid + "'", null,
				null, null, null);

		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			if (cursor != null)
				cursor.close();
			return true;
		}
		if (cursor != null)
			cursor.close();

		return false;
	}

	public boolean macAvailable(String mac) {
		// TODO Auto-generated method stub
		Cursor cursor = null;
		cursor = db.query("WifiInfo", null, "MacAddress='" + mac + "'", null,
				null, null, null);

		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			if (cursor != null)
				cursor.close();
			return true;
		}
		if (cursor != null)
			cursor.close();

		return false;
	}

	public void addWifiSSID(String ssid, String macAddress) {
		// TODO Auto-generated method stub
		ContentValues value = new ContentValues();
		value.put("MacAddress", macAddress);
		value.put("WifiSSID", ssid);

		db.insert("WifiInfo", null, value);
	}

	public ArrayList<String> getWifiSSID() {
		// TODO Auto-generated method stub

		ArrayList<String> ssidlist = new ArrayList<String>();

		Cursor cursor = null;

		cursor = db.query("WifiInfo", null, null, null, null, null, null);

		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			for (int i = 0; i < cursor.getCount(); i++) {
				ssidlist.add(cursor.getString(cursor.getColumnIndex("WifiSSID")));
				cursor.moveToNext();
			}

		}

		if (cursor != null)
			cursor.close();

		return ssidlist;
	}

	public void removeSSID(String ssid) {
		// TODO Auto-generated method stub

		db.delete("WifiInfo", "WifiSSID='" + ssid + "'", null);
	}

}
