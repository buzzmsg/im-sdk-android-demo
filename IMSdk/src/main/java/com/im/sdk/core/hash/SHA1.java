package com.im.sdk.core.hash;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.FileUtils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

/**
 *
 * @description
 * @time 2021/6/3 4:17 下午
 */
public class SHA1 {

//    public static String get(String str) {
//
//        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
//                'a', 'b', 'c', 'd', 'e', 'f'};
//        try {
//            MessageDigest mdTemp = MessageDigest.getInstance("SHA1");
//            mdTemp.update(str.getBytes(StandardCharsets.UTF_8));
//            byte[] md = mdTemp.digest();
//            int j = md.length;
//            char[] buf = new char[j * 2];
//            int k = 0;
//            for (byte byte0 : md) {
//                buf[k++] = hexDigits[byte0 >>> 4 & 0xf];
//                buf[k++] = hexDigits[byte0 & 0xf];
//            }
//            return new String(buf);
//        } catch (Exception e) {
//            return null;
//        }
//    }

    public static String get(String path) {
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("SHA1");
            byte[] buffer = new byte[65536];
            InputStream fis = new FileInputStream(FileUtils.getFileByPath(path));
            int n = 0;
            while (n != -1) {
                n = fis.read(buffer);
                if (n > 0) {
                    mdTemp.update(buffer, 0, n);
                }
            }
            fis.close();
            byte[] digestResult = mdTemp.digest();
            return ConvertUtils.bytes2HexString(digestResult,false);
        } catch (Exception e) {
            return null;
        }
    }

    public static String get(byte[] input) {

        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("SHA1");
            mdTemp.update(input);
            byte[] md = mdTemp.digest();
            int j = md.length;
            char[] buf = new char[j * 2];
            int k = 0;
            for (byte byte0 : md) {
                buf[k++] = hexDigits[byte0 >>> 4 & 0xf];
                buf[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(buf);
        } catch (Exception e) {
            return null;
        }
    }
}
