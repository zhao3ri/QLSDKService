package com.qinglan.sdk.server.platform.domain.dto;

/**
 * Created by engine on 16/9/29.
 */
public class FansdkSession extends BaseSession {
   private String userID;//上面登录认证成功之后，YXFSDKJU Server返回的userID
   private String token;//上面登录认证成功之后，YXFSDKJU Server返回的token

   public String getUserID() {
      return userID;
   }

   public void setUserID(String userID) {
      this.userID = userID;
   }

   public String getToken() {
      return token;
   }

   public void setToken(String token) {
      this.token = token;
   }
}
