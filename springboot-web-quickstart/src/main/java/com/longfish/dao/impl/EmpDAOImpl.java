package com.longfish.dao.impl;

import com.longfish.dao.EmpDAO;
import com.longfish.pojo2.Emp;
import com.longfish.util.XmlParserUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class EmpDAOImpl implements EmpDAO {


    @Override
    public List<Emp> listEmp() {
        String file = Objects.requireNonNull(this.getClass().getClassLoader().getResource("emp.xml")).getFile();

        return XmlParserUtils.parse(file, Emp.class);
    }
}
