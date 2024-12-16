package com.naiomi.employee.data.repository;

import com.naiomi.employee.data.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing Employee entities.
 * <p>
 * Provides CRUD operations and query methods for the Employee entity by extending
 * the {@link JpaRepository} interface.
 * </p>
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
