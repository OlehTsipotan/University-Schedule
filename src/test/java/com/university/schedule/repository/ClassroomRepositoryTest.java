package com.university.schedule.repository;

import com.university.schedule.model.Building;
import com.university.schedule.model.Classroom;
import org.flywaydb.core.Flyway;
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
public class ClassroomRepositoryTest {

    @Container
    public static PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>("postgres:latest").withDatabaseName("databaseName").withUsername("username")
            .withPassword("password");

    @Autowired
    ClassroomRepository classroomRepository;
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
    @CsvSource(value = {"ClassroomName:BuildingName:BuildingAddress"}, delimiter = ':')
    public void save_byClassroomObject(String classroomName, String buildingName, String buildingAddress) {

        // Creating Building instance to save
        Building building = new Building(buildingName, buildingAddress);

        // Saving
        building = entityManager.persist(building);

        // Creating Classroom instance to save
        Classroom classroomToSave = new Classroom(classroomName, building);

        // Saving
        Long savedClassroomId = classroomRepository.save(classroomToSave).getId();

        // Retrieving
        Classroom retrievedClassroom = entityManager.find(Classroom.class, savedClassroomId);

        // Testing
        assertThat(retrievedClassroom).isNotNull();
        assertEquals(retrievedClassroom.getName(), classroomName);
    }

    @ParameterizedTest
    @CsvSource(value = {"ClassroomName:BuildingName:BuildingAddress"}, delimiter = ':')
    public void findById(String classroomName, String buildingName, String buildingAddress) {

        // Creating Building instance to save
        Building building = new Building(buildingName, buildingAddress);

        // Saving
        building = entityManager.persist(building);

        // Creating Classroom instance to save
        Classroom classroomToSave = new Classroom(classroomName, building);

        // Saving
        Long savedClassroomId = entityManager.persist(classroomToSave).getId();

        // Retrieving
        Classroom retrievedClassroom = classroomRepository.findById(savedClassroomId).get();

        // Testing
        assertThat(retrievedClassroom).isNotNull();
        assertEquals(retrievedClassroom.getName(), classroomName);
    }

    @ParameterizedTest
    @CsvSource(value = {"BuildingName:BuildingAddress:ClassroomName"}, delimiter = ':')
    public void findByBuilding(String buildingName, String buildingAddress, String classroomName) {
        // Creating Building instances to save
        Building building1 = new Building(buildingName + 1, buildingAddress + 1);
        Building building2 = new Building(buildingName + 2, buildingAddress + 2);

        // Saving buildings
        building1 = entityManager.persist(building1);
        building2 = entityManager.persist(building2);

        // Creating classroom instances to save
        Classroom classroom1 = new Classroom(classroomName + 1, building1);
        Classroom classroom2 = new Classroom(classroomName + 2, building2);
        Classroom classroom3 = new Classroom(classroomName + 3, building1);

        // Saving classrooms
        classroom1 = entityManager.persist(classroom1);
        classroom2 = entityManager.persist(classroom2);
        classroom3 = entityManager.persist(classroom3);

        // Preparing data for testing
        List<Classroom> classrooms = new ArrayList<>();
        classrooms.add(classroom1);
        classrooms.add(classroom3);

        // Retrieving
        List<Classroom> retrievedClassroomList = classroomRepository.findByBuilding(building1);

        // Testing
        assertEquals(retrievedClassroomList, classrooms);
    }

    @ParameterizedTest
    @ValueSource(ints = {10})
    public void findAll(int amount) {
        Classroom classroom;
        List<Classroom> ownClassroomList = new ArrayList<>();

        // Creating Building instance to save
        String buildingName = "BuildingName";
        String buildingAddress = "BuildingAddress";
        Building building = new Building(buildingName, buildingAddress);

        // Saving
        building = entityManager.persist(building);

        for (int i = 0; i < amount; i++) {
            // Creating instance to save
            classroom = new Classroom("Name" + i, building);
            // Saving
            ownClassroomList.add(entityManager.persist(classroom));
        }

        // Retrieving
        List<Classroom> classroomList = classroomRepository.findAll();

        // Testing
        assertEquals(classroomList, ownClassroomList);
    }

    @ParameterizedTest
    @CsvSource(value = {"ClassroomName:BuildingName:BuildingAddress"}, delimiter = ':')
    public void deleteById(String classroomName, String buildingName, String buildingAddress) {

        // Creating Building instance to save
        Building building = new Building(buildingName, buildingAddress);

        // Saving
        building = entityManager.persist(building);

        // Creating Classroom instance to save
        Classroom classroomToSave = new Classroom(classroomName, building);

        // Saving
        Long savedClassroomId = entityManager.persist(classroomToSave).getId();

        // Deleting
        classroomRepository.deleteById(savedClassroomId);

        // Testing
        assertNull(entityManager.find(Classroom.class, savedClassroomId));
    }


    @BeforeEach
    void clearDatabase(@Autowired Flyway flyway) {
        flyway.clean();
        flyway.migrate();
    }
}

