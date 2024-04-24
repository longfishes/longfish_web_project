package com.itheima.mp.longfish;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.mp.domain.po.User;
import com.itheima.mp.service.IUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class PageTest {

    @Autowired
    private IUserService userService;

    @Test
    public void testPage() {
        Page<User> page = Page.of(1, 2);
        page.addOrder(new OrderItem().setColumn("update_time").setAsc(true));
        page.addOrder(new OrderItem().setColumn("balance").setAsc(true));
        userService.page(page, new LambdaQueryWrapper<>());

        long total = page.getTotal();
        long pages = page.getPages();
        List<User> records = page.getRecords();

        System.out.println("total = " + total);
        System.out.println("pages = " + pages);
        System.out.println("records = ");
        records.forEach(System.out::println);
    }
}
