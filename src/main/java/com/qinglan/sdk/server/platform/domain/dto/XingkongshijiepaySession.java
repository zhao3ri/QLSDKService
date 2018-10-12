package com.qinglan.sdk.server.platform.domain.dto;


import com.qinglan.sdk.server.platform.domain.dto.BaseSession;

/**
 * Created by hoog on 2017/5/4.
 */
public class XingkongshijiepaySession extends BaseSession {

    private String ygAppId;
    private String platformId;
    private String appid;
    private String waresid;
    private String waresname;
    private String cporderid;
    private String price;
    private String currency;
    private String appuserid;
    private String cpprivateinfo;
    private String notifyUrl;

    @Override
    public String getYgAppId() {
        return ygAppId;
    }

    @Override
    public void setYgAppId(String ygAppId) {
        this.ygAppId = ygAppId;
    }

    @Override
    public String getPlatformId() {
        return platformId;
    }

    @Override
    public void setPlatformId(String platformId) {
        this.platformId = platformId;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getWaresid() {
        return waresid;
    }

    public void setWaresid(String waresid) {
        this.waresid = waresid;
    }

    public String getWaresname() {
        return waresname;
    }

    public void setWaresname(String waresname) {
        this.waresname = waresname;
    }

    public String getCporderid() {
        return cporderid;
    }

    public void setCporderid(String cporderid) {
        this.cporderid = cporderid;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getAppuserid() {
        return appuserid;
    }

    public void setAppuserid(String appuserid) {
        this.appuserid = appuserid;
    }

    public String getCpprivateinfo() {
        return cpprivateinfo;
    }

    public void setCpprivateinfo(String cpprivateinfo) {
        this.cpprivateinfo = cpprivateinfo;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }
}
