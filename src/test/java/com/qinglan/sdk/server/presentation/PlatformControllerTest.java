package com.qinglan.sdk.server.presentation;


import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import javax.annotation.Resource;

import com.qinglan.sdk.server.application.ChannelUtilsService;
import com.qinglan.sdk.server.domain.basic.ChannelGameEntity;
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
import com.qinglan.sdk.server.application.OrderService;
import com.qinglan.sdk.server.application.ChannelService;
import com.qinglan.sdk.server.BasicRepository;
import com.qinglan.sdk.server.domain.basic.Order;
import com.qinglan.sdk.server.domain.platform.MMYPayResult;

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
