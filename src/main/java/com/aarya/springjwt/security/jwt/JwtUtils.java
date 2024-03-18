package com.aarya.springjwt.security.jwt;

import com.aarya.springjwt.security.services.UserDetailsImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${aarya.app.jwtSecret}")
    private String jwtSecret;

    @Value("${aarya.app.jwtExpirationMs}")
    private int jwtExpirationMs;


    private final SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS512);


   /* public String generateJwtToken(Authentication authentication) {

        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        var map = new HashMap<String, Object>();
        map.put("role", userPrincipal.getAuthorities());
        map.put("username", userPrincipal.getUsername());
        map.put("id", userPrincipal.getId());
        map.put("email", userPrincipal.getEmail());

        return Jwts.builder()
                .setSubject((userPrincipal.getUsername()))
                .addClaims(map)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
//                .signWith(key(), SignatureAlgorithm.HS256)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }*/

    public String generateJwtToken(UserDetailsImpl userPrincipal) {
        /*var map = new HashMap<String, Object>();
        map.put("role", userPrincipal.getAuthorities());
        map.put("username", userPrincipal.getUsername());
        map.put("id", userPrincipal.getId());
        map.put("email", userPrincipal.getEmail());*/
        return generateTokenFromUsername(userPrincipal.getUsername());
    }

    /*private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }*/

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String generateTokenFromUsername(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    /*public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }*/

   /* public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody().getSubject();
    }*/

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(key).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }

}
