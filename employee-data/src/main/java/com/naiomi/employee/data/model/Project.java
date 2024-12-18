package com.naiomi.employee.data.model;

import jakarta.persistence.*;

/**
 * Entity class representing a Project in the database.
 * Each project is assigned to an employee and has a unique name.
 */
@Entity
@Table(name = "projects", indexes = {
        @Index(name = "idx_project_name", columnList = "name"),
        @Index(name = "idx_project_employee_id", columnList = "employee_id")
})
public class Project {

    /**
     * Unique identifier for the project.
     * Auto-generated by the database.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name of the project.
     * Cannot be null.
     */
    @Column(nullable = false)
    private String name;

    /**
     * The employee assigned to the project.
     * Many projects can belong to one employee.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    // Constructors

    /**
     * Default constructor for JPA.
     */
    public Project() {
        // Default constructor
    }

    /**
     * Parameterized constructor to create a project with a specific name and assigned employee.
     *
     * @param name     Name of the project.
     * @param employee Employee assigned to the project.
     */
    public Project(String name, Employee employee) {
        this.name = name;
        this.employee = employee;
    }

    /**
     * Gets the ID of the project.
     *
     * @return ID of the project.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the ID of the project.
     *
     * @param id ID to set.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the name of the project.
     *
     * @return Name of the project.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the project.
     *
     * @param name Name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the employee assigned to the project.
     *
     * @return Employee assigned to the project.
     */
    public Employee getEmployee() {
        return employee;
    }

    /**
     * Sets the employee assigned to the project.
     *
     * @param employee Employee to set.
     */
    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    // Overrides

    /**
     * Returns a string representation of the project.
     * Excludes the employee details to avoid potential lazy loading issues.
     *
     * @return String representation of the project.
     */
    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
