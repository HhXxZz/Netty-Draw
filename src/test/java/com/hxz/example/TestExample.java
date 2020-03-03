package com.hxz.example;

import com.hxz.example.client.IMClient;
import com.hxz.example.common.RedisConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.Jedis;

/**
 * @Author: hxz
 * @Description:
 * @Date: 2019/11/4 11:28
 */

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes={MySpringBootApplication.class})
public class TestExample {

    @Autowired
    IMClient client;

    @Test
    public void testOne2(){
        client.sendMsg();
    }




}
