package com.example.ems;

import com.example.ems.exception.EmployeeException;
import com.example.ems.model.Employee;
import com.example.ems.repository.InMemoryEmployeeRepository;
import com.example.ems.service.EmployeeService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class EmployeeManagementConsole {

    private static final String TABLE_HEADER = "ID     | Name   | Department   | Salary | Status";
    private final EmployeeService service = new EmployeeService(new InMemoryEmployeeRepository());

    public static void main(String[] args) {
        new EmployeeManagementConsole().run();
    }

    private void run() {
        loadSampleEmployees();
        try (Scanner scanner = new Scanner(System.in)) {
            boolean running = true;
            while (running) {
                printMenu();
                String choice;
                try {
                    choice = scanner.nextLine().trim();
                } catch (NoSuchElementException exception) {
                    break;
                }
                try {
                    switch (choice) {
                        case "1" -> addEmployee(scanner);
                        case "2" -> printEmployees(service.getAllEmployees());
                        case "3" -> findEmployee(scanner);
                        case "4" -> printByDepartment(scanner);
                        case "5" -> printAboveSalary(scanner);
                        case "0" -> running = false;
                        default -> System.out.println("Please choose a valid menu option.");
                    }
                } catch (EmployeeException | IllegalArgumentException exception) {
                    System.out.println("Error: " + exception.getMessage());
                }
            }
        }
        System.out.println("Goodbye.");
    }

    private void loadSampleEmployees() {
        service.addEmployee(101, "Alex", "Engineering", 90000, true);
        service.addEmployee(102, "Sam", "Engineering", 125000, true);
        service.addEmployee(103, "John", "Finance", 140000, false);
        service.addEmployee(104, "Priya", "Engineering", 150000, true);
    }

    private void printMenu() {
        System.out.println("\n=== Employee Management Console ===");
        System.out.println("1. Add employee");
        System.out.println("2. Display all employees");
        System.out.println("3. Search by employee ID");
        System.out.println("4. Display employees by department");
        System.out.println("5. Display active employees above salary");
        System.out.println("0. Exit");
        System.out.print("Choose an option: ");
    }

    private void addEmployee(Scanner scanner) {
        int id = readInt(scanner, "Employee ID: ");
        System.out.print("Name: ");
        String name = scanner.nextLine();
        System.out.print("Department: ");
        String department = scanner.nextLine();
        double salary = readDouble(scanner, "Salary: ");
        System.out.print("Is the employee active? (y/n): ");
        boolean active = scanner.nextLine().trim().equalsIgnoreCase("y");
        System.out.println("Added: " + service.addEmployee(id, name, department, salary, active));
    }

    private void findEmployee(Scanner scanner) {
        int id = readInt(scanner, "Employee ID: ");
        System.out.println(service.getEmployeeById(id));
    }

    private void printByDepartment(Scanner scanner) {
        System.out.print("Department: ");
        List<Employee> employees = service.getEmployeesByDepartment(scanner.nextLine());
        printEmployeesOrMessage(employees, "No employees found in that department.");
    }

    private void printAboveSalary(Scanner scanner) {
        double minimumSalary = readDouble(scanner, "Minimum salary: ");
        List<Employee> employees = service.getActiveEmployeesAboveSalary(minimumSalary);
        printEmployeesOrMessage(employees, "No active employees earn more than that amount.");
    }

    private void printEmployeesOrMessage(List<Employee> employees, String emptyMessage) {
        if (employees.isEmpty()) {
            System.out.println(emptyMessage);
        } else {
            printEmployees(employees);
        }
    }

    private void printEmployees(List<Employee> employees) {
        System.out.println(TABLE_HEADER);
        employees.forEach(System.out::println);
    }

    private int readInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException exception) {
                System.out.println("Please enter a whole number.");
            }
        }
    }

    private double readDouble(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                double value = Double.parseDouble(scanner.nextLine().trim());
                if (!Double.isFinite(value)) {
                    throw new NumberFormatException();
                }
                return value;
            } catch (NumberFormatException exception) {
                System.out.println("Please enter a finite number.");
            }
        }
    }
}