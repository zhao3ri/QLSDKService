package com.qinglan.sdk.server.application.redis;


import javax.annotation.Resource;

import com.qinglan.sdk.server.common.DateUtils;
import org.junit.Assert;
import org.junit.Test;
import com.qinglan.sdk.server.release.BaseTestCase;
import com.qinglan.sdk.server.application.basic.redis.RedisUtil;

import java.util.Date;

public class RedisUtilTest extends BaseTestCase {

    @Resource(name = "redisUtil")
    private RedisUtil redisUtil;

    @Test
    public void testsetLpush() throws Exception {
        for (int i = 0; i < 100; i++) {
            redisUtil.setLpush("Test-" + i);
        }
    }

    @Test
    public void testgetBrpop() throws Exception {
        for (int i = 0; i < 10; i++) {
            System.out.println(redisUtil.getBrpop());
        }
    }
    @Test
    public void testIncre() {
        String totalPayHead = "totolpay_";
        String key = totalPayHead + DateUtils.format(new Date(), "yyyy-MM-dd") + "_" + 1234 + "_" + 12345;
        for (int i = 0; i < 10; i++) {
            redisUtil.increment(key, 1);
        }
        Assert.assertEquals(redisUtil.getValue(key),"10");

    }
}
