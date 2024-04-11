package cn.itcast.order.service;

import cn.itcast.order.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@SuppressWarnings("all")
public class OrderService {

    private final String userService = "userservice";

    private final String scheme = "http";

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private RestTemplate restTemplate;

    public Map queryOrderById(Long orderId) {
        Map order = orderMapper.findById(orderId);
        Long userId = (Long) order.get("user_id");
        String url = scheme + "://" + userService + "/user/" + userId;
        Map user = restTemplate.getForObject(url, Map.class);
        order.put("user", user);
        return order;
    }
}
