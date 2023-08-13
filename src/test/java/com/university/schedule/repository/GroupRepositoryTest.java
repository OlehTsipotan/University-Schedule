package com.university.schedule.repository;

import com.university.schedule.model.Group;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class GroupRepositoryTest {

    @Container
    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("databaseName")
            .withUsername("username")
            .withPassword("password");

    @Autowired
    GroupRepository groupRepository;

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

    @ParameterizedTest
    @ValueSource(strings = {"GroupName"})
    public void save_byGroupObject(String name) {

        // Creating Group instance to save
        Group groupToSave = new Group(name);

        // Saving
        Long savedGroupId = groupRepository.save(groupToSave).getId();

        // Retrieving
        Group retrievedGroup = entityManager.find(Group.class, savedGroupId);

        // Testing
        assertThat(retrievedGroup).isNotNull();
        assertEquals(retrievedGroup.getName(), name);
    }

    @ParameterizedTest
    @ValueSource(strings = {"GroupName"})
    public void findById(String name) {

        // Creating Group instance to save
        Group groupToSave = new Group(name);

        // Saving
        Long savedGroupId = entityManager.persist(groupToSave).getId();

        // Retrieving
        Group retrievedGroup = groupRepository.findById(savedGroupId).get();

        // Testing
        assertThat(retrievedGroup).isNotNull();
        assertEquals(retrievedGroup.getName(), name);
    }

    @ParameterizedTest
    @ValueSource(ints = {10})
    public void findAll(int amount) {

        // Creating Group instances to save
        Set<Group> ownGroupSet = new HashSet<>();
        for (int i = 0; i < amount; i++) {
            String groupName = "Sample Group " + (i + 1);
            Group group = new Group(groupName);
            ownGroupSet.add(entityManager.persist(group));
        }

        // Retrieving
        Set<Group> groupSet = new HashSet<>(groupRepository.findAll());

        // Testing
        assertEquals(groupSet, ownGroupSet);
    }

    @ParameterizedTest
    @ValueSource(strings = {"GroupName"})
    public void deleteById(String name) {
        // Creating Group instance to save
        Group groupToSave = new Group(name);

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

