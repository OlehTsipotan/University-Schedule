# University Schedule App

This project is a Java Spring web application for accessing and managing university schedules. It provides functionalities to create, view, and manage schedules for teachers, students and groups.

## Table of Contents
- [Project Description](#project-description)
- [Technology Stack](#technology-stack)
- [How to Install and Run the Project](#how-to-install-and-run-the-project)
- [How to Use the Project](#how-to-use-the-project)
- [Credits](#credits)

## Project Description
The University Schedule App is designed to streamline the scheduling process for universities. It allows users to easily create, manage, and view schedules. The application is built using Java Spring framework, providing a scalable foundation.

### Key features:

Admin & Admin dashboard for managing all entities and partly other users (Students, Teachers)
User authentication and authorization for secure access to schedules.
User registration process.
Search and filter schedules based on different criteria.

## Technology Stack

- **Backend**:
  - [Java](https://www.java.com/) - A general-purpose programming language that is class-based, object-oriented, and designed to have as few implementation dependencies as possible.
  - [Spring Framework](https://spring.io/) - An application framework and inversion of control container for the Java platform.
    - [Spring Boot](https://spring.io/projects/spring-boot) - An extension of the Spring framework that simplifies the process of building production-ready applications.
    - [Spring Web](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html) - Provides key web-related features, including multipart file upload functionality and initialization of the IoC container.
    - [Spring Data JPA](https://spring.io/projects/spring-data-jpa) - Provides a simple and consistent programming model for data access.
    - [Spring Security](https://spring.io/projects/spring-security) - Provides comprehensive security services for Java EE-based enterprise software applications.
    - [Spring MVC](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html) - The web module of the Spring framework that simplifies the work needed to develop web applications.
  - [PostgreSQL](https://www.postgresql.org/) - An open-source relational database system.

- **Testing**:
  - [JUnit 5](https://junit.org/junit5/) - A programming and testing model for Java applications.
  - [Testcontainers](https://www.testcontainers.org/) - Provides throwaway instances of common databases, Selenium web browsers, or anything else that can run in a Docker container.
  - [Flyway](https://flywaydb.org/) - Database migration tool.

- **Frontend**:
  - [Thymeleaf](https://www.thymeleaf.org/) - A Java-based templating engine for server-side rendering of web pages.
  
- **Development Tools**:
  - [Lombok](https://projectlombok.org/) - A Java library that helps to reduce boilerplate code.

- **Containerization and Deployment**:
  - [Docker](https://www.docker.com/) - A platform for developing, shipping, and running applications.



## How to Install and Run the Project
### Prerequisites:

Java 11 or higher installed.
PostreSQL database server set up.

### Installing 
1. Clone the repo

```sh
git clone https://github.com/ShaanCoding/ReadME-Generator.git
```
2. SetUp env. variables - Create a Postgresql database and assign url, username, password to relative env. variable 
- SPRING_DATASOURCE_URL
- SPRING_DATASOURCE_USERNAME 
- SPRING_DATASOURCE_PASSWORD

3. Run
```sh
cd university-schedule-app
```
```sh
./mvnw spring-boot:run
```

4. Have fun!
The application will be accessible at http://localhost:8080.

## How to Use the Project
#### **User**
Register or Login (Student, Teacher) --> http://localhost:8080/user/login,

#### **Admin**
Login as Admin -->  http://localhost:8080/admin/login 
- email: admin@admin.com
- password: adminPassword

<u>Change in properties file and turn on data generation</u>

## Credits
Oleh Tsipotan - developer (https://www.linkedin.com/in/oleh-tsipotan/)
