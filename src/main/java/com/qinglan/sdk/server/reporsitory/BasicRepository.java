package com.qinglan.sdk.server.reporsitory;

import com.qinglan.sdk.server.domain.*;
import com.qinglan.sdk.server.dao.*;

import java.util.List;


public interface BasicRepository {
	//查询创建角色时间
	Account getRoleCreateTime(Long appId, Integer platformId, String zoneId, String roleId, String roleName);
	/*******用户表*******/
	int saveAccount(Account account);
	
	int insertbatch(List<Account> list);
	
	Account getAccount(int platformId, String uid);
	
	/*******游戏表*******/
	Game getGameById(long id);
	
	PlatformGame getByPlatformAndAppId(int platformId, long appId);

	Platform getPlatform(int platformId);

	int updatePlatformBalance(Platform platform);
	
	/*******订单表*******/
	int saveOrder(Order order);
	
	Order getOrderByOrderId(String orderId);
	
	int updateStatusPay(Order order);
	
	int updateStatusNotify(Order order);
	
	List<String> getNotifyOrder();
	
	/*******行为表*******/
	List<BehaviorUser> getUserBehavior(Integer clientType, String uid, Integer platformId, Long appId);
	
	void insertUserBehavior(BehaviorUser behaviorUserZone);
	
	int updateUserBehavior(BehaviorUser behaviorUser);
	
	
	void save(BehaviorDevice behaviorDevice);
	
	BehaviorDevice getByUniqueKey(Integer clientType, Long appId, String deviceId);
	
	void updateDevicePlatform(BehaviorDevice behaviorDevice);
	
	void updateDeviceLoginPlatform(BehaviorDevice behaviorDevice);
	
	void updateDeviceLoginZone(BehaviorDevice behaviorDevice);
	
	void updateDeviceRoleZone(BehaviorDevice behaviorDevice);
	
	GameTrace getGameTrace(Integer clientType, String uid, Integer platformId, Long appId);
	
	ZoneTrace getZoneTrace(Integer clientType, String uid, Integer platformId, Long appId, String zoneId);
	
	RoleTrace getRoleTrace(Integer clientType, String uid, Integer platformId, Long appId, String zoneId, String roleId, String roleName);
	
	String updateBehaviorUserRole(BehaviorUser behaviorUser, RoleTrace roleTrace);
	
	/*******用户日志记录*******/
	HLastLogin getLastLogin(String uid, Integer pid, Integer clientType, Long gameId, String zoneId);
	int insertHLastLogin(HLastLogin lastLogin);
	int updateIsPaidUser(String uid, Integer pid, Integer clientType, Long gameId, String zoneId);
	int updateLastLoginDate(String uid, Integer pid, Integer clientType, Long gameId, String zoneId);
	
	void insertRole(Role role);
}
