package com.example.ems.service;

import com.example.ems.exception.DuplicateEmployeeException;
import com.example.ems.exception.EmployeeNotFoundException;
import com.example.ems.exception.InvalidEmployeeException;
import com.example.ems.model.Employee;
import com.example.ems.repository.EmployeeRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class EmployeeService {

    private final EmployeeRepository repository;

    public EmployeeService(EmployeeRepository repository) {
        this.repository = repository;
    }

    public Employee addEmployee(int id, String name, String department, double salary, boolean active) {
        if (id <= 0) {
            throw new InvalidEmployeeException("Employee ID must be positive.");
        }
        if (name == null || name.isBlank()) {
            throw new InvalidEmployeeException("Name is required.");
        }
        if (department == null || department.isBlank()) {
            throw new InvalidEmployeeException("Department is required.");
        }
        if (!Double.isFinite(salary) || salary < 0) {
            throw new InvalidEmployeeException("Salary must be a non-negative number.");
        }
        if (repository.existsById(id)) {
            throw new DuplicateEmployeeException(id);
        }

        Employee employee = new Employee(id, name.trim(), department.trim(), salary, active);
        repository.save(employee);
        return employee;
    }

    public List<Employee> getAllEmployees() {
        return List.copyOf(repository.findAll());
    }

    public Employee getEmployeeById(int id) {
        return repository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
    }

    public List<Employee> getEmployeesByDepartment(String department) {
        String requestedDepartment = normalizeDepartment(department);
        return repository.findAll().stream()
                .filter(employee -> employee.getDepartment().equalsIgnoreCase(requestedDepartment))
                .toList();
    }

    public List<Employee> getActiveEmployeesAboveSalary(double minimumSalary) {
        if (!Double.isFinite(minimumSalary)) {
            throw new InvalidEmployeeException("Minimum salary must be a finite number.");
        }
        return repository.findAll().stream()
                .filter(Employee::isActive)
                .filter(employee -> employee.getSalary() > minimumSalary)
                .sorted(Comparator.comparingDouble(Employee::getSalary).reversed())
                .toList();
    }

    public Set<String> getDepartments() {
        return repository.findAll().stream()
                .map(Employee::getDepartment)
                .collect(Collectors.toCollection(TreeSet::new));
    }

    private String normalizeDepartment(String department) {
        return department == null ? "" : department.trim();
    }
}