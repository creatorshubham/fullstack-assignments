package com.example.ems.exception;

public class EmployeeNotFoundException extends EmployeeException {
    public EmployeeNotFoundException(int id) {
        super("No employee found with ID " + id + ".");
    }
}