package com.example.shift_scheduling.mapper;

import com.example.shift_scheduling.entity.Employee;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface EmployeeMapper {

    @Select("SELECT * FROM employee WHERE id = #{id}")
    Employee findById(Long id);

    @Select("SELECT * FROM employee ORDER BY id")
    List<Employee> findAll();

    @Insert("INSERT INTO employee(name, email, position) VALUES(#{name}, #{email}, #{position})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Employee employee);

    @Update("UPDATE employee SET name=#{name}, email=#{email}, position=#{position} WHERE id=#{id}")
    int update(Employee employee);

    @Delete("DELETE FROM employee WHERE id = #{id}")
    int deleteById(Long id);
}
