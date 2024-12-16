-- Create roles table
CREATE TABLE roles (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(50) NOT NULL UNIQUE
);

-- Create employees table
CREATE TABLE employees (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           first_name VARCHAR(100) NOT NULL,
                           surname VARCHAR(100) NOT NULL,
                           role_id BIGINT NOT NULL,
                           FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE
);

-- Create projects table
CREATE TABLE projects (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          name VARCHAR(100) NOT NULL,
                          employee_id BIGINT NOT NULL,
                          FOREIGN KEY (employee_id) REFERENCES employees (id) ON DELETE CASCADE
);
