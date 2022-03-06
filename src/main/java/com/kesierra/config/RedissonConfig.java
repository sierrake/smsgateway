package com.kesierra.config;


import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.redisson.config.Config;
@Configuration
public class RedissonConfig {
    @Value("${spring.redis.host}")
    private String host; // redisson.address=redis://127.0.0.1:6379
    @Value("${spring.redis.port}")
    private String port;

    @Bean
    public RedissonClient getRedisson() throws Exception {
        RedissonClient redisson = null;
        String addressUrl = "redis://" + host + ":" + port;
        Config config = new Config();
        //redis://192.168.31.28:6379
        config.useSingleServer()
                .setAddress(addressUrl);
        redisson = Redisson.create(config);

        return redisson;
    }
}
