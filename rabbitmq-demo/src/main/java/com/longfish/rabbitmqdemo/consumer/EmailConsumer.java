package com.longfish.rabbitmqdemo.consumer;

import com.alibaba.fastjson.JSON;
import com.longfish.rabbitmqdemo.pojo.EmailDTO;
import com.longfish.rabbitmqdemo.util.EmailUtil;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.longfish.rabbitmqdemo.constant.RabbitMQConstant.*;

@Component
@RabbitListener(queues = EMAIL_QUEUE)
public class EmailConsumer {

    @Autowired
    private EmailUtil emailUtil;

    @RabbitHandler
    public void process(byte[] data) {
        EmailDTO emailDTO = JSON.parseObject(new String(data), EmailDTO.class);
        emailUtil.sendHtmlMail(emailDTO);
    }

}
