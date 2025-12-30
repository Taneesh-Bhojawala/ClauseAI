package com.clauseai.backend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils{
    private final String jwtSecret = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private final int jwtExpirationMs = 86400000;

    private Key key(){
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }
    public String generateJwtToken(Authentication authentication){
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        return Jwts.builder()
                .setSubject((userPrincipal.getUsername()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key(),SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUserNameFromJwtToken(String token){
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateJwtToken(String authToken){
        try{
            Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(authToken);
            return true;
        }catch(SecurityException | MalformedJwtException e){
            System.err.println("Invalid JWT signature: " + e.getMessage());
        }catch (ExpiredJwtException e){
            System.err.println("JWT token is expired: " + e.getMessage());
        }catch(UnsupportedJwtException e){
            System.err.println("JWT token is unsupported: " + e.getMessage());
        }catch(IllegalArgumentException e){
            System.err.println("JWT claims string is empty: " + e.getMessage());
        }

        return false;
    }
}