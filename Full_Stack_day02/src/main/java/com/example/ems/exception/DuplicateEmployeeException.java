package com.example.ems.exception;

public class DuplicateEmployeeException extends EmployeeException {
    public DuplicateEmployeeException(int id) {
        super("An employee with ID " + id + " already exists.");
    }
}