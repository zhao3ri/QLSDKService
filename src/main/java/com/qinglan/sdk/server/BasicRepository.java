package com.qinglan.sdk.server;

import com.qinglan.sdk.server.domain.basic.*;

import java.util.List;


public interface BasicRepository {
    //查询创建角色时间
    Role getRoleCreateTime(Long gameId, Integer channelId, String zoneId, String roleId, String roleName);

    /*******用户表*******/
    int saveAccount(Account account);

    int insertbatch(List<Account> list);

    Account getAccount(int channelId, String uid);

    /*******游戏表*******/
    Game getGameById(long id);

    ChannelGameEntity getByChannelAndGameId(int channelId, long gameId);

    ChannelEntity getChannel(int channelId);

    int updateChannelBalance(ChannelEntity channel);

    /*******订单表*******/
    int saveOrder(Order order);

    Order getOrderByOrderId(String orderId);

    Order getOrderStatus(String orderId, long gameId, int platform);

    int updateStatusPay(Order order);

    int updateStatusNotify(Order order);

    List<String> getNotifyOrder();

    /*******行为表*******/
    List<BehaviorUser> getUserBehavior(Integer clientType, String uid, Integer channelId, Long gameId);

    void insertUserBehavior(BehaviorUser behaviorUserZone);

    int updateUserBehavior(BehaviorUser behaviorUser);

    void save(BehaviorDevice behaviorDevice);

    BehaviorDevice getByUniqueKey(Integer clientType, Long appId, String deviceId);

    void updateDevicePlatform(BehaviorDevice behaviorDevice);

    void updateDeviceLoginPlatform(BehaviorDevice behaviorDevice);

    void updateDeviceLoginZone(BehaviorDevice behaviorDevice);

    void updateDeviceRoleZone(BehaviorDevice behaviorDevice);

    GameTrace getGameTrace(Integer clientType, String uid, Integer channelId, Long gameId);

    ZoneTrace getZoneTrace(Integer clientType, String uid, Integer channelId, Long gameId, String zoneId);

    RoleTrace getRoleTrace(Integer clientType, String uid, Integer channelId, Long gameId, String zoneId, String roleId, String roleName);

    String updateBehaviorUserRole(BehaviorUser behaviorUser, RoleTrace roleTrace);

    /*******用户日志记录*******/
    HLastLogin getLastLogin(String uid, Integer pid, Integer clientType, Long gameId, String zoneId);

    int insertHLastLogin(HLastLogin lastLogin);

    int updateIsPaidUser(String uid, Integer pid, Integer clientType, Long gameId, String zoneId);

    int updateLastLoginDate(String uid, Integer pid, Integer clientType, Long gameId, String zoneId);

    void insertRole(Role role);
}
