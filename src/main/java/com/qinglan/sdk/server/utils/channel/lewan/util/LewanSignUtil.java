package com.qinglan.sdk.server.utils.channel.lewan.util;

import java.util.TreeMap;


import com.alibaba.fastjson.JSON;
import com.qinglan.sdk.server.utils.channel.lewan.entity.LewanPayOrder;
import com.qinglan.sdk.server.utils.channel.lewan.util.encrypt.AES;
import com.qinglan.sdk.server.utils.channel.lewan.util.encrypt.EncryUtil;
import com.qinglan.sdk.server.utils.channel.lewan.util.encrypt.RSA;

public class LewanSignUtil {
		/**
		 * 获取带签名的加密数据
		 * @param returnLOP
		 * @param privateKey
		 * @param merchantAesKey
		 * @return
		 */
		public static String getSignDate(LewanPayOrder returnLOP, String privateKey, String merchantAesKey){
			TreeMap<String, Object> gameMap = new TreeMap<String, Object>();
			gameMap.put("gameId", returnLOP.getGameId());
			gameMap.put("gameOrderId", returnLOP.getGameOrderId());
			gameMap.put("gameUserId", returnLOP.getUserId());
			gameMap.put("payState", returnLOP.getPayState());
			gameMap.put("errorCode", returnLOP.getErrorCode());
			gameMap.put("errorMsg", returnLOP.getErrorMsg());
			gameMap.put("expandMsg", returnLOP.getExpandMsg());
			gameMap.put("paySuccessMoney",returnLOP.getPaySuccessMoney()+"");
			gameMap.put("lewanOrderId", returnLOP.getPayCode());
			gameMap.put("serverId", returnLOP.getGameServerId());
			gameMap.put("balanceAmt", returnLOP.getBalanceAmt());
			//用自己的私钥s进行签名
			String sign = EncryUtil.handleRSA(gameMap, privateKey);
			gameMap.put("sign", sign);
			String info = JSON.toJSONString(gameMap);
			
			String gamedata = AES.encryptToBase64(info, merchantAesKey);
			return gamedata;
		}
		/**
		 * 用游戏的公钥进行加密
		 * @param merchantAesKey
		 * @param gamePublickey
		 * @return
		 * @throws Exception
		 */
		public static String getEncryptkey(String merchantAesKey ,String gamePublickey) throws Exception{
			String encryptkey= RSA.encrypt(merchantAesKey, gamePublickey);
			return encryptkey;
		}
}	
