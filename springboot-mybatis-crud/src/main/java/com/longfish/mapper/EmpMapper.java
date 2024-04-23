package com.longfish.mapper;

import com.longfish.pojo.Emp;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface EmpMapper {

    /**
     *
     * 删除id为?的员工
     */
    int delete(Integer ... ids);

    /**
     *
     * 插入数据，已设置主键返回
     */
    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into emp(username, name, gender, image, job, entrydate, dept_id, create_time, update_time) " +
            "values (#{username}, #{name}, #{gender}, #{image}, #{job}, #{entrydate}, #{deptId}, #{createTime}, #{updateTime})")
    int insert(Emp emp);

    /**
     *
     * 更新数据
     */
    int update(Emp emp);


    /**
     *
     * 根据id查询
     */
//    @Results({
//            @Result(column = "dept_id", property = "deptId"),
//            @Result(column = "create_time", property = "createTime"),
//            @Result(column = "update_time", property = "updateTime")
//    })
//    @Select("select * from emp where id = #{id}")
    Emp getByID(Integer id);


    List<Emp> list(String name, Integer gender, LocalDateTime begin, LocalDateTime end);

}
