package com.longfish.jclogindemo;

import com.longfish.jclogindemo.mapper.UserAuthMapper;
import com.longfish.jclogindemo.pojo.UserAuth;
import com.longfish.jclogindemo.pojo.dto.UserRegDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SpringBootTest
public class MybatisTest {

    @Autowired
    private UserAuthMapper userAuthMapper;

    @Test
    public void testMB() {
        UserRegDTO userRegDTO = UserRegDTO.builder()
                .username("fjdlksa@qq.com")
                .password("sa")
                .createTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .updateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
        UserAuth userAuth = new UserAuth();
        BeanUtils.copyProperties(userRegDTO, userAuth);
        userAuthMapper.insert(userAuth);
    }
}
