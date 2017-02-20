package com.earphone.clone.config;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author YaoJiamin
 * @description
 * @createDate 2017/1/19
 * @createTime 13:02
 */
@Configuration
public class RedisConfig {
    @Value("${redis.ip}")
    private String host;
    @Value("${redis.port}")
    private Integer port;
    @Value("${redis.password}")
    private String password;
    @Value("${redis.db}")
    private Integer database;
    @Value("${redis.timeout}")
    private Integer timeout;

    @Bean
    @Lazy(false)
    public JedisPool jedisPool() {
        return new JedisPool(jedisPoolConfig(), host, port, timeout, password, database);
    }

    @Value("${redis.maxTotal}")
    private Integer maxTotal;
    @Value("${redis.maxIdle}")
    private Integer maxIdle;
    @Value("${redis.minIdle}")
    private Integer minIdle;

    private Boolean blockWhenExhausted;

    @Value("${redis.blockWhenExhausted}")
    public void setBlockWhenExhausted(String blockWhenExhausted) {
        this.blockWhenExhausted = Boolean.valueOf(blockWhenExhausted);
    }

    @Value("${redis.minEvictableIdleTimeMillis}")
    private Integer minEvictableIdleTimeMillis;
    @Value("${redis.timeBetweenEvictionRunsMillis}")
    private Long timeBetweenEvictionRunsMillis;
    @Value("${redis.numTestsPerEvictionRun}")
    private Integer numTestsPerEvictionRun;

    private Boolean testWhileIdle;

    @Value("${redis.testWhileIdle}")
    public void setTestWhileIdle(String testWhileIdle) {
        this.testWhileIdle = Boolean.valueOf(testWhileIdle);
    }

    @Bean
    public GenericObjectPoolConfig jedisPoolConfig() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setBlockWhenExhausted(blockWhenExhausted);
        config.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        config.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        config.setNumTestsPerEvictionRun(numTestsPerEvictionRun);
        config.setTestWhileIdle(testWhileIdle);
        return config;
    }
}
