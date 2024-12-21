package com.dattruongdev.bookstore_cqrs.utils;

import com.dattruongdev.bookstore_cqrs.security.user.BookstoreUserDetails;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
public class JwtUtils {
    @Value("${jwt.secret}")
    private String jwtSecret;

    public boolean validateJwtToken(String jwt) {
        try {
            Jwts.parser().verifyWith(key()).build().parseSignedClaims(jwt);
            return true;
        } catch(JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getUserNameFromJwtToken(String jwt) {
        return Jwts.parser().verifyWith(key()).build().parseSignedClaims(jwt).getPayload().getSubject();
    }

    public String generateJwtToken(Authentication auth) {
        BookstoreUserDetails userDetails = (BookstoreUserDetails) auth.getPrincipal();

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("id", userDetails.getId())
                .claim("username", userDetails.getUsername())
                .claim("email", userDetails.getEmail())
                .signWith(key())
                .compact();
    }

    private SecretKey key() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }
}
