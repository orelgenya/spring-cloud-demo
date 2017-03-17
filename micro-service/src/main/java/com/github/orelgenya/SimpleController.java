package com.github.orelgenya;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@RestController
@RequestMapping("simple")
@RefreshScope
public class SimpleController {

    @Value("${my.param}")
    String myParam;

    @Value("${auth.url}")
    String authUrl;

    @Value("${jaxrsms.url}")
    String jaxrsmsUrl;

    @Autowired
    Environment env;

    @Autowired
    RestTemplate restTemplate;

    @GetMapping
    public ResponseEntity<String> get(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        String jwtToken = getJwtToken(authHeader);
        try {
            return ResponseEntity.ok("Simple micro-service! my-param = " + env.getProperty("my.param") + "|" + myParam + "\n"
                    + "top secret is '" + getTopSecret(jwtToken) + "'");
        } catch (HttpClientErrorException ex) {
            return ResponseEntity.badRequest().body(ex.getResponseBodyAsString());
        }
    }

    private String getJwtToken(String authHeader) {
        RequestEntity requestEntity = RequestEntity.get(URI.create(authUrl))
                .header(HttpHeaders.AUTHORIZATION, authHeader).build();
        return restTemplate.exchange(requestEntity, String.class).getBody();
    }

    private String getTopSecret(String jwtToken) {
        RequestEntity requestEntity = RequestEntity.get(URI.create(jaxrsmsUrl))
                .header("JWT-Token", jwtToken).build();
        return restTemplate.exchange(requestEntity, String.class).getBody();
    }
}
