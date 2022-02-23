package com.imooc.mall.utils;

import com.imooc.mall.common.Constant;
import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.spec.PSource;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {
    public static String getMD5Str(String password) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("md5");
        return Base64.encodeBase64String(md5.digest((password+ Constant.SALT).getBytes()));
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        String md5Str = getMD5Str("1234");
        System.out.println(md5Str);

    }
}
