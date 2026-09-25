# Employee Management Console Application

A Java 17 console application for managing employee records. The code demonstrates object-oriented design, collection management, exception handling, interfaces, and the Stream API.

## Features

- Add employees with ID, name, department, salary, and active status
- Display all employees
- Search by employee ID
- Filter employees by department
- Filter active employees whose salary is above a given amount
- Handle missing, duplicate, invalid, and malformed employee data
- Keep repository results read-only to protect application state
- Provide clear, text-based prompts that work with keyboard-only and screen-reader workflows

## Project structure

```text
src/main/java/com/example/ems/
├── EmployeeManagementConsole.java
├── model/Employee.java
├── repository/EmployeeRepository.java
├── repository/InMemoryEmployeeRepository.java
├── service/EmployeeService.java
└── exception/
    ├── EmployeeException.java
    ├── EmployeeNotFoundException.java
    ├── DuplicateEmployeeException.java
    └── InvalidEmployeeException.java
```

## Run

Requires JDK 17 or newer and Maven.

```bash
mvn test
mvn exec:java
```

The application starts with four sample employees. Choose option `5` and enter `100000` to see Sam and Priya.

## Design notes

- `EmployeeService` owns business rules and validates data before it reaches the repository.
- `EmployeeRepository` hides storage details, so another persistence implementation can be added without changing the service API.
- `InMemoryEmployeeRepository` preserves insertion order and returns snapshots rather than its internal collection.
- Console prompts use plain text, explicit labels, and predictable menu options; no information is conveyed by color alone.

## Accessibility and input behavior

The console is usable without a mouse or color output. Each value is requested with a visible text label, invalid numeric input receives a corrective message, and table output includes a text header describing every column. Names and departments are trimmed before storage, while IDs and salaries are validated before an employee is created.