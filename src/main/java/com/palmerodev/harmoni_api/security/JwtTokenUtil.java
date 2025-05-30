package com.palmerodev.harmoni_api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;


@Component
public class JwtTokenUtil implements Serializable {

    public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;
    private static final String SERIAL_VERSION_UUID = generateCompactUUID();

    @Value("${jwt.secret}")
    private String secret;

    public static String generateCompactUUID() {
        return UUID.randomUUID().toString().replace("-", ""); // ejemplo: "f47ac10b58cc4372a5670e02b2c3d479"
    }

    public String getUsernameFromToken(String token) {

        return getClaimFromToken(token, Claims::getSubject);

    }

    public Date getIssuedAtDateFromToken(String token) {


        return getClaimFromToken(token, Claims::getIssuedAt);


    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {


        final var claims = getAllClaimsFromToken(token);

        return claimsResolver.apply(claims);

    }

    private Claims getAllClaimsFromToken(String token) {


        return Jwts.parserBuilder().setSigningKey(generateKey()).build().parseClaimsJws(token).getBody();


    }

    private Key generateKey() {
        return io.jsonwebtoken.security.Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(UserDetails userDetails) {


        Map<String, Object> claims = new HashMap<>();

        return doGenerateToken(claims, userDetails.getUsername());


    }

    private String doGenerateToken(Map<String, Object> claims, String subject) {

        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                   .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000)).signWith(generateKey(), SignatureAlgorithm.HS512).compact();

    }

    public Boolean canTokenBeRefreshed(String token) {


        return (!isTokenExpired(token) || ignoreTokenExpiration(token));


    }

    private Boolean isTokenExpired(String token) {

        final var expiration = getExpirationDateFromToken(token);

        return expiration.before(new Date());

    }

    private Boolean ignoreTokenExpiration(String token) {


        return false;


    }

    public Date getExpirationDateFromToken(String token) {


        return getClaimFromToken(token, Claims::getExpiration);

    }

    public Boolean validateToken(String token, UserDetails userDetails) {


        final var username = getUsernameFromToken(token);

        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));


    }

}