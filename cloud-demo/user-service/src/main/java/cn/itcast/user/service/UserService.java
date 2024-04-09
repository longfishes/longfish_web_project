package cn.itcast.user.service;

import cn.itcast.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public Map queryById(Long id) {
        return userMapper.findById(id);
    }
}
