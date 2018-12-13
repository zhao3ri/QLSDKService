package com.qinglan.sdk.server.presentation.dto.channel.channel2;

import com.qinglan.sdk.server.presentation.dto.channel.SessionBase;

public class XmwOrderSession extends SessionBase {

    String access_token;
    String client_id;
    String client_secret;
    String app_order_id;
    String app_user_id;
    String notify_url;
    String amount;
    String timestamp;
    String sign;
    String app_subject;
    String app_description;
    String app_ext1;
    String app_ext2;
    String game_detail;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getClient_secret() {
        return client_secret;
    }

    public void setClient_secret(String client_secret) {
        this.client_secret = client_secret;
    }

    public String getApp_order_id() {
        return app_order_id;
    }

    public void setApp_order_id(String app_order_id) {
        this.app_order_id = app_order_id;
    }

    public String getApp_user_id() {
        return app_user_id;
    }

    public void setApp_user_id(String app_user_id) {
        this.app_user_id = app_user_id;
    }

    public String getNotify_url() {
        return notify_url;
    }

    public void setNotify_url(String notify_url) {
        this.notify_url = notify_url;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getApp_subject() {
        return app_subject;
    }

    public void setApp_subject(String app_subject) {
        this.app_subject = app_subject;
    }

    public String getApp_description() {
        return app_description;
    }

    public void setApp_description(String app_description) {
        this.app_description = app_description;
    }

    public String getApp_ext1() {
        return app_ext1;
    }

    public void setApp_ext1(String app_ext1) {
        this.app_ext1 = app_ext1;
    }

    public String getApp_ext2() {
        return app_ext2;
    }

    public void setApp_ext2(String app_ext2) {
        this.app_ext2 = app_ext2;
    }

    public String getGame_detail() {
        return game_detail;
    }

    public void setGame_detail(String game_detail) {
        this.game_detail = game_detail;
    }
}
