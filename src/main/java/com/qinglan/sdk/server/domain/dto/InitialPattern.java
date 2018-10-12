package com.qinglan.sdk.server.domain.dto;

import java.io.Serializable;

import lombok.ToString;

@ToString(callSuper = true)
public class InitialPattern extends BaseDto implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String deviceId;
	private Integer clientType;
	private String manufacturer;
	private String model;
	private String systemVersion;
	private String platform;
	private String latitude;
	private String longitude;
	private String imsi;
	private String location;
	private String networkCountryIso;
	private String networkType;
	private String phonetype;
	private String simoperatorname;
	private String resolution;
	
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public Integer getClientType() {
		return clientType;
	}
	public void setClientType(Integer clientType) {
		this.clientType = clientType;
	}
	public String getManufacturer() {
		return manufacturer;
	}
	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getSystemVersion() {
		return systemVersion;
	}
	public void setSystemVersion(String systemVersion) {
		this.systemVersion = systemVersion;
	}
	public String getPlatform() {
		return platform;
	}
	public void setPlatform(String platform) {
		this.platform = platform;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getImsi() {
		return imsi;
	}
	public void setImsi(String imsi) {
		this.imsi = imsi;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getNetworkCountryIso() {
		return networkCountryIso;
	}
	public void setNetworkCountryIso(String networkCountryIso) {
		this.networkCountryIso = networkCountryIso;
	}
	public String getNetworkType() {
		return networkType;
	}
	public void setNetworkType(String networkType) {
		this.networkType = networkType;
	}
	public String getPhonetype() {
		return phonetype;
	}
	public void setPhonetype(String phonetype) {
		this.phonetype = phonetype;
	}
	public String getSimoperatorname() {
		return simoperatorname;
	}
	public void setSimoperatorname(String simoperatorname) {
		this.simoperatorname = simoperatorname;
	}
	public String getResolution() {
		return resolution;
	}
	public void setResolution(String resolution) {
		this.resolution = resolution;
	}
	
	@Override
	public boolean isEmpty() {
		if(null == getAppId()) return true;
		if(null == getPlatformId()) return true;
		if(null == getDeviceId() || getDeviceId().trim().isEmpty()) return true;
		if(null == getClientType()) return true;
		return false;
	}
	
}
