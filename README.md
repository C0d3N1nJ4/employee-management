# employee-management system

Employee Management is a multi-module Maven project designed to manage employee data with two applications:

1. employee-api: Exposes REST APIs for managing employees. It handles validations, role-based access control, and data transformations. It connects to employee-data for database operations.
2. employee-data: Handles database interactions using Spring Data JPA with an H2 in-memory database.

Both applications are containerized using Docker and can work together seamlessly to deliver a complete solution.

## Features
- REST API endpoints for CRUD operations on employees.
- Role-based access control using predefined roles (ADMIN, USER, MANAGER).
- Request validation for role and input data.
- Data transformation between employee-api and employee-data.
- Retry mechanism for robust communication between employee-api and employee-data.
- In-memory H2 database with a predefined schema, including employees, roles, and projects.
- Swagger API documentation for easy testing.

## Prerequisites

Before running the project, ensure the following are installed on your system:
- Git
- Java: Version 17
- Maven: Version 3.8.3 or higher
- Docker: Optional for containerized deployment

### Steps to Build and Run
1. 1. Clone the Repository

Clone the project from your GitHub repository:
```bash
git clone git@github.com:C0d3N1nJ4/employee-management.git
```
2. Navigate to the project directory [employee-management] in your terminal or command prompt 
```bash
cd employee-management
```
3. Run the following command to build and install the project
```bash
mvn clean install
```

## employee-api

### About

The employee-api module provides REST endpoints for managing employees, including creating, updating, retrieving, and deleting employees. It connects to employee-data for database operations.

### How to run the application locally
1. Navigate to the employee-api directory in your terminal or command prompt
2. Run the following command to build and install the project
```bash
mvn spring-boot:run
```
3. Verify the application logs to ensure the application is running successfully. 

The application can be accessed at: http://localhost:9090/employees
Swagger api documentation can be accessed at: http://localhost:9090/swagger-ui.html and http://localhost:9090/v3/api-docs

## employee-data

### About

The employee-data module serves as the backend database service. It uses Spring Data JPA to interact with an H2 in-memory database.

### How to run the application locally

1. Navigate to the employee-data directory:
2. Run the following command to build and install the project
```bash
mvn spring-boot:run
```
3. Start the application:
4. Verify the application logs to ensure the H2 database is initialized successfully. The H2 console can be accessed (if enabled) at: http://localhost:9091/h2-console
5. To access the H2 console:

JDBC URL: jdbc:h2:mem:employee-db

Default credentials:
•	Username: sa
•	Password: password

6. You can access the employee-data application at : http://localhost:9091/api/employees

## API Testing
You can test the APIs using tools like Postman or cURL.

### Create Employee

```bash
curl -v -X POST http://localhost:9090/employees \
  -u "user:d1b82d02-3868-4575-8ca6-41abc3e9edd2" \
  -H "Role: ADMIN" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "surname": "Doe"
  }'
```

### Get Employee by ID

```bash
curl -v -X GET http://localhost:9090/employees/1 \
  -u "user:d1b82d02-3868-4575-8ca6-41abc3e9edd2" \
  -H "Role: USER"

```

### Update Employee

```bash
curl -v -X PUT http://employee-api:9090/employees/1 \
  -u "user:d1b82d02-3868-4575-8ca6-41abc3e9edd2" \
  -H "Role: USER" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Jane",
    "surname": "Doe"
  }'

```

### Delete Employee

```bash
curl -v -X DELETE http://localhost:9090/employees/1 \
  -u "user:d1b82d02-3868-4575-8ca6-41abc3e9edd2" \
  -H "Role: ADMIN"

```

### Running with Docker

1. Build Docker Images
```bash
sudo docker network create employee-network

sudo docker build -t employee-api .
sudo docker run -d --name employee-api --network employee-network -p 9090:9090 employee-api
sudo docker start employee-api

sudo docker build -t employee-data .
sudo docker run -d --name employee-data --network employee-network -p 9091:9091 employee-data
sudo docker start employee-data
```

## Access Applications

   employee-api: http://localhost:9090
   employee-data: http://localhost:9091

### Retry Mechanism
Retries: App1 retries up to 3 times if App2 is unavailable.

Scenario Testing:
1. Stop App2.
2. Send a request to App1.
3. Observe retry logs.
4. Restart App2 before the 3rd retry to confirm successful recovery.