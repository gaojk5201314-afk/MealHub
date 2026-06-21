package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.anno.anno;
import com.sky.entity.Employee;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);
    @Insert("insert into employee (username, name,password, phone, sex, id_number, status,create_time," +
            " update_time, create_user, update_user) values" +
            " (#{username}, #{name}, #{password}, #{phone}, #{sex}, #{idNumber}, #{status}, #{createTime}," +
            " #{updateTime}, #{createUser}, #{updateUser})")
    int insert(Employee employee);


    Page<Employee> FindAll(String name);
    void update(Employee employee);
    void updateById(Employee employee);

    Employee getById(Long id);
}
