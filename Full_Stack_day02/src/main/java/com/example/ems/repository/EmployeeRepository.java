package com.example.ems.repository;

import com.example.ems.model.Employee;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository {
    void save(Employee employee);
    boolean existsById(int id);
    Optional<Employee> findById(int id);
    List<Employee> findAll();
}