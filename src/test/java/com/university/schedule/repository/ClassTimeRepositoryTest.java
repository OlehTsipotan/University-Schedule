package com.university.schedule.repository;

import com.university.schedule.model.Building;
import com.university.schedule.model.ClassTime;
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

import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ClassTimeRepositoryTest {

    @Container
    public static PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>("postgres:latest").withDatabaseName("databaseName").withUsername("username")
            .withPassword("password");
    @Autowired
    ClassTimeRepository classTimeRepository;

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

    @Test
    public void save_byClassTimeObject() {

        // Creating ClassTime instance to save
        LocalTime time = LocalTime.now();
        Duration duration = Duration.ofMinutes(150);
        Integer order = 1;

        ClassTime classTimeToSave = new ClassTime(order, time, duration);

        // Saving
        Long savedClassTimeId = classTimeRepository.save(classTimeToSave).getId();

        // Retrieving
        ClassTime retrievedClassTime = entityManager.find(ClassTime.class, savedClassTimeId);

        // Testing
        assertThat(retrievedClassTime).isNotNull();
        assertEquals(retrievedClassTime.getOrderNumber(), order);
        assertEquals(retrievedClassTime.getStartTime(), time);
        assertEquals(retrievedClassTime.getDuration(), duration);
    }

    @Test
    public void findById() {

        // Creating ClassTime instance to save
        LocalTime time = LocalTime.now();
        Duration duration = Duration.ofMinutes(150);
        Integer order = 1;

        ClassTime classTimeToSave = new ClassTime(order, time, duration);

        // Saving
        Long savedClassTimeId = entityManager.persist(classTimeToSave).getId();

        // Retrieving
        ClassTime retrievedClassTime = classTimeRepository.findById(savedClassTimeId).get();

        // Testing
        assertThat(retrievedClassTime).isNotNull();
        assertEquals(retrievedClassTime.getOrderNumber(), order);
        assertEquals(retrievedClassTime.getStartTime(), time);
        assertEquals(retrievedClassTime.getDuration(), duration);
    }

    @Test
    public void findByOrder() {

        // Creating ClassTime instance to save
        LocalTime time = LocalTime.now().truncatedTo(ChronoUnit.SECONDS);
        Duration duration = Duration.ofMinutes(150);
        Integer order = 1;

        ClassTime classTimeToSave = new ClassTime(order, time, duration);
        System.out.println(classTimeToSave);

        // Saving
        Long savedClassTimeId = entityManager.persist(classTimeToSave).getId();

        // Retrieving
        ClassTime retrievedClassTime = classTimeRepository.findByOrderNumber(order).get();

        // Testing
        assertThat(retrievedClassTime).isNotNull();
        assertEquals(retrievedClassTime.getOrderNumber(), order);
        assertEquals(retrievedClassTime.getStartTime(), time);
        assertEquals(retrievedClassTime.getDuration(), duration);
    }

    @ParameterizedTest
    @ValueSource(ints = {10})
    public void findAll(int amount) {

        // Creating ClassTime instance to save
        LocalTime time = LocalTime.now().truncatedTo(ChronoUnit.SECONDS);
        Duration duration = Duration.ofMinutes(150);

        ClassTime classTime;
        List<ClassTime> ownClassTimeList = new ArrayList<>();
        for (int order = 1; order < amount; order++) {
            // Creating instance to save
            classTime = new ClassTime(order, time, duration);
            // Saving
            ownClassTimeList.add(entityManager.persist(classTime));
        }

        // Retrieving
        List<ClassTime> classTimeList = classTimeRepository.findAll();

        // Testing
        assertEquals(classTimeList, ownClassTimeList);
    }


    @Test
    public void deleteById() {
        // Creating ClassTime instance to save
        LocalTime time = LocalTime.now();
        Duration duration = Duration.ofMinutes(150);
        Integer order = 1;

        ClassTime classTimeToSave = new ClassTime(order, time, duration);

        // Saving
        Long savedClassTimeId = entityManager.persist(classTimeToSave).getId();

        // Deleting
        classTimeRepository.deleteById(savedClassTimeId);

        // Testing
        assertNull(entityManager.find(Building.class, savedClassTimeId));
    }


    @BeforeEach
    void clearDatabase(@Autowired Flyway flyway) {
        flyway.clean();
        flyway.migrate();
    }
}


