package com.qaprosoft.zafira.dbaccess.utils;

import java.util.Random;

public class KeyGenerator {

    public static Integer getKey() {
        Random random = new Random();
        Integer key = 0;

        for(int i = 0; i < 10; i++) {
            key = key * 10 + random.nextInt();
        }
        return key;
    }
}
