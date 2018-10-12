package com.qinglan.sdk.server.common;

import java.math.BigInteger;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

public class RSAUtils {

    public static final String KEY_ALGORITHM = "RSA";
    public static final String SIGNATURE_ALGORITHM_MD5 = "MD5withRSA";
    public static final String SIGNATURE_ALGORITHM_SHA = "SHA1WithRSA";

    private static final String PUBLIC_KEY = "RSAPublicKey";
    private static final String PRIVATE_KEY = "RSAPrivateKey";

    private static final int NUMBIT = 64;

    /**
     * 生成公钥和私钥
     *
     * @return
     * @throws Exception
     */
    public static Map<String, Object> generateKeys() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator
                .getInstance(KEY_ALGORITHM);
        keyPairGen.initialize(1024);

        KeyPair keyPair = keyPairGen.generateKeyPair();

        // 公钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

        // 私钥
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        Map<String, Object> keyMap = new HashMap<String, Object>(2);

        keyMap.put(PUBLIC_KEY, publicKey);
        keyMap.put(PRIVATE_KEY, privateKey);
        return keyMap;
    }

    /**
     * 使用公钥对RSA签名有效性进行检查
     *
     * @param content       待签名数据
     * @param sign          签名值
     * @param publicKey     爱贝公钥
     * @param input_charset 编码格式
     * @return 布尔值
     */
    public static boolean verify(String content, String sign, String publicKey, String input_charset) {
        return verify(content, sign, publicKey, input_charset, SIGNATURE_ALGORITHM_MD5);
    }

    /**
     * 使用公钥对RSA签名有效性进行检查
     *
     * @param content       待签名数据
     * @param sign          签名值
     * @param publicKey     爱贝公钥
     * @param input_charset 编码格式
     * @return 布尔值
     */
    public static boolean verify(String content, String sign, String publicKey, String input_charset, String algorithm) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            byte[] encodedKey = Base64.decode2Bytes(publicKey);
            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));


            Signature signature = Signature
                    .getInstance(algorithm);

            signature.initVerify(pubKey);
            signature.update(content.getBytes(input_charset));

            return signature.verify(Base64.decode2Bytes(sign));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 使用私钥对数据进行RSA签名
     *
     * @param content       待签名数据
     * @param privateKey    商户私钥
     * @param input_charset 编码格式
     * @return 签名值
     */
    public static String sign(String content, String privateKey, String input_charset) {
        return sign(content, privateKey, input_charset, SIGNATURE_ALGORITHM_MD5);
    }

    /**
     * 使用私钥对数据进行RSA签名
     *
     * @param content       待签名数据
     * @param privateKey    商户私钥
     * @param input_charset 编码格式
     * @return 签名值
     */
    public static String sign(String content, String privateKey, String input_charset, String algorithm) {
        try {
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.decode2Bytes(privateKey));
            KeyFactory keyf = KeyFactory.getInstance(KEY_ALGORITHM);
            PrivateKey priKey = keyf.generatePrivate(priPKCS8);

            Signature signature = Signature
                    .getInstance(algorithm);

            signature.initSign(priKey);
            signature.update(content.getBytes(input_charset));

            byte[] signed = signature.sign();

            return Base64.encode(signed);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * 取得私钥
     *
     * @param keyMap
     * @return
     * @throws Exception
     */
    public static String getPrivateKey(Map<String, Object> keyMap)
            throws Exception {
        Key key = (Key) keyMap.get(PRIVATE_KEY);

        return Base64.encode(key.getEncoded());
    }

    /**
     * 取得公钥
     *
     * @param keyMap
     * @return
     * @throws Exception
     */
    public static String getPublicKey(Map<String, Object> keyMap)
            throws Exception {
        Key key = (Key) keyMap.get(PUBLIC_KEY);

        return Base64.encode(key.getEncoded());
    }

    /**
     * 获取随机质数
     *
     * @return
     */
    public static BigInteger getPrimes() {
        return BigInteger.probablePrime(NUMBIT, new Random());
    }

    /**
     * 获取公钥(128位)
     * <p>
     * 具体代码如下: <code><font color="red"><br>
     * BigInteger p = RSAUtil.getPrimes();<br>
     * BigInteger q = RSAUtil.getPrimes();<br>
     * BigInteger ran = RSAUtil.getRan(p, q);<br>
     * BigInteger n = RSAUtil.getN(p, q);//modkey -- N值<br>
     * BigInteger pKey = RSAUtil.getPublicKey(ran);//publicKey -- 公钥<br>
     * </font></code>
     *
     * @param ran 通过getRan静态方法计算出来的值
     * @return
     */
    public static BigInteger getPublicKey(BigInteger ran) {
        BigInteger temp = null;
        BigInteger e = BigInteger.ZERO;
        do {
            temp = BigInteger.probablePrime(NUMBIT, new Random());
            /*
             * 随机生成一个素数，看他是否与ran的公约数为1，如果为1，e=temp退出循环
             */
            if ((temp.gcd(ran)).equals(BigInteger.ONE)) {
                e = temp;
            }
        } while (!((temp.gcd(ran)).equals(BigInteger.ONE)));

        return e;
    }

    /**
     * 获取私钥(128位)
     * <p>
     * 具体代码如下: <code><font color="red"><br>
     * BigInteger priKey = RSAUtil.getPrivateKey(ran,pKey);//ran是产生公钥的ran变量,pKey是公钥<br>
     * </font></code>
     *
     * @param ran       通过getRan静态方法计算出来的值
     * @param publicKey 公钥
     * @return
     */
    public static BigInteger getPrivateKey(BigInteger ran, BigInteger publicKey) {
        return publicKey.modInverse(ran);
    }

    /**
     * 通过P,Q计算N值
     *
     * @param p 一个素数
     * @param q 一个素数
     * @return 返回P*Q的值n
     */
    public static BigInteger getN(BigInteger p, BigInteger q) {
        return p.multiply(q);
    }

    /**
     * 通过P,Q计算ran值 modkey
     *
     * @param p 一个素数
     *          ,不能为空
     * @param q 一个素数
     *          ,不能为空
     * @return 返回(P - 1)*(Q-1)的值ran
     */
    public static BigInteger getRan(BigInteger p, BigInteger q) {
        return (p.subtract(BigInteger.ONE))
                .multiply(q.subtract(BigInteger.ONE));
    }


    /**
     * 对明文进行加密，通过公式 密文=(明文（e次幂） mod m)
     *
     * @param em 明文
     * @param e  公钥
     * @param n  模数
     * @return 加密后的密文encodeM
     */
    private static BigInteger[] encodeRSA(byte[][] em, BigInteger e,
                                          BigInteger n) {
        BigInteger[] encodeM = new BigInteger[em.length];
        for (int i = 0; i < em.length; i++) {
            encodeM[i] = new BigInteger(em[i]);
            encodeM[i] = encodeM[i].modPow(e, n);
        }
        return encodeM;
    }

    /**
     * 对密文进行解密，通过公式 明文 = （密文（d次幂）mod m）
     *
     * @param encodeM 密文
     * @param d       密钥
     * @param n       模数
     * @return 解密后的明文dencodeM
     */
    private static byte[][] dencodeRSA(BigInteger[] encodeM, BigInteger d,
                                       BigInteger n) {
        byte[][] dencodeM = new byte[encodeM.length][];
        int i;
        int lung = encodeM.length;
        for (i = 0; i < lung; i++) {
            dencodeM[i] = encodeM[i].modPow(d, n).toByteArray();
        }
        return dencodeM;
    }

    /**
     * 将数组byte[]arrayByte,转化为二维数组,分段加密/解密
     *
     * @param arrayByte
     * @param numBytes
     * @return arrayEm 不会为空
     */
    private static byte[][] byteToEm(byte[] arrayByte, int numBytes) {
        int total = arrayByte.length;
        int dab = (total - 1) / numBytes + 1, iab = 0;
        byte[][] arrayEm = new byte[dab][];
        int i, j;
        for (i = 0; i < dab; i++) {
            arrayEm[i] = new byte[numBytes];

            for (j = 0; j < numBytes && iab < total; j++, iab++) {
                arrayEm[i][j] = arrayByte[iab];
            }
            /**
             * 补齐空格字符(ox20=32)
             */
            for (; j < numBytes; j++) {
                arrayEm[i][j] = ' ';
            }
        }
        return arrayEm;
    }

    /**
     * <font color="red"> 加密方法(如果使用了产生密钥功能,则需要同步使用此方法加密)</font>
     *
     * @param source ： 明文
     * @param e      公钥
     * @param n      modkey
     * @return 密文 带","
     * @throws Exception
     */
    public static String encrypt(String source, BigInteger e, BigInteger n)
            throws Exception {
        return encrypt(source, e, n, NUMBIT * 2);
    }

    /**
     * * 加密方法
     *
     * @param source ： 明文
     * @param e      公钥
     * @param n
     * @return 密文 带","
     * @throws Exception
     */
    public static String encrypt(String source, BigInteger e, BigInteger n,
                                 int numBit) throws Exception {
        String text = URLEncoder.encode(source, "UTF-8");// 为了支持汉字、汉字和英文混排
        if (text == null || "".equals(text)) {
            throw new Exception("明文转换为UTF-8,导致转换异常!!!");
        }
        byte[] arraySendM = text.getBytes("UTF-8");
        if (arraySendM == null) {
            throw new Exception("明文转换为UTF-8,导致转换异常!!!");
        }
        if (numBit <= 1) {
            throw new Exception("随机数位数不能少于2!!!");
        }
        int numeroByte = (numBit - 1) / 8;
        byte[][] encodSendM = byteToEm(arraySendM, numeroByte);
        BigInteger[] encodingM = encodeRSA(encodSendM, e, n);
        StringBuilder encondSm = new StringBuilder();
        for (BigInteger em : encodingM) {
            encondSm.append(em.toString(16));
            encondSm.append(" ");
        }
        return encondSm.toString();
    }

    /**
     * <font color="red"> 解密算法(如果使用了产生密钥功能,则需要同步使用此方法解密)</font>
     *
     * @param cryptograph :密文,带","
     * @param d           私钥
     * @param n           modkey
     * @return
     * @throws Exception
     */
    public static String decrypt(String cryptograph, BigInteger d, BigInteger n)
            throws Exception {
        return decrypt(cryptograph, d, n, NUMBIT * 2);
    }

    /**
     * 解密算法
     *
     * @param cryptograph :密文,带","
     * @param d           私钥
     * @param n
     * @param numBit      位数
     * @return
     * @throws Exception
     */
    public static String decrypt(String cryptograph, BigInteger d,
                                 BigInteger n, int numBit) throws Exception {
        String[] chs = cryptograph.split(" ");
        if (chs == null || chs.length <= 0) {
            throw new Exception("密文不符合要求!!!");
        }
        int numeroToken = chs.length;
        BigInteger[] StringToByte = new BigInteger[numeroToken];
        for (int i = 0; i < numeroToken; i++) {
            StringToByte[i] = new BigInteger(chs[i], 16);
        }
        byte[][] encodeM = dencodeRSA(StringToByte, d, n);
        byte[] sendMessage = Tools.StringToByte(encodeM);
        String message = new String(sendMessage, "UTF-8");
        String result = URLDecoder.decode(message, "UTF-8");
        return result;
    }

    public static void main(String[] args) throws Exception {

        Map<String, Object> keys = generateKeys();

        String pubKey = getPublicKey(keys);
        String priKey = getPrivateKey(keys);

        System.out.println("The pubKey is ");
        System.out.println(pubKey);
        System.out.println(priKey);

    }

}