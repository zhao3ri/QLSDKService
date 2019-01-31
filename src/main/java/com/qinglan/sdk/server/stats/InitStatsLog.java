package com.qinglan.sdk.server.stats;

import com.qinglan.sdk.server.dto.InitialPattern;

class InitStatsLog extends BaseStatsLog<InitialPattern> {
    private String manufacturer;
    private String model;
    private int apiVersion;
    private String osVersion;
    private String latitude;
    private String longitude;
    private String imsi;
    private String location;
    private String networkCountryIso;
    private String networkType;
    private String phoneType;
    private String simOperatorName;
    private String resolution;
    private boolean isFirstInitGame;
    private boolean isFirstInitChannel;

    static final String EXTRA_FIRST_INIT_GAME = "isFirstInitGame";
    static final String EXTRA_FIRST_INIT_CHANNEL = "isFirstInitChannel";

    public InitStatsLog(int code, int version, InitialPattern dto) {
        super(code, version, dto);
    }

    @Override
    public void setEntity(InitialPattern dto) {
        if (dto == null) {
            return;
        }
        this.os = dto.getClientType();
        this.deviceId = dto.getDeviceId();
        this.manufacturer = dto.getManufacturer();
        this.model = dto.getModel();
        this.apiVersion = dto.getApiVersion();
        this.osVersion = dto.getOsVersion();
        this.latitude = dto.getLatitude();
        this.longitude = dto.getLongitude();
        this.imsi = dto.getImsi();
        this.location = dto.getLocation();
        this.networkCountryIso = dto.getNetworkCountryIso();
        this.networkType = dto.getNetworkType();
        this.phoneType = dto.getPhoneType();
        this.simOperatorName = dto.getSimOperatorName();
        this.resolution = dto.getResolution();
    }

    @Override
    protected <E> void handleExtras(String key, E val) {
        if (key.equals(EXTRA_FIRST_INIT_GAME) && val instanceof Boolean) {
            this.isFirstInitGame = (Boolean) val;
        } else if (key.equals(EXTRA_FIRST_INIT_CHANNEL) && val instanceof Boolean) {
            this.isFirstInitChannel = (Boolean) val;
        }
    }

    @Override
    protected void appendLog(StringBuffer buffer) {
        buffer.append(deviceId).append(SEPARATOR);
        buffer.append(manufacturer).append(SEPARATOR);
        buffer.append(model).append(SEPARATOR);
        buffer.append(apiVersion).append(SEPARATOR);
        buffer.append(osVersion).append(SEPARATOR);
        buffer.append(latitude).append(SEPARATOR);
        buffer.append(longitude).append(SEPARATOR);
        buffer.append(imsi).append(SEPARATOR);
        buffer.append(location).append(SEPARATOR);
        buffer.append(networkCountryIso).append(SEPARATOR);
        buffer.append(networkType).append(SEPARATOR);
        buffer.append(phoneType).append(SEPARATOR);
        buffer.append(simOperatorName).append(SEPARATOR);
        buffer.append(resolution).append(SEPARATOR);
        buffer.append(boolean2Int(isFirstInitGame)).append(SEPARATOR);
        buffer.append(boolean2Int(isFirstInitChannel));
    }
}
