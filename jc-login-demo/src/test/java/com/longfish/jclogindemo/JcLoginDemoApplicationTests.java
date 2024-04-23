package com.longfish.jclogindemo;

import com.alibaba.fastjson.JSON;
import com.longfish.jclogindemo.pojo.dto.EmailDTO;
import com.longfish.jclogindemo.util.CodeRedisUtil;
import com.longfish.jclogindemo.util.EmailUtil;
import com.longfish.jclogindemo.constant.RabbitMQConstant;
import com.longfish.jclogindemo.util.RandomUtil;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class JcLoginDemoApplicationTests {

    @Autowired
    private EmailUtil emailUtil;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private CodeRedisUtil codeRedisUtil;

    @Autowired
    private RandomUtil randomUtil;

    @Test
    public void testRand() {
        for (int i = 0; i < 10; i++) {
            System.out.println(randomUtil.getRandomCode());
        }
    }

    @Test
    @SuppressWarnings("all")
    public void testRedis2() {
        codeRedisUtil.insert("3327407524@qq.com", String.valueOf(222222));
        System.out.println(codeRedisUtil.get("3327407524@qq.com"));
    }

    @Test
    @SuppressWarnings("all")
    public void testRedis() {
        redisTemplate.opsForValue().set("key", "val");
    }

    @Test
    public void testMQ() {
        String username = "longfishes@qq.com";
        Map<String, Object> map = new HashMap<>();
        map.put("content", "001111");
        EmailDTO emailDTO = EmailDTO.builder()
                .email(username)
                .subject("消息队列测试")
                .template("code.html")
                .commentMap(map)
                .build();
        System.out.println(emailDTO);
        rabbitTemplate.convertAndSend(RabbitMQConstant.EMAIL_EXCHANGE, "*", new Message(JSON.toJSONBytes(emailDTO), new MessageProperties()));
    }

    @Test
    public void testSend() {
        String username = "longfishes@qq.com";
        Map<String, Object> map = new HashMap<>();
        map.put("content", "测试2");
        EmailDTO emailDTO = EmailDTO.builder()
                .email(username)
                .subject("测试标题")
                .template("code.html")
                .commentMap(map)
                .build();
        System.out.println(emailDTO);
        emailUtil.sendHtmlMail(emailDTO);
    }

    @Test
    public void test2() {
        for (int i = 0; i < 100; i++) {
            System.out.println(randomUtil.getRandomCode());
        }
    }
}
