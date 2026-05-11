package com.healthcare.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordUtils {
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // Hàm dùng để băm mật khẩu khi Đăng ký
    public static String hashPassword(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    // Hàm dùng để kiểm tra mật khẩu khi Đăng nhập
    public static boolean checkPassword(String rawPassword, String hashedPassword) {
        return encoder.matches(rawPassword, hashedPassword);
    }
}