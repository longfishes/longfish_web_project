package com.longfish.jclogindemo.mapper;

import com.longfish.jclogindemo.pojo.UserAuth;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserAuthMapper {

    void insert(UserAuth userAuth);

    List<UserAuth> select(UserAuth userAuth);

    void updateById(UserAuth auth);
}
