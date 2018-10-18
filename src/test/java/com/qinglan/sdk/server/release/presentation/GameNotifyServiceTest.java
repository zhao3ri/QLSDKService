package com.qinglan.sdk.server.release.presentation;

import com.qinglan.sdk.server.common.Sign;
import com.qinglan.sdk.server.release.BaseTestCase;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by engine on 16/7/5.
 */
public class GameNotifyServiceTest extends BaseTestCase {
    private static Logger logger = LoggerFactory.getLogger(PlatformControllerTest.class);
    @Test
    public void testSigin(){
//        Map<String, Object> params = new HashMap<String, Object>();
//        params.put("platformId", "1009");
//        params.put("uid", "6113843");
//        params.put("zoneId", "4");
//        params.put("roleId", "12222");
//        params.put("cpOrderId", "1607041649533883");
//        params.put("orderId", "20160704165210020008623567416623");
//        params.put("orderStatus", 1);
//        params.put("amount", "100");
//        params.put("extInfo", "");
//        params.put("payTime", "20160704165210");
//        params.put("paySucTime", "20160704165324");
//        params.put("notifyUrl", "http://apitest.19you.com/apinotify/96/yl");
//        params.put("clientType", "1");
//        System.out.println(Sign.signByMD5(params,"nvycbrjtolhrbztocongzlufpdfgrpfu"));
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("platformId", "1009");
        params.put("uid", "6113019");
        params.put("zoneId", "4");
        params.put("roleId", "12222");
        params.put("cpOrderId", "1607051024163010");
        params.put("orderId", "20160705102635532867173943278894");
        params.put("orderStatus", "1");
        params.put("amount", "100");
        params.put("extInfo", "1607051024163010");
        params.put("payTime", "20160705102635");
        params.put("paySucTime", "20160705102823");
        params.put("notifyUrl", "http://apitest.19you.com/apinotify/96/yl");
        params.put("clientType", "1");
        System.out.println(Sign.signByMD5(params, "nvycbrjtolhrbztocongzlufpdfgrpfu"));
    }
}
