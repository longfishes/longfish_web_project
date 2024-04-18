package cn.itcast.user.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@Slf4j
@RequestMapping("/date")
public class DateController {

    @Value("${pattern.dateformat}")
    private String dateformat;

    @GetMapping("/now")
    public String getTimeNow() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(dateformat));
    }
}
