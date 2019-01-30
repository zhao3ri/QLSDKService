package com.qinglan.sdk.server.application.listener;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import com.qinglan.sdk.server.common.DateUtils;
import com.qinglan.sdk.server.common.HttpUtils;
import com.qinglan.sdk.server.common.JsonMapper;
import com.qinglan.sdk.server.common.MD5;
import com.qinglan.sdk.server.data.annotation.event.EventListener;
import com.qinglan.sdk.server.stats.StatsLogger;
import com.qinglan.sdk.server.domain.basic.*;
import com.qinglan.sdk.server.dto.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import com.qinglan.sdk.server.application.kafka.KafkaProducerClient;
import com.qinglan.sdk.server.application.redis.RedisUtil;
import com.qinglan.sdk.server.BasicRepository;
import com.qinglan.sdk.server.domain.basic.ChannelGameEntity;
import com.qinglan.sdk.server.domain.basic.event.HeartbeatEvent;
import com.qinglan.sdk.server.domain.basic.event.InitialEvent;
import com.qinglan.sdk.server.domain.basic.event.LoginEvent;
import com.qinglan.sdk.server.domain.basic.event.LogoutEvent;
import com.qinglan.sdk.server.domain.basic.event.OraderCountEvent;
import com.qinglan.sdk.server.domain.basic.event.OrderGenerateEvent;
import com.qinglan.sdk.server.domain.basic.event.QuitEvent;
import com.qinglan.sdk.server.domain.basic.event.RoleEstablishEvent;

@Component
@EventListener
public class GameOperateListener {

    private final static Logger logger = LoggerFactory.getLogger(GameOperateListener.class);

    @Resource
    private KafkaProducerClient kafkaProducerClient;
    @Resource
    private BasicRepository basicRepository;
    @Resource
    private RedisUtil redisUtil;
    private String totalPayHead = "totalpay_";

    @EventListener(asynchronous = true)
    public void handleInitialEvent(InitialEvent event) {
        InitialPattern params = event.getHelper();
        try {
            boolean isFirstInitGame = false;
            boolean isFirstInitChannel = false;
            BehaviorDevice behaviorDevice = basicRepository.getByUniqueKey(params.getClientType(), params.getGameId(), params.getDeviceId());
            if (null == behaviorDevice) {
                behaviorDevice = new BehaviorDevice();
                behaviorDevice.setGameId(params.getGameId());
                behaviorDevice.setClientType(params.getClientType());
                behaviorDevice.setDevice(params.getDeviceId());
                behaviorDevice.addChannelId(params.getChannelId());
                basicRepository.save(behaviorDevice);

                isFirstInitGame = true;
                isFirstInitChannel = true;
            } else {
                if (null == behaviorDevice.getChannelIds() || !behaviorDevice.getChannelIds().contains(params.getChannelId())) {
                    behaviorDevice.addChannelId(params.getChannelId());
                    basicRepository.updateDevicePlatform(behaviorDevice);
                    isFirstInitChannel = true;
                }
            }
            String log = StatsLogger.initial(params, isFirstInitGame, isFirstInitChannel);
            kafkaProducerClient.send(log);
        } catch (Exception e) {
            logger.error("handle Initial Event exception", e);
        }
    }

    @EventListener(asynchronous = true)
    public void handleLoginEvent(LoginEvent event) {
        LoginPattern params = event.getHelper();

        boolean isNewUser = false;
        Account account = basicRepository.getAccount(params.getChannelId(), params.getUid());
        if (null == account) {
            isNewUser = true;
            account = new Account(params.getChannelId(), params.getUid());
            basicRepository.saveAccount(account);
        }

        BehaviorDevice behaviorDevice = basicRepository.getByUniqueKey(params.getClientType(), params.getGameId(), params.getDeviceId());
        boolean isGameActiveDevice = false;
        boolean isPlatformActiveDevice = false;
        boolean isZoneActiveDevice = false;

        if (null == behaviorDevice) {
            behaviorDevice = new BehaviorDevice();
            behaviorDevice.setGameId(params.getGameId());
            behaviorDevice.setClientType(params.getClientType());
            behaviorDevice.setDevice(params.getDeviceId());

            behaviorDevice.addLoginChannelId(params.getChannelId());
            behaviorDevice.addLoginZoneId(params.getZoneId());
            basicRepository.save(behaviorDevice);

            isGameActiveDevice = true;
            isPlatformActiveDevice = true;
            isZoneActiveDevice = true;
        } else {
            if (null == behaviorDevice.getLoginChannelIds() || !behaviorDevice.getLoginChannelIds().contains(params.getChannelId())) {
                behaviorDevice.addLoginChannelId(params.getChannelId());
                basicRepository.updateDeviceLoginPlatform(behaviorDevice);
                isPlatformActiveDevice = true;
            }

            if (null == behaviorDevice.getLoginZoneIds() || !behaviorDevice.getLoginZoneIds().contains(params.getZoneId())) {
                behaviorDevice.addLoginZoneId(params.getZoneId());
                basicRepository.updateDeviceLoginZone(behaviorDevice);
                isZoneActiveDevice = true;
                isGameActiveDevice = true;
            }
        }
        GameTrace gameTrace = basicRepository.getGameTrace(params.getClientType(), params.getUid(), params.getChannelId(), params.getGameId());
        ZoneTrace zoneTrace = basicRepository.getZoneTrace(params.getClientType(), params.getUid(), params.getChannelId(), params.getGameId(), params.getZoneId());
        RoleTrace roleTrace = basicRepository.getRoleTrace(params.getClientType(), params.getUid(), params.getChannelId(), params.getGameId(), params.getZoneId(), params.getRoleId(), params.getRoleName());

        BehaviorUser behaviorUser = new BehaviorUser();
        BeanUtils.copyProperties(zoneTrace, behaviorUser);
        behaviorUser.setGameId(params.getGameId());
        behaviorUser.setClientType(params.getClientType());
        behaviorUser.setChannelId(params.getChannelId());
        behaviorUser.setUid(params.getUid());
        behaviorUser.setZoneId(params.getZoneId());
        behaviorUser.setLastLoginTime(System.currentTimeMillis());
        behaviorUser.setLoginTimesToday(behaviorUser.getLoginTimesToday() + 1);
        behaviorUser.setLoginRecord(zoneTrace.late35Login());
        if (behaviorUser.getFirstInTime() == null || behaviorUser.getFirstInTime() == 0) {
            behaviorUser.setFirstInTime(System.currentTimeMillis());
        }
        if (roleTrace.getFirstRoleTime() == null || roleTrace.getFirstRoleTime() == 0) {
            if (zoneTrace.getFirstRoleTime() == null || zoneTrace.getFirstRoleTime() == 0) {
                roleTrace.setFirstRoleTime(System.currentTimeMillis());
            } else {
                roleTrace.setFirstRoleTime(zoneTrace.getFirstRoleTime());
            }
        }
        if (roleTrace.getLoginTimesToday() == null) roleTrace.setLoginTimesToday(0);
        if (DateUtils.getIntervalDays(roleTrace.getLastLoginTime(), System.currentTimeMillis()) == 0) {
            roleTrace.setLoginTimesToday(roleTrace.getLoginTimesToday() + 1);
        } else {
            roleTrace.setLoginTimesToday(1);
        }
        roleTrace.setLogin35DaysRecord(roleTrace.late35Login());

        String log = StatsLogger.login(params, gameTrace, zoneTrace, roleTrace, isNewUser, isGameActiveDevice, isPlatformActiveDevice, isZoneActiveDevice);
        kafkaProducerClient.send(log);
        updateLastLoginDate(params, gameTrace, roleTrace, behaviorUser);

        //榴莲平台发送post用户信息
        if (params.getChannelId() == 1068) {
            ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(params.getChannelId()), Long.valueOf(params.getGameId()));
            if (null != channelGame) {
                String appKey = channelGame.getConfigParamsList().get(1);
                String appsecret = channelGame.getConfigParamsList().get(2);
                String privateKey = MD5.encode(appKey + "#" + appsecret);
                String appid = channelGame.getConfigParamsList().get(3);
                String url = channelGame.getConfigParamsList().get(4);

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

    private void updateLastLoginDate(LoginPattern params, GameTrace gameTrace, RoleTrace roleTrace, BehaviorUser behaviorUser) {
        if (roleTrace.getFirstInTime() == null || roleTrace.getFirstInTime() == 0) {
            roleTrace.setFirstInTime(System.currentTimeMillis());
        }
        roleTrace.setLastLoginTime(System.currentTimeMillis());
        behaviorUser.setRoleData(basicRepository.updateBehaviorUserRole(behaviorUser, roleTrace));

        gameTrace.setLastLoginTime(System.currentTimeMillis());
        gameTrace.setLoginTimesToday(gameTrace.getLoginTimesToday() == null ? 0 : gameTrace.getLoginTimesToday() + 1);
        gameTrace.setLogin35DaysRecord(gameTrace.late35Login());
        if (null == gameTrace.getFirstInTime() || gameTrace.getFirstInTime() == 0) {
            gameTrace.setFirstInTime(System.currentTimeMillis());
        }
        refreshCache(behaviorUser, gameTrace);
        basicRepository.updateUserBehavior(behaviorUser);

        HLastLogin hLastLogin = basicRepository.getLastLogin(params.getUid(), params.getChannelId(), params.getClientType(), params.getGameId(), params.getZoneId());
        if (hLastLogin == null) {
            hLastLogin = new HLastLogin();
            hLastLogin.setUid(params.getUid());
            hLastLogin.setPid(params.getChannelId());
            hLastLogin.setClientType(params.getClientType());
            hLastLogin.setGameId(params.getGameId());
            hLastLogin.setZoneId(params.getZoneId());
            hLastLogin.setIsPaidUser(0);
            basicRepository.insertHLastLogin(hLastLogin);
        } else {
            basicRepository.updateLastLoginDate(params.getUid(), params.getChannelId(), params.getClientType(), params.getGameId(), params.getZoneId());
        }
    }

    @EventListener(asynchronous = true)
    public void handleOraderCountEvent(OraderCountEvent event) {
        OrderGenerateRequest params = event.getHelper();
        GameTrace gameTrace = basicRepository.getGameTrace(params.getClientType(), params.getUid(), params.getChannelId(), params.getGameId());
        int distanceDate = DateUtils.getIntervalDays(DateUtils.parse(gameTrace.loginFirstDay(), "yyyy-MM-dd"), new Date());

        String log = StatsLogger.pay(params,distanceDate, gameTrace.loginFirstDay());
        String key = totalPayHead + DateUtils.format(new Date(), "yyyy-MM-dd") + "_" + params.getGameId() + "_" + params.getChannelId();
        redisUtil.increment(key, params.getAmount());
        kafkaProducerClient.send(log);
    }


    @EventListener(asynchronous = true)
    public void handleHeartbeatEvent(HeartbeatEvent event) {
        HeartbeatPattern params = event.getHelper();
        ZoneTrace zoneTrace = basicRepository.getZoneTrace(params.getClientType(), params.getUid(), params.getChannelId(), params.getGameId(), params.getZoneId());
        RoleTrace roleTrace = basicRepository.getRoleTrace(params.getClientType(), params.getUid(), params.getChannelId(), params.getGameId(), params.getZoneId(), params.getRoleId(), "");
        if (null == zoneTrace.getLastLoginTime() || null == roleTrace.getLastLoginTime()) {
            logger.warn("channelId: {}, uid: {} request hearbeat, but don't login", params.getChannelId(), params.getUid());
            return;
        }

        BehaviorUser behaviorUser = new BehaviorUser();
        BeanUtils.copyProperties(zoneTrace, behaviorUser);
        behaviorUser.setGameId(params.getGameId());
        behaviorUser.setClientType(params.getClientType());
        behaviorUser.setChannelId(params.getChannelId());
        behaviorUser.setUid(params.getUid());
        behaviorUser.setZoneId(params.getZoneId());
        behaviorUser.setLastHeartTime(System.currentTimeMillis());
        roleTrace.setLastHeartTime(System.currentTimeMillis());
        behaviorUser.setRoleData(basicRepository.updateBehaviorUserRole(behaviorUser, roleTrace));

        refreshCache(behaviorUser, null);
        //更新的是最后心跳时间。我觉得是不需要更新进数据库的。
        // 哎哟我去，这是之前谁认为的…不管了，你们自己看着办吧
        //basicRepository.updateUserBehavior(behaviorUser);

        String log = StatsLogger.heartbeat(params, DateUtils.toStringDate(new Date(zoneTrace.getLastLoginTime())), roleTrace.getRname());
        kafkaProducerClient.send(log);
    }

    @EventListener(asynchronous = true)
    public void handleLogoutEvent(LogoutEvent event) {
        LogoutPattern params = event.getHelper();
        if (StringUtils.isBlank(params.getZoneId()) || "null".equalsIgnoreCase(params.getZoneId().trim())) {
            logger.warn("channelId: {}, uid: {} zoneId:{} request logout, but don't login", params.getChannelId(), params.getUid() + "zoneId:" + params.getZoneId());
            return;
        }
        ZoneTrace zoneTrace = basicRepository.getZoneTrace(params.getClientType(), params.getUid(), params.getChannelId(), params.getGameId(), params.getZoneId());
        RoleTrace roleTrace = basicRepository.getRoleTrace(params.getClientType(), params.getUid(), params.getChannelId(), params.getGameId(), params.getZoneId(), params.getRoleId(), "");

        if (null == zoneTrace.getLastLoginTime() || null == roleTrace.getLastLoginTime()) {
            logger.warn("channelId: {}, uid: {} request logout, but don't login", params.getChannelId(), params.getUid());
            return;
        }
        if (null != zoneTrace.getLastLogoutTime() && zoneTrace.getLastLoginTime() < zoneTrace.getLastLogoutTime()) {
            logger.warn("channelId: {}, uid: {} request logout, but already logout", params.getChannelId(), params.getUid());
            return;
        }

        BehaviorUser behaviorUser = new BehaviorUser();
        BeanUtils.copyProperties(zoneTrace, behaviorUser);
        behaviorUser.setGameId(params.getGameId());
        behaviorUser.setClientType(params.getClientType());
        behaviorUser.setChannelId(params.getChannelId());
        behaviorUser.setUid(params.getUid());
        behaviorUser.setZoneId(params.getZoneId());
        behaviorUser.setLastLogoutTime(System.currentTimeMillis());
        roleTrace.setLastLogoutTime(System.currentTimeMillis());
        behaviorUser.setRoleData(basicRepository.updateBehaviorUserRole(behaviorUser, roleTrace));

        refreshCache(behaviorUser, null);
        basicRepository.updateUserBehavior(behaviorUser);

        String log = StatsLogger.logout(params, DateUtils.toStringDate(new Date(zoneTrace.getLastLoginTime())),
                DateUtils.toStringDate(new Date(zoneTrace.getLastHeartTime() == null ? System.currentTimeMillis() : zoneTrace.getLastHeartTime())), roleTrace.getRname());
        kafkaProducerClient.send(log);
    }

    @EventListener(asynchronous = true)
    public void handleQuitEvent(QuitEvent event) {
        QuitPattern params = event.getHelper();
        if (StringUtils.isBlank(params.getZoneId()) || "null".equalsIgnoreCase(params.getZoneId().trim())) {
            logger.warn("channelId: {}, uid: {} zoneId:{} request logout, but don't login", params.getChannelId(), params.getUid() + "zoneId:" + params.getZoneId());
            return;
        }
        ZoneTrace zoneTrace = basicRepository.getZoneTrace(params.getClientType(), params.getUid(), params.getChannelId(), params.getGameId(), params.getZoneId());
        RoleTrace roleTrace = basicRepository.getRoleTrace(params.getClientType(), params.getUid(), params.getChannelId(), params.getGameId(), params.getZoneId(), params.getRoleId(), "");

        if (null == zoneTrace.getLastLoginTime() || null == roleTrace.getLastLoginTime()) {
            logger.warn("channelId: {}, uid: {} request quit, but don't login", params.getChannelId(), params.getUid());
            return;
        }
        if (null != zoneTrace.getLastLogoutTime() && zoneTrace.getLastLoginTime() < zoneTrace.getLastLogoutTime()) {
            logger.warn("channelId: {}, uid: {} request quit, but already quit", params.getChannelId(), params.getUid());
            return;
        }

        BehaviorUser behaviorUser = new BehaviorUser();
        BeanUtils.copyProperties(zoneTrace, behaviorUser);
        behaviorUser.setGameId(params.getGameId());
        behaviorUser.setClientType(params.getClientType());
        behaviorUser.setChannelId(params.getChannelId());
        behaviorUser.setUid(params.getUid());
        behaviorUser.setZoneId(params.getZoneId());
        behaviorUser.setLastLogoutTime(System.currentTimeMillis());
        roleTrace.setLastLogoutTime(System.currentTimeMillis());
        behaviorUser.setRoleData(basicRepository.updateBehaviorUserRole(behaviorUser, roleTrace));

        refreshCache(behaviorUser, null);
        basicRepository.updateUserBehavior(behaviorUser);

        String log = StatsLogger.quit(params, DateUtils.toStringDate(new Date(zoneTrace.getLastLoginTime())),
                DateUtils.toStringDate(new Date(zoneTrace.getLastHeartTime() == null ? System.currentTimeMillis() : zoneTrace.getLastHeartTime())), roleTrace.getRname());
        kafkaProducerClient.send(log);
    }

    @EventListener(asynchronous = true)
    public void handleRoleEstablishEvent(RoleEstablishEvent event) {
        RoleCreatePattern params = event.getHelper();

        Role role = new Role(params.getClientType(), params.getGameId(), params.getChannelId(), params.getZoneId(), params.getRoleId(), params.getRoleName());
        role.setCreateTime(params.getCreatTime());
        basicRepository.insertRole(role);

        boolean isDeviceGameFirstEstaRole = false;
        boolean isDeviceZoneFirstEstaRole = false;
        BehaviorDevice behaviorDevice = basicRepository.getByUniqueKey(params.getClientType(), params.getGameId(), params.getDeviceId());
        if (null == behaviorDevice) {
            behaviorDevice = new BehaviorDevice();
            behaviorDevice.setGameId(params.getGameId());
            behaviorDevice.setClientType(params.getClientType());
            behaviorDevice.setDevice(params.getDeviceId());
            behaviorDevice.addRoleZoneId(params.getZoneId());
            basicRepository.save(behaviorDevice);

            isDeviceGameFirstEstaRole = true;
            isDeviceZoneFirstEstaRole = true;
        } else {
            if (null == behaviorDevice.getRoleZoneIds() || behaviorDevice.getRoleZoneIds().isEmpty()) {
                isDeviceGameFirstEstaRole = true;
            }

            if (null == behaviorDevice.getRoleZoneIds() || !behaviorDevice.getRoleZoneIds().contains(params.getZoneId())) {
                isDeviceZoneFirstEstaRole = true;
                behaviorDevice.addRoleZoneId(params.getZoneId());
                basicRepository.updateDeviceRoleZone(behaviorDevice);
            }
        }
        GameTrace gameTrace = basicRepository.getGameTrace(params.getClientType(), params.getUid(), params.getChannelId(), params.getGameId());
        ZoneTrace zoneTrace = basicRepository.getZoneTrace(params.getClientType(), params.getUid(), params.getChannelId(), params.getGameId(), params.getZoneId());
        RoleTrace roleTrace = basicRepository.getRoleTrace(params.getClientType(), params.getUid(), params.getChannelId(), params.getGameId(), params.getZoneId(), params.getRoleId(), params.getRoleName());

        BehaviorUser behaviorUser = new BehaviorUser();
        BeanUtils.copyProperties(zoneTrace, behaviorUser);
        behaviorUser.setGameId(params.getGameId());
        behaviorUser.setClientType(params.getClientType());
        behaviorUser.setChannelId(params.getChannelId());
        behaviorUser.setUid(params.getUid());
        behaviorUser.setZoneId(params.getZoneId());

        if (roleTrace.getFirstRoleTime() == null || roleTrace.getFirstRoleTime() == 0) {
            roleTrace.setFirstRoleTime(zoneTrace.getFirstRoleTime());
        }
        behaviorUser.setRoleData(basicRepository.updateBehaviorUserRole(behaviorUser, roleTrace));

        String log = StatsLogger.roleCreate(params, gameTrace.isFirstRole(), zoneTrace.isFirstRole(), isDeviceGameFirstEstaRole, isDeviceZoneFirstEstaRole);
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

        OrderGenerateRequest params = event.getHelper();
        GameTrace gameTrace = basicRepository.getGameTrace(params.getClientType(), params.getUid(), params.getChannelId(), params.getGameId());
        ZoneTrace zoneTrace = basicRepository.getZoneTrace(params.getClientType(), params.getUid(), params.getChannelId(), params.getGameId(), params.getZoneId());
        RoleTrace roleTrace = basicRepository.getRoleTrace(params.getClientType(), params.getUid(), params.getChannelId(), params.getGameId(), params.getZoneId(), params.getRoleId(), params.getRoleName());

        BehaviorUser behaviorUser = new BehaviorUser();
        BeanUtils.copyProperties(zoneTrace, behaviorUser);
        behaviorUser.setGameId(params.getGameId());
        behaviorUser.setClientType(params.getClientType());
        behaviorUser.setChannelId(params.getChannelId());
        behaviorUser.setUid(params.getUid());
        behaviorUser.setZoneId(params.getZoneId());
        if (behaviorUser.getFirstPayTime() == null || behaviorUser.getFirstPayTime() == 0) {
            behaviorUser.setFirstPayTime(System.currentTimeMillis());
        }
        behaviorUser.setLastPayTime(System.currentTimeMillis());
        behaviorUser.setPayTimesToday(behaviorUser.getPayTimesToday() + 1);

        if (roleTrace.getPayTimesToday() == null) roleTrace.setPayTimesToday(0);
        if (DateUtils.getIntervalDays(roleTrace.getLastPayTime(), System.currentTimeMillis()) == 0) {
            roleTrace.setPayTimesToday(roleTrace.getPayTimesToday() + 1);
        } else {
            roleTrace.setPayTimesToday(1);
        }
        if (roleTrace.getFirstPayTime() == null || roleTrace.getFirstPayTime() == 0) {
            roleTrace.setFirstPayTime(System.currentTimeMillis());
        }
        roleTrace.setPay35DaysRecord(roleTrace.late35Pay());

        String log = StatsLogger.paySuccess(params,gameTrace,zoneTrace,roleTrace);
        kafkaProducerClient.send(log);

        roleTrace.setLastPayTime(System.currentTimeMillis());
        behaviorUser.setRoleData(basicRepository.updateBehaviorUserRole(behaviorUser, roleTrace));

        if (null == gameTrace.getFirstPayTime() || gameTrace.getFirstPayTime() == 0) {
            gameTrace.setFirstPayTime(System.currentTimeMillis());
        }
        gameTrace.setLastPayTime(System.currentTimeMillis());
        gameTrace.setPayTimesToday(behaviorUser.getPayTimesToday() == null ? 0 : behaviorUser.getPayTimesToday() + 1);

        refreshCache(behaviorUser, gameTrace);
        basicRepository.updateUserBehavior(behaviorUser);

        if (1 == zoneTrace.isFirstPayUser()) {
            HLastLogin hLastLogin = basicRepository.getLastLogin(params.getUid(), params.getChannelId(), params.getClientType(), params.getGameId(), params.getZoneId());
            if (hLastLogin == null) {
                hLastLogin = new HLastLogin();
                hLastLogin.setUid(params.getUid());
                hLastLogin.setPid(params.getChannelId());
                hLastLogin.setClientType(params.getClientType());
                hLastLogin.setGameId(params.getGameId());
                hLastLogin.setZoneId(params.getZoneId());
                hLastLogin.setIsPaidUser(1);
                basicRepository.insertHLastLogin(hLastLogin);
            } else {
                basicRepository.updateIsPaidUser(params.getUid(), params.getChannelId(), params.getClientType(), params.getGameId(), params.getZoneId());
            }
        }
    }

    private void refreshCache(BehaviorUser behaviorUser, GameTrace gameTrace) {
        if (null != behaviorUser) {
            redisUtil.setKeyValue("zoneTrace" + behaviorUser.getClientType() + "_" + behaviorUser.getUid() + "_" + behaviorUser.getChannelId() + "_" + behaviorUser.getGameId() + "_" + behaviorUser.getZoneId(), JsonMapper.toJson(behaviorUser));
        }
        if (null != gameTrace) {
            redisUtil.setKeyValue("gameTrace" + behaviorUser.getClientType() + "_" + behaviorUser.getUid() + "_" + behaviorUser.getChannelId() + "_" + behaviorUser.getGameId(), JsonMapper.toJson(gameTrace));
        }
        if (StringUtils.isNotEmpty(behaviorUser.getRoleData())) {
            redisUtil.setKeyValue("roleTrace" + behaviorUser.getClientType() + "_" + behaviorUser.getUid() + "_" + behaviorUser.getChannelId() + "_" + behaviorUser.getGameId() + "_" + behaviorUser.getZoneId(), behaviorUser.getRoleData());
        }
    }

    private int bol2Int(boolean bol) {
        return bol ? 1 : 0;
    }
}
