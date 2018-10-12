package com.qinglan.sdk.server.platform.domain.dto;

/**
 * Created by engine on 16/9/29.
 */
public class AoChuangSession extends BaseSession {
   private String appid;
   private String sessionid;
   private String ac;
   private String sdkversion;
   private String sign;

   public String getAppid() {
      return appid;
   }

   public void setAppid(String appid) {
      this.appid = appid;
   }

   public String getSessionid() {
      return sessionid;
   }

   public void setSessionid(String sessionid) {
      this.sessionid = sessionid;
   }

   public String getAc() {
      return ac;
   }

   public void setAc(String ac) {
      this.ac = ac;
   }

   public String getSdkversion() {
      return sdkversion;
   }

   public void setSdkversion(String sdkversion) {
      this.sdkversion = sdkversion;
   }

   public String getSign() {
      return sign;
   }

   public void setSign(String sign) {
      this.sign = sign;
   }
}
