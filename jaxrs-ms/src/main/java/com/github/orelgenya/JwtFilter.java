package com.github.orelgenya;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.security.Principal;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Provider
@Component
public class JwtFilter implements ContainerRequestFilter {
    public static final String JWT_HEADER = "JWT-Token";

    @Value("${jwt.key}")
    String jwtKey;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String jwtHeader = requestContext.getHeaderString(JWT_HEADER);
        if (jwtHeader == null) {
            return;
        }
        SecurityContext sc = new JwtSecurityContext(parseJwtToken(jwtHeader),
                requestContext.getUriInfo().getBaseUri().getScheme());
        requestContext.setSecurityContext(sc);
    }

    private Jwt<Header, Claims> parseJwtToken(String jwtToken) {
        return Jwts.parser().setSigningKey(jwtKey).parse(jwtToken);
    }

    class JwtSecurityContext implements SecurityContext {

        private final Jwt<Header, Claims> token;
        private final String scheme;
        private final boolean isSecure;
        private final JwtPrincipal principal;

        JwtSecurityContext(Jwt<Header, Claims> token, String scheme) {
            System.out.println("Creating security context: " + token);
            this.token = token;
            this.scheme = scheme + "/jwt";
            this.isSecure = "https".equals(scheme);
            this.principal = new JwtPrincipal(token.getBody().getSubject(),
                    Arrays.stream(token.getBody().get("roles").toString().split(","))
                    .collect(Collectors.toSet()));
        }

        @Override
        public JwtPrincipal getUserPrincipal() {
            return principal;
        }

        @Override
        public boolean isUserInRole(String role) {
            return principal.getRoles().contains(role);
        }

        @Override
        public boolean isSecure() {
            return isSecure;
        }

        @Override
        public String getAuthenticationScheme() {
            return scheme;
        }
    }

    class JwtPrincipal implements Principal {

        private final String name;
        private final Set<String> roles;

        JwtPrincipal(String name, Set<String> roles) {
            this.name = name;
            this.roles = roles;
        }

        @Override
        public String getName() {
            return name;
        }

        public Set<String> getRoles() {
            return roles;
        }
    }
}
