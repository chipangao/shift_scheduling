package com.example.shift_scheduling.service;

import com.example.shift_scheduling.entity.Employee;
import com.example.shift_scheduling.mapper.EmployeeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    @Cacheable(value = "employee", key = "#id")
    public Employee getEmployeeById(Long id) {
        System.out.println("查資料庫: employee id=" + id);
        return employeeMapper.findById(id);
    }

    public List<Employee> getAllEmployees() {
        return employeeMapper.findAll();
    }

    @CacheEvict(value = "employee", key = "#employee.id")
    public Employee createEmployee(Employee employee) {
        employeeMapper.insert(employee);
        return employee;
    }

    @CacheEvict(value = "employee", key = "#id")
    public Employee updateEmployee(Long id, Employee employee) {
        employee.setId(id);
        employeeMapper.update(employee);
        return employee;
    }

    @CacheEvict(value = "employee", key = "#id")
    public void deleteEmployee(Long id) {
        employeeMapper.deleteById(id);
    }
}
