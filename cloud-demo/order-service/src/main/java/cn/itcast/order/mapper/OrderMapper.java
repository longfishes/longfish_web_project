package cn.itcast.order.mapper;

import org.apache.ibatis.annotations.Select;

import java.util.Map;

public interface OrderMapper {

    @Select("select * from test_longfish.tb_order where id = #{id}")
    Map findById(Long id);
}
