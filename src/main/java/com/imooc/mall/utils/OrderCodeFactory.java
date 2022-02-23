package com.imooc.mall.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class OrderCodeFactory {
    private static String getDateTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(new Date());
    }

    private static int getRandom(Long n) {
        Random random = new Random();
        return (int)(random.nextDouble() * (90000)) + 10000;
    }

    public static String getOrderCode(Long userId) {
        return getDateTime() + getRandom(userId);
    }
}
