package com.qinglan.sdk.server.channel.entity;

import java.io.Serializable;

public class YSBaseResponseEntity implements Serializable {
    private static final long serialVersionUID = -487043331378327791L;
    private Integer errNum;// 错误码
    private String errMsg;// 错误信息
    private String sign;// 签名

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

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    @Override
    public String toString() {
        return "YSBaseResponseEntity [errNum=" + errNum + ", errMsg=" + errMsg + ", sign=" + sign + "]";
    }

}
