package com.github.orelgenya;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.web.bind.annotation.*;

import javax.naming.NamingEnumeration;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("tokens")
@RefreshScope
public class TokensController {
    private Logger logger = LoggerFactory.getLogger (TokensController.class);

    private static final String AUTHORIZATION_PREFIX = "Basic ";
    @Value("${tokens.ttl}")
    long ttlMillis;

    @Value("${secret}")
    String secret;

    @Autowired
    Environment env;

    @Autowired
    LdapTemplate ldapTemplate;

    @GetMapping
    public ResponseEntity<String> create(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth) {
        Credentials credentials = parseAuthHeader(auth);
        if (credentials == null) {
            logger.info("Unauthorized user!");
            return ResponseEntity.status(401).build();
        }

        UserInfo userInfo = authenticate(credentials);
        if (userInfo == null) {
            logger.info("Unauthorized user '{}'!", credentials.username);
            return ResponseEntity.status(401).build();
        }

        //Get current timestamp
        long nowMillis = System.currentTimeMillis();

        Set<String> roles = new HashSet<>();
        roles.addAll(userInfo.roles);

        // Let's set the JWT Claims
        JwtBuilder builder = Jwts.builder()
                .setIssuedAt(new Date(nowMillis))
                .setSubject(credentials.username)
                .setIssuer(TokensController.class.toString())
                .claim("roles", String.join(",", roles))
                .signWith(SignatureAlgorithm.HS256, secret)
                .setExpiration(getExpDate(nowMillis, ttlMillis));
        // Builds the JWT and serializes it to a compact, URL-safe string
        return ResponseEntity.ok(builder.compact());
    }

    private Credentials parseAuthHeader(String auth) {
        if (!auth.startsWith(AUTHORIZATION_PREFIX)) {
            return null;
        }
        String base64token = auth.substring(AUTHORIZATION_PREFIX.length());
        byte[] data;
        try {
            data = Base64.decodeBase64(base64token);
        } catch (IllegalArgumentException e) {
            return null;
        }
        String userToken = new String(data);
        int index = userToken.indexOf(':');
        if (index == -1) {
            return null;
        }
        Credentials credentials = new Credentials();
        credentials.username = userToken.substring(0, index);
        credentials.password = userToken.substring(index+1);
        return credentials;
    }

    private UserInfo authenticate(Credentials credentials) {
        logger.info("Authenticating '{}'...", credentials.username);

        LdapQuery query = LdapQueryBuilder.query()
                .where("uid")
                .is(credentials.username);

        try {
            ldapTemplate.authenticate(query, credentials.password);
        } catch (Exception ex) {
            return null;
        }

        List<String> result = ldapTemplate.search(
                LdapQueryBuilder.query()//.base("ou=Roles,dc=jboss,dc=org")
                        .where("member")
                        .is("uid=" + credentials.username + ",ou=Users,dc=jboss,dc=org"), //uid=mt_api,ou=Users,dc=jboss,dc=org
                (AttributesMapper<String>) attributes -> {
                    NamingEnumeration enumeration = attributes.getAll();
                    while (enumeration.hasMore()) {
                        System.out.println(enumeration.next());
                    }
                    return (String) attributes.get("cn").get();
                });

        if (result.isEmpty()) {
            return null;
        }

        UserInfo userInfo = new UserInfo();
        userInfo.id = credentials.username;
        userInfo.roles = result;
        userInfo.roles.add("Sample");

        logger.info("Authenticated '{}'. {}", credentials.username, userInfo);
        return userInfo;
    }

    private Date getExpDate(long nowMillis, long ttlMillis) {
        return new Date(nowMillis + ttlMillis);
    }

    class UserInfo {
        String id;
        String email;
        List<String> roles;

        @Override
        public String toString() {
            return "UserInfo{" +
                    "id='" + id + '\'' +
                    ", email='" + email + '\'' +
                    ", roles='" + roles + '\'' +
                    '}';
        }
    }

    class Credentials {
        String username;
        String password;
    }
}
