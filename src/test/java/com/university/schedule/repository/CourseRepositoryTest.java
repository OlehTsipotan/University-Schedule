package com.university.schedule.repository;

import com.university.schedule.model.Course;
import com.university.schedule.model.Discipline;
import com.university.schedule.model.Group;
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

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CourseRepositoryTest {

	@Container
	public static PostgreSQLContainer<?> postgres =
			new PostgreSQLContainer<>("postgres:latest").withDatabaseName("databaseName").withUsername("username")
					.withPassword("password");
	@Autowired
	CourseRepository courseRepository;

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
	@ValueSource(strings = "CourseName")
	public void save_byCourseObject(String name) {

		// Creating Course instance to save
		Course courseToSave = new Course(name);

		// Saving
		Long savedCourseId = courseRepository.save(courseToSave).getId();

		// Retrieving
		Course retrievedCourse = entityManager.find(Course.class, savedCourseId);

		// Testing
		assertThat(retrievedCourse).isNotNull();
		assertEquals(retrievedCourse.getName(), name);
	}

	@ParameterizedTest
	@ValueSource(strings = "CourseName")
	public void findById(String name) {

		// Creating Course instance to save
		Course courseToSave = new Course(name);

		// Saving
		Long savedCourseId = entityManager.persist(courseToSave).getId();

		// Retrieving
		Course retrievedCourse = courseRepository.findById(savedCourseId).get();

		// Testing
		assertThat(retrievedCourse).isNotNull();
		assertEquals(retrievedCourse.getName(), name);
	}

	@ParameterizedTest
	@ValueSource(strings = "CourseName")
	public void findByName(String name) {
		// Creating Course instance to save
		Course courseToSave = new Course(name);

		// Saving
		Long savedCourseId = entityManager.persist(courseToSave).getId();

		// Retrieving
		Course retrievedCourse = courseRepository.findByName(name).get();

		// Testing
		assertThat(retrievedCourse).isNotNull();
		assertEquals(retrievedCourse.getId(), savedCourseId);
		assertEquals(retrievedCourse.getName(), name);
	}

	@ParameterizedTest
	@CsvSource(value = {"CourseName:GroupName:Math"}, delimiter = ':')
	public void findByGroupsName(String courseName, String groupName, String disciplineName) {

		Discipline discipline = new Discipline(disciplineName);

		discipline = entityManager.persist(discipline);

		// Creating Course instances to save
		Course course1 = new Course(courseName + 1);
		Course course2 = new Course(courseName + 2);
		Course course3 = new Course(courseName + 3);

		// Saving Course without many-to-many group relation
		course1 = entityManager.persist(course1);
		course2 = entityManager.persist(course2);
		course3 = entityManager.persist(course3);
		// Crating Group instances to save
		Group group1 = new Group(groupName + 1, discipline);
		group1.setCourses(Set.of(course1, course2));
		Group group2 = new Group(groupName + 2, discipline);
		group2.setCourses(Set.of(course3));

		group1 = entityManager.persist(group1);
		group2 = entityManager.persist(group2);

		// Set for testing
		Set<Course> ownCourseSet = Set.of(course1, course2);

		// Retrieving
		Set<Course> retrievedCourses = new HashSet<>(courseRepository.findByGroupsName(groupName + 1));

		// Testing
		assertEquals(retrievedCourses, ownCourseSet);
	}

	@ParameterizedTest
	@ValueSource(ints = {10})
	public void findAll(int amount) {
		Course course;
		Set<Course> ownCourseList = new HashSet<>();
		for (int i = 0; i < amount; i++) {
			// Creating instance to save
			course = new Course("Name" + i);
			// Saving
			ownCourseList.add(entityManager.persist(course));
		}

		// Retrieving
		Set<Course> courseList = new HashSet<>(courseRepository.findAll());

		// Testing
		assertEquals(courseList, ownCourseList);
	}

	@ParameterizedTest
	@CsvSource(value = {"BuildingName:BuildingAddress"}, delimiter = ':')
	public void deleteById(String name) {
		// Creating Course instance to save
		Course courseToSave = new Course(name);

		// Saving
		Long savedCourseId = entityManager.persist(courseToSave).getId();

		// Deleting
		courseRepository.deleteById(savedCourseId);

		// Testing
		Assertions.assertNull(entityManager.find(Course.class, savedCourseId));
	}

	@BeforeEach
	void clearDatabase(@Autowired Flyway flyway) {
		flyway.clean();
		flyway.migrate();
	}
}
