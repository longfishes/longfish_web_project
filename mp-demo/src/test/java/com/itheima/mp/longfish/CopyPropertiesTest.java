package com.itheima.mp.longfish;

import cn.hutool.core.bean.BeanUtil;
import com.itheima.mp.domain.po.User;
import com.itheima.mp.domain.po.UserInfo;
import com.itheima.mp.domain.vo.AddressVO;
import com.itheima.mp.domain.vo.UserVO;
import com.itheima.mp.enums.UserStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

public class CopyPropertiesTest {

    @Test
    public void testCopyForward() {
        UserVO userVO = UserVO.builder()
                .id(1L)
                .balance(1000)
                .info(UserInfo.of(1, "a", "b"))
                .status(UserStatus.NORMAL)
                .username("long")
                .addresses(List.of(AddressVO.builder().id(1L).build()))
                .build();
        User user = BeanUtil.copyProperties(userVO, User.class);
        System.out.println(user);
    }

    @Test
    public void testCopyReserve() {
        User user = User.builder()
                .id(1L)
                .balance(1000)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .info(UserInfo.of(1, "a", "b"))
                .password("111")
                .phone("743829")
                .status(UserStatus.NORMAL)
                .username("long")
                .build();
        UserVO userVO = BeanUtil.copyProperties(user, UserVO.class);
        System.out.println(userVO);
    }
}
