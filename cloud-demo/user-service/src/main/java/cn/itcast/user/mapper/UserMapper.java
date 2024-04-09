package cn.itcast.user.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

public interface UserMapper {

    @Select("select * from test_longfish.tb_user where id = #{id}")
    Map findById(@Param("id") Long id);
}
