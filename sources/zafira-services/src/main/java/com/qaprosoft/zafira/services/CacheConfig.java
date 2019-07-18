package com.qaprosoft.zafira.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Arrays;

@Configuration
@EnableCaching
public class CacheConfig {

    private static final String[] CACHE_NAMES = {"projects", "users", "testCases", "environments", "testRunStatistics", "groups"};

    @Bean
    JedisConnectionFactory jedisConnectionFactory(
            @Value("${zafira.redis.host}") String hostname,
            @Value("${zafira.redis.port}") int port
    ) {
        JedisConnectionFactory connectionFactory = new JedisConnectionFactory();
        connectionFactory.setHostName(hostname);
        connectionFactory.setPort(port);
        connectionFactory.setUsePool(Boolean.TRUE);
        return new JedisConnectionFactory();
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(@Autowired JedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        return template;
    }

    @Bean
    public CacheManager cacheManager(@Autowired RedisTemplate<String, Object> redisTemplate) {
        RedisCacheManager cacheManager = new RedisCacheManager(redisTemplate);
        cacheManager.setUsePrefix(Boolean.TRUE);
        cacheManager.setDefaultExpiration(43200);
        cacheManager.setCacheNames(Arrays.asList(CACHE_NAMES));
        return cacheManager;
    }

}
