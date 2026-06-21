package com.sky.service;

import com.sky.anno.anno;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.enumeration.OperationType;
import com.sky.result.PageResult;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    void add(Employee employee);

    PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    void updateStatus(Employee employee);

    void update(Employee emp);

    Employee getById(Long id);
}