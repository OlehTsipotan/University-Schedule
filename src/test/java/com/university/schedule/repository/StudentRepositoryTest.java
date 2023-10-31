package com.university.schedule.repository;

import com.university.schedule.model.Discipline;
import com.university.schedule.model.Group;
import com.university.schedule.model.Student;
import org.junit.jupiter.api.BeforeAll;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class StudentRepositoryTest {

    public static PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>("postgres:latest").withDatabaseName("databaseName").withUsername("username")
            .withPassword("password").withReuse(true);
    @Autowired
    StudentRepository studentRepository;
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
    @CsvSource({
        "test1@example.com, password1, John, Doe, GroupA, Math",
        "test2@example.com, password2, Jane, Smith, GroupB, Math",
        "test3@example.com, password3, Alex, Johnson, GroupC, Math"})
    public void findByEmailAndPassword(String email, String password, String firstName, String lastName,
                                       String groupName, String disciplineName) {

        Discipline discipline = new Discipline(disciplineName);

        discipline = entityManager.persist(discipline);

        // Creating Group instance to save
        Group groupToSave = new Group(groupName, discipline);

        // Saving Group
        entityManager.persist(groupToSave);

        Student studentToSave = new Student(email, password, firstName, lastName);
        studentToSave.setGroup(groupToSave);

        // Saving Student
        Long savedStudentId = entityManager.persist(studentToSave).getId();

        // Retrieving
        Optional<Student> retrievedStudent = studentRepository.findByEmailAndPassword(email, password);

        // Testing
        assertTrue(retrievedStudent.isPresent());
        assertEquals(retrievedStudent.get().getId(), savedStudentId);
        assertEquals(retrievedStudent.get().getFirstName(), firstName);
        assertEquals(retrievedStudent.get().getLastName(), lastName);
    }

    @ParameterizedTest
    @CsvSource({
        "GroupA, test1@example.com, password1, John, Doe, Math",
        "GroupB, test2@example.com, password2, Jane, Smith, Math",
        "GroupC, test3@example.com, password3, Alex, Johnson, Math"})
    public void findByGroupsName(String groupName, String email, String password, String firstName, String lastName,
                                 String disciplineName) {

        Discipline discipline = new Discipline(disciplineName);

        discipline = entityManager.persist(discipline);

        // Creating Group instance to save
        Group groupToSave = new Group(groupName, discipline);

        // Saving Group
        entityManager.persist(groupToSave);

        Student studentToSave = new Student(email, password, firstName, lastName);
        studentToSave.setGroup(groupToSave);

        // Saving Student
        entityManager.persist(studentToSave);

        // Retrieving Students by Group Name
        List<Student> studentsInGroup = studentRepository.findByGroupsName(groupName);

        // Testing
        assertFalse(studentsInGroup.isEmpty());
        assertEquals(studentsInGroup.get(0).getFirstName(), firstName);
        assertEquals(studentsInGroup.get(0).getLastName(), lastName);
        assertEquals(studentsInGroup.get(0).getGroup().getName(), groupName);
    }

    @ParameterizedTest
    @CsvSource({
        "GroupA, 3, Math", "GroupB, 2, Math", "GroupC, 1, Math"})
    public void findAllByGroupsName(String groupName, int expectedSize, String disciplineName) {
        Discipline discipline = new Discipline(disciplineName);

        discipline = entityManager.persist(discipline);


        // Creating Group instance to save
        Group group = new Group(groupName, discipline);
        group = entityManager.persist(group);

        // Creating Student instances to save
        List<Student> studentsToSave = new ArrayList<>();
        for (int i = 0; i < expectedSize; i++) {
            String email = "test" + (i + 1) + "@example.com";
            String password = "password" + (i + 1);
            String firstName = "John" + (i + 1);
            String lastName = "Doe" + (i + 1);


            Student studentToSave = new Student(email, password, firstName, lastName);
            studentToSave.setGroup(group);
            studentsToSave.add(studentToSave);
            entityManager.persist(studentToSave);
        }

        // Retrieving Students by Group Name
        List<Student> studentsInGroup = studentRepository.findByGroupsName(groupName);

        // Testing
        assertEquals(studentsInGroup.size(), expectedSize);
        for (int i = 0; i < expectedSize; i++) {
            assertEquals(studentsInGroup.get(i).getFirstName(), studentsToSave.get(i).getFirstName());
            assertEquals(studentsInGroup.get(i).getLastName(), studentsToSave.get(i).getLastName());
            assertEquals(studentsInGroup.get(i).getGroup().getName(), groupName);
        }
    }

    @Test
    public void findAll() {
        // Creating Student instances to save
        Student student1 = new Student("test1@example.com", "password1", "John", "Doe");
        Student student2 = new Student("test2@example.com", "password2", "Jane", "Smith");
        Student student3 = new Student("test3@example.com", "password3", "Alex", "Johnson");

        entityManager.persist(student1);
        entityManager.persist(student2);
        entityManager.persist(student3);

        // Retrieving all students
        List<Student> allStudents = studentRepository.findAll();

        // Testing
        assertEquals(allStudents.size(), 3);
        assertTrue(allStudents.contains(student1));
        assertTrue(allStudents.contains(student2));
        assertTrue(allStudents.contains(student3));
    }

    @Test
    public void findById() {
        // Creating Student instance to save
        Student studentToSave = new Student("test@example.com", "password", "John", "Doe");

        // Saving
        Long savedStudentId = entityManager.persist(studentToSave).getId();

        // Retrieving by ID
        Optional<Student> retrievedStudent = studentRepository.findById(savedStudentId);

        // Testing
        assertTrue(retrievedStudent.isPresent());
        assertEquals(retrievedStudent.get(), studentToSave);
    }

    @Test
    public void deleteById() {
        // Creating Student instance to save
        Student studentToSave = new Student("test@example.com", "password", "John", "Doe");

        // Saving
        Long savedStudentId = entityManager.persist(studentToSave).getId();

        // Deleting by ID
        studentRepository.deleteById(savedStudentId);

        // Retrieving by ID
        Optional<Student> retrievedStudent = studentRepository.findById(savedStudentId);

        // Testing
        assertFalse(retrievedStudent.isPresent());
    }
}

