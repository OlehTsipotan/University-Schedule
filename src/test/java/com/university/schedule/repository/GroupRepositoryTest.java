package com.university.schedule.repository;

import com.university.schedule.model.Discipline;
import com.university.schedule.model.Group;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class GroupRepositoryTest {

    public static PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>("postgres:latest").withDatabaseName("databaseName").withUsername("username")
            .withPassword("password").withReuse(true);
    @Autowired
    GroupRepository groupRepository;
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

    @ParameterizedTest
    @CsvSource(value = {"GroupName:DisciplineName"}, delimiter = ':')
    public void save_byGroupObject(String groupName, String disciplineName) {

        Discipline discipline = new Discipline(disciplineName);

        discipline = entityManager.persist(discipline);

        // Creating Group instance to save
        Group groupToSave = new Group(groupName, discipline);

        // Saving
        Long savedGroupId = groupRepository.save(groupToSave).getId();

        // Retrieving
        Group retrievedGroup = entityManager.find(Group.class, savedGroupId);

        // Testing
        assertThat(retrievedGroup).isNotNull();
        assertEquals(retrievedGroup.getName(), groupName);
    }

    @ParameterizedTest
    @CsvSource(value = {"GroupName:DisciplineName"}, delimiter = ':')
    public void findById(String groupName, String disciplineName) {

        Discipline discipline = new Discipline(disciplineName);

        discipline = entityManager.persist(discipline);

        // Creating Group instance to save
        Group groupToSave = new Group(groupName, discipline);

        // Saving
        Long savedGroupId = entityManager.persist(groupToSave).getId();

        // Retrieving
        Group retrievedGroup = groupRepository.findById(savedGroupId).get();

        // Testing
        assertThat(retrievedGroup).isNotNull();
        assertEquals(retrievedGroup.getName(), groupName);
    }

    @ParameterizedTest
    @ValueSource(ints = {10})
    public void findAll(int amount) {

        Discipline discipline = new Discipline("DisciplineName");

        discipline = entityManager.persist(discipline);

        // Creating Group instances to save
        Set<Group> ownGroupSet = new HashSet<>();
        for (int i = 0; i < amount; i++) {
            String groupName = "Sample Group " + (i + 1);
            Group group = new Group(groupName, discipline);
            ownGroupSet.add(entityManager.persist(group));
        }

        // Retrieving
        Set<Group> groupSet = new HashSet<>(groupRepository.findAll());

        // Testing
        assertEquals(groupSet, ownGroupSet);
    }

    @ParameterizedTest
    @CsvSource(value = {"GroupName:DisciplineName"}, delimiter = ':')
    public void deleteById(String groupName, String disciplineName) {

        Discipline discipline = new Discipline(disciplineName);

        discipline = entityManager.persist(discipline);

        // Creating Group instance to save
        Group groupToSave = new Group(groupName, discipline);

        // Saving
        Long savedGroupId = entityManager.persist(groupToSave).getId();

        // Deleting
        groupRepository.deleteById(savedGroupId);

        // Testing
        assertNull(entityManager.find(Group.class, savedGroupId));
    }

    @BeforeEach
    void clearDatabase(@Autowired Flyway flyway) {
        flyway.clean();
        flyway.migrate();
    }
}

