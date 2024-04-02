package com.longfish.jclogindemo.controller;

import com.alibaba.fastjson.JSON;
import com.longfish.jclogindemo.constant.RabbitMQConstant;
import com.longfish.jclogindemo.pojo.Result;
import com.longfish.jclogindemo.pojo.dto.EmailDTO;
import com.longfish.jclogindemo.util.CodeRedisUtil;
import com.longfish.jclogindemo.util.RandomUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@Tag(name = "通用接口")
@RequestMapping("/common")
public class CommonController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private CodeRedisUtil codeRedisUtil;

    @Autowired
    private RandomUtil randomUtil;

    @Operation(summary = "发送邮箱验证码")
    @PostMapping("/code")
    @Parameters({
            @Parameter(name = "username", description = "用户名", required = true)
    })
    public Result sendCode(String username) {
        Map<String, Object> map = new HashMap<>();
        String code = randomUtil.getRandomCode();
        map.put("content", code);
        EmailDTO emailDTO = EmailDTO.builder()
                .email(username)
                .subject("验证码")
                .template("common.html")
                .commentMap(map)
                .build();
        rabbitTemplate.convertAndSend(RabbitMQConstant.EMAIL_EXCHANGE, "*", new Message(JSON.toJSONBytes(emailDTO), new MessageProperties()));
        codeRedisUtil.insert(username, code);
        return Result.success();
    }
}
