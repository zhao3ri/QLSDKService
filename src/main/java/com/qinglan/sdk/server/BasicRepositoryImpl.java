package com.qinglan.sdk.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.qinglan.sdk.server.data.infrastructure.persistence.MybatisRepository;
import com.qinglan.sdk.server.domain.basic.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import com.qinglan.sdk.server.common.DateUtils;
import com.qinglan.sdk.server.common.JsonMapper;
import com.qinglan.sdk.server.application.basic.redis.RedisUtil;

@Repository
public class BasicRepositoryImpl implements BasicRepository {
    private static final String PARAM_PLATFORM_ID = "platformId";
    private static final String PARAM_UID = "uid";
    private static final String PARAM_GAME_ID = "gameId";
    private static final String PARAM_ZONE_ID = "zoneId";
    private static final String PARAM_ROLE_ID = "roleId";
    private static final String PARAM_ROLE_NAME = "roleName";
    private static final String PARAM_ORDER_ID = "orderId";
    private static final String PARAM_STATUS = "status";
    private static final String PARAM_NOTIFY_STATUS = "notifyStatus";
    private static final String PARAM_UPDATE_TIME = "updateTime";
    private static final String PARAM_ERROR_MSG = "errorMsg";
    private static final String PARAM_ROLE_DATA = "roleData";
    private static final String PARAM_DATA = "data";
    private static final String PARAM_OS = "clientType";
    private static final String PARAM_DEVICE = "device";

    private static final String SEPARATOR = "_";

    @Resource
    private MybatisRepository mybatisRepository;
    @Resource
    private RedisUtil redisUtil;

    @Override
    public int saveAccount(Account account) {
        return mybatisRepository.save(account);
    }

    @Override
    public int insertbatch(List<Account> list) {
        return mybatisRepository.insert(Account.class, "insertbatch", list);
    }

    @Override
    public Account getAccount(int platformId, String uid) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(PARAM_PLATFORM_ID, platformId);
        params.put(PARAM_UID, uid);
        return mybatisRepository.findOne(Account.class, "getAccount", params);
    }


    @Override
    public PlatformGame getByPlatformAndGameId(int channelId, long gameId) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(PARAM_PLATFORM_ID, channelId);
        params.put(PARAM_GAME_ID, gameId);
        PlatformGame platformGame = mybatisRepository.findOne(PlatformGame.class, "getByPlatformAndGameId", params);
        if (platformGame == null) return null;

        //redisUtil.setKeyValue("pg_"+platformId+"_"+gameId, JsonMapper.toJson(platformGame));
        return platformGame;
    }


    @Override
    public Platform getPlatform(int channelId) {
        return mybatisRepository.findOne(Platform.class, "getPlatform", channelId);
    }

    @Override
    public int updatePlatformBalance(Platform platform) {
        return mybatisRepository.update(Platform.class, "updateBalance", platform);
    }


    @Override
    public int saveOrder(Order order) {
        return mybatisRepository.save(order);
    }

    @Override
    public Order getOrderByOrderId(String orderId) {
        if (StringUtils.isBlank(orderId))
            return null;

        return mybatisRepository.findOne(Order.class, "getOrderByOrderId", orderId);
    }

    @Override
    public Order getOrderStatus(String orderId, long gameId, int platformId) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(PARAM_PLATFORM_ID, platformId);
        params.put(PARAM_GAME_ID, gameId);
        params.put(PARAM_ORDER_ID, orderId);
        return mybatisRepository.findOne(Order.class, "getOrderStatus", params);
    }

    @Override
    public int updateStatusPay(Order order) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(PARAM_ORDER_ID, order.getOrderId());
        params.put(PARAM_STATUS, order.getStatus());
        params.put(PARAM_NOTIFY_STATUS, order.getNotifyStatus());
        params.put(PARAM_UPDATE_TIME, order.getUpdateTime());
        params.put(PARAM_ERROR_MSG, order.getErrorMsg());
        return mybatisRepository.update(Order.class, "updateStatusPay", params);
    }

    @Override
    public int updateStatusNotify(Order order) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(PARAM_ORDER_ID, order.getOrderId());
        params.put(PARAM_NOTIFY_STATUS, order.getNotifyStatus());
        params.put(PARAM_ERROR_MSG, order.getErrorMsg());
        params.put(PARAM_UPDATE_TIME, order.getUpdateTime());
        return mybatisRepository.update(Order.class, "updateStatusNotify", params);
    }

    @Override
    public List<String> getNotifyOrder() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(PARAM_STATUS, 2);
        params.put(PARAM_NOTIFY_STATUS, 1);
        return mybatisRepository.findList(Order.class, "getNotifyOrder", params);
    }

    @Override
    public Game getGameById(long id) {
        return mybatisRepository.findOne(Game.class, "getGameById", id);
    }

    @Override
    public List<BehaviorUser> getUserBehavior(Integer clientType, String uid, Integer platformId, Long gameId) {
        List<BehaviorUser> result = new ArrayList<BehaviorUser>();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(PARAM_OS, clientType);
        params.put(PARAM_UID, uid);
        params.put(PARAM_PLATFORM_ID, platformId);
        params.put(PARAM_GAME_ID, gameId);
        List<BehaviorUser> behaviorUsers = mybatisRepository.findList(BehaviorUser.class, "getUserBehavior", params);
        if (!CollectionUtils.isEmpty(behaviorUsers)) {
            for (BehaviorUser behaviorUser : behaviorUsers) {
                BehaviorUser user = JsonMapper.toObject(behaviorUser.getData(), BehaviorUser.class);
                if (user != null) {
                    user.setGameId(behaviorUser.getGameId());
                    user.setClientType(behaviorUser.getClientType());
                    user.setUid(behaviorUser.getUid());
                    user.setPlatformId(behaviorUser.getPlatformId());
                    user.setZoneId(behaviorUser.getZoneId());
                    result.add(user);
                }
            }
        }
        return result;
    }

    public BehaviorUser getUserBehavior(Integer clientType, String uid, Integer platformId, Long appId, String zoneId) {
        BehaviorUser result = null;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(PARAM_OS, clientType);
        params.put(PARAM_UID, uid);
        params.put(PARAM_PLATFORM_ID, platformId);
        params.put(PARAM_GAME_ID, appId);
        params.put(PARAM_ZONE_ID, zoneId);
        String key = "userBehavior_" + clientType + SEPARATOR + uid + SEPARATOR + platformId + SEPARATOR + appId + SEPARATOR + zoneId;
        String userBehaviorJson = redisUtil.getValue(key);
        BehaviorUser behaviorUser = null;
        if (StringUtils.isEmpty(userBehaviorJson)) {
            behaviorUser = mybatisRepository.findOne(BehaviorUser.class, "getUserBehavior", params);
            if (behaviorUser != null) {
                BehaviorUserRedis redis = toRedis(behaviorUser);
                redisUtil.setKeyValue(key, JsonMapper.toJson(redis));
            }
        } else {
            System.out.println("###########################################");
            BehaviorUserRedis redis = JsonMapper.toObject(userBehaviorJson, BehaviorUserRedis.class);
            behaviorUser = fromRedis(redis);
            return behaviorUser;
        }
        if (null != behaviorUser) {
            result = JsonMapper.toObject(behaviorUser.getData(), BehaviorUser.class);
            result.setGameId(behaviorUser.getGameId());
            result.setClientType(behaviorUser.getClientType());
            result.setUid(behaviorUser.getUid());
            result.setPlatformId(behaviorUser.getPlatformId());
            result.setZoneId(behaviorUser.getZoneId());
            if (StringUtils.isNotEmpty(behaviorUser.getRoleData())) {
                result.setRoleData(behaviorUser.getRoleData());
            }
        }
        return result;
    }


    private BehaviorUserRedis toRedis(BehaviorUser behaviorUser) {
        BehaviorUserRedis behaviorUserRedis = new BehaviorUserRedis();
        behaviorUserRedis.setGameId(behaviorUser.getGameId());
        behaviorUserRedis.setClientType(behaviorUser.getClientType());
        behaviorUserRedis.setPlatformId(behaviorUser.getPlatformId());
        behaviorUserRedis.setZoneId(behaviorUser.getZoneId());
        behaviorUserRedis.setData(behaviorUser.getData());
        behaviorUserRedis.setUid(behaviorUser.getUid());
        behaviorUserRedis.setRoleData(behaviorUser.getRoleData());


        behaviorUserRedis.setFirstInTime(behaviorUser.getFirstInTime());
        behaviorUserRedis.setFirstPayTime(behaviorUser.getFirstPayTime());
        behaviorUserRedis.setFirstRoleTime(behaviorUser.getFirstRoleTime());

        behaviorUserRedis.setLastHeartTime(behaviorUser.getLastHeartTime());
        behaviorUserRedis.setLastLogoutTime(behaviorUser.getLastLogoutTime());
        behaviorUserRedis.setLastPayTime(behaviorUser.getLastPayTime());
        behaviorUserRedis.setLastLoginTime(behaviorUser.getLastLoginTime());

        behaviorUserRedis.setLoginTimesToday(behaviorUser.getLoginTimesToday());
        behaviorUserRedis.setLoginRecord(behaviorUser.getLoginRecord());
        behaviorUserRedis.setPayTimesToday(behaviorUser.getPayTimesToday());
        return behaviorUserRedis;
    }

    private BehaviorUser fromRedis(BehaviorUserRedis redis) {
        BehaviorUser behaviorUser = new BehaviorUser();
        behaviorUser.setGameId(redis.getGameId());
        behaviorUser.setClientType(redis.getClientType());
        behaviorUser.setPlatformId(redis.getPlatformId());
        behaviorUser.setZoneId(redis.getZoneId());
        behaviorUser.setData(redis.getData());
        behaviorUser.setUid(redis.getUid());
        behaviorUser.setRoleData(redis.getRoleData());


        behaviorUser.setFirstInTime(redis.getFirstInTime());
        behaviorUser.setFirstPayTime(redis.getFirstPayTime());
        behaviorUser.setFirstRoleTime(redis.getFirstRoleTime());

        behaviorUser.setLastHeartTime(redis.getLastHeartTime());
        behaviorUser.setLastLogoutTime(redis.getLastLogoutTime());
        behaviorUser.setLastPayTime(redis.getLastPayTime());
        behaviorUser.setLastLoginTime(redis.getLastLoginTime());

        behaviorUser.setLoginTimesToday(redis.getLoginTimesToday());
        behaviorUser.setLoginRecord(redis.getLoginRecord());
        behaviorUser.setPayTimesToday(redis.getPayTimesToday());
        return behaviorUser;
    }

    @Override
    public void insertUserBehavior(BehaviorUser behaviorUser) {
        String data = JsonMapper.toJson(behaviorUser);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(PARAM_OS, behaviorUser.getClientType());
        params.put(PARAM_UID, behaviorUser.getUid());
        params.put(PARAM_PLATFORM_ID, behaviorUser.getPlatformId());
        params.put(PARAM_ZONE_ID, behaviorUser.getZoneId());
        params.put(PARAM_GAME_ID, behaviorUser.getGameId());
        params.put(PARAM_DATA, data);
        params.put(PARAM_ROLE_DATA, behaviorUser.getRoleData());
        BehaviorUserRedis redis = toRedis(behaviorUser);
        redisUtil.setKeyValue("userBehavior_" + behaviorUser.getClientType() + "_" + behaviorUser.getUid() + "_" + behaviorUser.getPlatformId() + "_" + behaviorUser.getGameId() + "_" + behaviorUser.getZoneId(), JsonMapper.toJson(redis));
        mybatisRepository.insert(BehaviorUser.class, "insert", params);
    }

    @Override
    public int updateUserBehavior(BehaviorUser behaviorUser) {
        String data = JsonMapper.toJson(behaviorUser);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(PARAM_OS, behaviorUser.getClientType());
        params.put(PARAM_UID, behaviorUser.getUid());
        params.put(PARAM_PLATFORM_ID, behaviorUser.getPlatformId());
        params.put(PARAM_ZONE_ID, behaviorUser.getZoneId());
        params.put(PARAM_GAME_ID, behaviorUser.getGameId());
        params.put(PARAM_DATA, data);
        params.put(PARAM_ROLE_DATA, behaviorUser.getRoleData());
        BehaviorUserRedis redis = toRedis(behaviorUser);
        redisUtil.setKeyValue("userBehavior_" + behaviorUser.getClientType() + "_" + behaviorUser.getUid() + "_" + behaviorUser.getPlatformId() + "_" + behaviorUser.getGameId() + "_" + behaviorUser.getZoneId(), JsonMapper.toJson(redis));
        return mybatisRepository.update(BehaviorUser.class, "update", params);
    }

    @Override
    public void save(BehaviorDevice behaviorDevice) {
        behaviorDevice.jsonAttribute();
        mybatisRepository.insert(BehaviorDevice.class, "insert", behaviorDevice);
    }

    @Override
    public BehaviorDevice getByUniqueKey(Integer clientType, Long appId, String deviceId) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(PARAM_OS, clientType);
        params.put(PARAM_GAME_ID, appId);
        params.put(PARAM_DEVICE, deviceId);
        BehaviorDevice result = mybatisRepository.findOne(BehaviorDevice.class, "findByUnique", params);
        if (null != result) {
            result.rebuildAttribute();
        }
        return result;
    }

    @Override
    public void updateDevicePlatform(BehaviorDevice behaviorDevice) {
        BehaviorDevice data = new BehaviorDevice();
        data.setGameId(behaviorDevice.getGameId());
        data.setClientType(behaviorDevice.getClientType());
        data.setDevice(behaviorDevice.getDevice());
        data.setPlatforms(JsonMapper.toJson(behaviorDevice.getPlatformIds()));
        mybatisRepository.update(BehaviorDevice.class, "update", data);
    }

    @Override
    public void updateDeviceLoginZone(BehaviorDevice behaviorDevice) {
        BehaviorDevice data = new BehaviorDevice();
        data.setGameId(behaviorDevice.getGameId());
        data.setClientType(behaviorDevice.getClientType());
        data.setDevice(behaviorDevice.getDevice());
        data.setLoginZones(JsonMapper.toJson(behaviorDevice.getLoginZoneIds()));
        mybatisRepository.update(BehaviorDevice.class, "update", data);
    }

    @Override
    public void updateDeviceRoleZone(BehaviorDevice behaviorDevice) {
        BehaviorDevice data = new BehaviorDevice();
        data.setGameId(behaviorDevice.getGameId());
        data.setClientType(behaviorDevice.getClientType());
        data.setDevice(behaviorDevice.getDevice());
        data.setRoleZones(JsonMapper.toJson(behaviorDevice.getRoleZoneIds()));
        mybatisRepository.update(BehaviorDevice.class, "update", data);
    }

    @Override
    public void updateDeviceLoginPlatform(BehaviorDevice behaviorDevice) {
        BehaviorDevice data = new BehaviorDevice();
        data.setGameId(behaviorDevice.getGameId());
        data.setClientType(behaviorDevice.getClientType());
        data.setDevice(behaviorDevice.getDevice());
        data.setLoginPlatforms(JsonMapper.toJson(behaviorDevice.getLoginPlatformIds()));
        mybatisRepository.update(BehaviorDevice.class, "update", data);
    }

    @Override
    public GameTrace getGameTrace(Integer clientType, String uid, Integer platformId, Long gameId) {
        String json = redisUtil.getValue("gameTrace" + clientType + "_" + uid + "_" + platformId + "_" + gameId);
        if (StringUtils.isNotBlank(json)) {
            GameTrace gameTrace = JsonMapper.toObject(json, GameTrace.class);
            Integer loginDel = DateUtils.getIntervalDays(gameTrace.getLastLoginTime(), System.currentTimeMillis());
            if (loginDel > 0) {
                String record = Long.toBinaryString(gameTrace.getLoginRecord());
                if (record.length() > 34) {
                    record = record.substring(record.length() - 34);
                }
                gameTrace.setLoginRecord((Long.parseLong(record, 2) << loginDel));
            }
            return gameTrace;
        } else {
            GameTrace gameTrace = new GameTrace();
            List<BehaviorUser> behaviorUsers = getUserBehavior(clientType, uid, platformId, gameId);
            if (!CollectionUtils.isEmpty(behaviorUsers)) {
                Long firstInTime = 0L;
                Long lastLoginTime = 0L;
                Integer loginTimesToday = 0;
                Long lastLogoutTime = 0L;
                Long firstRoleTime = 0L;
                Long firstPayTime = 0L;
                Long lastPayTime = 0L;
                Integer payTimesToday = 0;
                Long loginRecord = 0L;
                for (BehaviorUser behaviorUser : behaviorUsers) {
                    if (firstInTime == 0 && behaviorUser.getFirstInTime() != null) {
                        firstInTime = behaviorUser.getFirstInTime();
                    } else if (behaviorUser.getFirstInTime() != null && firstInTime > behaviorUser.getFirstInTime()) {
                        firstInTime = behaviorUser.getFirstInTime();
                    }

                    if (behaviorUser.getLastLoginTime() != null && behaviorUser.getLastLoginTime() > lastLoginTime) {
                        lastLoginTime = behaviorUser.getLastLoginTime();
                    }

                    if (DateUtils.getIntervalDays(behaviorUser.getLastLoginTime(), new Date().getTime()) == 0) {
                        loginTimesToday += behaviorUser.getLoginTimesToday();
                    }

                    if (behaviorUser.getLastLogoutTime() != null && behaviorUser.getLastLogoutTime() > lastLogoutTime) {
                        lastLogoutTime = behaviorUser.getLastLogoutTime();
                    }

                    if (firstRoleTime == 0 && behaviorUser.getFirstRoleTime() != null) {
                        firstRoleTime = behaviorUser.getFirstRoleTime();
                    } else if (behaviorUser.getFirstRoleTime() != null && firstRoleTime > behaviorUser.getFirstRoleTime()) {
                        firstRoleTime = behaviorUser.getFirstRoleTime();
                    }

                    if (firstPayTime == 0 && behaviorUser.getFirstPayTime() != null) {
                        firstPayTime = behaviorUser.getFirstPayTime();
                    } else if (behaviorUser.getFirstPayTime() != null && firstPayTime > behaviorUser.getFirstPayTime()) {
                        firstPayTime = behaviorUser.getFirstPayTime();
                    }

                    if (behaviorUser.getLastPayTime() != null && behaviorUser.getLastPayTime() > lastPayTime) {
                        lastPayTime = behaviorUser.getLastPayTime();
                    }

                    if (DateUtils.getIntervalDays(behaviorUser.getLastPayTime(), new Date().getTime()) == 0) {
                        payTimesToday += behaviorUser.getPayTimesToday();
                    }

                    if (behaviorUser.getLoginRecord() != null) {
                        loginRecord = loginRecord | behaviorUser.late35Login();
                    }
                }
                gameTrace.setFirstInTime(firstInTime);
                gameTrace.setLastLoginTime(lastLoginTime);
                gameTrace.setLoginTimesToday(loginTimesToday);
                gameTrace.setLastLogoutTime(lastLogoutTime);
                gameTrace.setFirstRoleTime(firstRoleTime);
                gameTrace.setFirstPayTime(firstPayTime);
                gameTrace.setLastPayTime(lastPayTime);
                gameTrace.setPayTimesToday(payTimesToday);
                gameTrace.setLoginRecord(loginRecord);
            }
            return gameTrace;
        }
    }

    @Override
    public RoleTrace getRoleTrace(Integer clientType, String uid,
                                  Integer platformId, Long gameId, String zoneId, String roleId, String roleName) {
        RoleTrace roleTrace = null;
        String json = redisUtil.getValue("roleTrace" + clientType + SEPARATOR + uid + SEPARATOR + platformId + SEPARATOR + gameId + SEPARATOR + zoneId);
        if (StringUtils.isNoneBlank(json)) {
            roleTrace = getRoleTraceByJson(json, roleId);
            if (roleTrace != null) return roleTrace;
        }
        BehaviorUser behaviorUser = getUserBehavior(clientType, uid, platformId, gameId, zoneId);
        if (StringUtils.isNotEmpty(behaviorUser.getRoleData())) {
            roleTrace = getRoleTraceByJson(behaviorUser.getRoleData(), roleId);
        }
        if (roleTrace == null) {
            roleTrace = new RoleTrace();
            roleTrace.setRid(roleId);
            roleTrace.setRname(roleName);
        }
        return roleTrace;
    }

    private RoleTrace getRoleTraceByJson(String json, String roleId) {
        List<RoleTrace> roleTraceList = JsonMapper.stringToList(json, RoleTrace.class);
        if (!CollectionUtils.isEmpty(roleTraceList)) {
            for (RoleTrace roleTrace : roleTraceList) {
                if (roleId.equals(roleTrace.getRid())) {
                    return roleTrace;
                }
            }
        }
        return null;
    }

    @Override
    public String updateBehaviorUserRole(BehaviorUser behaviorUser, RoleTrace roleTrace) {
        List<RoleTrace> roleData = new ArrayList<RoleTrace>();
        boolean flag = true;
        behaviorUser = getUserBehavior(behaviorUser.getClientType(), behaviorUser.getUid(), behaviorUser.getPlatformId(), behaviorUser.getGameId(), behaviorUser.getZoneId());
        if (behaviorUser != null && StringUtils.isNotEmpty(behaviorUser.getRoleData())) {
            List<RoleTrace> roleTraceList = JsonMapper.stringToList(behaviorUser.getRoleData(), RoleTrace.class);
            if (!CollectionUtils.isEmpty(roleTraceList)) {
                for (RoleTrace obj : roleTraceList) {
                    if (roleTrace.getRid().equals(obj.getRid())) {
                        BeanUtils.copyProperties(roleTrace, obj);
                        flag = false;
                    }
                    roleData.add(obj);
                }
            }
        }
        if (flag) roleData.add(roleTrace);
        return JsonMapper.toJson(roleData);
    }


    @Override
    public ZoneTrace getZoneTrace(Integer clientType, String uid, Integer platformId, Long gameId, String zoneId) {
        String json = redisUtil.getValue("zoneTrace" + clientType + "_" + uid + "_" + platformId + "_" + gameId + "_" + zoneId);
        if (StringUtils.isNotBlank(json)) {
            return JsonMapper.toObject(json, ZoneTrace.class);
        } else {
            ZoneTrace zoneTrace = new ZoneTrace();
            BehaviorUser behaviorUser = getUserBehavior(clientType, uid, platformId, gameId, zoneId);
            if (null != behaviorUser) {
                BeanUtils.copyProperties(behaviorUser, zoneTrace);
            } else {
                behaviorUser = new BehaviorUser();
                behaviorUser.setGameId(gameId);
                behaviorUser.setClientType(clientType);
                behaviorUser.setUid(uid);
                behaviorUser.setZoneId(zoneId);
                behaviorUser.setPlatformId(platformId);
                insertUserBehavior(behaviorUser);
            }
            return zoneTrace;
        }
    }


    @Override
    public HLastLogin getLastLogin(String uid, Integer pid, Integer clientType, Long gameId, String zoneId) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("uid", uid);
        params.put("pid", pid);
        params.put("clientType", clientType);
        params.put("gameId", gameId);
        params.put("zoneId", zoneId);
        return mybatisRepository.findOne(HLastLogin.class, "findOne", params);
    }

    @Override
    public int insertHLastLogin(HLastLogin lastLogin) {
        return mybatisRepository.insert(HLastLogin.class, "insert", lastLogin);
    }

    @Override
    public int updateIsPaidUser(String uid, Integer pid, Integer clientType, Long gameId, String zoneId) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("uid", uid);
        params.put("pid", pid);
        params.put("clientType", clientType);
        params.put("gameId", gameId);
        params.put("zoneId", zoneId);
        return mybatisRepository.update(HLastLogin.class, "updateIsPaidUser", params);
    }

    @Override
    public int updateLastLoginDate(String uid, Integer pid, Integer clientType, Long gameId, String zoneId) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("uid", uid);
        params.put("pid", pid);
        params.put("clientType", clientType);
        params.put("gameId", gameId);
        params.put("zoneId", zoneId);
        return mybatisRepository.update(HLastLogin.class, "updateLastLoginDate", params);
    }

    @Override
    public void insertRole(Role role) {
        mybatisRepository.insert(Role.class, "insert", role);
    }

    /**
     * 获取创建角色的时间
     */
    @Override
    public Account getRoleCreateTime(Long appId, Integer platformId,
                                     String zoneId, String roleId, String roleName) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(PARAM_PLATFORM_ID, platformId);
        params.put(PARAM_GAME_ID, appId);
        params.put(PARAM_ZONE_ID, zoneId);
        params.put(PARAM_ROLE_ID, roleId);
        params.put(PARAM_ROLE_NAME, roleName);
        return mybatisRepository.findOne(Account.class, "getRoleCreateTime", params);
    }

}
