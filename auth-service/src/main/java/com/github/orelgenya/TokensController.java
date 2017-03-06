package com.github.orelgenya;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("tokens")
@RefreshScope
public class TokensController {

    @Value("${secret}")
    String secret;

    @Autowired
    Environment env;

    @PostMapping
    public String create(long userId, String userEmail, long ttlMillis) {
        //Get current timestamp
        long nowMillis = System.currentTimeMillis();
        // Let's set the JWT Claims
        JwtBuilder builder = Jwts.builder().setIssuedAt(new Date(nowMillis)).setSubject(String.valueOf(userId))
                .setIssuer("MyAuthoritativeService")
                .claim("email", userEmail)
                .signWith(SignatureAlgorithm.HS256, secret)
                .setExpiration(getExpDate(nowMillis, ttlMillis));
        // Builds the JWT and serializes it to a compact, URL-safe string
        return builder.compact();
    }

    private Date getExpDate(long nowMillis, long ttlMillis) {
        return new Date(nowMillis + ttlMillis);
    }
}
