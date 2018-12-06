/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qinglan.sdk.server.common;

/**
 * @author Administrator
 */

public class Base64 {
    private static final char[] base64Map =  //base64 character table

            {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
                    'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b',
                    'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
                    'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3',
                    '4', '5', '6', '7', '8', '9', '+', '/'};

    static private final int BASELENGTH = 128;
    static private final int LOOKUPLENGTH = 64;
    static private final int TWENTYFOURBITGROUP = 24;
    static private final int EIGHTBIT = 8;
    static private final int SIXTEENBIT = 16;
    static private final int FOURBYTE = 4;
    static private final int SIGN = -128;
    static private final char PAD = '=';
    static private final boolean fDebug = false;
    static final private byte[] base64Alphabet = new byte[BASELENGTH];
    static final private char[] lookUpBase64Alphabet = new char[LOOKUPLENGTH];

    static {
        for (int i = 0; i < BASELENGTH; ++i) {
            base64Alphabet[i] = -1;
        }
        for (int i = 'Z'; i >= 'A'; i--) {
            base64Alphabet[i] = (byte) (i - 'A');
        }
        for (int i = 'z'; i >= 'a'; i--) {
            base64Alphabet[i] = (byte) (i - 'a' + 26);
        }

        for (int i = '9'; i >= '0'; i--) {
            base64Alphabet[i] = (byte) (i - '0' + 52);
        }

        base64Alphabet['+'] = 62;
        base64Alphabet['/'] = 63;

        for (int i = 0; i <= 25; i++) {
            lookUpBase64Alphabet[i] = (char) ('A' + i);
        }

        for (int i = 26, j = 0; i <= 51; i++, j++) {
            lookUpBase64Alphabet[i] = (char) ('a' + j);
        }

        for (int i = 52, j = 0; i <= 61; i++, j++) {
            lookUpBase64Alphabet[i] = (char) ('0' + j);
        }
        lookUpBase64Alphabet[62] = (char) '+';
        lookUpBase64Alphabet[63] = (char) '/';

    }

    /**
     * convert the platform dependent string characters to UTF8 which can
     * also be done by calling the java String method getBytes("UTF-8"),but I
     * hope to do it from the ground up.
     */

    private static byte[] toUTF8ByteArray(String s) {
        int ichar;
        byte buffer[] = new byte[3 * (s.length())];
        byte hold[];
        int index = 0;
        int count = 0; //count the actual bytes in the
        //buffer array

        for (int i = 0; i < s.length(); i++) {
            ichar = (int) s.charAt(i);

            //determine the bytes for a specific character
            if ((ichar >= 0x0080) & (ichar <= 0x07FF)) {
                buffer[index++] = (byte) ((6 << 5) | ((ichar >> 6) & 31));
                buffer[index++] = (byte) ((2 << 6) | (ichar & 63));
                count += 2;
            }

            //determine the bytes for a specific character
            else if ((ichar >= 0x0800) & (ichar <= 0x0FFFF)) {
                buffer[index++] = (byte) ((14 << 4) | ((ichar >> 12) & 15));
                buffer[index++] = (byte) ((2 << 6) | ((ichar >> 6) & 63));
                buffer[index++] = (byte) ((2 << 6) | (ichar & 63));
                count += 3;
            }

            //determine the bytes for a specific character
            else if ((ichar >= 0x0000) & (ichar <= 0x007F)) {
                buffer[index++] = (byte) ((0 << 7) | (ichar & 127));
                count += 1;
            }

            //longer than 16 bit Unicode is not supported
            else throw new RuntimeException("Unsupported encoding character length!\n");
        }
        hold = new byte[count];
        System.arraycopy(buffer, 0, hold, 0, count); //trim to size
        return hold;
    }

    public static String encode(String s) {
        byte buf[] = toUTF8ByteArray(s);
        return encode(buf);
    }

    public static String encode(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        String padder = "";

        if (buf.length == 0) return "";

        //cope with less than 3 bytes conditions at the end of buf

        switch (buf.length % 3) {
            case 1: {
                padder += base64Map[((buf[buf.length - 1] >>> 2) & 63)];
                padder += base64Map[((buf[buf.length - 1] << 4) & 63)];
                padder += "==";
                break;
            }
            case 2: {
                padder += base64Map[(buf[buf.length - 2] >>> 2) & 63];
                padder += base64Map[(((buf[buf.length - 2] << 4) & 63)) | (((buf[buf.length - 1] >>> 4) & 63))];
                padder += base64Map[(buf[buf.length - 1] << 2) & 63];
                padder += "=";
                break;
            }
            default:
                break;
        }

        int temp = 0;
        int index = 0;

        //encode buf.length-buf.length%3 bytes which must be a multiply of 3

        for (int i = 0; i < (buf.length - (buf.length % 3)); ) {
            //get three bytes and encode them to four base64 characters
            temp = ((buf[i++] << 16) & 0xFF0000) | ((buf[i++] << 8) & 0xFF00) | (buf[i++] & 0xFF);
            index = (temp >> 18) & 63;
            sb.append(base64Map[index]);
            if (sb.length() % 76 == 0)//a Base64 encoded line is no longer than 76 characters
                sb.append('\n');

            index = (temp >> 12) & 63;
            sb.append(base64Map[index]);
            if (sb.length() % 76 == 0)
                sb.append('\n');

            index = (temp >> 6) & 63;
            sb.append(base64Map[index]);
            if (sb.length() % 76 == 0)
                sb.append('\n');

            index = temp & 63;
            sb.append(base64Map[index]);
            if (sb.length() % 76 == 0)
                sb.append('\n');
        }

        sb.append(padder);  //add the remaining one or two bytes
        return sb.toString();
    }

    public static String decode(String s) {

        byte buf[];
        try {
            buf = decodeToByteArray(s);

            s = new String(buf, "UTF-8");
            s = s.replaceAll("[\\n|\\r]", "");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            return "";
        }
        return s;
    }

    public static byte[] decodeToByteArray(String s) throws Exception {
        byte hold[];

        if (s.length() == 0) return null;
        byte buf[] = s.getBytes("iso-8859-1");
        byte debuf[] = new byte[buf.length * 3 / 4];
        byte tempBuf[] = new byte[4];
        int index = 0;
        int index1 = 0;
        int temp;
        int count = 0;
        int count1 = 0;

        //decode to byte array
        for (int i = 0; i < buf.length; i++) {
            if (buf[i] >= 65 && buf[i] < 91)
                tempBuf[index++] = (byte) (buf[i] - 65);
            else if (buf[i] >= 97 && buf[i] < 123)
                tempBuf[index++] = (byte) (buf[i] - 71);
            else if (buf[i] >= 48 && buf[i] < 58)
                tempBuf[index++] = (byte) (buf[i] + 4);
            else if (buf[i] == '+')
                tempBuf[index++] = 62;
            else if (buf[i] == '/')
                tempBuf[index++] = 63;
            else if (buf[i] == '=') {
                tempBuf[index++] = 0;
                count1++;
            }

            //Discard line breaks and other nonsignificant characters
            else {
                if (buf[i] == '\n' || buf[i] == '\r' || buf[i] == ' ' || buf[i] == '\t')
                    continue;
                else throw new RuntimeException("Illegal character found in encoded string!");
            }
            if (index == 4) {
                temp = ((tempBuf[0] << 18)) | ((tempBuf[1] << 12)) | ((tempBuf[2] << 6)) | (tempBuf[3]);
                debuf[index1++] = (byte) (temp >> 16);
                debuf[index1++] = (byte) ((temp >> 8) & 255);
                debuf[index1++] = (byte) (temp & 255);
                count += 3;
                index = 0;
            }
        }
        hold = new byte[count - count1];
        System.arraycopy(debuf, 0, hold, 0, count - count1); //trim to size
        return hold;
    }

    public static byte[] decode2Bytes(String encoded) {

        if (encoded == null) {
            return null;
        }

        char[] base64Data = encoded.toCharArray();
        // remove white spaces
        int len = removeWhiteSpace(base64Data);

        if (len % FOURBYTE != 0) {
            return null;//should be divisible by four
        }

        int numberQuadruple = (len / FOURBYTE);

        if (numberQuadruple == 0) {
            return new byte[0];
        }

        byte decodedData[] = null;
        byte b1 = 0, b2 = 0, b3 = 0, b4 = 0;
        char d1 = 0, d2 = 0, d3 = 0, d4 = 0;

        int i = 0;
        int encodedIndex = 0;
        int dataIndex = 0;
        decodedData = new byte[(numberQuadruple) * 3];

        for (; i < numberQuadruple - 1; i++) {

            if (!isData((d1 = base64Data[dataIndex++])) || !isData((d2 = base64Data[dataIndex++]))
                    || !isData((d3 = base64Data[dataIndex++]))
                    || !isData((d4 = base64Data[dataIndex++]))) {
                return null;
            }//if found "no data" just return null

            b1 = base64Alphabet[d1];
            b2 = base64Alphabet[d2];
            b3 = base64Alphabet[d3];
            b4 = base64Alphabet[d4];

            decodedData[encodedIndex++] = (byte) (b1 << 2 | b2 >> 4);
            decodedData[encodedIndex++] = (byte) (((b2 & 0xf) << 4) | ((b3 >> 2) & 0xf));
            decodedData[encodedIndex++] = (byte) (b3 << 6 | b4);
        }

        if (!isData((d1 = base64Data[dataIndex++])) || !isData((d2 = base64Data[dataIndex++]))) {
            return null;//if found "no data" just return null
        }

        b1 = base64Alphabet[d1];
        b2 = base64Alphabet[d2];

        d3 = base64Data[dataIndex++];
        d4 = base64Data[dataIndex++];
        if (!isData((d3)) || !isData((d4))) {//Check if they are PAD characters
            if (isPad(d3) && isPad(d4)) {
                if ((b2 & 0xf) != 0)//last 4 bits should be zero
                {
                    return null;
                }
                byte[] tmp = new byte[i * 3 + 1];
                System.arraycopy(decodedData, 0, tmp, 0, i * 3);
                tmp[encodedIndex] = (byte) (b1 << 2 | b2 >> 4);
                return tmp;
            } else if (!isPad(d3) && isPad(d4)) {
                b3 = base64Alphabet[d3];
                if ((b3 & 0x3) != 0)//last 2 bits should be zero
                {
                    return null;
                }
                byte[] tmp = new byte[i * 3 + 2];
                System.arraycopy(decodedData, 0, tmp, 0, i * 3);
                tmp[encodedIndex++] = (byte) (b1 << 2 | b2 >> 4);
                tmp[encodedIndex] = (byte) (((b2 & 0xf) << 4) | ((b3 >> 2) & 0xf));
                return tmp;
            } else {
                return null;
            }
        } else { //No PAD e.g 3cQl
            b3 = base64Alphabet[d3];
            b4 = base64Alphabet[d4];
            decodedData[encodedIndex++] = (byte) (b1 << 2 | b2 >> 4);
            decodedData[encodedIndex++] = (byte) (((b2 & 0xf) << 4) | ((b3 >> 2) & 0xf));
            decodedData[encodedIndex++] = (byte) (b3 << 6 | b4);

        }

        return decodedData;
    }

    private static int removeWhiteSpace(char[] data) {
        if (data == null) {
            return 0;
        }

        // count characters that's not whitespace
        int newSize = 0;
        int len = data.length;
        for (int i = 0; i < len; i++) {
            if (!isWhiteSpace(data[i])) {
                data[newSize++] = data[i];
            }
        }
        return newSize;
    }

    private static boolean isWhiteSpace(char octect) {
        return (octect == 0x20 || octect == 0xd || octect == 0xa || octect == 0x9);
    }

    private static boolean isPad(char octect) {
        return (octect == PAD);
    }

    private static boolean isData(char octect) {
        return (octect < BASELENGTH && base64Alphabet[octect] != -1);
    }

    public static void main(String[] args) {
        System.out.println(decode("QUFDMjc5ODI3MkI1MzBBODM1MTZERjY4MjY5NzhFMDg3NDdCODNDOU1UY3lNVEV4TWpJek5qa3hNVEk0TkRJek9Ea3JNakkwTURrME56QTJNVEl6TnpBek5qSTJOREV4T0RRNE5qRXdOakV5T0RrNE16YzBNell4"));
    }
}    