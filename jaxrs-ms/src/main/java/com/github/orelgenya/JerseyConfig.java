package com.github.orelgenya;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

@Component
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        register(VersionController.class);
        register(SecretController.class);
        register(JwtFilter.class);
    }
}
