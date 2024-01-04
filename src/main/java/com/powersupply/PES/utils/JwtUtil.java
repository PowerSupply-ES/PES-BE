package com.powersupply.PES.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Date;

public class JwtUtil {

    public static String getMemberEmailFromToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof String) {
                return (String) principal;
            }
        }
        return null;
    }

    public static boolean isExpired(String token, String secretKey) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
                .getBody().getExpiration().before(new Date());
    }

    public static String getMemberEmail(String token, String secretKey) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
                .getBody().get("memberEmail", String.class);
    }

    public static String getMemberStatus(String token, String secretKey) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
                .getBody().get("memberStatus", String.class);
    }

    public static String createToken(String memberEmail, String memberStatus, String secretKey, Long expireTimeMs) {
        Claims claims = Jwts.claims();
        claims.put("memberEmail", memberEmail);
        claims.put("memberStatus", memberStatus);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expireTimeMs))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }
}
