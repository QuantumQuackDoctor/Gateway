package com.smoothstack.SSGateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    public Claims getAllClaims(String token) throws ExpiredJwtException {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    public boolean isInvalid(String token) {
        try {
            return getAllClaims(token).getExpiration().before(new Date(System.currentTimeMillis()));
        } catch (ExpiredJwtException e) {
            System.out.println(e.getMessage());
            return true;
        }
    }

}
