package cn.itcast.user.web;

import cn.itcast.user.properties.PatternProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@Slf4j
@RefreshScope
public class DateController {

    @Value("${pattern.dateformat}")
    private String dateformat;

    @Value("${pattern.shared}")
    private String shared;

    @Autowired
    private PatternProperties patternProperties;

    @GetMapping("/now")
    public String getTimeNow() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(dateformat));
    }

    @GetMapping("/now2")
    public String getTimeNow2() {
        System.out.println(patternProperties);
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(patternProperties.getDateformat()));
    }

    @GetMapping("/prop")
    private String getTestVal() {
        System.out.println("shared = " + shared);
        return shared;
    }
}
