package com.itheima.mp.longfish;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.mp.domain.po.User;
import com.itheima.mp.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class CustomerSqlTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void testUpdate() {
        int amount = 200;
        List<Long> ids = List.of(1L, 2L, 4L);

        userMapper.updateBalanceByIds(new LambdaQueryWrapper<User>().in(User::getId, ids), amount);
    }
}
