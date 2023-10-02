package com.university.schedule.repository;

import com.university.schedule.model.Building;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Assertions;
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

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BuildingRepositoryTest {

	@Container
	public static PostgreSQLContainer<?> postgres =
			new PostgreSQLContainer<>("postgres:latest").withDatabaseName("databaseName").withUsername("username")
					.withPassword("password");
	@Autowired
	BuildingRepository buildingRepository;
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
	@CsvSource(value = {"BuildingName:BuildingAddress"}, delimiter = ':')
	public void save_byBuildingObject(String name, String address) {

		// Creating Building instance to save
		Building buildingToSave = new Building(name, address);

		// Saving
		Long savedBuildingId = buildingRepository.save(buildingToSave).getId();

		// Retrieving
		Building retrievedBuilding = entityManager.find(Building.class, savedBuildingId);

		// Testing
		assertThat(retrievedBuilding).isNotNull();
		assertEquals(retrievedBuilding.getName(), name);
		assertEquals(retrievedBuilding.getAddress(), address);
	}

	@ParameterizedTest
	@CsvSource(value = {"BuildingName:BuildingAddress"}, delimiter = ':')
	public void findById(String name, String address) {

		// Creating Building instance to save
		Building buildingToSave = new Building(name, address);

		// Saving
		Long savedBuildingId = entityManager.persist(buildingToSave).getId();

		// Retrieving
		Building retrievedBuilding = buildingRepository.findById(savedBuildingId).get();

		// Testing
		assertThat(retrievedBuilding).isNotNull();
		assertEquals(retrievedBuilding.getName(), name);
		assertEquals(retrievedBuilding.getAddress(), address);
	}

	@ParameterizedTest
	@CsvSource(value = {"BuildingName:BuildingAddress"}, delimiter = ':')
	public void findByName(String name, String address) {
		// Creating Building instance to save
		Building buildingToSave = new Building(name, address);

		// Saving
		Long savedBuildingId = entityManager.persist(buildingToSave).getId();

		// Retrieving
		Building retrievedBuilding = buildingRepository.findByName(name).get();

		// Testing
		assertThat(retrievedBuilding).isNotNull();
		assertEquals(retrievedBuilding.getId(), savedBuildingId);
		assertEquals(retrievedBuilding.getName(), name);
		assertEquals(retrievedBuilding.getAddress(), address);
	}

	@ParameterizedTest
	@CsvSource(value = {"BuildingName:BuildingAddress"}, delimiter = ':')
	public void findByAddress(String name, String address) {
		// Creating Building instance to save
		Building buildingToSave = new Building(name, address);

		// Saving
		Long savedBuildingId = entityManager.persist(buildingToSave).getId();

		// Retrieving
		Building retrievedBuilding = buildingRepository.findByAddress(address).get();

		// Testing
		assertThat(retrievedBuilding).isNotNull();
		assertEquals(retrievedBuilding.getId(), savedBuildingId);
		assertEquals(retrievedBuilding.getName(), name);
		assertEquals(retrievedBuilding.getAddress(), address);
	}

	@ParameterizedTest
	@ValueSource(ints = {10})
	public void findAll(int amount) {
		Building building;
		List<Building> ownBuildingList = new ArrayList<>();
		for (int i = 0; i < amount; i++) {
			// Creating instance to save
			building = new Building("Name" + i, "Address" + i);
			// Saving
			ownBuildingList.add(entityManager.persist(building));
		}

		// Retrieving
		List<Building> buildingList = buildingRepository.findAll();

		// Testing
		assertEquals(buildingList, ownBuildingList);
	}

	@ParameterizedTest
	@CsvSource(value = {"BuildingName:BuildingAddress"}, delimiter = ':')
	public void deleteById(String name, String address) {
		// Creating Building instance to save
		Building buildingToSave = new Building(name, address);

		// Saving
		Long savedBuildingId = entityManager.persist(buildingToSave).getId();

		// Deleting
		buildingRepository.deleteById(savedBuildingId);

		// Testing
		Assertions.assertNull(entityManager.find(Building.class, savedBuildingId));
	}

	@BeforeEach
	void clearDatabase(@Autowired Flyway flyway) {
		flyway.clean();
		flyway.migrate();
	}

}
