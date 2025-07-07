package com.villagenews.backend;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoder {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "village"; // Replace with your desired password
        String encodedPassword = encoder.encode(rawPassword);
        System.out.println(encodedPassword);
    }
}

