package com.qinglan.sdk.server.platform.domain.dto;

/**
 * Created by engine on 16/9/29.
 */
public class TaoShouYouSession extends BaseSession {
   private String userid;
   private String token;


   public String getUserid() {
      return userid;
   }

   public void setUserid(String userid) {
      this.userid = userid;
   }

   public String getToken() {
      return token;
   }

   public void setToken(String token) {
      this.token = token;
   }
}
