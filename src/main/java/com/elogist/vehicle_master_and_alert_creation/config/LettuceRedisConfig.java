package com.elogist.vehicle_master_and_alert_creation.config;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class LettuceRedisConfig {

    @Value("${redis.host}")
    private String redisHost;

    @Value("${redis.port}")
    private String redisPort;

    @Value("${redis.password}")
    private String redisPassword;


    @Bean("redisConnectionFactory")
    public RedisConnectionFactory connectionFactory() {

        LettucePoolingClientConfiguration lettucePoolConfig = getLettucePoolingClientConfiguration();

        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(redisHost, Integer.parseInt(redisPort));

        redisStandaloneConfiguration.setPassword(redisPassword);

        return new LettuceConnectionFactory(redisStandaloneConfiguration, lettucePoolConfig);
    }

    @Bean("redisTemplate")
    public RedisTemplate<Object, Object> redisTemplate() {

        RedisConnectionFactory redisConnectionFactory = connectionFactory();

        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        StringRedisSerializer redisSerializer = new StringRedisSerializer();
        template.setKeySerializer(redisSerializer);
        template.setValueSerializer(redisSerializer);
        template.setHashKeySerializer(redisSerializer);
        template.setHashValueSerializer(redisSerializer);

        return template;
    }

    private LettucePoolingClientConfiguration getLettucePoolingClientConfiguration() {

        ClientResources dcr = DefaultClientResources.create();

        ClientOptions options = ClientOptions.builder()
                .disconnectedBehavior(ClientOptions.DisconnectedBehavior.REJECT_COMMANDS)
                .autoReconnect(true)
                .build();

        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
        genericObjectPoolConfig.setMinIdle(5);
        genericObjectPoolConfig.setMaxIdle(20);

        LettucePoolingClientConfiguration lettucePoolConfig = LettucePoolingClientConfiguration.builder()
                .poolConfig(genericObjectPoolConfig)
                .clientOptions(options)
                .clientResources(dcr)
                .build();

        return lettucePoolConfig;
    }
}
