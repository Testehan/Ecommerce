package com.testehan.ecommerce.backend.user;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

public class PasswordEncoderTest {

    @Test
    public void test(){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String raw = "test123456";
        var encodedPassword = passwordEncoder.encode(raw);

        System.out.println(encodedPassword);

        assertThat(passwordEncoder.matches(raw,encodedPassword)).isTrue();
    }
}
