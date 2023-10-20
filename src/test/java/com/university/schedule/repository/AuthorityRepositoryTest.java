package com.university.schedule.repository;


import com.university.schedule.model.Authority;
import com.university.schedule.model.Discipline;
import com.university.schedule.model.Role;
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

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AuthorityRepositoryTest {

    private static String DATABASE_NAME = "databaseName";
    private static String DATABASE_USERNAME = "databaseName";
    private static String DATABASE_USER_PASSWORD = "databaseName";

    @Container
    public static PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>("postgres:latest").withDatabaseName(DATABASE_NAME).withUsername(DATABASE_USERNAME)
            .withPassword(DATABASE_USER_PASSWORD);

    @Autowired
    AuthorityRepository authorityRepository;
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
    @CsvSource(value = {"AuthorityName"}, delimiter = ':')
    public void save_withNoRoleSet_success(String name) {
        Authority authorityToSave = new Authority(name);
        authorityRepository.save(authorityToSave);

        Authority authorityFromDb = entityManager.find(Authority.class, authorityToSave.getId());
        assertEquals(authorityToSave, authorityFromDb);
    }

    @ParameterizedTest
    @CsvSource(value = {"AuthorityName:RoleName"}, delimiter = ':')
    public void save_withRoleSet_success(String authorityName, String roleName) {
        Role role = new Role(roleName);
        entityManager.persist(role);

        Authority authorityToSave = new Authority(authorityName, Set.of(role));
        authorityRepository.save(authorityToSave);

        Authority authorityFromDb = entityManager.find(Authority.class, authorityToSave.getId());
        assertEquals(authorityToSave, authorityFromDb);
    }

    @ParameterizedTest
    @CsvSource(value = {"AuthorityName"}, delimiter = ':')
    public void findById_success(String name) {
        Authority authorityToSave = new Authority(name);
        entityManager.persist(authorityToSave);

        Authority authorityFromDb = authorityRepository.findById(authorityToSave.getId()).get();
        assertEquals(authorityToSave, authorityFromDb);
    }

    @ParameterizedTest
    @CsvSource(value = {"AuthorityName"}, delimiter = ':')
    public void findByName_success(String authorityName) {
        Authority authorityToSave = new Authority(authorityName);
        entityManager.persist(authorityToSave);

        Authority authorityFromDb = authorityRepository.findByName(authorityName).get();
        assertEquals(authorityToSave, authorityFromDb);
    }

    @Test
    public void findByRoles_success() {
        Role properRole = new Role("ProperRole");
        Role wrongRole = new Role("WrongRole");

        entityManager.persist(properRole);
        entityManager.persist(wrongRole);

        Authority authorityWithProperRoleToSave1 = new Authority("AuthorityName1", Set.of(properRole));
        Authority authorityWithProperRoleToSave2 = new Authority("AuthorityName2", Set.of(properRole));
        Authority authorityWithWrongRoleToSave = new Authority("AuthorityName3", Set.of(wrongRole));
        Authority authorityWithNoRoleToSave = new Authority("AuthorityName4");

        entityManager.persist(authorityWithProperRoleToSave1);
        entityManager.persist(authorityWithProperRoleToSave2);
        entityManager.persist(authorityWithWrongRoleToSave);
        entityManager.persist(authorityWithNoRoleToSave);

        assertEquals(Set.of(authorityWithProperRoleToSave1, authorityWithProperRoleToSave2),
            new HashSet<>(authorityRepository.findByRoles(properRole)));
    }

    @ParameterizedTest
    @CsvSource(value = {"AuthorityName"}, delimiter = ':')
    public void delete_success(String name) {
        Authority authorityToSave = new Authority(name);
        entityManager.persist(authorityToSave);

        authorityRepository.delete(authorityToSave);

        assertNull(entityManager.find(Discipline.class, authorityToSave.getId()));
    }


}
