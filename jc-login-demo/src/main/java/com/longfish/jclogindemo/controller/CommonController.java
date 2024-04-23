package com.longfish.jclogindemo.controller;

import com.longfish.jclogindemo.pojo.Result;
import com.longfish.jclogindemo.pojo.dto.CodeDTO;
import com.longfish.jclogindemo.util.EmailUtil;
import com.longfish.jclogindemo.util.RandomUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "通用接口")
@RequestMapping("/common")
public class CommonController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RandomUtil randomUtil;

    @Autowired
    private EmailUtil emailUtil;

    @Operation(summary = "发送邮箱验证码")
    @PostMapping("/code")
    public Result sendCode(@RequestBody CodeDTO codeDTO) {
//        if (!emailUtil.check(codeDTO.getUsername())) {
//            throw new BizException(StatusCodeEnum.EMAIL_FORMAT_ERROR);
//        }
//        Map<String, Object> map = new HashMap<>();
//        String code = randomUtil.getRandomCode();
//        map.put("content", code);
//        EmailDTO emailDTO = EmailDTO.builder()
//                .email(codeDTO.getUsername())
//                .subject("验证码")
//                .template("code.html")
//                .commentMap(map)
//                .build();
//        rabbitTemplate.convertAndSend(RabbitMQConstant.EMAIL_EXCHANGE, "*", new Message(JSON.toJSONBytes(emailDTO), new MessageProperties()));

        return Result.success();
    }
}
