package com.longfish.jclogindemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.longfish.jclogindemo.pojo.UserAuth;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserAuthMapper extends BaseMapper<UserAuth> {

}
