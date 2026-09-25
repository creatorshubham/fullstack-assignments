package com.example.ems.exception;

public class InvalidEmployeeException extends EmployeeException {
    public InvalidEmployeeException(String message) {
        super(message);
    }
}