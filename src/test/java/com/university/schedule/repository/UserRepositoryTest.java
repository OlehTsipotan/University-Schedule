package com.university.schedule.repository;

import com.university.schedule.model.User;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    public static PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>("postgres:latest").withDatabaseName("databaseName").withUsername("username")
            .withPassword("password").withReuse(true);
    @Autowired
    UserRepository userRepository;
    @Autowired
    TestEntityManager entityManager;

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

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

    @Test
    public void save() {
        User userToSave = new User("test@example.com", "password", "John", "Doe");
        User savedUser = userRepository.save(userToSave);

        assertNotNull(savedUser.getId());
        assertEquals(userToSave.getEmail(), savedUser.getEmail());
        assertEquals(userToSave.getPassword(), savedUser.getPassword());
        assertEquals(userToSave.getFirstName(), savedUser.getFirstName());
        assertEquals(userToSave.getLastName(), savedUser.getLastName());
    }

    @Test
    public void findAll() {
        User user1 = new User("test1@example.com", "password1", "John", "Doe");
        User user2 = new User("test2@example.com", "password2", "Jane", "Smith");
        User user3 = new User("test3@example.com", "password3", "Alex", "Johnson");

        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.persist(user3);

        List<User> allUsers = userRepository.findAll();

        assertEquals(3, allUsers.size());
        assertTrue(allUsers.contains(user1));
        assertTrue(allUsers.contains(user2));
        assertTrue(allUsers.contains(user3));
    }

    @Test
    public void deleteById() {
        User userToSave = new User("test@example.com", "password", "John", "Doe");
        entityManager.persist(userToSave);

        userRepository.deleteById(userToSave.getId());

        Optional<User> deletedUser = userRepository.findById(userToSave.getId());

        assertFalse(deletedUser.isPresent());
    }

    @ParameterizedTest
    @CsvSource({
        "test1@example.com, password1, John, Doe", "test2@example.com, password2, Jane, Smith",
        "test3@example.com, password3, Alex, Johnson"})
    public void findByEmail(String email, String password, String firstName, String lastName) {
        User userToSave = new User(email, password, firstName, lastName);
        entityManager.persist(userToSave);

        Optional<User> foundUser = userRepository.findByEmail(email);

        assertTrue(foundUser.isPresent());
        assertEquals(userToSave.getEmail(), foundUser.get().getEmail());
        assertEquals(userToSave.getPassword(), foundUser.get().getPassword());
        assertEquals(userToSave.getFirstName(), foundUser.get().getFirstName());
        assertEquals(userToSave.getLastName(), foundUser.get().getLastName());
    }

    @ParameterizedTest
    @CsvSource({
        "test1@example.com, password1, John, Doe", "test2@example.com, password2, Jane, Smith",
        "test3@example.com, password3, Alex, Johnson"})
    public void findById(String email, String password, String firstName, String lastName) {
        User userToSave = new User(email, password, firstName, lastName);
        User savedUser = entityManager.persist(userToSave);

        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        assertTrue(foundUser.isPresent());
        assertEquals(savedUser, foundUser.get());
    }
}

