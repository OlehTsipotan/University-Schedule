package com.university.schedule.repository;

import com.university.schedule.model.Discipline;
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
public class DisciplineRepositoryTest {

    private static final String DATABASE_NAME = "databaseName";
    private static final String DATABASE_USERNAME = "databaseName";
    private static final String DATABASE_USER_PASSWORD = "databaseName";
    public static PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>("postgres:latest").withDatabaseName(DATABASE_NAME).withUsername(DATABASE_USERNAME)
            .withPassword(DATABASE_USER_PASSWORD).withReuse(true);
    @Autowired
    DisciplineRepository disciplineRepository;
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
    @CsvSource(value = {"DisciplineName"}, delimiter = ':')
    public void save_success(String name) {
        Discipline disciplineToSave = new Discipline(name);
        disciplineRepository.save(disciplineToSave);

        Discipline disciplineFromDb = entityManager.find(Discipline.class, disciplineToSave.getId());
        assertEquals(disciplineToSave, disciplineFromDb);
    }

    @ParameterizedTest
    @CsvSource(value = {"DisciplineName"}, delimiter = ':')
    public void findById_success(String name) {
        Discipline disciplineToSave = new Discipline(name);
        entityManager.persist(disciplineToSave);

        Discipline disciplineFromDb = disciplineRepository.findById(disciplineToSave.getId()).get();
        assertEquals(disciplineToSave, disciplineFromDb);
    }

    @ParameterizedTest
    @CsvSource(value = {"DisciplineName"}, delimiter = ':')
    public void findByName_success(String name) {
        Discipline disciplineToSave = new Discipline(name);
        entityManager.persist(disciplineToSave);

        Discipline disciplineFromDb = disciplineRepository.findByName(name).get();
        assertEquals(disciplineToSave, disciplineFromDb);
    }


    @ParameterizedTest
    @CsvSource(value = {"DisciplineName"}, delimiter = ':')
    public void delete_success(String name) {
        Discipline disciplineToSave = new Discipline(name);
        entityManager.persist(disciplineToSave);

        disciplineRepository.delete(disciplineToSave);

        assertNull(entityManager.find(Discipline.class, disciplineToSave.getId()));
    }

}
