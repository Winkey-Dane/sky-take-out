package com.sky.mapper;

import com.sky.entity.Employee;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    @Select("select * from employee where username = #{username}")  // 简单sql查询可以直接使用注解
    Employee getByUsername(String username);

    @Insert("INSERT INTO employee (username, name, phone, sex, id_number, password, status, create_time, update_time,create_user,update_user) " +
            "VALUES (#{username}, #{name}, #{phone}, #{sex}, #{idNumber}, #{password}, #{status}, #{createTime}, #{updateTime},#{createUser}, #{updateUser})")
    void insert(Employee employee);

    @Select("SELECT * FROM employee WHERE id_number = #{IdNumber}")
    Employee selectByIdNumber(String IdNumber);

    @Select("SELECT * FROM employee WHERE username = #{username}")
    Employee selectByUsername(String username);
}
