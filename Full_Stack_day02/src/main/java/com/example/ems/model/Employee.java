package com.example.ems.model;

public class Employee {

    private final int id;
    private String name;
    private String department;
    private double salary;
    private boolean active;

    public Employee(int id, String name, String department, double salary, boolean active) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.salary = salary;
        this.active = active;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getDepartment() { return department; }
    public double getSalary() { return salary; }
    public boolean isActive() { return active; }

    public void setName(String name) { this.name = name; }
    public void setDepartment(String department) { this.department = department; }
    public void setSalary(double salary) { this.salary = salary; }
    public void setActive(boolean active) { this.active = active; }

    @Override
    public String toString() {
        return String.format("%d | %-6s | %-12s | %,.0f | %s",
                id, name, department, salary, active ? "Active" : "Inactive");
    }
}