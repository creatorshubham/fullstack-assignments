package com.example.ems.repository;

import com.example.ems.model.Employee;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryEmployeeRepository implements EmployeeRepository {

    private final Map<Integer, Employee> employees = new LinkedHashMap<>();

    @Override
    public void save(Employee employee) {
        employees.put(employee.getId(), employee);
    }

    @Override
    public boolean existsById(int id) {
        return employees.containsKey(id);
    }

    @Override
    public Optional<Employee> findById(int id) {
        return Optional.ofNullable(employees.get(id));
    }

    @Override
    public List<Employee> findAll() {
        return List.copyOf(employees.values());
    }
}