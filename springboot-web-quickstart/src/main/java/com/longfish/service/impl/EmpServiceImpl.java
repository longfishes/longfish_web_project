package com.longfish.service.impl;

import com.longfish.dao.EmpDAO;
import com.longfish.pojo2.Emp;
import com.longfish.service.EmpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EmpServiceImpl implements EmpService {
    @Autowired
    private EmpDAO empDAO;

    @Override
    public List<Emp> listEmp() {
        List<Emp> empList = empDAO.listEmp();

        empList.forEach(emp -> {
            String gender = emp.getGender();
            if ("1".equals(gender))
                emp.setGender("男");
            else
                emp.setGender("女");
        });

        empList.forEach(emp -> {
            String job = emp.getJob();
            if ("1".equals(job))
                emp.setJob("讲师");
            else if ("2".equals(job))
                emp.setJob("班主任");
            else
                emp.setJob("就业指导");
        });

        return empList;
    }
}
