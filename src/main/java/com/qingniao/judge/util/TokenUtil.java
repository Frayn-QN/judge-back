package com.qingniao.judge.util;

import jakarta.servlet.http.HttpServletRequest;

import java.security.SecureRandom;
import java.util.Date;

public class TokenUtil {
    public static String generateToken(String seed) {
        int length = 20;
        SecureRandom random = new SecureRandom();// 以id与时间为种子
        random.setSeed(seed.getBytes());
        random.setSeed(new Date().getTime());

        StringBuilder stringBuilder = new StringBuilder();
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        for(int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            stringBuilder.append(characters.charAt(index));
        }
        return stringBuilder.toString();
    }

    public static String extractToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7); // 返回token字符串，"Bearer "之后的部分
        }
        return null; // 如果没有找到token，则返回null
    }

    public static String generateVerifyCode(String seed) {
        int length = 6;
        SecureRandom random = new SecureRandom();// 以id与时间为种子
        random.setSeed(seed.getBytes());
        random.setSeed(new Date().getTime());

        StringBuilder stringBuilder = new StringBuilder();
        String characters = "0123456789";
        for(int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            stringBuilder.append(characters.charAt(index));
        }
        return stringBuilder.toString();
    }
}
