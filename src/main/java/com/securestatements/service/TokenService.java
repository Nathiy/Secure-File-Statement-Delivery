package com.securestatements.service;

import java.util.UUID;

public class TokenService {

    public static String generateToken(){

        return UUID.randomUUID().toString();
    }

    public static long generateExpiry(){

        long now = System.currentTimeMillis();

        return now + (10 * 60 * 1000);
    }
}
