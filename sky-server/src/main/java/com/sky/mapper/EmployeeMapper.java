package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.EmployeePageQueryDTO;
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
    @Select("select * from employee where username = #{username}")  // 简单sql查询可以直接使用注解
    Employee getByUsername(String username);

    @AutoFill(value = OperationType.INSERT)
    @Insert("INSERT INTO employee (username, name, phone, sex, id_number, password, status, create_time, update_time,create_user,update_user) " +
            "VALUES (#{username}, #{name}, #{phone}, #{sex}, #{idNumber}, #{password}, #{status}, #{createTime}, #{updateTime},#{createUser}, #{updateUser})")
    void insert(Employee employee);

    @Select("SELECT * FROM employee WHERE id_number = #{IdNumber}")
    Employee selectByIdNumber(String IdNumber);

    @Select("SELECT * FROM employee WHERE username = #{username}")
    Employee selectByUsername(String username);

    Page<Employee> pageQuery(EmployeePageQueryDTO employeePageQueryDTO); // 涉及到动态sql，用xml配置

    // @Update("UPDATE employee SET status = #{status} WHERE id = #{id}")
    @AutoFill(value = OperationType.UPDATE)
    void updateById(Employee employee);

    @Select("SELECT * FROM employee WHERE id = #{id}")
    Employee getById(Long id);
}
