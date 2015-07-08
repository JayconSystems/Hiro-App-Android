/**
 * Created by Jaycon Systems on 03/01/15.
 * Copyright @ 2014 Jaycon Systems. All rights reserved.
 */

package com.hiroapp.common;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCallback;

public interface ScanInterface { 
	// this class is not used
	public void connectedDevice(BluetoothDevice device);
	public void addDevice(BluetoothDevice scandevices, String uuid, String string);
	
}
