package com.naiomi.employee.data.model;

import com.naiomi.employee.data.constant.RoleType;
import jakarta.persistence.*;

/**
 * Entity class representing a Role in the database.
 * A role can have multiple employees assigned to it.
 */
@Entity
@Table(name = "roles", indexes = {
        @Index(name = "idx_role_name", columnList = "name")
})
public class Role {

    /**
     * Unique identifier for the role.
     * Auto-generated by the database.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name of the role.
     * Enumerated as a string value for better readability and consistency.
     * The column is unique and cannot be null.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false, unique = true)
    private RoleType name;

    /**
     * Default constructor for JPA.
     */
    public Role() {
    }

    /**
     * Constructor to initialize a role with a specific name.
     *
     * @param name The name of the role.
     */
    public Role(RoleType name) {
        this.name = name;
    }

    /**
     * Gets the ID of the role.
     *
     * @return The role ID.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the ID of the role.
     *
     * @param id The ID to set.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the name of the role.
     *
     * @return The role name.
     */
    public RoleType getName() {
        return name;
    }

    /**
     * Sets the name of the role.
     *
     * @param name The role name to set.
     */
    public void setName(RoleType name) {
        this.name = name;
    }

    /**
     * Provides a string representation of the role entity.
     * Excludes employees to avoid potential issues with lazy loading.
     *
     * @return A string representation of the role.
     */
    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", name=" + name +
                '}';
    }
}
