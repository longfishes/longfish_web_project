package com.longfish;

import com.longfish.mapper.EmpMapper;
import com.longfish.pojo.Emp;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;

@SpringBootTest
class SpringbootMybatisCrudApplicationTests {

    @Autowired
    private EmpMapper empMapper;

    @Test
    public void testDelete(){
        System.out.println(empMapper.delete(14, 15, 16));
    }

    @Test
    public void testInsert(){
        Emp emp = new Emp();
        emp.setUsername("Tom3");
        emp.setName("汤姆3");
        emp.setImage("1.jpg");
        emp.setGender((short)1);
        emp.setJob((short)1);
        emp.setEntrydate(LocalDate.of(2000,1,1));
        emp.setCreateTime(LocalDateTime.now());
        emp.setUpdateTime(LocalDateTime.now());
        emp.setDeptId(1);
        System.out.println(empMapper.insert(emp));
    }

    @Test
    public void testUpdate() {
        Emp emp = new Emp();
        emp.setId(18);
        emp.setUsername("Tom11111");
        emp.setName("汤姆11111");
//        emp.setImage("1.jpg");
//        emp.setGender((short) 1);
//        emp.setJob((short) 1);
//        emp.setEntrydate(LocalDate.of(2000, 1, 1));
        emp.setUpdateTime(LocalDateTime.now());
        emp.setDeptId(1);

        System.out.println(empMapper.update(emp));
    }

    @Test
    public void testGetByID(){
        System.out.println(empMapper.getByID(18));
    }

    @Test
    public void testList(){
//        empMapper.list("张", null, null, null).forEach(System.out::println);
        empMapper.list(null, 2, null, null).forEach(System.out::println);
    }

}
