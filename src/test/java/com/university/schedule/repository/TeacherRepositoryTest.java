package com.university.schedule.repository;

import com.university.schedule.model.Teacher;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TeacherRepositoryTest {

    @Container
    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("databaseName")
            .withUsername("username")
            .withPassword("password");

    @Autowired
    TeacherRepository teacherRepository;

    @Autowired
    TestEntityManager entityManager;

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        // Postgresql
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        // Flyway
        registry.add("spring.flyway.cleanDisabled", () -> false);
    }

    @BeforeEach
    void clearDatabase(@Autowired Flyway flyway) {
        flyway.clean();
        flyway.migrate();
    }

    @ParameterizedTest
    @CsvSource(value = {"test@example.com:password:FirstName:LastName"}, delimiter = ':')
    public void save() {
        Teacher teacherToSave = new Teacher("test@example.com", "password", "John", "Doe");
        Long savedTeacherId = teacherRepository.save(teacherToSave).getId();

        Teacher retrievedTeacher = entityManager.find(Teacher.class, savedTeacherId);

        assertNotNull(retrievedTeacher);
        assertEquals(teacherToSave.getEmail(), retrievedTeacher.getEmail());
        assertEquals(teacherToSave.getPassword(), retrievedTeacher.getPassword());
        assertEquals(teacherToSave.getFirstName(), retrievedTeacher.getFirstName());
        assertEquals(teacherToSave.getLastName(), retrievedTeacher.getLastName());
    }

    @Test
    public void findAll() {
        Teacher teacher1 = new Teacher("test1@example.com", "password1", "John", "Doe");
        Teacher teacher2 = new Teacher("test2@example.com", "password2", "Jane", "Smith");
        Teacher teacher3 = new Teacher("test3@example.com", "password3", "Alex", "Johnson");

        entityManager.persist(teacher1);
        entityManager.persist(teacher2);
        entityManager.persist(teacher3);

        List<Teacher> allTeachers = teacherRepository.findAll();

        assertEquals(3, allTeachers.size());
        assertTrue(allTeachers.contains(teacher1));
        assertTrue(allTeachers.contains(teacher2));
        assertTrue(allTeachers.contains(teacher3));
    }

    @ParameterizedTest
    @CsvSource(value = {"test@example.com:password:FirstName:LastName"}, delimiter = ':')
    public void findByEmail(String email, String password, String firstName, String lastName) {
        Teacher teacherToSave = new Teacher(email, password, firstName, lastName);
        entityManager.persist(teacherToSave);

        Optional<Teacher> foundTeacher = teacherRepository.findByEmail(email);

        assertTrue(foundTeacher.isPresent());
        assertEquals(teacherToSave, foundTeacher.get());
    }


    @ParameterizedTest
    @CsvSource(value = {"test@example.com:password:FirstName:LastName"}, delimiter = ':')
    public void findById(String email, String password, String firstName, String lastName) {
        Teacher teacherToSave = new Teacher(email, password, firstName, lastName);
        entityManager.persist(teacherToSave);

        Optional<Teacher> foundTeacher = teacherRepository.findById(teacherToSave.getId());

        assertTrue(foundTeacher.isPresent());
        assertEquals(teacherToSave, foundTeacher.get());
    }

    @ParameterizedTest
    @CsvSource(value = {"test@example.com:password:FirstName:LastName"}, delimiter = ':')
    public void deleteById(String email, String password, String firstName, String lastName) {
        Teacher teacherToSave = new Teacher(email, password, firstName, lastName);
        entityManager.persist(teacherToSave);

        teacherRepository.deleteById(teacherToSave.getId());

        Optional<Teacher> deletedTeacher = teacherRepository.findById(teacherToSave.getId());

        assertFalse(deletedTeacher.isPresent());
    }
}

