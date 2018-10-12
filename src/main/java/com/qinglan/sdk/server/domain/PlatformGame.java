package com.qinglan.sdk.server.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class PlatformGame implements Serializable {
    private Long id;
    private Integer platformId;
    private Long appId;
    private String configParams;

    private Date createTime;
    private Integer status = 0;
    private Integer registStatus = 0;
    private Integer discount;
    private static final long serialVersionUID = 1L;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPlatformId() {
        return platformId;
    }

    public void setPlatformId(Integer platformId) {
        this.platformId = platformId;
    }

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    public String getConfigParams() {
        return configParams;
    }

    public void setConfigParams(String configParams) {
        this.configParams = configParams == null ? null : configParams.trim();
    }

    public Integer getRegistStatus() {
        return registStatus;
    }

    public void setRegistStatus(Integer registStatus) {
        this.registStatus = registStatus;
    }

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @JsonIgnore
    public List<String> getConfigParamsList() {
        if (null != configParams && !configParams.trim().isEmpty()) {
            String[] array = configParams.split(",");
            return Arrays.asList(array);
        }
        return null;
    }

    @JsonIgnore
    public List<String> getConfigParamsList(String splitStr) {
        if (null != configParams && !configParams.trim().isEmpty()) {
            String[] array = configParams.split(splitStr);
            return Arrays.asList(array);
        }
        return null;
    }
}