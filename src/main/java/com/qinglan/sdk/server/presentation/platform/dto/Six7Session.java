package com.qinglan.sdk.server.presentation.platform.dto;

public class Six7Session {
 private String apk_id ;
 private String uid ;
 private String device_code ;
 private String token ;
 private String platform ;
 private String ygAppId;

 public String getYgAppId() {
  return ygAppId;
 }

 public void setYgAppId(String ygAppId) {
  this.ygAppId = ygAppId;
 }

 public String getPlatform() {
  return platform;
 }

 public void setPlatform(String platform) {
  this.platform = platform;
 }

 public String getApk_id() {
  return apk_id;
 }

 public void setApk_id(String apk_id) {
  this.apk_id = apk_id;
 }

 public String getUid() {
  return uid;
 }

 public void setUid(String uid) {
  this.uid = uid;
 }

 public String getDevice_code() {
  return device_code;
 }

 public void setDevice_code(String device_code) {
  this.device_code = device_code;
 }

 public String getToken() {
  return token;
 }

 public void setToken(String token) {
  this.token = token;
 }
}
