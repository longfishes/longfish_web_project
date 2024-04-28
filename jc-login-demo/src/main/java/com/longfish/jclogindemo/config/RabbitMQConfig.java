package com.longfish.jclogindemo.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.longfish.jclogindemo.constant.RabbitMQConstant.*;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue emailQueue() {
        return new Queue(LOGIN_EMAIL_QUEUE, true);
    }

    @Bean
    public FanoutExchange emailExchange() {
        return new FanoutExchange(LOGIN_EMAIL_EXCHANGE, true, false);
    }

    @Bean
    public Binding bindingEmailDirect() {
        return BindingBuilder.bind(emailQueue()).to(emailExchange());
    }

}
