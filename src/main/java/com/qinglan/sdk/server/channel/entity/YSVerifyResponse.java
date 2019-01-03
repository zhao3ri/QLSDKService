package com.qinglan.sdk.server.channel.entity;

import java.io.Serializable;
import java.util.Map;

public class YSVerifyResponse implements Serializable {
    private static final long serialVersionUID = -487043331378327792L;
    private Integer errNum;// 错误码
    private String errMsg;// 错误信息
    private Map<String, Object> transdata;// 业务数据

    public Map<String, Object> getTransdata() {
        return transdata;
    }

    public void setTransdata(Map<String, Object> transdata) {
        this.transdata = transdata;
    }

    public Integer getErrNum() {
        return errNum;
    }

    public void setErrNum(Integer errNum) {
        this.errNum = errNum;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    @Override
    public String toString() {
        return "YSVerifyResponse [errNum=" + errNum + ", errMsg=" + errMsg + ", transdata=" + transdata + "]";
    }

}

