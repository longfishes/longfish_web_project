package com.itheima.mp.longfish;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.itheima.mp.domain.po.User;
import com.itheima.mp.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

@SpringBootTest
@Slf4j
public class MpTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void testQueryWrapperSelect() {
        userMapper.selectList(
                new QueryWrapper<User>()
                        .select("id", "username", "info", "balance")
                        .like("username", "o")
                        .ge("balance", 1000)
        ).forEach(user -> log.info("{}", user));
    }

    @Test
    public void testLambda() {
        userMapper.selectList(
                new LambdaQueryWrapper<User>()
                        .select(User::getId, User::getUsername, User::getInfo, User::getBalance)
                        .like(User::getUsername, "o")
                        .ge(User::getBalance, 1000)
        ).forEach(user -> log.info("{}", user));
    }

    @Test
    public void testQueryWrapperUpdate() {
        userMapper.update(
                User.builder().balance(2000).build(), new QueryWrapper<User>().eq("username", "jack"));
    }

    @Test
    public void testUpdateWrapper() {
        userMapper.update(null,
                new UpdateWrapper<User>()
                        .setSql("balance = balance - 200")
                        .in("id", Arrays.asList(1L, 2L, 4L)));
    }

    private void listAll() {
        userMapper.selectList(new QueryWrapper<>()).forEach(System.out::println);
    }
}
