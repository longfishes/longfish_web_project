package com.longfish.mapper;

import com.longfish.pojo2.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

//mapper，类似于DAO
@Mapper
public interface UserMapper {

    @Select("select * from user")
    List<User> list();
}
