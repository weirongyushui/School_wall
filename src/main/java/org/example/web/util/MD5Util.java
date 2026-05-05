package org.example.web.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {
    
    /**
     * MD5 加密方法
     * @param input 原始字符串
     * @return 加密后的 32 位十六进制字符串
     */
    public static String md5(String input) {
        try {
            //拿到一个可用的 MD5 消息摘要对象 md，准备开始计算。
            MessageDigest md = MessageDigest.getInstance("MD5");
            /**
             * input.getBytes()："hello" 按默认编码转成字节数组 →
             * [104, 101, 108, 108, 111]（对应 h、e、l、l、o 的 ASCII 码）。
             *
             * md.digest( … )：对这 5 个字节进行 MD5 计算，得到 16 个字节 的摘要结果。
             * 有符号字节值（Java 中直接看到的值）：
             * [93, 65, 64, 42, -68, 75, 42, 118, -71, 113, -100, -47, 17, 23, -59, -110]
             * 对应无符号十六进制（方便理解）：
             * 5D 41 40 2A BC 4B 2A 76 B9 71 9C D1 11 17 C5 92
             *
             * 存入 messageDigest：这个字节数组就是 MD5 的原始指纹。
             */
            byte[] messageDigest = md.digest(input.getBytes());
            //准备拼接器：一个空的可变字符串 hexString，等待填入内容。
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * 验证密码方法
     * @param input 用户输入的密码
     * @param encryptedPassword 数据库中存储的加密密码
     * @return 验证结果，true 表示匹配，false 表示不匹配
     */
    public static boolean verify(String input, String encryptedPassword) {
        return md5(input).equals(encryptedPassword);
    }
}
