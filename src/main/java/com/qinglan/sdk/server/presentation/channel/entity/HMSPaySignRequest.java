package com.qinglan.sdk.server.presentation.channel.entity;

/**
 * 华为支付签名请求实体
 */
public class HMSPaySignRequest extends BaseRequest {
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
