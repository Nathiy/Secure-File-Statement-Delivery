package com.securestatements.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TokenServiceTest {

    @Test
    public void testGenerateToken() {
        String token = TokenService.generateToken();
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    public void testGenerateExpiry() {
        long expiry = TokenService.generateExpiry();
        assertTrue(expiry > System.currentTimeMillis());
    }
}
