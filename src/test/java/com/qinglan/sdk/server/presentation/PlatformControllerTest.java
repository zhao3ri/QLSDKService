package com.qinglan.sdk.server.presentation;


import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import javax.annotation.Resource;

import com.qinglan.sdk.server.application.platform.ChannelUtilsService;
import com.qinglan.sdk.server.domain.basic.ChannelGameEntity;
import com.qinglan.sdk.server.platform.qq.JSONException;
import com.qinglan.sdk.server.application.platform.log.ChannelStatsLogger;
import com.qinglan.sdk.server.presentation.platform.dto.WdjPayCallback;
import com.qinglan.sdk.server.presentation.platform.dto.YunxiaotanSession;
import com.qinglan.sdk.server.platform.ibei.SignHelper;
import com.qinglan.sdk.server.platform.lewan.util.encrypt.EncryUtil;
import com.qinglan.sdk.server.platform.qq.JSONObject;
import com.qinglan.sdk.server.platform.quicksdk.QuickXmlBean;
import com.qinglan.sdk.server.platform.quicksdk.XmlUtils;
import com.qinglan.sdk.server.platform.xiao7.VerifyXiao7;
import junit.framework.Assert;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;

import com.qinglan.sdk.server.common.Base64;
import com.qinglan.sdk.server.common.HttpUtils;
import com.qinglan.sdk.server.common.JsonMapper;
import com.qinglan.sdk.server.common.MD5;
import com.qinglan.sdk.server.common.Sign;
import com.qinglan.sdk.server.release.BaseTestCase;
import com.qinglan.sdk.server.application.basic.OrderService;
import com.qinglan.sdk.server.application.platform.ChannelService;
import com.qinglan.sdk.server.BasicRepository;
import com.qinglan.sdk.server.domain.basic.Order;
import com.qinglan.sdk.server.domain.platform.MMYPayResult;
import com.qinglan.sdk.server.presentation.platform.dto.PlaySession;
import com.qinglan.sdk.server.presentation.platform.dto.VivoPaySign;

import egame.openapi.common.RequestParasUtil;
import sun.misc.BASE64Encoder;

public class PlatformControllerTest extends BaseTestCase {

    private static Logger logger = LoggerFactory.getLogger(PlatformControllerTest.class);
    private static String host = "http://localhost:7014";

    @Resource
    private BasicRepository basicRepository;

    @Resource
    private OrderService orderService;

    @Resource
    private ChannelUtilsService channelUtilsService;
    @Resource
    private ChannelService channelService;

    @Test
    @SuppressWarnings("unchecked")
    public void zhidian() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("zdappId", "150204878739");
        params.put("platformId", "150204878739");
        params.put("gid", "150204878739");
        params.put("user_id", "150204878739");
        params.put("session_key", "150204878739");

        String url = host + "/sougou/session";
        String result = HttpUtils.post(url, params);
        logger.debug("result: {}", result);
    }

    @Test
    public void testUc() {
        String sign = "6e05b94df5c81124b34d8d924e6c5cae";
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("amount", "1.00");
        map.put("accountId", "05da37a4de402ec9aa5c7c152283e710");
        map.put("callbackInfo", "com.51zhangle.sxmj.yuanbaotest1");
        map.put("cpOrderId", "20150429205111054827621250750505");
        map.put("creator", "JY");
        map.put("failedDesc", "");
        map.put("gameId", "555346");
        map.put("orderId", "201504292051190035839");
        map.put("orderStatus", "S");
        map.put("payWay", "999");
        String signStr = Sign.signParamsByMD5(map, "2489168f315123a76be2aee218cdb4fb");
        System.out.println(signStr);
    }

    @Test
    public void testMmy() throws Exception {
        String temp = "{\"uid\":\"7523040\",\"orderID\":\"015051803204660944\",\"productName\":\"u94bbu77f3\",\"productPrice\":\"0.01\",\"productDesc\":\"20150518152026764088874583579581\",\"orderTime\":1431933657,\"tradeSign\":\"da85dd47WvL8YMVgQEU1NXD1QHBlFQVVICClAA\",\"tradeState\":\"success\"}";
        MMYPayResult mmYPayResult = JsonMapper.toObject(temp, MMYPayResult.class);
        System.out.println(mmYPayResult);

        if (mmYPayResult == null || StringUtils.isEmpty(mmYPayResult.getTradeSign()) || StringUtils.isEmpty(mmYPayResult.getOrderID())) {
        }
        Order order = basicRepository.getOrderByOrderId(mmYPayResult.getProductDesc());
        if (null == order) {
        }
        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        if (null == channelGame) {
        }
        if (channelUtilsService.verifyMmy(mmYPayResult.getTradeSign(), channelGame.getConfigParamsList().get(0), mmYPayResult.getOrderID())) {
            if ("success".equals(mmYPayResult.getTradeState())) {
                if (order.getAmount() <= Double.valueOf(mmYPayResult.getProductPrice()) * 100) {
                    orderService.paySuccess(order.getOrderId());
                }
            }
            orderService.payFail(order.getOrderId(), "pay state callback fail");
        } else {
        }
    }

    @Test
    public void testLetv() throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("sign", "b99184f1b1af51ab799f9dc58b1076a7");
        params.put("price", 0.01);
        params.put("product_id", 0);
        params.put("letv_user_id", 106931470);
        params.put("trade_result", "TRADE_SUCCESS");
        params.put("sign_type", "MD5");
        params.put("app_id", 200007);
        params.put("pay_time", "2015-05-18 16:55:18");
        params.put("lepay_order_no", "3551");
        params.put("out_trade_no", "20150518165507382742588038201996");
        params.put("version", "1.0");

        String sign = params.get("sign").toString();
        String out_trade_no = params.get("out_trade_no").toString();

        Order order = basicRepository.getOrderByOrderId(out_trade_no);
        if (null == order) {
        }

        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(order.getChannelId(), order.getGameId());
        if (null == channelGame) {
        }

        if (!"TRADE_SUCCESS".equals(params.get("trade_result"))) {
        }
        try {
            String secretKey = channelGame.getConfigParamsList().get(0);
            params.remove("sign");

            String authSign = Sign.encode(getSourceFromMap(params) + "&key=" + secretKey);

            String authSign2 = Sign.encode(getSourceFromMap(params) + "&key=" + secretKey);
            if (StringUtils.equalsIgnoreCase(authSign, sign)) {
                if (order.getAmount() > Double.valueOf((String) params.get("price")) * 100) {
                    ChannelStatsLogger.info(ChannelStatsLogger.LETV, "order amount error");
                    orderService.payFail(order.getOrderId(), "order amount error");
                }
                orderService.paySuccess(order.getOrderId());
            } else {
            }
        } catch (Exception e) {
            ChannelStatsLogger.error(ChannelStatsLogger.LETV, params.toString(), "verifyLetv error :" + e);
        }
    }

    /**
     * 生成签名原串
     *
     * @param queryMap
     * @return
     */
    public static String getSourceFromMap(Map<String, Object> queryMap) {
        if (null == queryMap || queryMap.isEmpty()) {
            return null;
        }
        Object[] objArr = queryMap.keySet().toArray();

        StringBuilder buf = new StringBuilder();
        int i = 0;
        for (Object key : objArr) {
            if (!"sign".equals(key)) {
                buf.append(i++ == 0 ? "" : "&").append(key).append("=").append(queryMap.get(key));
            }
        }
        return buf.toString();
    }

    @Test
    public void testPlay() {
        PlaySession session = new PlaySession();
        session.setZdappId("150212661932");
        session.setPlatformId("1035");
        session.setCode("2da75adb072b5d823a16e91dbd6a904e");

        if (StringUtils.isBlank(session.getZdappId()) || StringUtils.isBlank(session.getPlatformId()) || StringUtils.isBlank(session.getCode())) {
        }
        ChannelGameEntity channelGame = basicRepository.getByChannelAndGameId(Integer.valueOf(session.getPlatformId()), Long.valueOf(session.getZdappId()));
        if (channelGame == null) {
        }
        String client_id = channelGame.getConfigParamsList().get(0);
        String client_secret = channelGame.getConfigParamsList().get(1);
        String verifyUrl = channelGame.getConfigParamsList().get(2);
        Map<String, String> params = new HashMap<String, String>();
        params.put("grant_type", "authorization_code");
        params.put("code", session.getCode());
        params.put("client_secret", client_secret);
        try {
            // 进行数字签名，并把签名相关字段放入请求参数MAP
            RequestParasUtil.signature("2", client_id, client_secret, "MD5", "v1.0", params);
            // 发起请求
            String temp = RequestParasUtil.sendPostRequest(verifyUrl, params);
            System.out.println(temp);
        } catch (Exception e) {
            logger.error("verifyPlaySession error", e);
        }
    }

    @Test
    public void testQihoo() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("app_key", "4a40b253ac4bd1268324b64f1fa36b06");
        params.put("gateway_flag", "success");
        params.put("app_order_id", "20150906111807201341268851547807");
        params.put("product_id", "2");
        params.put("order_id", "1509064070797061591");
        params.put("app_ext1", "com.51zhangle.sxmj.yuanbaotest1");
        params.put("user_id", "2545725277");
        params.put("app_uid", "10000205");
        params.put("sign_return", "bddf481d623e691c3bd9306ef7a9c970");
        params.put("amount", "100");
        params.put("sign_type", "md5");
        params.put("sign", "cf9ae9a7dddbf9dbccc07a0dadb6a21e");

        String result = HttpUtils.post("http://rsservice2.y6.cn/channel/qihoo", params);
        System.out.println(result);

		/*app_key=4a40b253ac4bd1268324b64f1fa36b06
            &gateway_flag=success
			&app_order_id=20150906111807201341268851547807
			&product_id=2
			&order_id=1509064070797061591
			&app_ext1=com.51zhangle.sxmj.yuanbaotest1
			&user_id=2545725277
			&app_uid=10000205
			&sign_return=bddf481d623e691c3bd9306ef7a9c970
			&amount=100
			&sign_type=md5
			&sign=cf9ae9a7dddbf9dbccc07a0dadb6a21e
		 */
    }

    @Test
    public void testMuzhi() throws Exception {
        String content = "eyJ1c2VybmFtZSI6IjE1OTE0MDUxNjA5IiwidXNlcl9pZCI6MTQ4ODYwLCJkZXZpY2VfaWQiOiIzNTI3NDYwNTk0MDc3OTQiLCJzZXJ2ZXJfaWQiOiIyIiwicGFja2V0X2lkIjoiMTAwMTUyMDAxIiwiZ2FtZV9pZCI6IjE1MiIsImNwX29yZGVyX2lkIjoiMTQwNTIzMDEwNDQ3MTI4MTAwMDEwNzQxIiwicGF5X3R5cGUiOiJhbGlwYXkiLCJwYXlfbm8iOiIwNTIzMDEwNDI4LTEwMDMiLCJhbW91bnQiOjEwLCJwYXlTdGF0dXMiOjB9";
        String sign = "5f7e09e085258f973bb5f7de54906713";
        content = new String(Base64.decode(content));

        String validSign = channelUtilsService.muzhiMd5(content + "&key=" + "zty_150");

        System.out.println(content);
        System.out.println(validSign);
        assertEquals(sign, validSign);
    }

    @Test
    public void testKaiuc() throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("sign", "9964436e3da8df9b655a565df5aec87b");
        params.put("uid", "356908");
        params.put("fee", "1.00");
        params.put("callbackInfo", "20150918145544963974177668626801");
        params.put("status", "1");
        params.put("payType", "1");
        params.put("orderId", "201509181456351442559395091");
        params.put("areaId", "1");
        params.put("roleId", "111111");

        String result = HttpUtils.post("http://rsservice2.y6.cn/channel/kaiuc", params);
        assertEquals("success", result);
    }

    @Test
    public void testBaidu() throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("OrderSerial", "7c84a5f32830473c_01001_2015092116_000000");
        params.put("CooperatorOrderSerial", "20150921165645495722577565864253");
        params.put("Content", "eyJVSUQiOjc5MzM1NjAsIk1lcmNoYW5kaXNlTmFtZSI6IjYw5YWD5a6dIiwiT3JkZXJNb25leSI6IjYuMDAiLCJTdGFydERhdGVUaW1lIjoiMjAxNS0wOS0yMSAxNjo1Njo0OSIsIkJhbmtEYXRlVGltZSI6IjIwMTUtMDktMjEgMTY6NTY6NTQiLCJPcmRlclN0YXR1cyI6MSwiU3RhdHVzTXNnIjoi5oiQ5YqfIiwiRXh0SW5mbyI6ImNvbS41MXpoYW5nbGUuc3htai55dWFuYmFvdGVzdDEiLCJWb3VjaGVyTW9uZXkiOjB9");
        params.put("AppID", "5436585");
        params.put("Sign", "17e39ee24f98d6df66f4ff4aab6b1260");

        HashMap<String, Object> objMap = JsonMapper.toObject(Base64.decode("eyJVSUQiOjc5MzM1NjAsIk1lcmNoYW5kaXNlTmFtZSI6IjYw5YWD5a6dIiwiT3JkZXJNb25leSI6IjYuMDAiLCJTdGFydERhdGVUaW1lIjoiMjAxNS0wOS0yMSAxNjo1Njo0OSIsIkJhbmtEYXRlVGltZSI6IjIwMTUtMDktMjEgMTY6NTY6NTQiLCJPcmRlclN0YXR1cyI6MSwiU3RhdHVzTXNnIjoi5oiQ5YqfIiwiRXh0SW5mbyI6ImNvbS41MXpoYW5nbGUuc3htai55dWFuYmFvdGVzdDEiLCJWb3VjaGVyTW9uZXkiOjB9"), HashMap.class);
        System.out.println(objMap);

        StringBuilder strSign = new StringBuilder();
        strSign.append("5436585");
        strSign.append("7c84a5f32830473c_01001_2015092116_000000");
        strSign.append("20150921165645495722577565864253");
        strSign.append("eyJVSUQiOjc5MzM1NjAsIk1lcmNoYW5kaXNlTmFtZSI6IjYw5YWD5a6dIiwiT3JkZXJNb25leSI6IjYuMDAiLCJTdGFydERhdGVUaW1lIjoiMjAxNS0wOS0yMSAxNjo1Njo0OSIsIkJhbmtEYXRlVGltZSI6IjIwMTUtMDktMjEgMTY6NTY6NTQiLCJPcmRlclN0YXR1cyI6MSwiU3RhdHVzTXNnIjoi5oiQ5YqfIiwiRXh0SW5mbyI6ImNvbS41MXpoYW5nbGUuc3htai55dWFuYmFvdGVzdDEiLCJWb3VjaGVyTW9uZXkiOjB9");
        strSign.append("YmU2K93bp1xROeytUWxxq7xGh3NliMii");

        System.out.println(MD5.encode(strSign.toString()));

        //String result = HttpUtils.post("http://rsservice.y6.cn/platform/baidu", params);
        //System.out.println(result);
    }

    @Test
    public void testLeshan() {
        String content = "{\"amount\":\"1.0\",\"gameid\":\"287\",\"orderid\":\"1445322969303560\",\"serverid\":\"1\",\"time\":1445322969,\"uid\":\"cy15102014352168\",\"sign\":\"e456170427479639221ba057ebd04fdd\",\"extendsInfo\":\"bzlB7xLhq+f/tz3d9j/8+oY5SqPwQM5TUCzj0rTLQ82+kvhTaBokyw==\"}";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("data", content);

        HttpUtils.post("http://zdsdktest.zhidian3g.cn/channel/07073", params);
    }

    @Test
    public void testSougou() throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("gid", "1097");
        params.put("auth", "4ce179347d3519a205b39e10efc9386f");
        params.put("oid", "P150921_12156805");
        params.put("appdata", "20150921165834557034705584975061");
        params.put("time", "20150921165856");
        params.put("sid", "1");
        params.put("date", "150921");
        params.put("uid", "61181847");
        params.put("role", "");
        params.put("realAmount", "6");
        params.put("amount1", "6");
        params.put("amount2", "0");

        String result = HttpUtils.post("http://rsservice.y6.cn/channel/sougou", params);
        System.out.println(result);
    }

    @Test
    public void testKupai() throws Exception {
        /*String data = "{\"exorderno\":\"20150923214330153363888313247172\",\"transid\":\"02115092321422002455\",\"waresid\":1,\"appid\":\"5000001872\",\"feetype\":0,\"money\":100,\"count\":1,\"result\":0,\"transtype\":0,\"transtime\":\"2015-09-23 21:42:47\",\"cpprivate\":\"com.51zhangle.sxmj.yuanbaotest1\",\"paytype\":401}";
        String sign = "1e769f0394f916f6d88be1334414ca07 9a47d9fbc1513e348bf8bb31f5c2743 2d4e41872eddddb9bca3bc65192d2658 ";
		String key = "Q0ZEN0Y1NDMzREIzQ0M5M0MyQUI2QjY1NjI3OTMzRkUxOTBEMUFDNE1URXpNRFExTVRnek1EazBOakU1TURJME16a3JNVEl6TXpNNE1UTXhNamt3TlRrME1URTNORGN4TWpjeE1Ua3pNVGt4T1RJek1EWXpNalV4";
		
		String data = "{\"appid\":\"5000002408\",\"appuserid\":\"黎承宣\",\"cporderid\":\"20151121175443741650209755119610\",\"cpprivate\":\"no\",\"currency\":\"RMB\",\"feetype\":0,\"money\":6.00,\"paytype\":401,\"result\":0,\"transid\":\"32081511211753483025\",\"transtime\":\"2015-11-21 17:54:05\",\"transtype\":0,\"waresid\":1}";
		String sign = "SDNJ+HsD8gb/tY37sj3u6idYAgal+kq2e+6y98uTYPyw5+YhVq8wAmqhTPE2t0cZYLp1I48QW6Nqv+bflOTrzxb8YRP5GtYAXnILrXEPfu+nCXlCK/HLXN2Mfhn5kE3jT6S8GrgvDBuhjbcRR/EoX4VyCAsbRcKA8jJf/vGFhGU=";
		String key = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCYTQcs9iRQMBCRaPKzbLDwdHzi7PxS/nDvXR+v/RbJuefE9GLHHfac5NZvMWaisvs2gqnWR2pOKihch3HILiT/gpwJdSSAjK4QZJscG2pHe/b7ch5PCsOTymmp09J2zW2h6CKrgU0tio9SgQhA1CX0ni2H6Xek2dNgeldYwDSGdQIDAQAB";
		System.out.println(channelUtilsService.verifyKupai(data, sign, key));*/
        String transdata = "{\"appid\":\"5000002408\",\"appuserid\":\"黎承宣\",\"cporderid\":\"20151122200056467747830115289925\",\"cpprivate\":\"no\",\"currency\":\"RMB\",\"feetype\":0,\"money\":6.00,\"paytype\":401,\"result\":0,\"transid\":\"32011511222000566552\",\"transtime\":\"2015-11-22 20:00:14\",\"transtype\":0,\"waresid\":1}";
        String signtype = "RSA";
        String sign = "NRlR+0kuYLdVIVvVz1dMjoRsnX50ocOHxnqskdXo9MI75OINYZ+s4bwtJgN/Sl7dBLd76LOVviOHz2dQmY4tYjt2o7zYsUp/PMXnvvNMU03f1VR6OQf8lCCdVnw7hT2gvuBKJpXace5lZKseYQGejFc8LVy7EGTq4udI9f1HGt8=";

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("transdata", transdata);
        params.put("signtype", signtype);
        params.put("sign", sign);

        System.out.println(HttpUtils.post("http://rsservice.y6.cn/channel/kupai", params));
    }

    @Test
    public void testAtet() throws Exception {
        String sign = "fU7WHh6tZczBCm69LjcFmHLLNbF6XSF4/BG5hpHQYNppmtLKPjj/qrq0I8McuC7L8hJdy8AFfK3bv2QXsm5sbiSRMwHhmgVa9cQpk17o PdiGfr 4yQfrcJlL/kdmf6kyY7VRpUfVD7CCzpbCiSQaeiD s8kp47TEw0yi9Pgcsg=";
        String content = "{\"exOrderNo\":\"20151103173129661872585860026086\",\"payOrderNo\":\"10031511031731003705\",\"appId\":\"20151019172257048954\",\"amount\":100,\"payType\":1,\"transTime\":\"2015-11-03 17:31:50\",\"counts\":1,\"payPoint\":\"2203\",\"result\":0}";
        String key = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCcd4UcY1KRWwy5POoF4+GTqKCkTE6dU9W8z+lOmfn4zRkS1/mioXsKN1Qj9sAoUNZGD8VPgCR9KDE9LV3G8TFwgUX50nb/8TfZ3ypqGMm7k/m2OzrTUzHNtbh1ytKx/i6TedIkf7Qs0iuTwDkVuMqc6UTQPyFi6reyDsGd8I4H6QIDAQAB";
        System.out.println(channelUtilsService.verifyAtet(content, sign.replace(" ", "+"), key));
    }

    @Test
    public void testVivoPaySign() {
        String data = "{\"zdappId\":\"151110191986\",\"platformId\":\"1018\",\"cpOrderNumber\":\"20151118094814642913686750422000\",\"cpId\":\"20150403171244382610\",\"appId\":\"285f886432c87f3698b3779cdff7faa7\",\"orderTitle\":\"20钻石\",\"orderDesc\":\"货币\",\"cpExtInfo\":\"normal notes\"}";
        VivoPaySign vivoPaySign = JsonMapper.toObject(data, VivoPaySign.class);
        String result = channelService.vivoPaySign(vivoPaySign);
        System.out.println("============================");
        System.err.println(result);
    }

    @Test
    public void paojiao() {
        //{sign=e72eb1349ccc1cfe61603a3a4ce924d0, uid=5146345, orderNo=PG_1447845305232, price=6.0, status=5, remark=no,
        //subject=元宝, gameId=1472, payTime=2015-11-18 19:15:05, ext=20151118191500791563781986812756}
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("sign", "e72eb1349ccc1cfe61603a3a4ce924d0");
        params.put("uid", "5146345");
        params.put("orderNo", "PG_1447845305232");
        params.put("price", "6.0");
        params.put("status", "5");
        params.put("remark", "no");
        params.put("subject", "元宝");
        params.put("gameId", "1472");
        params.put("payTime", "2015-11-18 19:15:05");
        params.put("ext", "20151118191500791563781986812756");

        String result = HttpUtils.post("http://rsservice.y6.cn/channel/paojiao", params);
    }

    @Test
    public void test3899() throws UnsupportedEncodingException {
        String decodeSid = URLDecoder.decode("Pmc2IDY2LCFnf2d2dnF3DiMjDTQpIHwgcBwDNS0GJyk0GWoBES8CLiI0cBQnE3dxdQANACQUfSIrPRAEdDcDLwgXBgMnKTcXKAATEQF3DhMBfS11JncOAwgjPAMobiY3DS8cZ2lnMDYgNyskKCBnf2d0dnRzfX10d3Zydmc4");

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("ac", "check");
        params.put("appid", "2867");
        params.put("sessionid", decodeSid);
        params.put("time", System.currentTimeMillis());

        Map<String, Object> signParams = new LinkedHashMap<String, Object>();
        signParams.put("ac", "check");
        signParams.put("appid", "2867");
        signParams.put("sessionid", URLEncoder.encode(decodeSid, "utf-8"));
        signParams.put("time", params.get("time"));
        params.put("sign", Sign.signByMD5(signParams, "cdf38486cb8f38123f8921c2f80d6829"));

        System.out.println(HttpUtils.post("http://api.3899w.com/sdkapi.php", params));
    }

    @Test
    public void testPengyouwan() throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put("sign", "9171978212f4155e0d60b6053564d943");
        params.put("gamekey", "27e7c8b3");
        params.put("channel", "PYW");
        params.put("cp_orderid", "20151202135943992845238695581560");
        params.put("ch_orderid", "N1512021N2038413");
        params.put("amount", "6.00");

        System.err.println(HttpUtils.doPostToJson("http://rsservice.y6.cn/channel/pengyouwan", JsonMapper.toJson(params), 100000));
    }

    @Test
    public void testQbao() throws Exception {
        //String zdappId = request.getParameter("zdappId");
        //String platformId = request.getParameter("platformId");
        //String appCode = request.getParameter("appCode");
        //String orderNo = request.getParameter("orderNo");
        //String money = request.getParameter("money");
        //String payNotifyUrl = request.getParameter("payNotifyUrl");

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("zdappId", "151110191986");
        params.put("platformId", "1089");
        params.put("appCode", "sdfsdfsd");
        params.put("orderNo", "20160415153742438469");
        params.put("money", "6");
        params.put("payNotifyUrl", "sdfsdfsdf");

        String url = "http://zdsdktest.zhidian3g.cn/channel/qbao/pay/sign";
        String result = HttpUtils.doPost(url, params);
        System.out.println(result);

    }

    @Test
    public void testYunxiaoTanSession() throws JSONException {
        YunxiaotanSession session = new YunxiaotanSession();
        session.setAppId("5432");
        session.setZdAppId("151110191986");
        session.setPlatformId("1095");
        session.setSid("8a42aqjJDqx9Bc4IkE8jK5X2XgOdEs7582feBbVHQ6FMZNJY5sHYL1bz%2F%2FEShSRPoX3zQ554RtTNOBep%2Biif6oxX");
        String result = channelService.verifyYunxiaotanSession(session);
        System.out.println("-----------");
        System.out.println(result);
        System.out.println("-----------");
        JSONObject jsonObject = new JSONObject(result);
        Assert.assertEquals(1, jsonObject.optInt("code"));
    }

    @Test
    public void testYunxiaotanOrder() throws UnsupportedEncodingException {
        Map<String, Object> params = new LinkedHashMap<String, Object>();


        params.put("cporderid", "20160613104118363160296632940782");
        params.put("appid", "5432");
        params.put("charid", URLEncoder.encode("111111", "utf-8"));
        params.put("gold", "0.01");
        params.put("extinfo", URLEncoder.encode("http%3A%2F%2Fzdsdktest.zhidian3g.cn%2Fplatform%2Fyxt", "utf-8"));
        params.put("amount", "0.01");
        params.put("orderid", "201606131041191790");
        params.put("serverid", URLEncoder.encode("1", "utf-8"));
        params.put("time", "1465785707");
        params.put("uid", "8566");
        String validSign = Sign.signByMD5(params, "b652adb4bbc44f783fbf86bcdf8f7d95");
        Assert.assertEquals("5415b2a320a120db25a42bd273373064", validSign);
    }

    @Test
    public void testGzpdOrder() throws UnsupportedEncodingException {
        Map<String, Object> params = new LinkedHashMap<String, Object>();

        params.put("g_id", "11");
        params.put("u_id", "25");
        params.put("c_id", "8");
        params.put("a_id", "25");
        //params.put("goods_name",URLEncoder.encode("钻石","utf-8"));
        params.put("goods_name", "钻石");
        params.put("goods_id", "001");
        params.put("goods_body", "body");
        params.put("goods_num", "10");
        params.put("goods_price", "0.01");
        params.put("goods_amount", "0.01");
        params.put("cp_order_no", "20160721092851407597090814347091");

        String validSign = Sign.signByMD5KeyPre(params, "2213575d2e90d524e");
        Assert.assertEquals("01e9ba33e355b4605186409daaa3ef75", validSign);
    }

    @Test
    public void testDianyooOrder() {
        String OrderNo = "20160617095533046516763355390025";
        String Money = "0.01";
        String ResultCode = "0";
        String ExtensionField = "353535";
        String TimeStamp = "1466157347";
        String SKey = "3DD24ECC06FE3C1C35FE5F8D3F0097CC";
        StringBuilder sb = new StringBuilder();
        sb.append(OrderNo).append(Money).append(ResultCode).append(TimeStamp).append("4BB69D66735153DF76182E74317DE4AB");
        System.out.println(sb.toString());
        String validSign = MD5.encode(sb.toString());
        Assert.assertEquals(SKey, validSign);
    }

    @Test
    public void testChongChong() {
        Map<String, Object> params = new LinkedHashMap<String, Object>();
        params.put("transactionNo", "2016062317153529379903886");
        params.put("partnerTransactionNo", "20160623171534883486514214248953");
        params.put("statusCode", "0000");
        params.put("productId", "108269");
        params.put("orderPrice", "0.01");
        params.put("packageId", "106718");
        Map<String, Object> result = new TreeMap<String, Object>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        result.putAll(params);
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : result.entrySet()) {
            sb.append(entry.getKey() + "=" + entry.getValue() + "&");
        }
        System.out.println(sb.toString());
        String validSign = MD5.encode(sb.toString() + "49076695968942508ebdd060711b37bd");
        Assert.assertEquals("8098a40d6e6bbd536238004b61658c28", validSign);
    }

    @Test
    public void testQishi() {
        String paymoney = "10";
        String payorder = "2016062457505648";
        long md5str = Long.parseLong(payorder) - Integer.parseInt(paymoney);
        String validSign = MD5.encode(MD5.encode(md5str + "") + "d9207e2294325d4fe85f40068209309594325d4fe85f4006");
        Assert.assertEquals("f2d393585dea59849545289d2c58ede7", validSign);
    }

    @Test
    public void testTT() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        String str = "{\"cpOrderId\":\"20160624165824376195511990925955\",\"exInfo\":\"\",\"gameId\":207706211,\"payDate\":\"2016-06-24 16:58:25\",\"payFee\":0.01,\"payResult\":\"1\",\"sdkOrderId\":\"201606241658241191459934128379\",\"uid\":11914599}";
        String appkey = "0d0cdbef018392f012f6bbd0744b54c0";
        md5.update((str + appkey).getBytes("utf-8"));
        String validSign = (new BASE64Encoder()).encodeBuffer(md5.digest());
//		sigin:f072RwcBWadBlX4uWD0GYw==validSign:f072RwcBWadBlX4uWD0GYw==
//		Assert.assertEquals("tGns338t601u9eGwBjjoyg==","tGns338t601u9eGwBjjoyg==");
//		Assert.assertEquals("tGns338t601u9eGwBjjoyg==",validSign);
        Assert.assertEquals(validSign.trim(), "tGns338t601u9eGwBjjoyg==");
    }

    @Test
    public void testLewan() throws Exception {
        String data = "DyLZTH4BfB2LlxmJWnlTdY8MxjbAsUAUyRreC0eGT4gH4pSxNNWHSvV/BOxziEMdLC2fnyqMQvCXAx5eG1OjKSk5pWcTTzlfi+NovlRA72A0HWoDgP/vd2di4niNhh5/OQcoPWuoO+I/a5PIBsQ87JQ4XrA9T7mlpEm0u3ZFInmA3R/RiHvup2i1F77i2sC2JfVQwMP04xdapZ779v9vjPHtq6SIv4rpXqOHFQ2RCIoOiZaOC6dAbUHjPGhLkelwDOzGgY6nZC2vADt9tJmdFo+98BnttThRhWJiN/t8NwUxpp1xHPmmd+QYaRDuQgTsFznac9AcZCOCHQNK7Bw3IKaD1qPwNGaBISOTgKBeDqiMiP96kB5WZRzPR1ClfNpEWL1gyNySiMteUMXRaZ1DydvqcCjeadmMvZbhTcCsZTy7CjQAVijmdGTBTEx+8hITUby7VIcG5TnwpB7z2g7+FbA5KYA6+PgWRe2CcNOn9bliidNf7n3BOO8ZAbBsc0IJ+ZjJY4sW14InaU3GDLwOSw==";
        String encryptkey = "n/4lifWS8UNW+BPYupj5uSuW0DuGB1ocz/DCcRsT/r0F30XuOoBsU8oiYeGD8Y7zTUOYphAqYx+mO9JvIdimC4M/iYe4ggylFVvi8C0kjrE4ZDjpf3jodcCdJ1IXtVNvmloQ6l1K3YrH6Rv6bOFSnzQeOh5/T/OgvPh41sxmHOo=";
        String lewanpublickKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC+GY2/8wJuINxzJo9uWoMRUDcxONuK/48Fikze8EFpKWLLr6mBpqeoDVvZQoqGhGKn5wdtHujiCUYSn6pcWKY2Fz2Rxw6/1uA1gzKcLE36KLUkqvFbA3gItSiO3ADNCwJ1ochhdfcEnH2dtbiv5+f7m+xv5B1aEP142v2CtYKFFQIDAQAB";
        String gameprivateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAMEXRA81AwYSW9b6UmyBHhFwCAqZ76T1WAB05myssl7M8S/c81X7lo5rDQMZ6+Z9taItGbbNf8Zg55xwTu+9NQAU9bNg2BuepNGu1phDspn8KtDVrBDq4PorW4BY94M8p+M5VED8bCAF61y4PiBUSaGw2JbD/4gwa0ZUFtlIX8ApAgMBAAECgYEAgSOFUkJbJllq8Olz7LuiF3l891Ii04l+9Lc6UN5jYUHh4Of+Grhr5g40oqlYe0wCCVfuhbMEzPGMlbULJg29/obMdXnK2vQ1gLtWtU4HerUqNpttin9ayUHucA0pXvqRxGi6GJQjj9zCEh1fGGAZaAyVU3zpT4j4qzWq4vvRVB0CQQD/MuDywua2ujlkxoZuJqvbs8qan9jZrDoPeqYJnk4CUJGuNSBA2LJLVSYAWofSefo8MdyRmNdXFLuJK9rV9BWjAkEAwbJ3d4B3F2XeA/eMCHXjpR4ROAz2EaF5yh+Iy3oCDAPKwbX7uyYRplH6hDpCoCMLqcT8tVrQce9C8f24klH3wwJBAODhXdaupw4IZkEGrDluvGy7L2M4TqfqG7O+OYgqRsXFZ8qiqAxcqCi4HOec3yk7MaZPrfVjQQdMjsGespVtJI8CQEejqTnioqldvMp80ScD6ylTwIyZhp0ouvG9zgtr2bv13xTcKPmj790y7tPe9Gtj6tlkiQ7OQtAQ7RKxg4VimZ8CQFginXwEudBeZlDuhlkf/CAuCb8tgdfsL39lE085bniQm5cX1xzwxA8ee5RWdozmqTud3DZYTXMubL9fvT1nUTw=";
        boolean isRight = true;
        isRight = EncryUtil.checkDecryptAndSign(data, encryptkey, lewanpublickKey, gameprivateKey);
        Assert.assertTrue(isRight);
    }

    @Test
    public void testWxdl() {
        String orderId = "201607181739456641143";
        String cpOrderId = "20160718173941104614824552864545";
        String payPointId = "1";
        String payRealMoney = "1";
        String payVirMoney = "1";
        String status = "1";
        String time = "1468834798";
        StringBuilder md5srcsb = new StringBuilder(orderId);
        md5srcsb.append(payPointId).append(payRealMoney).append(time).append(cpOrderId).append("99c88c36b00bf903f423b1704831ef6e");
        String validString = MD5.encode(md5srcsb.toString());
        Assert.assertEquals("9d892f3b6d34e1f29caa8ce087a52eef", validString);
    }

    @Test
    public void testWanke() {
        Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
        paramMap.put("orderid", "14682199028010493");
        paramMap.put("username", "d973680443");
        paramMap.put("gameid", "71");
        paramMap.put("roleid", "111111");
        paramMap.put("serverid", "1");
        paramMap.put("paytype", "ptb");
        paramMap.put("amount", 1);
        paramMap.put("paytime", "1468219902");
        paramMap.put("attach", "20160711145135587328122993530027");
        paramMap.put("appkey", "e072d570baf69eaa6d8b72701ce499dc");
        Assert.assertEquals("0a54b51214214d706578f00a50d05f42", Sign.signByMD5Unsort(paramMap, ""));
    }

    @Test
    public void testwdj() {
        String content = "{\"orderId\":339451820,\"appKeyId\":100037442,\"buyerId\":208291576,\"cardNo\":null,\"money\":1200,\"chargeType\":\"BALANCEPAY\",\"timeStamp\":1479781822721,\"out_trade_no\":null,\"discount\":null,\"settlement\":null}";
        WdjPayCallback callback = JsonMapper.toObject(content, WdjPayCallback.class);
        Assert.assertNotNull(callback);
    }

    @Test
    public void testXiao7() {
        Map<String, String> paramMap = new TreeMap<String, String>();
        paramMap.put("encryp_data", "IfVXsWGhcLXS+bfI5u227tcf8slXVTqHqGIblh9TUgaBqeZjF/gNivLrcqBbAvBOUIzKLuxi/dqXkyodhUJ0101F+VNcqZVZUHCf01FcpIgV4Eku3T8XzG6UyN8GF2L6qtQdEeGav16pTKQ/9D6gGKk34ZKmSwcvs+q6xqp1ArA=");
        paramMap.put("xiao7_goid", "43061");
        paramMap.put("game_orderid", "20160816104943608879722826722240");
        paramMap.put("guid", "23306");
        paramMap.put("subject", "钻石");
        VerifyXiao7.PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDZfYMI9FzwaovaF8TARRcaVxjlTxa+9s2QpOoYoh4nNrAblbmQyRqvlgRGJFdkb/8kyrZnSCwxhFl9rmE20/Qfy5toZCNb19BVWxylu5fw9gTKpnt/im/dJV5JULVcUcwrCEdNgKeg0jy3NfKiMiHoIfQMu7St+pooxeSZvu03AQIDAQAB";
        String sign_data = "y4jgdqCAGQhgfmBixFMhF7Q5G26STndB22ASE1LR2PUZxsC4fbrRvDqvlUWM6wXZryJU9PhkmvAG+mW+0+moO7oqcsVFclVdkFfFmFlCgsLRxy6Yb1deL4StGG3/rpQLWDPpyrW3V6gHvfL1BU1C/onDTExNpyq7DMR78ZSeaTs=";
        ;
        try {
            String httpstring = VerifyXiao7.buildHttpQuery(paramMap);
            boolean checked = VerifyXiao7.doCheck(httpstring, sign_data, VerifyXiao7.loadPublicKeyByStr());
            String decryptData = new String(VerifyXiao7.decrypt(VerifyXiao7.loadPublicKeyByStr(), VerifyXiao7.decode(paramMap.get("encryp_data"))));
            Map<String, String> decryptMap = VerifyXiao7.decodeHttpQuery(decryptData);
            boolean flag = (decryptMap.containsKey("game_orderid") && decryptMap.get("game_orderid").equals(paramMap.get("game_orderid")) && decryptMap.get("payflag").equals("1"));
            Assert.assertEquals(true, 1 >= Float.parseFloat(decryptMap.get("pay")) * 100);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testQuickXml() {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                "<quicksdk_message>\n" +
                "<message>\n" +
                "<is_test>0</is_test>\n" +
                "<channel>8888</channel>\n" +
                "<channel_uid>231845</channel_uid>\n" +
                "<game_order>123456789</game_order>\n" +
                "<order_no>12520160612114220441168433</order_no>\n" +
                "<pay_time>2016-06-12 11:42:20</pay_time>\n" +
                "<amount>1.00</amount>\n" +
                "<status>0</status>\n" +
                "<extras_params>{1}_{2}</extras_params>\n" +
                "</message>\n" +
                "</quicksdk_message> ";
        QuickXmlBean bean = XmlUtils.parserXML(xml);
        Assert.assertEquals("0", bean.getStatus());
    }

    @Test
    public void testYiHuan() {
        String amount = "0.1";
        String md5Str = "46AE3C4AE15F4724F5F63482550416A6";
        String pOrderId = "JWQM1483847508807XIK";
        String serverCode = "1";
        String creditId = "111111";
        String userId = "10000677140";
        String stone = "1";
        String time = "1473846843546";
        String localKey = "27A44CACAF6556BF28A516B508D0CE1D";
        String st = pOrderId + serverCode + creditId + userId + amount + stone + time + localKey;
        String validsign = MD5.encode(st).toUpperCase();
        logger.info(st);
        Assert.assertEquals(md5Str, validsign);
    }

    @Test
    public void testZhuoyi() {
        Map<String, Object> params = new LinkedHashMap<String, Object>();
        params.put("Recharge_Id", "D160919182737336111716420781333");
        params.put("App_Id", "3361");
        params.put("Uin", "11717737");
        params.put("Urecharge_Id", "20160919182710786078100300393936");
        params.put("Extra", "");
        params.put("Recharge_Money", "0.01");
        params.put("Recharge_Gold_Count", "0");
        params.put("Pay_Status", "1");
        params.put("Create_Time", "1474280857");
        String validSign = Sign.signByMD5(params, "a6f2d982ef815c25b8e88e82a117063d");
        Assert.assertEquals("5aaf085572260735ff06b87b8eddb6d4", validSign);
    }

    @Test
    public void testQitianledi() {
        StringBuilder sb = new StringBuilder();
        sb.append("amount=").append("600");
        sb.append("cash=").append("6.00");
        sb.append("exchange=").append("100");
        sb.append("extra=").append("20170330143859533202291261516699");
        sb.append("money=").append("6.00");
        sb.append("orderid=").append("201703301439047811");
        sb.append("pay_time=").append("2017-03-30 15:20:58");
        sb.append("sid=").append("10001");
        sb.append("uid=").append("7832197");
        sb.append("way_name=").append("微信支付");
        sb.append("c01d37224fe99f14c22e40f42bb8589b");
        String validSigin = MD5.encode(sb.toString());

//        String way_name = "微信支付";
//        Map<String, Object> sbMap = new HashMap<String, Object>();
//        sbMap.put("sid", "10001");
//        sbMap.put("orderid", "201703301439047811");
//        sbMap.put("cash", "6.00");
//        sbMap.put("money", "6.00");
//        sbMap.put("amount", "600");
//        sbMap.put("exchange", "100");
//        sbMap.put("pay_time", "2017-03-30 15:20:58");
//        sbMap.put("uid", "7832197");
//        sbMap.put("extra", "20170330143859533202291261516699");
//        try {
//            way_name = URLEncoder.encode(way_name, "utf-8");
//            sbMap.put("way_name", way_name);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        String validSigin = Sign.signByMD5(sbMap, "c01d37224fe99f14c22e40f42bb8589b");
        Assert.assertEquals("710d26195a9ddf19c2ee8cf651ba94ab", validSigin);
    }

    @Test
    public void testCangluan() {
        StringBuilder sb = new StringBuilder();
        sb.append("CNY");
        sb.append("20170426154136913464704506112719");
        sb.append("001");
        sb.append("0.1");
        sb.append("201704261541583409");
        sb.append("alipay");
        sb.append("111111");
        sb.append("GHFHKKNHFGLJSLLDFJJKBNLPTTBBT");
        System.out.print("输出：" + sb.toString());
        String validSigin = MD5.encode(sb.toString());
        validSigin = validSigin.toUpperCase();//转大写
        Assert.assertEquals("F3341B6CB92D1786AB9C24CBCE0BFFB0", validSigin);
    }

    @Test
    public void testLingdongLogin() {

        String ldurl = "http://aoneldapi01.smartspace-game.com:8000/api/user_verify.do";
        String appkey = "cb398d62efe24f6b9fca5a037ee27db8";
        String ldToken = "5118B507BE2D51C0A7B9D5846229A37436B4C5CF";
        String ldAppId = "2009";
        int user_id = 5480181;

        Map<String, Object> body = new LinkedHashMap<String, Object>();
        body.put("user_id", user_id);
        body.put("token", ldToken);
        String bodyJsonStr = JsonMapper.toJson(body);
        String sign = MD5.encode(bodyJsonStr + appkey);
        Map<String, Object> params = new LinkedHashMap<String, Object>();
        params.put("appid", ldAppId);
        params.put("sign", sign);
        String data = "appid=" + ldAppId + "&" + "sign=" + sign;
        ldurl = ldurl + "?" + data;

        try {
            HttpClient httpclient = new HttpClient();
            PostMethod post = new PostMethod(ldurl);
            httpclient.getHttpConnectionManager().getParams().setConnectionTimeout(1000 * 10);//链接超时10秒
            httpclient.getHttpConnectionManager().getParams().setSoTimeout(1000 * 10); //读取超时10秒
            post.addRequestHeader("Content-Type", "application/json; charset=UTF-8");
            RequestEntity entity = new StringRequestEntity(bodyJsonStr, "application/json", "UTF-8");
            post.setRequestEntity(entity);
            httpclient.executeMethod(post);
            String returnMsg = new String(post.getResponseBody(), "utf-8");
            System.out.println("加密的appkey：" + appkey);
            System.out.println("请求的URL：" + ldurl);
            System.out.println("发送的键值：" + data);
            System.out.println("传入的body：" + bodyJsonStr);
            System.out.println("返回：" + returnMsg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testLingdongPay() {
        String keyValue = "{sign=8e4247d921197268047f4d04db61b333}";
        String bodyJson = "{\"user_id\":5480181,\"role_id\":111111,\"pay_no\":\"170428152159000548018165\",\"cp_ext\":\"20170428152158941136244097729343\",\"product_id\":\"2009_7_0_10\",\"currency\":\"CNY\",\"amount\":0.01,\"amount_usd\":0.01,\"coin\":10,\"pay_channel\":\"ipaynow\",\"cp_role_id\":\"111111\",\"cp_group_id\":\"15001\"}";
        String appkey = "cb398d62efe24f6b9fca5a037ee27db8";

        JSONObject bodyObj = null;
        JSONObject keyValueObj = null;
        try {
            bodyObj = new JSONObject(bodyJson);
            keyValueObj = new JSONObject(keyValue);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Double amount = bodyObj.optDouble("amount");
        Double amount_usd = bodyObj.optDouble("amount_usd");
        int coin = bodyObj.optInt("coin");
        String cp_ext = bodyObj.optString("cp_ext");
        String cp_role_id = bodyObj.optString("cp_role_id");
        String currency = bodyObj.optString("currency");
        String pay_channel = bodyObj.optString("pay_channel");
        String pay_no = bodyObj.optString("pay_no");
        String product_id = bodyObj.optString("product_id");
        int role_id = bodyObj.optInt("role_id");
        int user_id = bodyObj.optInt("user_id");

        Map<String, Object> body = new LinkedHashMap<String, Object>();
        body.put("amount", amount);
        body.put("amount_usd", amount_usd);
        body.put("coin", coin);
        body.put("cp_ext", cp_ext);
        body.put("cp_role_id", cp_role_id);
        body.put("currency", currency);
        body.put("pay_channel", pay_channel);
        body.put("pay_no", pay_no);
        body.put("product_id", product_id);
        body.put("role_id", role_id);
        body.put("user_id", user_id);
        String jsonStr = JsonMapper.toJson(body);
        StringBuilder sb = new StringBuilder();
        sb.append(bodyJson);
        sb.append(appkey);
        String validSign = MD5.encode(sb.toString());
        String sign = keyValueObj.optString("sign");
        System.out.println("signValue:" + sb.toString());
        System.out.println("sign1:" + sign);
        System.out.println("sign2:" + validSign);
        //Assert.assertEquals(sign, validSign);
    }

    @Test
    public void testZhizhuyouLogin() {

        long loginTime = System.currentTimeMillis();
        String zzyAppId = "157";
        String ac = "check";
        String sdkversion = "4.1";
        String time = "1493784237";
        String url = "http://api.zhizhuy.com/sdkapi.php";
        String appkey = "3095c0598a04218c948c92ca43841ce3";
        String sessionId = "1205Ww6HfOi%2FT7IgKAplfVvWi4tiGPlAdtCXWDqS4JfylMwY6uRrw21KreDNG%2FImGgAT8M18RYKeTIZ14Y2NY3Fi";
        String sessionIdDecode = URLDecoder.decode(sessionId);
        //String sessionIdEndcode = URLEncoder.encode(sessionId);

        StringBuffer sb = new StringBuffer();
        sb.append("ac=").append(ac).append("&");
        sb.append("appid=").append(zzyAppId).append("&");
        sb.append("sdkversion=").append(sdkversion).append("&");
        sb.append("sessionid=").append(sessionId).append("&");
        sb.append("time=").append(time).append(appkey);

        String signStr = sb.toString();
        String sign = MD5.encode(signStr);

        Map<String, Object> params = new LinkedHashMap<String, Object>();
        params.put("ac", ac);
        params.put("appid", zzyAppId);
        params.put("sdkversion", sdkversion);
        params.put("sessionid", sessionIdDecode);
        params.put("time", time);
        params.put("sign", sign);

        try {
            String msg = HttpUtils.doPost(url, params);
            System.out.println("**********[ac]:" + ac);
            System.out.println("**********[appid]:" + zzyAppId);
            System.out.println("**********[time]:" + time);
            System.out.println("**********[url]:" + url);
            System.out.println("**********[appkey]:" + appkey);
            System.out.println("**********[sessionId]:" + sessionIdDecode);
            System.out.println("**********[sessionIdEndcode]:" + sessionId);
            System.out.println("**********[signStr]:" + signStr);
            System.out.println("**********[sign]:" + sign);
            System.out.println("**********[params]:" + params);
            System.out.println("**********[msg]:" + msg);
            JSONObject msgJson = new JSONObject(msg);
            int code = msgJson.optInt("code");
            JSONObject userInfo = msgJson.optJSONObject("userInfo");
            System.out.println("**********[code]:" + code);
            System.out.println("**********[userInfo]:" + userInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testZhizhuyouPay() {
        String amount = "0.01";
        String appid = "157";
        String charid = "111111";
        String cporderid = "20170503144727495487513719851455";
        String extinfo = "http%3A%2F%2Fesrsservice.yaoyuenet.com%2Fplatform2%2Fzhizhuyou";
        String gold = "0";
        String orderid = "201705031447342890";
        String serverid = "1";
        String time = "1493794098";
        String uid = "7521";
        String pay_key = "854fa9391c219a0f0cd417c7d9980e78";
        String sign = "4507ac0eb9d49281885eb5b4fe3d2856";

        StringBuilder sb = new StringBuilder();
        sb.append("amount=").append(amount).append("&");
        sb.append("appid=").append(appid).append("&");
        sb.append("charid=").append(URLEncoder.encode(charid)).append("&");
        sb.append("cporderid=").append(cporderid).append("&");
        sb.append("extinfo=").append(URLEncoder.encode(extinfo)).append("&");
        sb.append("gold=").append(gold).append("&");
        sb.append("orderid=").append(orderid).append("&");
        sb.append("serverid=").append(URLEncoder.encode(serverid)).append("&");
        sb.append("time=").append(time).append("&");
        sb.append("uid=").append(uid);
        sb.append(pay_key);
        String validSigin = MD5.encode(sb.toString());
        try {
            System.out.println("**********[sign]:" + sign);
            System.out.println("**********[validSigin]:" + validSigin);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testXingkongshijiePay() {

        String json = "{\"appid\":\"3012639381\",\"appuserid\":\"111111#1\",\"cporderid\":\"20170510174553300934085140558995\",\"cpprivate\":\"qq\",\"currency\":\"RMB\",\"feetype\":0,\"money\":0.01,\"paytype\":103,\"result\":0,\"transid\":\"32421705101745531764\",\"transtime\":\"2017-05-10 17:46:42\",\"transtype\":0,\"waresid\":1}";
        String APPV_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCve8IsUih5wbC9C3t/nENz2mWnBfEmq6ToJmNlTM6mfOxMbwHBm7RsWFfm8EaqtrpI95pwbU1ofKSky6x1qTgVQLBphtwVG+xatgxMBIkkxEafuh+VSKf/cLwsCOJXdO/inTWodnEGx9hk+u3b+fglqeLRf/YyU1wcGzGYF+H6DwIDAQAB";
        String sign = "WnFgH3PnQFdqxMg5ZYF+3F0oZ1hPP6mVjEsAeMrvHLP3e3mjaIoqj+iS5UV5Kcga7CL1qD14CzdFFpTIx7zwWxajfmow0PzG3QBOu79F9beNdXby05tqKQmb1KfqxnvBdKRo1BTAMHvs6wPKzebZevUJOIX2U2CLJ1ZMXMehjW0=";

        JSONObject transdata = null;
        try {
            transdata = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println("**********[transdata]:" + transdata);
        boolean validSigin = SignHelper.verify(transdata.toString(), sign, APPV_KEY);
        try {
            System.out.println("**********[sign]:" + sign);
            System.out.println("**********[validSigin]:" + validSigin);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testMoguwanPay() {
        String app_secret = "GHJUY3UJ65BHYOL9874HG";
        String out_trade_no = "SP_20170522165530nJbn";
        String price = "0.01";
        String pay_status = "1";
        String extend = "20170522165527769132374847903503";
        String sign = "b8c24ca4493a78be68777d6e6427300a";

        StringBuilder sb = new StringBuilder();
        sb.append(out_trade_no);
        sb.append(price);
        sb.append(pay_status);
        sb.append(extend);
        sb.append(app_secret);
        String validSigin = MD5.encode(sb.toString());

        System.out.println("sign:" + sign);
        System.out.println("validSigin:" + validSigin);
    }

    @Test
    public void testM2166Pay() {

        String sign = "MjRmMTY3OTc3OTUxY2E2NThjNjE3ZGQ3YTA1ZDI1MDY%3D";
        String pay_order_number = "PF_20170524113450oprE";
        String server_id = "1";
        String pay_amount = "0.01";
        String user_account = "hoog001";
        String cp_order_id = "20170524113446154613260394218420";
        String extend_info = "http://esrsservice.yaoyuenet.com/platform2/m2166";
        String game_id = "157";
        String pay_time = "1495596890";
        String props_name = "钻石";
        String pay_status = "1";
        String signKey = "AAEPEhILBgAWBQYFDwoQ";

        StringBuilder sb = new StringBuilder();
        sb.append("cp_order_id=").append(cp_order_id).append("&");
        sb.append("extend_info=").append(URLEncoder.encode(extend_info)).append("&");
        sb.append("game_id=").append(game_id).append("&");
        sb.append("pay_amount=").append(pay_amount).append("&");
        sb.append("pay_order_number=").append(pay_order_number).append("&");
        sb.append("pay_status=").append(pay_status).append("&");
        sb.append("pay_time=").append(pay_time).append("&");
        sb.append("props_name=").append(URLEncoder.encode(props_name)).append("&");
        sb.append("server_id=").append(server_id).append("&");
        sb.append("user_account=").append(user_account);
        sb.append(signKey);

        String validSign = URLEncoder.encode(Base64.encode(MD5.encode(sb.toString()).toLowerCase()));
        //String signDecode = URLDecoder.decode(sign);

        if (sign.equals(validSign)) {
            System.out.println("签名一致:");
        }
        System.out.println("sign:" + sign);
        System.out.println("validSign:" + validSign);

    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        /*//{orderid=14421973251921437, username=d496469116, gameid=68, roleid=111111, serverid=110, paytype=zfb, amount=1, paytime=1442197326, attach=20150914102202539468574838978372}

		Map<String, Object> params = new LinkedHashMap<String, Object>();
		params.put("orderid", "14421973251921437");
		params.put("username", "d496469116");
		params.put("gameid", 68);
		params.put("roleid", 111111);
		params.put("serverid", 110);
		params.put("paytype", "zfb");
		params.put("amount", 1);
		params.put("paytime", "1442197326");
		params.put("attach", "20150914102202539468574838978372");
		
		String validSign = Sign.signByMD5Unsort(params, "&" + "ba66a32696bfbd6616775a1bd9e9ab59");
		System.out.println();*/

		
		/*Map<String, Object> params = new LinkedHashMap<String, Object>();
        params.put("ac", "check");
		params.put("appid", 2867);
		params.put("sessionid", URLDecoder.decode("123"));
		params.put("time", System.currentTimeMillis());
		
		Map<String, Object> params2 = new LinkedHashMap<String, Object>();
		params2.put("ac", "check");
		params2.put("appid", 2867);
		params2.put("sessionid", URLEncoder.encode("123"));
		params2.put("time", System.currentTimeMillis());
		
		params.put("sign", Sign.signByMD5Unsort(params2, "cdf38486cb8f38123f8921c2f80d6829"));
		
		
		String result = HttpUtils.post("http://api.3899w.com/sdkapi.php", params);
		System.err.println(result);*/

        Map<String, String> result = new HashMap<String, String>();
        result.put("6", "C0002651");
        result.put("30", "C0002653");
        result.put("98", "C0002655");
        result.put("198", "C0002657");
        result.put("328", "C0002659");
        result.put("648", "C0002661");
        System.out.println(JsonMapper.toJson(result));

    }


}
