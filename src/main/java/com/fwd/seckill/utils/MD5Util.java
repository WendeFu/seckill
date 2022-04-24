package com.fwd.seckill.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

@Component
public class MD5Util {

    private static final String slat = "1a2b3c4d";  // 和前端的盐同步，第一次加密使用

    private static String md5(String src) {
        return DigestUtils.md5Hex(src);
    }

    /**
     * 第一次加密，从前端到服务器
     * @param inputPass 前端输入的明文密码
     * @return
     */
    public static String inputPassToFormPass(String inputPass) {
        String str = "" + slat.charAt(0) + slat.charAt(2) + inputPass + slat.charAt(5) + slat.charAt(4);
        return md5(str);
    }

    /**
     * 第二次加密，从服务器到数据库
     * @param formPass 前端加密过的密码
     * @param slatDB 随机生成的盐（要存入数据库），不同于第一次使用的盐
     * @return
     */
    public static String formPassToDBPass(String formPass, String slatDB) {
        String str = "" + slatDB.charAt(2) + slatDB.charAt(3) + formPass + slatDB.charAt(2) + slatDB.charAt(4);
        return md5(str);
    }

    /**
     *
     * @param inputPass 前端输入的明文密码
     * @param slatDB 随机生成的盐（要存入数据库），不同于第一次使用的盐
     * @return
     */
    public static String inputPassToDBPass(String inputPass, String slatDB) {
        String formPass = inputPassToFormPass(inputPass);
        String DBPass = formPassToDBPass(formPass, slatDB);
        return DBPass;
    }

    public static void main(String[] args) {
        /*String inputPass = "123456";
        String formPass = inputPassToFormPass(inputPass);
        System.out.println(formPass);
        String DBPass = formPassToDBPass(formPass, "2b3c4d5e");
        System.out.println(DBPass);
        System.out.println(inputPassToDBPass(inputPass, "2b3c4d5e"));*/
        System.out.println(md5("1"));
    }
}
