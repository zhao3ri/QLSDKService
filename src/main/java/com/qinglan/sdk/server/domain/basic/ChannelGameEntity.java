package com.qinglan.sdk.server.domain.basic;

import java.io.Serializable;
import java.util.*;
import java.util.regex.Pattern;

import com.qinglan.sdk.server.common.StringUtil;
import org.codehaus.jackson.annotate.JsonIgnore;

public class ChannelGameEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;

    private Integer channelId;

    private Long gameId;

    private String configParams;

    private Date createTime;
    private Integer status = 0;
    private Integer registStatus = 0;
    private Integer discount;
    private String appKey;

    private String appID;

    private String secretKey;

    private String publicKey;

    private String privateKey;

    private String notifyUrl;

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

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
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

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppID() {
        return appID;
    }

    public void setAppID(String appID) {
        this.appID = appID;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
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

    public Map<String, String> getConfigs() {
        if (StringUtil.isNullOrEmpty(configParams)) {
            return null;
        }
        String regex = "(\\w+)=(\\w+)";
        String[] params = configParams.split(";");
        Map<String, String> config = new HashMap<>();

        for (String param : params) {
            if (!Pattern.matches(regex, param)) {
                continue;
            }
            String key = param.substring(0, param.indexOf("=")).toLowerCase();
            String value = param.substring(param.indexOf("="));
            config.put(key, value);
        }
        return config;
    }

    public String getConfig(String key) {
        Map<String, String> config = getConfigs();
        if (null == config || config.isEmpty()) {
            return null;
        }
        if (StringUtil.isNullOrEmpty(key)) {
            return null;
        }
        return config.get(key.toLowerCase());
    }
}