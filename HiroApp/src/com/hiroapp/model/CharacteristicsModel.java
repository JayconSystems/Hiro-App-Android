/**
 * Created by Jaycon Systems on 26/12/14.
 * Copyright @ 2014 Jaycon Systems. All rights reserved.
 */
package com.hiroapp.model;

public class CharacteristicsModel {

	private int id;
	private String charUUID;
	private String charName;
	private int serviceId;
	private int deviceId;
	private int isObservable;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCharUUID() {
		return charUUID;
	}

	public void setCharUUID(String charUUID) {
		this.charUUID = charUUID;
	}

	public String getCharName() {
		return charName;
	}

	public void setCharName(String charName) {
		this.charName = charName;
	}

	public int getServiceId() {
		return serviceId;
	}

	public void setServiceId(int serviceId) {
		this.serviceId = serviceId;
	}

	public int getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(int deviceId) {
		this.deviceId = deviceId;
	}

	public int isObservable() {
		return isObservable;
	}

	public void setObservable(int isObservable) {
		this.isObservable = isObservable;
	}

}
