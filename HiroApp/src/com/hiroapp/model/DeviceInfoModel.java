/**
 * Created by Jaycon Systems on 05/01/15.
 * Copyright @ 2014 Jaycon Systems. All rights reserved.
 */

package com.hiroapp.model;

public class DeviceInfoModel {

	private String deviceSerialNumber;
	private String deviceMacAddress;
	private String deviceLogicalName;
	private int id;
	private double latitude;
	private double longitude;
	private String devicePhotoUrl = "";
	
	private int batteryIndication = 1;
	private int hiroBeepVolume = 1;
	private int hiroDisconnectBeep = -1;
	private int notificationIndication = 1;
	private int notificationDisconnectAlert = 1;
	private int hiroDisBeepAlert = 1;
	private String disconnectRing = "";
	private String phoneRing = "";

	
	public int isBatteryIndication() {
		return batteryIndication;
	}

	public void setBatteryIndication(int batteryIndication) {
		this.batteryIndication = batteryIndication;
	}

	public int isHiroBeepVolume() {
		return hiroBeepVolume;
	}

	public void setHiroBeepVolume(int hiroBeepVolume) {
		this.hiroBeepVolume = hiroBeepVolume;
	}

	public int isHiroDisconnectBeep() {
		return hiroDisconnectBeep;
	}

	public void setHiroDisconnectBeep(int hiroDisconnectBeep) {
		this.hiroDisconnectBeep = hiroDisconnectBeep;
	}

	public int isNotificationIndication() {
		return notificationIndication;
	}

	public void setNotificationIndication(int notificationIndication) {
		this.notificationIndication = notificationIndication;
	}

	public int isNotificationDisconnectAlert() {
		return notificationDisconnectAlert;
	}

	public void setNotificationDisconnectAlert(int notificationDisconnectAlert) {
		this.notificationDisconnectAlert = notificationDisconnectAlert;
	}

	public int isHiroDisBeepAlert() {
		return hiroDisBeepAlert;
	}

	public void setHiroDisBeepAlert(int hiroDisBeepAlert) {
		this.hiroDisBeepAlert = hiroDisBeepAlert;
	}

	public String getDisconnectRing() {
		return disconnectRing;
	}

	public void setDisconnectRing(String disconnectRing) {
		this.disconnectRing = disconnectRing;
	}

	public String getPhoneRing() {
		return phoneRing;
	}

	public void setPhoneRing(String phoneRing) {
		this.phoneRing = phoneRing;
	}

	public double getLatitude() {
		return latitude;
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

	public String getDevicePhotoUrl() {
		return devicePhotoUrl;
	}

	public void setDevicePhotoUrl(String devicePhotoUrl) {
		this.devicePhotoUrl = devicePhotoUrl;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDeviceSerialNumber() {
		return deviceSerialNumber;
	}

	public void setDeviceSerialNumber(String deviceSerialNumber) {
		this.deviceSerialNumber = deviceSerialNumber;
	}

	public String getDeviceMacAddress() {
		return deviceMacAddress;
	}

	public void setDeviceMacAddress(String deviceMacAddress) {
		this.deviceMacAddress = deviceMacAddress;
	}

	public String getDeviceLogicalName() {
		return deviceLogicalName;
	}

	public void setDeviceLogicalName(String deviceLogicalName) {
		this.deviceLogicalName = deviceLogicalName;
	}

}
