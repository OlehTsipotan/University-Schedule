package com.university.schedule.repository;

import com.university.schedule.model.Building;
import com.university.schedule.model.ClassType;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ClassTypeRepositoryTest {

    @Container
    public static PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>("postgres:latest").withDatabaseName("databaseName").withUsername("username")
            .withPassword("password");
    @Autowired
    ClassTypeRepository classTypeRepository;

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
    @ValueSource(strings = {"ClassTypeName"})
    public void save_byBuildingObject(String name) {

        // Creating ClassType instance to save
        ClassType classTypeToSave = new ClassType(name);

        // Saving
        Long savedClassTypeId = classTypeRepository.save(classTypeToSave).getId();

        // Retrieving
        ClassType retrievedClassType = entityManager.find(ClassType.class, savedClassTypeId);

        // Testing
        assertThat(retrievedClassType).isNotNull();
        Assertions.assertEquals(retrievedClassType.getName(), name);
    }

    @ParameterizedTest
    @ValueSource(strings = {"ClassTypeName"})
    public void findById(String name) {

        // Creating ClassType instance to save
        ClassType classTypeToSave = new ClassType(name);

        // Saving
        Long savedClassTypeId = entityManager.persist(classTypeToSave).getId();

        // Retrieving
        ClassType retrievedClassType = classTypeRepository.findById(savedClassTypeId).get();

        // Testing
        assertThat(retrievedClassType).isNotNull();
        assertEquals(retrievedClassType.getName(), name);
    }

    @ParameterizedTest
    @ValueSource(strings = {"ClassTypeName"})
    public void findByName(String name) {
        // Creating Building instance to save
        ClassType classTypeToSave = new ClassType(name);

        // Saving
        Long savedClassTypeId = entityManager.persist(classTypeToSave).getId();

        // Retrieving
        ClassType retrievedClassType = classTypeRepository.findByName(name).get();

        // Testing
        assertThat(retrievedClassType).isNotNull();
        assertEquals(retrievedClassType.getId(), savedClassTypeId);
        assertEquals(retrievedClassType.getName(), name);
    }

    @ParameterizedTest
    @ValueSource(ints = {10})
    public void findAll(int amount) {
        ClassType classType;
        List<ClassType> ownClassTypeList = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            // Creating instance to save
            classType = new ClassType("Name" + i);
            // Saving
            ownClassTypeList.add(entityManager.persist(classType));
        }

        // Retrieving
        List<ClassType> classTypeList = classTypeRepository.findAll();

        // Testing
        Assertions.assertEquals(classTypeList, ownClassTypeList);
    }

    @ParameterizedTest
    @ValueSource(strings = {"ClassTypeName"})
    public void deleteById(String name) {
        // Creating Building instance to save
        ClassType classTypeToSave = new ClassType(name);

        // Saving
        Long savedClassTypeId = entityManager.persist(classTypeToSave).getId();

        // Deleting
        classTypeRepository.deleteById(savedClassTypeId);

        // Testing
        assertNull(entityManager.find(Building.class, savedClassTypeId));
    }


    @BeforeEach
    void clearDatabase(@Autowired Flyway flyway) {
        flyway.clean();
        flyway.migrate();
    }
}
