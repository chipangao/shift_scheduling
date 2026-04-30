package com.example.shift_scheduling.service;

import com.example.shift_scheduling.entity.Employee;
import com.example.shift_scheduling.mapper.EmployeeMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeMapper employeeMapper;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee mockEmployee;

    @BeforeEach
    void setUp() {
        mockEmployee = new Employee();
        mockEmployee.setId(1L);
        mockEmployee.setName("John Doe");
        mockEmployee.setEmail("john@example.com");
        mockEmployee.setPosition("Engineer");
    }

    @Test
    void testGetEmployeeById() {
        when(employeeMapper.findById(1L)).thenReturn(mockEmployee);

        Employee result = employeeService.getEmployeeById(1L);

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        verify(employeeMapper, times(1)).findById(1L);
    }

    @Test
    void testGetAllEmployees() {
        List<Employee> employees = Arrays.asList(mockEmployee);
        when(employeeMapper.findAll()).thenReturn(employees);

        List<Employee> result = employeeService.getAllEmployees();

        assertEquals(1, result.size());
        verify(employeeMapper, times(1)).findAll();
    }

    @Test
    void testCreateEmployee() {
        when(employeeMapper.insert(any(Employee.class))).thenReturn(1);

        Employee result = employeeService.createEmployee(mockEmployee);

        assertNotNull(result);
        verify(employeeMapper, times(1)).insert(mockEmployee);
    }

    @Test
    void testUpdateEmployee() {
        when(employeeMapper.update(any(Employee.class))).thenReturn(1);

        Employee result = employeeService.updateEmployee(1L, mockEmployee);

        assertEquals(1L, result.getId());
        verify(employeeMapper, times(1)).update(mockEmployee);
    }

    @Test
    void testDeleteEmployee() {
        when(employeeMapper.deleteById(1L)).thenReturn(1);

        employeeService.deleteEmployee(1L);

        verify(employeeMapper, times(1)).deleteById(1L);
    }
}
