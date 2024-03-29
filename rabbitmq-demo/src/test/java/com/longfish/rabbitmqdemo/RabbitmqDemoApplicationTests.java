package com.longfish.rabbitmqdemo;

import com.alibaba.fastjson.JSON;
import com.longfish.rabbitmqdemo.pojo.EmailDTO;
import com.longfish.rabbitmqdemo.util.EmailUtil;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

import static com.longfish.rabbitmqdemo.constant.RabbitMQConstant.*;

@SpringBootTest
class RabbitmqDemoApplicationTests {

    @Autowired
    private EmailUtil emailUtil;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void testMQ() {
        String username = "longfishes@qq.com";
        Map<String, Object> map = new HashMap<>();
        map.put("content", "测试2");
        EmailDTO emailDTO = EmailDTO.builder()
                .email(username)
                .subject("消息队列测试")
                .template("commontest.html")
                .commentMap(map)
                .build();
        System.out.println(emailDTO);
        rabbitTemplate.convertAndSend(EMAIL_EXCHANGE, "*", new Message(JSON.toJSONBytes(emailDTO), new MessageProperties()));
    }

    @Test
    public void testSend() {
        String username = "spieny-dev@qq.com";
        Map<String, Object> map = new HashMap<>();
        map.put("content", "测试2");
        EmailDTO emailDTO = EmailDTO.builder()
                .email(username)
                .subject("测试标题")
                .template("commontest.html")
                .commentMap(map)
                .build();
        System.out.println(emailDTO);
        emailUtil.sendHtmlMail(emailDTO);
    }

}
