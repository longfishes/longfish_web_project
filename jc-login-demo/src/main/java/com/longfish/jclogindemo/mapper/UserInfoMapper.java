package com.longfish.jclogindemo.mapper;

import com.longfish.jclogindemo.pojo.UserInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserInfoMapper {

    List<UserInfo> selectById(Long id);
}
