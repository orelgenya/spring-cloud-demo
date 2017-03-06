package com.github.orelgenya;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spring.cache.HazelcastCacheManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
@EnableCaching
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    @Profile("!standalone")
    HazelcastInstance hazelcastInstance() {
        return HazelcastClient.newHazelcastClient();
    }


    @Bean
    CacheManager cacheManager() {
        return new HazelcastCacheManager(hazelcastInstance()); // (3)
    }
}
