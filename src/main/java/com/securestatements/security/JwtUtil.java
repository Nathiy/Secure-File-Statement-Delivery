
package com.securestatements.security;

import io.jsonwebtoken.*;
import java.util.Date;

public class JwtUtil {

    private static final String SECRET = "IGuBo3bplURFFX9ZAWRhK3p11cL7NC9Lb6M50OfUrDT";

    public static String generateToken(String statementId) {
        return Jwts.builder()
                .setSubject(statementId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1 hour
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();
    }

    public static String validate(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token)
                    .getBody();

            return claims.getSubject();

        } catch (Exception e) {
            System.out.println("JWT ERROR: " + e.getMessage());
            return null;
        }
    }
}
