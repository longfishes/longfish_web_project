package com.longfish.rabbitmqdemo.controller;

import com.alibaba.fastjson.JSON;
import com.longfish.rabbitmqdemo.pojo.EmailDTO;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import static com.longfish.rabbitmqdemo.constant.RabbitMQConstant.EMAIL_EXCHANGE;

@RestController
public class EmailController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/send")
    public String sendEmail() {
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
        return "OK";
    }
}
