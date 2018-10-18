package com.qinglan.sdk.server.release.application.basic.listener;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import com.qinglan.sdk.server.common.DateUtils;
import com.qinglan.sdk.server.common.HttpUtils;
import com.qinglan.sdk.server.common.JsonMapper;
import com.qinglan.sdk.server.common.MD5;
import com.qinglan.sdk.server.release.StatsLogger;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import com.zhidian3g.ddd.annotation.event.EventListener;
import com.qinglan.sdk.server.release.application.basic.kafka.KafkaProducerClient;
import com.qinglan.sdk.server.release.application.basic.redis.RedisUtil;
import com.qinglan.sdk.server.release.domain.basic.Account;
import com.qinglan.sdk.server.release.domain.basic.BasicRepository;
import com.qinglan.sdk.server.release.domain.basic.BehaviorDevice;
import com.qinglan.sdk.server.release.domain.basic.BehaviorUser;
import com.qinglan.sdk.server.release.domain.basic.GameTrace;
import com.qinglan.sdk.server.release.domain.basic.HLastLogin;
import com.qinglan.sdk.server.release.domain.basic.PlatformGame;
import com.qinglan.sdk.server.release.domain.basic.Role;
import com.qinglan.sdk.server.release.domain.basic.RoleTrace;
import com.qinglan.sdk.server.release.domain.basic.ZoneTrace;
import com.qinglan.sdk.server.release.domain.basic.event.HeartbeatEvent;
import com.qinglan.sdk.server.release.domain.basic.event.InitialEvent;
import com.qinglan.sdk.server.release.domain.basic.event.LoginEvent;
import com.qinglan.sdk.server.release.domain.basic.event.LogoutEvent;
import com.qinglan.sdk.server.release.domain.basic.event.OraderCountEvent;
import com.qinglan.sdk.server.release.domain.basic.event.OrderGenerateEvent;
import com.qinglan.sdk.server.release.domain.basic.event.QuitEvent;
import com.qinglan.sdk.server.release.domain.basic.event.RoleEstablishEvent;
import com.qinglan.sdk.server.release.presentation.basic.dto.HeartbeatPattern;
import com.qinglan.sdk.server.release.presentation.basic.dto.InitialPattern;
import com.qinglan.sdk.server.release.presentation.basic.dto.LoginPattern;
import com.qinglan.sdk.server.release.presentation.basic.dto.LogoutPattern;
import com.qinglan.sdk.server.release.presentation.basic.dto.OrderGeneratePattern;
import com.qinglan.sdk.server.release.presentation.basic.dto.QuitPattern;
import com.qinglan.sdk.server.release.presentation.basic.dto.RoleEstablishPattern;

@Component @EventListener
public class GameOperateListener {
	
	private final static Logger logger = LoggerFactory.getLogger(GameOperateListener.class);
	
	@Resource
	private KafkaProducerClient kafkaProducerClient;
	@Resource 
	private BasicRepository basicRepository;
	@Resource
	private RedisUtil redisUtil;
	private String totalPayHead = "totolpay_";
	@EventListener(asynchronous = true)
	public void handleInitialEvent(InitialEvent event) {
		InitialPattern params = event.getHelper();
		try {
			boolean isFirstInitGame = false;
			boolean isFirstInitPlatform = false;
			BehaviorDevice behaviorDevice = basicRepository.getByUniqueKey(params.getClientType(), params.getAppId(), params.getDeviceId());
			if(null == behaviorDevice) {
				behaviorDevice = new BehaviorDevice();
				behaviorDevice.setAppId(params.getAppId());
				behaviorDevice.setClientType(params.getClientType());
				behaviorDevice.setDevice(params.getDeviceId());
				behaviorDevice.addPlatformId(params.getPlatformId());
				basicRepository.save(behaviorDevice);
				
				isFirstInitGame = true;
				isFirstInitPlatform = true;
			} else {
				if(null == behaviorDevice.getPlatformIds() || !behaviorDevice.getPlatformIds().contains(params.getPlatformId())) {
					behaviorDevice.addPlatformId(params.getPlatformId());
					basicRepository.updateDevicePlatform(behaviorDevice);
					isFirstInitPlatform = true;
				}
			}
			StringBuffer buffer = new StringBuffer();
			buffer.append(DateUtils.toStringDate(new Date())).append("|").append(params.getClientType()).append("|");
			buffer.append(params.getAppId()).append("|").append(params.getPlatformId()).append("|");
			buffer.append(params.getDeviceId()).append("|").append(params.getManufacturer()).append("|");
			buffer.append(params.getModel()).append("|").append(params.getSystemVersion()).append("|");
			buffer.append(params.getPlatform()).append("|").append(params.getLatitude()).append("|");
			buffer.append(params.getLongitude()).append("|").append(params.getImsi()).append("|");
			buffer.append(params.getLocation()).append("|").append(params.getNetworkCountryIso()).append("|");
			buffer.append(params.getNetworkType()).append("|").append(params.getPhonetype()).append("|");
			buffer.append(params.getSimoperatorname()).append("|").append(params.getResolution()).append("|");
			buffer.append(isFirstInitGame ? 1 : 0).append("|").append(isFirstInitPlatform ? 1 : 0);
			
			String log = StatsLogger.initial(buffer.toString());
			kafkaProducerClient.send(log);
		} catch(Exception e) {
			logger.error("handle Initial Event exception", e);
		}
	}
	
	@EventListener(asynchronous = true)
	public void handleLoginEvent(LoginEvent event) {
		LoginPattern params = event.getHelper();
		
		boolean isNewUser = false;
		Account account = basicRepository.getAccount(params.getPlatformId(), params.getUid());
		if(null == account){
			isNewUser = true;
			account = new Account(params.getPlatformId(), params.getUid());
			basicRepository.saveAccount(account);
		}
		
		BehaviorDevice behaviorDevice = basicRepository.getByUniqueKey(params.getClientType(), params.getAppId(), params.getDeviceId());
		boolean isGameActiveDevice = false;
		boolean isPlatformActiveDevice = false;
		boolean isZoneActiveDevice = false;
		
		if(null == behaviorDevice) {
			behaviorDevice = new BehaviorDevice();
			behaviorDevice.setAppId(params.getAppId());
			behaviorDevice.setClientType(params.getClientType());
			behaviorDevice.setDevice(params.getDeviceId());
			
			behaviorDevice.addLoginPlatformId(params.getPlatformId());
			behaviorDevice.addLoginZoneId(params.getZoneId());
			basicRepository.save(behaviorDevice);
			
			isGameActiveDevice = true;
			isPlatformActiveDevice = true;
			isZoneActiveDevice = true;
		} else {
			if(null == behaviorDevice.getLoginPlatformIds() || !behaviorDevice.getLoginPlatformIds().contains(params.getPlatformId())) {
				behaviorDevice.addLoginPlatformId(params.getPlatformId());
				basicRepository.updateDeviceLoginPlatform(behaviorDevice);
				isPlatformActiveDevice = true;
			}
			
			if(null == behaviorDevice.getLoginZoneIds() || !behaviorDevice.getLoginZoneIds().contains(params.getZoneId())) {
				behaviorDevice.addLoginZoneId(params.getZoneId());
				basicRepository.updateDeviceLoginZone(behaviorDevice);
				isZoneActiveDevice = true;
				isGameActiveDevice = true;
			}
		}
		GameTrace gameTrace = basicRepository.getGameTrace(params.getClientType(), params.getUid(), params.getPlatformId(), params.getAppId());
		ZoneTrace zoneTrace = basicRepository.getZoneTrace(params.getClientType(), params.getUid(), params.getPlatformId(), params.getAppId(), params.getZoneId());
		RoleTrace roleTrace = basicRepository.getRoleTrace(params.getClientType(), params.getUid(), params.getPlatformId(), params.getAppId(), params.getZoneId(), params.getRoleId(),params.getRoleName());
		
		Map<Integer, Integer> gameUserKeep = gameTrace.userKeep();
		Map<Integer, Integer> zoneUserKeep = zoneTrace.userKeep();
		
		BehaviorUser behaviorUser = new BehaviorUser();
		BeanUtils.copyProperties(zoneTrace, behaviorUser);
		behaviorUser.setAppId(params.getAppId());
		behaviorUser.setClientType(params.getClientType());
		behaviorUser.setPlatformId(params.getPlatformId());
		behaviorUser.setUid(params.getUid());
		behaviorUser.setZoneId(params.getZoneId());
		behaviorUser.setLastLoginTime(System.currentTimeMillis());
		behaviorUser.setLoginTimesToday(behaviorUser.getLoginTimesToday() + 1);
		behaviorUser.setLoginRecord(zoneTrace.late35Login());
		if (behaviorUser.getFirstInTime() == null || behaviorUser.getFirstInTime() == 0) {
			behaviorUser.setFirstInTime(System.currentTimeMillis());
		}
		if(roleTrace.getFctime() == null || roleTrace.getFctime() == 0){
			if(zoneTrace.getFirstRoleTime() == null || zoneTrace.getFirstRoleTime() == 0){
				roleTrace.setFctime(System.currentTimeMillis());
			}else{
				roleTrace.setFctime(zoneTrace.getFirstRoleTime());
			}
		}
		if(roleTrace.getLttoday() == null)roleTrace.setLttoday(0);
		if(DateUtils.getIntervalDays(roleTrace.getLltime(), System.currentTimeMillis()) == 0){
			roleTrace.setLttoday(roleTrace.getLttoday()+1);
		}else{
			roleTrace.setLttoday(1);
		}
		roleTrace.setLrecord(roleTrace.late35Login());
		
		StringBuffer buffer = new StringBuffer();
		buffer.append(DateUtils.toStringDate(new Date())).append("|").append(params.getClientType()).append("|");
		buffer.append(params.getAppId()).append("|").append(params.getPlatformId()).append("|");
		buffer.append(params.getZoneId()).append("|").append(params.getRoleId()).append("|");
		buffer.append(params.getUid()).append("|").append(params.getDeviceId()).append("|");
		buffer.append(bol2Int(isNewUser)).append("|").append(bol2Int(isGameActiveDevice)).append("|");
		buffer.append(bol2Int(isPlatformActiveDevice)).append("|").append(bol2Int(isZoneActiveDevice)).append("|");
		buffer.append(gameTrace.isBackUser()).append("|").append(gameTrace.isBackPayUser()).append("|");
		buffer.append(zoneTrace.isBackUser()).append("|").append(zoneTrace.isBackPayUser()).append("|");
		buffer.append(gameTrace.isFirstLoginToday()).append("|").append(zoneTrace.isFirstLoginToday()).append("|");
		buffer.append(gameUserKeep.get(2)).append("|").append(gameUserKeep.get(3)).append("|");
		buffer.append(gameUserKeep.get(4)).append("|").append(gameUserKeep.get(5)).append("|").append(gameUserKeep.get(6)).append("|");
		buffer.append(gameUserKeep.get(7)).append("|").append(gameUserKeep.get(14)).append("|");
		buffer.append(gameUserKeep.get(30)).append("|").append(zoneUserKeep.get(2)).append("|");
		buffer.append(zoneUserKeep.get(3)).append("|").append(zoneUserKeep.get(4)).append("|");
		buffer.append(zoneUserKeep.get(5)).append("|").append(zoneUserKeep.get(6)).append("|");
		buffer.append(zoneUserKeep.get(7)).append("|").append(zoneUserKeep.get(14)).append("|");
		buffer.append(zoneUserKeep.get(30)).append("|").append(gameTrace.late7Login()).append("|");
		buffer.append(zoneTrace.late7Login()).append("|").append(params.getRoleName()).append("|");
		buffer.append(DateUtils.toStringDate(new Date(roleTrace.getFctime()))).append("|").append(roleTrace.getLttoday()).append("|");
		buffer.append(roleTrace.getLrecord()).append("|").append(roleTrace.isFirstRoleLogin()).append("|").append(roleTrace.isFirstLoginMonth());
		
		String log = StatsLogger.login(buffer.toString());
		kafkaProducerClient.send(log);
		if (roleTrace.getFltime() == null || roleTrace.getFltime() == 0) {
			roleTrace.setFltime(System.currentTimeMillis());
		}
		roleTrace.setLltime(System.currentTimeMillis());
		behaviorUser.setRoleData(basicRepository.updateBehaviorUserRole(behaviorUser, roleTrace));
		
		gameTrace.setLastLoginTime(System.currentTimeMillis());
		gameTrace.setLoginTimesToday(gameTrace.getLoginTimesToday() == null ? 0 : gameTrace.getLoginTimesToday() + 1);
		gameTrace.setLoginRecord(gameTrace.late35Login());
		if (null == gameTrace.getFirstInTime() || gameTrace.getFirstInTime() == 0) {
			gameTrace.setFirstInTime(System.currentTimeMillis());
		}
		refreshCache(behaviorUser, gameTrace);
		basicRepository.updateUserBehavior(behaviorUser);
		
		HLastLogin hLastLogin = basicRepository.getLastLogin(params.getUid(), params.getPlatformId(), params.getClientType(), params.getAppId(), params.getZoneId());
		if (hLastLogin == null) {
			hLastLogin = new HLastLogin();
			hLastLogin.setUid(params.getUid());
			hLastLogin.setPid(params.getPlatformId());
			hLastLogin.setClientType(params.getClientType());
			hLastLogin.setGameId(params.getAppId());
			hLastLogin.setZoneId(params.getZoneId());
			hLastLogin.setIsPaidUser(0);
			basicRepository.insertHLastLogin(hLastLogin);
		}else {
			basicRepository.updateLastLoginDate(params.getUid(), params.getPlatformId(), params.getClientType(), params.getAppId(), params.getZoneId());
		}
		
		
		//榴莲平台发送post用户信息
		if (params.getPlatformId() == 1068) {
			PlatformGame platformGame = basicRepository.getByPlatformAndAppId(Integer.valueOf(params.getPlatformId()), Long.valueOf(params.getAppId()));
			if (null != platformGame) {
				String appKey = platformGame.getConfigParamsList().get(1);
				String appsecret = platformGame.getConfigParamsList().get(2);
				String privateKey = MD5.encode(appKey + "#" + appsecret);
				String appid = platformGame.getConfigParamsList().get(3);
				String url = platformGame.getConfigParamsList().get(4);
				
				Map<String, Object> postParams = new HashMap<String, Object>();
				postParams.put("appid", appid);
				postParams.put("appkey", appKey);
				postParams.put("userid", params.getUid());
				postParams.put("serverId", params.getZoneId());
				postParams.put("serverName", params.getZoneName());
				postParams.put("roleId", params.getRoleId());
				postParams.put("roleName", params.getRoleName());
				postParams.put("roleLevel", params.getRoleLevel());
				String sign = MD5.encode(appid + appKey + privateKey + params.getUid() + params.getZoneId() + params.getZoneName() + params.getRoleId() + params.getRoleName() + params.getRoleLevel());
				postParams.put("sign", sign);
				try {
					String retStr = HttpUtils.post(url, postParams);
					logger.debug("post userinfo to liulian retStr: " + retStr);
				} catch (Exception e) {
					logger.error("post userinfo to liulian error", e);
				}
			}
		}
	}
	
	
	@EventListener(asynchronous = true)
	public void handleOraderCountEvent(OraderCountEvent event) {
		OrderGeneratePattern params = event.getHelper();
		GameTrace gameTrace = basicRepository.getGameTrace(params.getClientType(), params.getUid(), params.getPlatformId(), params.getAppId());
		int distanceDate = DateUtils.getIntervalDays(DateUtils.parse(gameTrace.loginFirstDay(),"yyyy-MM-dd"),new Date());
		
		StringBuffer buffer = new StringBuffer();
		buffer.append(DateUtils.toStringDate(new Date())).append("|").append(params.getClientType()).append("|");
		buffer.append(params.getAppId()).append("|").append(params.getPlatformId()).append("|");
		buffer.append(params.getZoneId()).append("|").append(params.getRoleId()).append("|");
		buffer.append(params.getUid()).append("|").append(params.getDeviceId()).append("|");
		buffer.append(params.getAmount()).append("|").append(distanceDate).append("|").append(gameTrace.loginFirstDay());
		
		String log = StatsLogger.pay(buffer.toString());
		String key = totalPayHead+DateUtils.format(new Date(),"yyyy-MM-dd")+"_"+params.getAppId()+"_"+params.getPlatformId();
		redisUtil.increment(key,params.getAmount());
		kafkaProducerClient.send(log);
	}
	
	
	@EventListener(asynchronous = true)
	public void handleHeartbeatEvent(HeartbeatEvent event) {
		HeartbeatPattern params = event.getHelper();
		ZoneTrace zoneTrace = basicRepository.getZoneTrace(params.getClientType(), params.getUid(), params.getPlatformId(), params.getAppId(), params.getZoneId());
		RoleTrace roleTrace = basicRepository.getRoleTrace(params.getClientType(), params.getUid(), params.getPlatformId(), params.getAppId(), params.getZoneId(), params.getRoleId(),"");
		if(null == zoneTrace.getLastLoginTime() || null == roleTrace.getLltime()) {
			logger.warn("platformId: {}, uid: {} request hearbeat, but don't login", params.getPlatformId(), params.getUid());
			return;
		}
		
		BehaviorUser behaviorUser = new BehaviorUser();
		BeanUtils.copyProperties(zoneTrace, behaviorUser);
		behaviorUser.setAppId(params.getAppId());
		behaviorUser.setClientType(params.getClientType());
		behaviorUser.setPlatformId(params.getPlatformId());
		behaviorUser.setUid(params.getUid());
		behaviorUser.setZoneId(params.getZoneId());
		behaviorUser.setLastHeartTime(System.currentTimeMillis());
		roleTrace.setLhtime(System.currentTimeMillis());
		behaviorUser.setRoleData(basicRepository.updateBehaviorUserRole(behaviorUser, roleTrace));
		
		refreshCache(behaviorUser, null);
		//更新的是最后心跳时间。我觉得是不需要更新进数据库的。
		//basicRepository.updateUserBehavior(behaviorUser);
		
		StringBuffer buffer = new StringBuffer();
		buffer.append(DateUtils.toStringDate(new Date())).append("|").append(params.getClientType()).append("|");
		buffer.append(params.getAppId()).append("|").append(params.getPlatformId()).append("|");
		buffer.append(params.getZoneId()).append("|").append(params.getRoleId()).append("|");
		buffer.append(params.getUid()).append("|").append(params.getDeviceId()).append("|");
		buffer.append(DateUtils.toStringDate(new Date(zoneTrace.getLastLoginTime()))).append("|");
		buffer.append(roleTrace.getRname());

		String log = StatsLogger.heartbeat(buffer.toString());
		kafkaProducerClient.send(log);
	}
	
	@EventListener(asynchronous = true)
	public void handleLogoutEvent(LogoutEvent event) {
		LogoutPattern params = event.getHelper();
		if (StringUtils.isBlank(params.getZoneId()) || "null".equalsIgnoreCase(params.getZoneId().trim())) {
			logger.warn("platformId: {}, uid: {} zoneId:{} request logout, but don't login", params.getPlatformId(), params.getUid() + "zoneId:" + params.getZoneId());
			return;
		}
		ZoneTrace zoneTrace = basicRepository.getZoneTrace(params.getClientType(), params.getUid(), params.getPlatformId(), params.getAppId(), params.getZoneId());
		RoleTrace roleTrace = basicRepository.getRoleTrace(params.getClientType(), params.getUid(), params.getPlatformId(), params.getAppId(), params.getZoneId(), params.getRoleId(),"");
		
		if(null == zoneTrace.getLastLoginTime() || null == roleTrace.getLltime()) {
			logger.warn("platformId: {}, uid: {} request logout, but don't login", params.getPlatformId(), params.getUid());
			return;
		}
		if (null != zoneTrace.getLastLogoutTime() && zoneTrace.getLastLoginTime() < zoneTrace.getLastLogoutTime()) {
			logger.warn("platformId: {}, uid: {} request logout, but already logout", params.getPlatformId(), params.getUid());
			return;
		}
		
		BehaviorUser behaviorUser = new BehaviorUser();
		BeanUtils.copyProperties(zoneTrace, behaviorUser);
		behaviorUser.setAppId(params.getAppId());
		behaviorUser.setClientType(params.getClientType());
		behaviorUser.setPlatformId(params.getPlatformId());
		behaviorUser.setUid(params.getUid());
		behaviorUser.setZoneId(params.getZoneId());
		behaviorUser.setLastLogoutTime(System.currentTimeMillis());
		roleTrace.setLutime(System.currentTimeMillis());
		behaviorUser.setRoleData(basicRepository.updateBehaviorUserRole(behaviorUser, roleTrace));
		
		refreshCache(behaviorUser, null);
		basicRepository.updateUserBehavior(behaviorUser);
		
		StringBuffer buffer = new StringBuffer();
		buffer.append(DateUtils.toStringDate(new Date())).append("|").append(params.getClientType()).append("|");
		buffer.append(params.getAppId()).append("|").append(params.getPlatformId()).append("|");
		buffer.append(params.getZoneId()).append("|").append(params.getRoleId()).append("|");
		buffer.append(params.getUid()).append("|").append(params.getDeviceId()).append("|");
		buffer.append(DateUtils.toStringDate(new Date(zoneTrace.getLastLoginTime()))).append("|");
		buffer.append(DateUtils.toStringDate(new Date(zoneTrace.getLastHeartTime() == null ? System.currentTimeMillis() : zoneTrace.getLastHeartTime()))).append("|");
		buffer.append(roleTrace.getRname());
		
		String log = StatsLogger.logout(buffer.toString());
		kafkaProducerClient.send(log);
	}
	
	@EventListener(asynchronous = true)
	public void handleQuitEvent(QuitEvent event) {
 		QuitPattern params = event.getHelper();
		if (StringUtils.isBlank(params.getZoneId()) || "null".equalsIgnoreCase(params.getZoneId().trim())) {
			logger.warn("platformId: {}, uid: {} zoneId:{} request logout, but don't login", params.getPlatformId(), params.getUid() + "zoneId:" + params.getZoneId());
			return;
		}
		ZoneTrace zoneTrace = basicRepository.getZoneTrace(params.getClientType(), params.getUid(), params.getPlatformId(), params.getAppId(), params.getZoneId());
		RoleTrace roleTrace = basicRepository.getRoleTrace(params.getClientType(), params.getUid(), params.getPlatformId(), params.getAppId(), params.getZoneId(), params.getRoleId(),"");
		
		if(null == zoneTrace.getLastLoginTime() || null == roleTrace.getLltime()) {
			logger.warn("platformId: {}, uid: {} request quit, but don't login", params.getPlatformId(), params.getUid());
			return;
		}
		if (null != zoneTrace.getLastLogoutTime() && zoneTrace.getLastLoginTime() < zoneTrace.getLastLogoutTime()) {
			logger.warn("platformId: {}, uid: {} request quit, but already quit", params.getPlatformId(), params.getUid());
			return;
		}
		
		BehaviorUser behaviorUser = new BehaviorUser();
		BeanUtils.copyProperties(zoneTrace, behaviorUser);
		behaviorUser.setAppId(params.getAppId());
		behaviorUser.setClientType(params.getClientType());
		behaviorUser.setPlatformId(params.getPlatformId());
		behaviorUser.setUid(params.getUid());
		behaviorUser.setZoneId(params.getZoneId());
		behaviorUser.setLastLogoutTime(System.currentTimeMillis());
		roleTrace.setLutime(System.currentTimeMillis());
		behaviorUser.setRoleData(basicRepository.updateBehaviorUserRole(behaviorUser, roleTrace));
		
		refreshCache(behaviorUser, null);
		basicRepository.updateUserBehavior(behaviorUser);
		
		StringBuffer buffer = new StringBuffer();
		buffer.append(DateUtils.toStringDate(new Date())).append("|").append(params.getClientType()).append("|");
		buffer.append(params.getAppId()).append("|").append(params.getPlatformId()).append("|");
		buffer.append(params.getZoneId()).append("|").append(params.getRoleId()).append("|");
		buffer.append(params.getUid()).append("|").append(params.getDeviceId()).append("|");
		buffer.append(DateUtils.toStringDate(new Date(zoneTrace.getLastLoginTime()))).append("|");
		buffer.append(DateUtils.toStringDate(new Date(zoneTrace.getLastHeartTime() == null ? System.currentTimeMillis() : zoneTrace.getLastHeartTime()))).append("|");
		buffer.append(roleTrace.getRname());
		
		String log = StatsLogger.quit(buffer.toString());
		kafkaProducerClient.send(log);
	}
	
	@EventListener(asynchronous = true)
	public void handleRoleEstablishEvent(RoleEstablishEvent event) {
		RoleEstablishPattern params = event.getHelper();
		
		Role role = new Role(params.getClientType(), params.getAppId(), params.getPlatformId(), params.getZoneId(), params.getRoleId(), params.getRoleName());
		role.setCreateTime(params.getCreatTime());
		basicRepository.insertRole(role);
		
		boolean isDeviceGameFirstEstaRole = false;
		boolean isDeviceZoneFirstEstaRole = false;
		BehaviorDevice behaviorDevice = basicRepository.getByUniqueKey(params.getClientType(), params.getAppId(), params.getDeviceId());
		if(null == behaviorDevice) {
			behaviorDevice = new BehaviorDevice();
			behaviorDevice.setAppId(params.getAppId());
			behaviorDevice.setClientType(params.getClientType());
			behaviorDevice.setDevice(params.getDeviceId());
			behaviorDevice.addRoleZoneId(params.getZoneId());
			basicRepository.save(behaviorDevice);
			
			isDeviceGameFirstEstaRole = true;
			isDeviceZoneFirstEstaRole = true;
		} else {
			if(null == behaviorDevice.getRoleZoneIds() || behaviorDevice.getRoleZoneIds().isEmpty()) {
				isDeviceGameFirstEstaRole = true;
			}
			
			if(null == behaviorDevice.getRoleZoneIds() || !behaviorDevice.getRoleZoneIds().contains(params.getZoneId())) {
				isDeviceZoneFirstEstaRole = true;
				behaviorDevice.addRoleZoneId(params.getZoneId());
				basicRepository.updateDeviceRoleZone(behaviorDevice);
			}
		}
		GameTrace gameTrace = basicRepository.getGameTrace(params.getClientType(), params.getUid(), params.getPlatformId(), params.getAppId());
		ZoneTrace zoneTrace = basicRepository.getZoneTrace(params.getClientType(), params.getUid(), params.getPlatformId(), params.getAppId(), params.getZoneId());
		RoleTrace roleTrace = basicRepository.getRoleTrace(params.getClientType(), params.getUid(), params.getPlatformId(), params.getAppId(), params.getZoneId(), params.getRoleId(),params.getRoleName());
		
		BehaviorUser behaviorUser = new BehaviorUser();
		BeanUtils.copyProperties(zoneTrace, behaviorUser);
		behaviorUser.setAppId(params.getAppId());
		behaviorUser.setClientType(params.getClientType());
		behaviorUser.setPlatformId(params.getPlatformId());
		behaviorUser.setUid(params.getUid());
		behaviorUser.setZoneId(params.getZoneId());
		
		if(roleTrace.getFctime() == null || roleTrace.getFctime() == 0){
			roleTrace.setFctime(zoneTrace.getFirstRoleTime());
		}
		behaviorUser.setRoleData(basicRepository.updateBehaviorUserRole(behaviorUser, roleTrace));
		
		StringBuffer buffer = new StringBuffer();
		buffer.append(DateUtils.toStringDate(params.getCreatTime())).append("|").append(params.getClientType()).append("|");
		buffer.append(params.getAppId()).append("|").append(params.getPlatformId()).append("|");
		buffer.append(params.getZoneId()).append("|").append(params.getRoleId()).append("|");
		buffer.append(params.getUid()).append("|").append(params.getDeviceId()).append("|");
		buffer.append(params.getZoneName()).append("|").append(params.getRoleName()).append("|");
		buffer.append(params.getRoleLevel()).append("|").append(gameTrace.isFirstRole()).append("|");
		buffer.append(zoneTrace.isFirstRole()).append("|").append(bol2Int(isDeviceGameFirstEstaRole)).append("|");
		buffer.append(bol2Int(isDeviceZoneFirstEstaRole));
		
		String log = StatsLogger.roleEstablish(buffer.toString());
		kafkaProducerClient.send(log);
		
		if (behaviorUser.getFirstRoleTime() == null || behaviorUser.getFirstRoleTime() == 0) {
			behaviorUser.setFirstRoleTime(System.currentTimeMillis());
		}
		if (null == gameTrace.getFirstRoleTime() || gameTrace.getFirstRoleTime() == 0) {
			gameTrace.setFirstRoleTime(params.getCreatTime().getTime());
		}
		
		refreshCache(behaviorUser, gameTrace);
		basicRepository.updateUserBehavior(behaviorUser);
		
	}
	
	@EventListener(asynchronous = true)
	public void handleOrderGenerateEvent(OrderGenerateEvent event) {
		
		OrderGeneratePattern params = event.getHelper();
		GameTrace gameTrace = basicRepository.getGameTrace(params.getClientType(), params.getUid(), params.getPlatformId(), params.getAppId());
		ZoneTrace zoneTrace = basicRepository.getZoneTrace(params.getClientType(), params.getUid(), params.getPlatformId(), params.getAppId(), params.getZoneId());
		RoleTrace roleTrace = basicRepository.getRoleTrace(params.getClientType(), params.getUid(), params.getPlatformId(), params.getAppId(), params.getZoneId(), params.getRoleId(),params.getRoleName());
		
		BehaviorUser behaviorUser = new BehaviorUser();
		BeanUtils.copyProperties(zoneTrace, behaviorUser);
		behaviorUser.setAppId(params.getAppId());
		behaviorUser.setClientType(params.getClientType());
		behaviorUser.setPlatformId(params.getPlatformId());
		behaviorUser.setUid(params.getUid());
		behaviorUser.setZoneId(params.getZoneId());
		if (behaviorUser.getFirstPayTime() == null || behaviorUser.getFirstPayTime() == 0) {
			behaviorUser.setFirstPayTime(System.currentTimeMillis());
		}
		behaviorUser.setLastPayTime(System.currentTimeMillis());
		behaviorUser.setPayTimesToday(behaviorUser.getPayTimesToday() + 1);
		
		if(roleTrace.getPttoday() == null)roleTrace.setPttoday(0);
		if(DateUtils.getIntervalDays(roleTrace.getLptime(), System.currentTimeMillis()) == 0){
			roleTrace.setPttoday(roleTrace.getPttoday()+1);
		}else{
			roleTrace.setPttoday(1);
		}
		if(roleTrace.getFptime() == null || roleTrace.getFptime() == 0){
			roleTrace.setFptime(System.currentTimeMillis());
		}
		roleTrace.setPrecord(roleTrace.late35Pay());
		
		StringBuffer buffer = new StringBuffer();
		buffer.append(DateUtils.toStringDate(new Date())).append("|").append(params.getClientType()).append("|");
		buffer.append(params.getAppId()).append("|").append(params.getPlatformId()).append("|");
		buffer.append(params.getZoneId()).append("|").append(params.getRoleId()).append("|");
		buffer.append(params.getUid()).append("|").append(params.getDeviceId()).append("|");
		buffer.append(params.getAmount()).append("|").append(gameTrace.isNewUser()).append("|");
		buffer.append(gameTrace.isFirstPayUser()).append("|").append(zoneTrace.isFirstPayUser()).append("|");
		buffer.append(gameTrace.isFirstPayToday()).append("|").append(zoneTrace.isFirstPayToday()).append("|");
		buffer.append(gameTrace.isFirstPayMonth()).append("|").append(zoneTrace.isFirstPayMonth()).append("|");
		buffer.append(params.getOrderId()).append("|").append(roleTrace.getRname()).append("|");
		buffer.append(roleTrace.getPttoday()).append("|").append(DateUtils.toStringDate(new Date(roleTrace.getFptime()))).append("|");
		buffer.append(roleTrace.getPrecord()).append("|");
		buffer.append(gameTrace.isNewMonthUser());
		String log = StatsLogger.paySuccess(buffer.toString());
		kafkaProducerClient.send(log);

		roleTrace.setLptime(System.currentTimeMillis());
		behaviorUser.setRoleData(basicRepository.updateBehaviorUserRole(behaviorUser, roleTrace));
		
		if (null == gameTrace.getFirstPayTime() || gameTrace.getFirstPayTime() == 0) {
			gameTrace.setFirstPayTime(System.currentTimeMillis());
		}
		gameTrace.setLastPayTime(System.currentTimeMillis());
		gameTrace.setPayTimesToday(behaviorUser.getPayTimesToday() == null ? 0 : behaviorUser.getPayTimesToday() + 1);
		
		refreshCache(behaviorUser, gameTrace);
		basicRepository.updateUserBehavior(behaviorUser);
		
		if (1 == zoneTrace.isFirstPayUser()) {
			HLastLogin hLastLogin = basicRepository.getLastLogin(params.getUid(), params.getPlatformId(), params.getClientType(), params.getAppId(), params.getZoneId());
			if (hLastLogin == null) {
				hLastLogin = new HLastLogin();
				hLastLogin.setUid(params.getUid());
				hLastLogin.setPid(params.getPlatformId());
				hLastLogin.setClientType(params.getClientType());
				hLastLogin.setGameId(params.getAppId());
				hLastLogin.setZoneId(params.getZoneId());
				hLastLogin.setIsPaidUser(1);
				basicRepository.insertHLastLogin(hLastLogin);
			}else {
				basicRepository.updateIsPaidUser(params.getUid(), params.getPlatformId(), params.getClientType(), params.getAppId(), params.getZoneId());
			}
		}
	}
	
	private void refreshCache(BehaviorUser behaviorUser, GameTrace gameTrace){
		if (null != behaviorUser) {
			redisUtil.setKeyValue("zoneTrace" + behaviorUser.getClientType() + "_" + behaviorUser.getUid() + "_" + behaviorUser.getPlatformId() + "_" + behaviorUser.getAppId() + "_" + behaviorUser.getZoneId(), JsonMapper.toJson(behaviorUser));
		}
		if (null != gameTrace) {
			redisUtil.setKeyValue("gameTrace" + behaviorUser.getClientType() + "_" + behaviorUser.getUid() + "_" + behaviorUser.getPlatformId() + "_" + behaviorUser.getAppId(), JsonMapper.toJson(gameTrace));
		}
		if(StringUtils.isNotEmpty(behaviorUser.getRoleData())){
			redisUtil.setKeyValue("roleTrace" + behaviorUser.getClientType() + "_" + behaviorUser.getUid() + "_" + behaviorUser.getPlatformId() + "_" + behaviorUser.getAppId() + "_" + behaviorUser.getZoneId(), behaviorUser.getRoleData());
		}
	}
	
	private int bol2Int(boolean bol) {
		return bol ? 1 : 0;
	}
}
