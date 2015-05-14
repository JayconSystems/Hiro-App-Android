/**
 * Created by Jaycon Systems on 03/01/15.
 * Copyright @ 2014 Jaycon Systems. All rights reserved.
 */

package com.hiroapp.common;


public interface DisconnectInterface {

	public void disconnectBda(BluetoothDeviceActor bluetoothDeviceActor);
	public void updateRSSI(BluetoothDeviceActor bluetoothDeviceActor);
	public void updateBattery(BluetoothDeviceActor bluetoothDeviceActor);
}
