package com.qinglan.sdk.server.utils.channel.quicksdk;

/**
 * Created by engine on 16/8/15.
 */
public class QuickXmlBean {
    private String is_test ;
    private String channel ;
    private String channel_uid ;
    private String game_order ;
    private String order_no ;
    private String pay_time ;
    private String amount ;
    private String status ;

    public String getIs_test() {
        return is_test;
    }

    public void setIs_test(String is_test) {
        this.is_test = is_test;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getChannel_uid() {
        return channel_uid;
    }

    public void setChannel_uid(String channel_uid) {
        this.channel_uid = channel_uid;
    }

    public String getGame_order() {
        return game_order;
    }

    public void setGame_order(String game_order) {
        this.game_order = game_order;
    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public String getPay_time() {
        return pay_time;
    }

    public void setPay_time(String pay_time) {
        this.pay_time = pay_time;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getExtras_params() {
        return extras_params;
    }

    public void setExtras_params(String extras_params) {
        this.extras_params = extras_params;
    }

    private String extras_params ;
}
