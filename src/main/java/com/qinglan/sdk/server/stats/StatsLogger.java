package com.qinglan.sdk.server.stats;

import com.qinglan.sdk.server.domain.basic.GameTrace;
import com.qinglan.sdk.server.domain.basic.RoleTrace;
import com.qinglan.sdk.server.domain.basic.ZoneTrace;
import com.qinglan.sdk.server.dto.*;

import com.sun.org.apache.xml.internal.resolver.readers.ExtendedXMLCatalogReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.qinglan.sdk.server.stats.HeartbeatStatsLog.EXTRA_LAST_LOGIN_TIME;
import static com.qinglan.sdk.server.stats.HeartbeatStatsLog.EXTRA_ROLE_NAME;
import static com.qinglan.sdk.server.stats.InitStatsLog.EXTRA_FIRST_INIT_CHANNEL;
import static com.qinglan.sdk.server.stats.InitStatsLog.EXTRA_FIRST_INIT_GAME;
import static com.qinglan.sdk.server.stats.LoginStatsLog.*;

public class StatsLogger {
    private final static Logger STATIS_LOGGER = LoggerFactory.getLogger("statisLogger");

    private final static int LOGGER_CODE_INITIAL = 1001;
    private final static int LOGGER_CODE_LOGIN = 1002;
    private final static int LOGGER_CODE_HEARTBEAT = 1003;
    private final static int LOGGER_CODE_LOGOUT = 1004;
    private final static int LOGGER_CODE_QUIT = 1005;
    private final static int LOGGER_CODE_ROLE_CREATE = 1006;
    private final static int LOGGER_CODE_PAY_SUCCESS = 1007;
    private final static int LOGGER_CODE_PAY = 1008;

    public static String initial(InitialPattern param, boolean firstGame, boolean firstChannel) {
        InitStatsLog log = new InitStatsLog(LOGGER_CODE_INITIAL, 1, param);
        log.setExtras(EXTRA_FIRST_INIT_GAME, firstGame).setExtras(EXTRA_FIRST_INIT_CHANNEL, firstChannel);
        String msg = log.toString();
        STATIS_LOGGER.info(msg);
        return msg;
    }

    public static String login(LoginPattern param, GameTrace gameTrace, ZoneTrace zoneTrace, RoleTrace roleTrace,
                               boolean isNewUser, boolean isGameActiveDevice, boolean isChannelActiveDevice, boolean isZoneActiveDevice) {
        LoginStatsLog log = new LoginStatsLog(LOGGER_CODE_LOGIN, 2, param);
        log.setExtras(EXTRA_GAME_TRACE, gameTrace)
                .setExtras(EXTRA_ZONE_TRACE, zoneTrace)
                .setExtras(EXTRA_ROLE_TRACE, roleTrace)
                .setExtras(EXTRA_NEW_USER, isNewUser)
                .setExtras(EXTRA_GAME_ACTIVE_DEVICE, isGameActiveDevice)
                .setExtras(EXTRA_CHANNEL_ACTIVE_DEVICE, isChannelActiveDevice)
                .setExtras(EXTRA_ZONE_ACTIVE_DEVICE, isZoneActiveDevice);
        String msg = log.toString();
        STATIS_LOGGER.info(msg);
        return msg;
    }

    public static String heartbeat(HeartbeatPattern param, String lastLoginTime, String rname) {
        HeartbeatStatsLog log = new HeartbeatStatsLog(LOGGER_CODE_HEARTBEAT, 2, param);
        log.setExtras(EXTRA_LAST_LOGIN_TIME, lastLoginTime).setExtras(EXTRA_ROLE_NAME, rname);
        String msg = log.toString();
        STATIS_LOGGER.info(msg);
        return msg;
    }

    public static String logout(LogoutPattern param, String lastLoginTime, String lastHeartbeatTime, String rname) {
        LogoutStatsLog log = new LogoutStatsLog(LOGGER_CODE_LOGOUT, 2, param);
        log.setExtras(LogoutStatsLog.EXTRA_LAST_LOGIN_TIME, lastLoginTime)
                .setExtras(LogoutStatsLog.EXTRA_LAST_HEARTBEAT_TIME, lastHeartbeatTime)
                .setExtras(LogoutStatsLog.EXTRA_ROLE_NAME, rname);
        String msg = log.toString();
        STATIS_LOGGER.info(msg);
        return msg;
    }

    public static String quit(QuitPattern param, String lastLoginTime, String lastHeartbeatTime, String rname) {
        QuitStatsLog log = new QuitStatsLog(LOGGER_CODE_QUIT, 2, param);
        log.setExtras(QuitStatsLog.EXTRA_LAST_LOGIN_TIME, lastLoginTime)
                .setExtras(QuitStatsLog.EXTRA_LAST_HEARTBEAT_TIME, lastHeartbeatTime)
                .setExtras(QuitStatsLog.EXTRA_ROLE_NAME, rname);
        String msg = log.toString();
        STATIS_LOGGER.info(msg);
        return msg;
    }

    public static String roleCreate(RoleCreatePattern param, int isGameFirstRole, int isZoneFirstRole,
                                    boolean isDeviceGameFirstCreateRole, boolean isDeviceZoneFirstCreateRole) {
        RoleCreateStatsLog log = new RoleCreateStatsLog(LOGGER_CODE_ROLE_CREATE, 1, param);
        log.setExtras(RoleCreateStatsLog.EXTRA_GAME_FIRST_ROLE, isGameFirstRole)
                .setExtras(RoleCreateStatsLog.EXTRA_ZONE_FIRST_ROLE, isZoneFirstRole)
                .setExtras(RoleCreateStatsLog.EXTRA_DEVICE_GAME_CREATE_ROLE, isDeviceGameFirstCreateRole)
                .setExtras(RoleCreateStatsLog.EXTRA_DEVICE_ZONE_CREATE_ROLE, isDeviceZoneFirstCreateRole);
        String msg = log.toString();
        STATIS_LOGGER.info(msg);
        return msg;
    }

    public static String paySuccess(OrderGenerateRequest param, GameTrace gameTrace, ZoneTrace zoneTrace, RoleTrace roleTrace) {
        OrderStatsLog log = new OrderStatsLog(LOGGER_CODE_PAY_SUCCESS, 2, param, OrderStatsLog.FLAG_PAY_SUCCESS);
        log.setExtras(OrderStatsLog.EXTRA_GAME_TRACE, gameTrace)
                .setExtras(OrderStatsLog.EXTRA_ZONE_TRACE, zoneTrace)
                .setExtras(OrderStatsLog.EXTRA_ROLE_TRACE, roleTrace);
        String msg = log.toString();
        STATIS_LOGGER.info(msg);
        return msg;
    }

    public static String pay(OrderGenerateRequest param, int distanceDay, String firstLoginTime) {
        OrderStatsLog log = new OrderStatsLog(LOGGER_CODE_PAY, 2, param, OrderStatsLog.FLAG_TOTAL_ORDER);
        log.setExtras(OrderStatsLog.EXTRA_DISTANCE_DAY, distanceDay)
                .setExtras(OrderStatsLog.EXTRA_FIRST_DAY_LOGIN_TIME, firstLoginTime);
        String msg = log.toString();
        STATIS_LOGGER.info(msg);
        return msg;
    }

    public static void main(String[] args) {
    }
}