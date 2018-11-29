package com.qinglan.sdk.server.presentation.platform.dto;

import java.io.Serializable;

public class HTCPayCallback implements Serializable{
	private static final long serialVersionUID = -7669337526539764715L;
	
	private String result_code;
	private String gmt_create;
	private String real_amount;
	private String result_msg;
	private String game_code;
	private String game_order_id;
	private String jolo_order_id;
	private String gmt_payment;
	
	public String getResult_code() {
		return result_code;
	}
	public void setResult_code(String result_code) {
		this.result_code = result_code;
	}
	public String getGmt_create() {
		return gmt_create;
	}
	public void setGmt_create(String gmt_create) {
		this.gmt_create = gmt_create;
	}
	public String getReal_amount() {
		return real_amount;
	}
	public void setReal_amount(String real_amount) {
		this.real_amount = real_amount;
	}
	public String getResult_msg() {
		return result_msg;
	}
	public void setResult_msg(String result_msg) {
		this.result_msg = result_msg;
	}
	public String getGame_code() {
		return game_code;
	}
	public void setGame_code(String game_code) {
		this.game_code = game_code;
	}
	public String getGame_order_id() {
		return game_order_id;
	}
	public void setGame_order_id(String game_order_id) {
		this.game_order_id = game_order_id;
	}
	public String getJolo_order_id() {
		return jolo_order_id;
	}
	public void setJolo_order_id(String jolo_order_id) {
		this.jolo_order_id = jolo_order_id;
	}
	public String getGmt_payment() {
		return gmt_payment;
	}
	public void setGmt_payment(String gmt_payment) {
		this.gmt_payment = gmt_payment;
	}
}
