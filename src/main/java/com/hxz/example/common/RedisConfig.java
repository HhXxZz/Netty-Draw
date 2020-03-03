package com.hxz.example.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

/**
 * @Author: hxz
 * @Description:
 * @Date: 2019/11/12 15:04
 */

@Configuration
public class RedisConfig {


//    @Value("${spring.redis.host}")
//    private String host;
//
//    @Value("${spring.redis.port}")
//    private String port;
//
//    @Value("${spring.redis.password}")
//    private String password;
//
//    @Value("${spring.redis.timeout}")
//    private int timeout;
//
//    private JedisPoolConfig poolConfig;
//
//    public JedisPool jedisPool(){
//        poolConfig = new JedisPoolConfig();
//        //poolConfig.setMaxTotal();
//        //poolConfig.setMaxIdle(Integer.valueOf(properties.getProperty("redis.pool.maxIdle").trim()));
//        //poolConfig.setMaxWaitMillis(Long.valueOf(properties.getProperty("redis.pool.maxWait").trim()));
//        //poolConfig.setTestOnBorrow(Boolean.valueOf(properties.getProperty("redis.pool.testOnBorrow").trim()));
//        //poolConfig.setTestOnReturn(Boolean.valueOf(properties.getProperty("redis.pool.testOnReturn").trim()));
//        //poolConfig.setBlockWhenExhausted(false);
//
//        return new JedisPool(poolConfig, host, Integer.valueOf(port), Protocol.DEFAULT_TIMEOUT,password);
//    }
//
//    public Jedis getResource(){
//        return jedisPool().getResource();
//    }

}
