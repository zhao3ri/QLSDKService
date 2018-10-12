package com.qinglan.sdk.server.platform.domain;

import com.qinglan.sdk.server.common.JsonMapper;

public class TencentBuyGoods {
	private String zdappId;			//指点游戏ID
	private String platformId; 		//指点联运平台ID
	
	private String orderId;
	
	private String session_id;		
	private String session_type;
	
	private String openid;	
	private String openkey;
	private String pay_token;
	private String appid;
	private String goodsId;
	private String goodsName;
	private String goodsDesc;
	private String pf;
	private String zoneid;
	private String pfkey;
	
	public String getZdappId() {
		return zdappId;
	}
	public void setZdappId(String zdappId) {
		this.zdappId = zdappId;
	}
	public String getPlatformId() {
		return platformId;
	}
	public void setPlatformId(String platformId) {
		this.platformId = platformId;
	}
	public String getSession_id() {
		return session_id;
	}
	public void setSession_id(String session_id) {
		this.session_id = session_id;
	}
	public String getSession_type() {
		return session_type;
	}
	public void setSession_type(String session_type) {
		this.session_type = session_type;
	}
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public String getOpenkey() {
		return openkey;
	}
	public void setOpenkey(String openkey) {
		this.openkey = openkey;
	}
	public String getPay_token() {
		return pay_token;
	}
	public void setPay_token(String pay_token) {
		this.pay_token = pay_token;
	}
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	public String getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(String goodsId) {
		this.goodsId = goodsId;
	}
	public String getGoodsName() {
		return goodsName;
	}
	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}
	public String getGoodsDesc() {
		return goodsDesc;
	}
	public void setGoodsDesc(String goodsDesc) {
		this.goodsDesc = goodsDesc;
	}
	public String getPf() {
		return pf;
	}
	public void setPf(String pf) {
		this.pf = pf;
	}
	public String getZoneid() {
		return zoneid;
	}
	public void setZoneid(String zoneid) {
		this.zoneid = zoneid;
	}
	public String getPfkey() {
		return pfkey;
	}
	public void setPfkey(String pfkey) {
		this.pfkey = pfkey;
	}
	
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	@Override
	public String toString() {
		return JsonMapper.toJson(this);
	}
}
