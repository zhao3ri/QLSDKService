package com.qinglan.sdk.server.domain;

import java.util.UUID;

import javax.annotation.Resource;

import com.qinglan.sdk.server.release.BaseTestCase;
import org.junit.Test;

import com.qinglan.sdk.server.common.JsonMapper;
import com.qinglan.sdk.server.BasicRepository;
import com.qinglan.sdk.server.domain.basic.BehaviorDevice;
import com.qinglan.sdk.server.domain.basic.Role;

public class BasicRepositoryTest extends BaseTestCase {

	@Resource
	private BasicRepository basicRepository;
	
	@Test
	public void saveBehaviorDevice() {
		BehaviorDevice device = new BehaviorDevice();
		device.setGameId(123L);
		device.setClientType(1);
		device.setDevice(UUID.randomUUID().toString());
		basicRepository.save(device);
	}
	
	@Test
	public void getByUniqueKey() {
		Integer clientType = 1;
		Long appId = 123L;
		String deviceId = "cf33d5a7-cd77-4b39-a88a-951fffed5b6e";
		BehaviorDevice device = basicRepository.getByUniqueKey(clientType, appId, deviceId);
		logger.debug("result: {}", JsonMapper.toJson(device));
	}
	
	@Test
	public void updateDevicePlatform() {
		Integer clientType = 1;
		Long appId = 123L;
		String deviceId = "9b893766-97c2-463c-9ea0-a9a033d08764";
		BehaviorDevice device = basicRepository.getByUniqueKey(clientType, appId, deviceId);
		logger.debug("result: {}", JsonMapper.toJson(device));
		
		device.addPlatformId(1001);
		basicRepository.updateDevicePlatform(device);
		
		device = basicRepository.getByUniqueKey(clientType, appId, deviceId);
		logger.debug("result: {}", JsonMapper.toJson(device));
	}
	
	@Test
	public void updateDeviceLoginZone() {
		Integer clientType = 1;
		Long appId = 123L;
		String deviceId = "9b893766-97c2-463c-9ea0-a9a033d08764";
		BehaviorDevice device = basicRepository.getByUniqueKey(clientType, appId, deviceId);
		logger.debug("result: {}", JsonMapper.toJson(device));
		
		device.addLoginZoneId("first");
		basicRepository.updateDeviceLoginZone(device);
		
		device = basicRepository.getByUniqueKey(clientType, appId, deviceId);
		logger.debug("result: {}", JsonMapper.toJson(device));
	}
	
	@Test
	public void updateDeviceRoleZone() {
		Integer clientType = 1;
		Long appId = 123L;
		String deviceId = "9b893766-97c2-463c-9ea0-a9a033d08764";
		BehaviorDevice device = basicRepository.getByUniqueKey(clientType, appId, deviceId);
		logger.debug("result: {}", JsonMapper.toJson(device));
		
		device.addRoleZoneId("first");
		basicRepository.updateDeviceRoleZone(device);
		
		device = basicRepository.getByUniqueKey(clientType, appId, deviceId);
		logger.debug("result: {}", JsonMapper.toJson(device));
	}
	
	@Test
	public void insertRole(){
		Role role = new Role(1, 1L, 1001, "1", "123456", "123456");
		basicRepository.insertRole(role);
	}
	
	
}
