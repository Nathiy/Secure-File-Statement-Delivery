package com.securestatements.security;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class JwtUtilTest {

    @Test
    void shouldGenerateAndValidateToken() {
        String statementId = "123";

        String token = JwtUtil.generateToken(statementId);
        String result = JwtUtil.validate(token);

        assertNotNull(token);
        assertEquals(statementId, result);
    }

    @Test
    void shouldFailWithInvalidToken() {
        String result = JwtUtil.validate("invalid.token.value");

        assertNull(result);
    }

    @Test
    void shouldFailWithTamperedToken() {
        String token = JwtUtil.generateToken("123");

        // Modify token slightly
        token = token + "abc";

        String result = JwtUtil.validate(token);

        assertNull(result);
    }
}