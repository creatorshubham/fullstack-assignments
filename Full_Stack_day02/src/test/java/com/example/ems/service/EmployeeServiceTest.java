package com.example.ems.service;

import com.example.ems.exception.DuplicateEmployeeException;
import com.example.ems.exception.EmployeeNotFoundException;
import com.example.ems.exception.InvalidEmployeeException;
import com.example.ems.repository.InMemoryEmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EmployeeServiceTest {

    private EmployeeService service;

    @BeforeEach
    void setUp() {
        service = new EmployeeService(new InMemoryEmployeeRepository());
        service.addEmployee(101, "Alex", "Engineering", 90000, true);
        service.addEmployee(102, "Sam", "Engineering", 125000, true);
        service.addEmployee(103, "John", "Finance", 140000, false);
        service.addEmployee(104, "Priya", "Engineering", 150000, true);
    }

    @Test
    void displaysAllEmployees() {
        assertEquals(4, service.getAllEmployees().size());
    }

    @Test
    void findsEmployeeById() {
        assertEquals("Sam", service.getEmployeeById(102).getName());
    }

    @Test
    void filtersDepartmentCaseInsensitively() {
        assertEquals(3, service.getEmployeesByDepartment("engineering").size());
    }

    @Test
    void filtersActiveEmployeesAboveSalaryWithStreams() {
        List<String> names = service.getActiveEmployeesAboveSalary(100000).stream()
                .map(employee -> employee.getName())
                .toList();
        assertEquals(List.of("Priya", "Sam"), names);
    }

    @Test
    void missingEmployeeThrowsCustomException() {
        assertThrows(EmployeeNotFoundException.class, () -> service.getEmployeeById(999));
    }

    @Test
    void duplicateAndInvalidEmployeesAreRejected() {
        assertThrows(DuplicateEmployeeException.class,
                () -> service.addEmployee(101, "Another", "HR", 50000, true));
        assertThrows(InvalidEmployeeException.class,
                () -> service.addEmployee(105, " ", "HR", 50000, true));
        assertThrows(InvalidEmployeeException.class,
                () -> service.addEmployee(106, "Taylor", "HR", -1, true));
    }

    @Test
    void trimsTextFieldsBeforeStoringEmployee() {
        Employee employee = service.addEmployee(105, "  Taylor  ", "  HR  ", 50000, true);

        assertEquals("Taylor", employee.getName());
        assertEquals("HR", employee.getDepartment());
    }

    @Test
    void rejectsNonFiniteSalaryThresholds() {
        assertThrows(InvalidEmployeeException.class,
                () -> service.getActiveEmployeesAboveSalary(Double.NaN));
        assertThrows(InvalidEmployeeException.class,
                () -> service.getActiveEmployeesAboveSalary(Double.POSITIVE_INFINITY));
    }
}