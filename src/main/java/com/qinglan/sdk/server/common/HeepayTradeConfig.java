package com.qinglan.sdk.server.common;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Properties;

public class HeepayTradeConfig {
	private final static Logger LOGGER = Logger.getLogger(HeepayTradeConfig.class);

	private static final Properties properties = new Properties();
	private static final HeepayTradeConfig INSTANCE = new HeepayTradeConfig();

	private HeepayTradeConfig() {
		try {
			properties.load(getClass().getClassLoader().getResourceAsStream("recharge_config.properties"));
		} catch (IOException e) {
			LOGGER.error("load config file recharge_config.properties exception", e);	
		}
	}
	
	public static HeepayTradeConfig getInstance() {
		return INSTANCE;
	}

	private String get(String key) {
		return properties.getProperty(key);
	}

	public String getInitUrl() {
		return get("heepaysdk.init.url");
	}
	public String getPhoneInitUrl(){
		return get("heepay.init.url") ;
	}
	public String getHeepayAgentid() {
		return get("heepay.charge.agentid");
	}

	public String getHeepayRetrunUrl(){
		return get("heepay.return.url");
	}
	public String getAlipayAgentid() {
		return get("payalipay.charge.agentid");
	}

	public int getSelfPay(){
		return  Integer.parseInt(get("zhidian.self")) ;
	}
	public String getCallbackUrl() {
		return get("heepaysdk.callback.url");
	}
	
	public String getWechatPayKey() {
		return get("heepay.pay.key");
	}
	public String getAliPayKey() {
		return get("payalipay.pay.key");
	}
}
