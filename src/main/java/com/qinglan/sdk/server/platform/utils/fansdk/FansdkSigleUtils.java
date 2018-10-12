package com.qinglan.sdk.server.platform.utils.fansdk;


/**
 * Created by engine on 16/9/30.
 */
public class FansdkSigleUtils {
    //生成签名
    public static String generateSign(UOrder order, String signType,String appsecret,String privateKey) {
        StringBuilder sb = new StringBuilder();
        sb.append("channelID=").append(order.getChannelID()).append("&")
                .append("currency=").append(order.getCurrency()).append("&")
                .append("extension=").append(order.getExtension()).append("&")
                .append("gameID=").append(order.getAppID()).append("&")
                .append("money=").append(order.getMoney()).append("&")
                .append("orderID=").append(order.getOrderID()).append("&")
                .append("productID=").append(order.getProductID()).append("&")
                .append("serverID=").append(order.getServerID()).append("&")
                .append("userID=").append(order.getUserID()).append("&")
                .append(appsecret);

        if ("md5".equalsIgnoreCase(signType)) {
            return EncryptUtils.md5(sb.toString()).toLowerCase();
        } else {
            return RSAUtils.sign(sb.toString(), privateKey, "UTF-8", "SHA1withRSA");
        }
    }

}
