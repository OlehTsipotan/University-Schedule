package com.university.schedule.repository;

import com.university.schedule.model.Discipline;
import com.university.schedule.model.Role;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RoleRepositoryTest {

    private static final String DATABASE_NAME = "databaseName";
    private static final String DATABASE_USERNAME = "databaseName";
    private static final String DATABASE_USER_PASSWORD = "databaseName";
    public static PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>("postgres:latest").withDatabaseName(DATABASE_NAME).withUsername(DATABASE_USERNAME)
            .withPassword(DATABASE_USER_PASSWORD).withReuse(true);
    @Autowired
    RoleRepository roleRepository;
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


    @ParameterizedTest
    @CsvSource(value = {"ROLE_ADMIN"})
    public void save_success(String name) {
        Role role = new Role();
        role.setName(name);
        roleRepository.save(role);
        assertEquals(role, roleRepository.findByName(name).get());
    }

    @ParameterizedTest
    @CsvSource(value = {"ROLE_ADMIN"})
    public void findById_success(String name) {
        Role roleToSave = new Role(name);
        entityManager.persist(roleToSave);

        Role roleFromDb = roleRepository.findById(roleToSave.getId()).get();
        assertEquals(roleToSave, roleFromDb);
    }

    @ParameterizedTest
    @CsvSource(value = {"ROLE_ADMIN"})
    public void findByName_success(String name) {
        Role roleToSave = new Role(name);
        entityManager.persist(roleToSave);

        Role roleFromDb = roleRepository.findByName(name).get();
        assertEquals(roleToSave, roleFromDb);
    }

    @ParameterizedTest
    @CsvSource(value = {"ROLE_ADMIN"})
    public void delete_success(String name) {
        Role roleToSave = new Role(name);
        entityManager.persist(roleToSave);

        roleRepository.delete(roleToSave);

        assertNull(entityManager.find(Discipline.class, roleToSave.getId()));
    }
}
