package com.github.orelgenya;

import com.hazelcast.core.HazelcastInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Component
@Path("/version")
public class VersionController {
    public static final String CONTENT_TYPE = MediaType.APPLICATION_JSON + ";charset=utf-8";

    @Value("${version}")
    String version;

    private int counter;

    @Autowired
    HazelcastInstance instance;

    @GET
    @Produces(CONTENT_TYPE)
    public String getVersion() {
        return version + '-' + instance.getName() + '-' + counter++;
    }
}
