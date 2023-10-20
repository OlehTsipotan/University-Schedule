package com.university.schedule.repository;

import com.university.schedule.model.*;
import org.checkerframework.checker.units.qual.C;
import org.flywaydb.core.Flyway;
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
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ScheduledClassRepositoryTest {

    @Container
    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("databaseName")
            .withUsername("username")
            .withPassword("password");

    @Autowired
    ScheduledClassRepository scheduledClassRepository;

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
    @CsvSource(
            value = {"CourseName:test@example.co:password:John:Doe:1:9:0:90:Lecture:" +
                    "ClassroomName:BuildingName:BuildingAddress"},
            delimiter = ':')
    public void save(String courseName, String email, String password, String firstName,
                     String lastName, Integer orderNumber, int hour, int minute,
                     int durationMinutes, String classTypeName, String classroomName, String buildingName,
                     String buildingAddress) {


        // Creating instance
        Building building = new Building(buildingName, buildingAddress);
        Classroom classroom = new Classroom(classroomName, building);
        Course course = new Course(courseName);
        Teacher teacher = new Teacher(email, password, firstName, lastName);
        ClassTime classTime = new ClassTime(orderNumber, LocalTime.of(hour, minute), Duration.ofMinutes(durationMinutes));
        LocalDate date = LocalDate.now();
        ClassType classType = new ClassType(classTypeName);

        entityManager.persist(building);
        entityManager.persist(classroom);
        entityManager.persist(course);
        entityManager.persist(teacher);
        entityManager.persist(classType);
        entityManager.persist(classTime);


        ScheduledClass scheduledClassToSave = ScheduledClass.builder()
                .course(course)
                .teacher(teacher)
                .classroom(classroom) // Set classroom if needed
                .classTime(classTime)
                .date(date)
                .classType(classType)
                .build();

        // Saving
        Long savedScheduledClassId = scheduledClassRepository.save(scheduledClassToSave).getId();

        // Retrieving
        ScheduledClass retrievedScheduledClass = entityManager.find(ScheduledClass.class, savedScheduledClassId);

        assertNotNull(retrievedScheduledClass);
        assertEquals(retrievedScheduledClass.getCourse(), course);
        assertEquals(retrievedScheduledClass.getTeacher(), teacher);
        assertEquals(retrievedScheduledClass.getClassroom(), classroom);
        assertEquals(retrievedScheduledClass.getClassTime(), classTime);
        assertEquals(retrievedScheduledClass.getDate(), date);
        assertEquals(retrievedScheduledClass.getClassType(), classType);
    }

    @ParameterizedTest
    @CsvSource(value = {"CourseName:test@example.co:password:John:Doe:1:9:0:90:Lecture:" +
            "ClassroomName:BuildingName:BuildingAddress"}, delimiter = ':')
    public void findById
            (String courseName, String email, String password, String firstName,
             String lastName, Integer orderNumber, int hour, int minute, int durationMinutes,
             String classTypeName, String classroomName, String buildingName,
             String buildingAddress) {
        // Creating instance
        Building building = new Building(buildingName, buildingAddress);
        Classroom classroom = new Classroom(classroomName, building);
        Course course = new Course(courseName);
        Teacher teacher = new Teacher(email, password, firstName, lastName);
        ClassTime classTime = new ClassTime(orderNumber, LocalTime.of(hour, minute), Duration.ofMinutes(durationMinutes));
        LocalDate date = LocalDate.now();
        ClassType classType = new ClassType(classTypeName);

        entityManager.persist(building);
        entityManager.persist(classroom);
        entityManager.persist(course);
        entityManager.persist(teacher);
        entityManager.persist(classType);
        entityManager.persist(classTime);

        ScheduledClass scheduledClassToSave = ScheduledClass.builder()
                .course(course)
                .teacher(teacher)
                .classroom(classroom) // Set classroom if needed
                .classTime(classTime)
                .date(date)
                .classType(classType)
                .build();


        // Saving
        Long savedScheduledClassId = entityManager.persist(scheduledClassToSave).getId();


        ScheduledClass retrievedScheduledClass = scheduledClassRepository.findById(savedScheduledClassId).get();

        assertNotNull(retrievedScheduledClass);
        assertEquals(retrievedScheduledClass.getCourse(), course);
        assertEquals(retrievedScheduledClass.getTeacher(), teacher);
        assertEquals(retrievedScheduledClass.getClassroom(), classroom);
        assertEquals(retrievedScheduledClass.getClassTime(), classTime);
        assertEquals(retrievedScheduledClass.getDate(), date);
        assertEquals(retrievedScheduledClass.getClassType(), classType);
    }

    @ParameterizedTest
    @CsvSource(value = {"CourseName:test@example.co:password:John:Doe:1:9:0:90:Lecture"}, delimiter = ':')
    public void findAll(String courseName, String email, String password, String firstName, String lastName, Integer orderNumber, int hour, int minute, int durationMinutes, String classTypeName) {
        // Creating instance
        Course course = new Course(courseName);
        Teacher teacher = new Teacher(email, password, firstName, lastName);
        ClassTime classTime = new ClassTime(orderNumber, LocalTime.of(hour, minute), Duration.ofMinutes(durationMinutes));
        LocalDate date1 = LocalDate.of(2023, 5, 1);
        LocalDate date2 = LocalDate.of(2023, 5, 2);
        LocalDate date3 = LocalDate.of(2023, 5, 3);
        ClassType classType = new ClassType(classTypeName);
        Set<Group> groups = new HashSet<>();

        entityManager.persist(course);
        entityManager.persist(teacher);
        entityManager.persist(classTime);
        entityManager.persist(classType);

        ScheduledClass scheduledClassToSave1 = ScheduledClass.builder()
                .course(course)
                .teacher(teacher)
                .classroom(null) // Set classroom if needed
                .classTime(classTime)
                .date(date1)
                .classType(classType)
                .groups(groups)
                .build();

        ScheduledClass scheduledClassToSave2 = ScheduledClass.builder()
                .course(course)
                .teacher(teacher)
                .classroom(null) // Set classroom if needed
                .classTime(classTime)
                .date(date2)
                .classType(classType)
                .groups(groups)
                .build();

        ScheduledClass scheduledClassToSave3 = ScheduledClass.builder()
                .course(course)
                .teacher(teacher)
                .classroom(null) // Set classroom if needed
                .classTime(classTime)
                .date(date3)
                .classType(classType)
                .groups(groups)
                .build();

        entityManager.persist(scheduledClassToSave1);
        entityManager.persist(scheduledClassToSave2);
        entityManager.persist(scheduledClassToSave3);

        List<ScheduledClass> allScheduledClasses = scheduledClassRepository.findAll();

        assertEquals(3, allScheduledClasses.size());
        assertTrue(allScheduledClasses.contains(scheduledClassToSave1));
        assertTrue(allScheduledClasses.contains(scheduledClassToSave2));
        assertTrue(allScheduledClasses.contains(scheduledClassToSave3));
    }

    @ParameterizedTest
    @CsvSource(value = {"CourseName:test@example.co:password:John:Doe:1:9:0:90:Lecture"}, delimiter = ':')
    public void findAllAsDTOByScheduleFilterItem(String courseName, String email, String password, String firstName, String lastName, Integer orderNumber, int hour, int minute, int durationMinutes, String classTypeName) {
        // Creating instance
        Course course = new Course(courseName);
        Teacher teacher = new Teacher(email, password, firstName, lastName);
        ClassTime classTime = new ClassTime(orderNumber, LocalTime.of(hour, minute), Duration.ofMinutes(durationMinutes));
        LocalDate date1 = LocalDate.of(2023, 5, 1);
        LocalDate date2 = LocalDate.of(2023, 5, 2);
        LocalDate date3 = LocalDate.of(2023, 5, 3);
        ClassType classType = new ClassType(classTypeName);
        Set<Group> groups = new HashSet<>();
        Discipline discipline = new Discipline("discipline1");


        entityManager.persist(course);
        entityManager.persist(discipline);
        entityManager.persist(teacher);
        entityManager.persist(classTime);
        entityManager.persist(classType);

        Group group1 = new Group("group1", discipline);
        Group group2 = new Group("group2", discipline);

        entityManager.persist(group2);
        entityManager.persist(group1);
        groups.add(group1);

        ScheduledClass scheduledClassToSave1 = ScheduledClass.builder()
                .course(course)
                .teacher(teacher)
                .classroom(null) // Set classroom if needed
                .classTime(classTime)
                .date(date1)
                .classType(classType)
                .groups(groups)
                .build();

        ScheduledClass scheduledClassToSave2 = ScheduledClass.builder()
                .course(course)
                .teacher(teacher)
                .classroom(null) // Set classroom if needed
                .classTime(classTime)
                .date(date2)
                .classType(classType)
                .groups(Set.of(group2, group1))
                .build();

        ScheduledClass scheduledClassToSave3 = ScheduledClass.builder()
                .course(course)
                .teacher(teacher)
                .classroom(null) // Set classroom if needed
                .classTime(classTime)
                .date(date3)
                .classType(classType)
                .groups(groups)
                .build();

        entityManager.persist(scheduledClassToSave1);
        entityManager.persist(scheduledClassToSave2);
        entityManager.persist(scheduledClassToSave3);
        List<Long> longs = new ArrayList<>();
        longs.add(1L);
        longs.add(2L);
        List<ScheduledClass> allScheduledClasses =
                scheduledClassRepository.findAllFiltered(date1, date2,
                        null, null, new ArrayList<>());
        System.out.printf(allScheduledClasses.toString());
        assertEquals(2, allScheduledClasses.size());

        assertTrue(allScheduledClasses.contains(scheduledClassToSave1));
        assertTrue(allScheduledClasses.contains(scheduledClassToSave2));
    }


    @ParameterizedTest
    @CsvSource(
            value = {"CourseName:test@example.co:password:John:Doe:1:9:0:90:" +
                    "Lecture:rightGroupName:wrongGroupName:DisciplineName"},
            delimiter = ':')
    public void findByDateBetweenAndGroups
            (String courseName, String email, String password, String firstName,
             String lastName, Integer orderNumber, int hour, int minute, int durationMinutes,
             String classTypeName, String rightGroupName, String wrongGroupName, String disciplineName) {


        // Creating instances
        Discipline discipline = new Discipline(disciplineName);
        Course course = new Course(courseName);
        Teacher teacher = new Teacher(email, password, firstName, lastName);
        ClassTime classTime1 = new ClassTime(orderNumber, LocalTime.of(hour, minute), Duration.ofMinutes(durationMinutes));
        ClassTime classTime2 = new ClassTime(orderNumber+1, LocalTime.of(hour+1, minute), Duration.ofMinutes(durationMinutes));
        LocalDate rightDate = LocalDate.of(2023, 5, 5);
        LocalDate wrongDate = rightDate.plusDays(10);
        ClassType classType = new ClassType(classTypeName);

        Group rightGroup = new Group(rightGroupName, discipline);
        Group wrongGroup = new Group(wrongGroupName, discipline);

        Set<Group> rightGroups = Set.of(rightGroup, wrongGroup);
        Set<Group> wrongGroups = Set.of(wrongGroup);

        // Saving
        entityManager.persist(discipline);
        entityManager.persist(course);
        entityManager.persist(teacher);
        entityManager.persist(classTime1);
        entityManager.persist(classTime2);
        entityManager.persist(classType);
        entityManager.persist(rightGroup);
        entityManager.persist(wrongGroup);

        // Creating scheduledClass instances
        ScheduledClass rightScheduledClassToSave = ScheduledClass.builder()
                .course(course)
                .teacher(teacher)
                .classroom(null) // Set classroom if needed
                .classTime(classTime1)
                .date(rightDate)
                .classType(classType)
                .groups(rightGroups)
                .build();

        ScheduledClass wrongGroupScheduledClassToSave = ScheduledClass.builder()
                .course(course)
                .teacher(teacher)
                .classroom(null) // Set classroom if needed
                .classTime(classTime2)
                .date(rightDate)
                .classType(classType)
                .groups(wrongGroups)
                .build();

        ScheduledClass wrongDateScheduledClassToSave = ScheduledClass.builder()
                .course(course)
                .teacher(teacher)
                .classroom(null) // Set classroom if needed
                .classTime(classTime1)
                .date(wrongDate)
                .classType(classType)
                .groups(rightGroups)
                .build();

        // Saving
        entityManager.persist(rightScheduledClassToSave);
        entityManager.persist(wrongDateScheduledClassToSave);
        entityManager.persist(wrongGroupScheduledClassToSave);

        // Retrieving
        List<ScheduledClass> retrievedScheduledClasses = scheduledClassRepository.findByDateBetweenAndGroups(rightDate.minusDays(1), rightDate.plusDays(1), rightGroup);

        // Asserting
        assertTrue(retrievedScheduledClasses.contains(rightScheduledClassToSave));
        assertFalse(retrievedScheduledClasses.contains(wrongDateScheduledClassToSave));
        assertFalse(retrievedScheduledClasses.contains(wrongGroupScheduledClassToSave));
    }

    @ParameterizedTest
    @CsvSource(
            value = {"RightCourseName:WrongCourseName:test@example.co:password:John:" +
                    "Doe:1:9:0:90:Lecture:rightGroupName:wrongGroupName:DisciplineName"},
            delimiter = ':')
    public void findByDateBetweenAndGroupsAndCourse
            (String rightCourseName, String wrongCourseName, String email, String password, String firstName,
             String lastName, Integer orderNumber, int hour, int minute, int durationMinutes, String classTypeName,
             String rightGroupName, String wrongGroupName, String disciplineName){
        // Creating instances
        Discipline discipline = new Discipline(disciplineName);
        Course rightCourse = new Course(rightCourseName);
        Course wrongCourse = new Course(wrongCourseName);
        Teacher teacher = new Teacher(email, password, firstName, lastName);
        ClassTime classTime1 = new ClassTime(orderNumber, LocalTime.of(hour, minute), Duration.ofMinutes(durationMinutes));
        ClassTime classTime2 = new ClassTime(orderNumber+1, LocalTime.of(hour+1, minute), Duration.ofMinutes(durationMinutes));
        LocalDate rightDate = LocalDate.of(2023, 5, 5);
        LocalDate wrongDate = rightDate.plusDays(10);
        ClassType classType = new ClassType(classTypeName);

        Group rightGroup = new Group(rightGroupName, discipline);
        Group wrongGroup = new Group(wrongGroupName, discipline);

        Set<Group> rightGroups = Set.of(rightGroup, wrongGroup);
        Set<Group> wrongGroups = Set.of(wrongGroup);

        // Saving
        entityManager.persist(discipline);
        entityManager.persist(rightCourse);
        entityManager.persist(wrongCourse);
        entityManager.persist(teacher);
        entityManager.persist(classTime1);
        entityManager.persist(classTime2);
        entityManager.persist(classType);
        entityManager.persist(rightGroup);
        entityManager.persist(wrongGroup);

        // Creating scheduledClass instances
        ScheduledClass rightScheduledClassToSave = ScheduledClass.builder()
                .course(rightCourse)
                .teacher(teacher)
                .classroom(null) // Set classroom if needed
                .classTime(classTime1)
                .date(rightDate)
                .classType(classType)
                .groups(rightGroups)
                .build();

        ScheduledClass wrongGroupScheduledClassToSave = ScheduledClass.builder()
                .course(rightCourse)
                .teacher(teacher)
                .classroom(null) // Set classroom if needed
                .classTime(classTime2)
                .date(rightDate)
                .classType(classType)
                .groups(wrongGroups)
                .build();

        ScheduledClass wrongDateScheduledClassToSave = ScheduledClass.builder()
                .course(rightCourse)
                .teacher(teacher)
                .classroom(null) // Set classroom if needed
                .classTime(classTime1)
                .date(wrongDate)
                .classType(classType)
                .groups(rightGroups)
                .build();

        ScheduledClass wrongCourseScheduledClassToSave = ScheduledClass.builder()
                .course(wrongCourse)
                .teacher(teacher)
                .classroom(null) // Set classroom if needed
                .classTime(classTime1)
                .date(rightDate)
                .classType(classType)
                .groups(rightGroups)
                .build();

        // Saving
        entityManager.persist(rightScheduledClassToSave);
        entityManager.persist(wrongDateScheduledClassToSave);
        entityManager.persist(wrongGroupScheduledClassToSave);

        // Retrieving
        List<ScheduledClass> retrievedScheduledClasses = scheduledClassRepository.findByDateBetweenAndGroups(rightDate.minusDays(1), rightDate.plusDays(1), rightGroup);

        // Asserting
        assertTrue(retrievedScheduledClasses.contains(rightScheduledClassToSave));
        assertFalse(retrievedScheduledClasses.contains(wrongDateScheduledClassToSave));
        assertFalse(retrievedScheduledClasses.contains(wrongGroupScheduledClassToSave));
        assertFalse(retrievedScheduledClasses.contains(wrongCourseScheduledClassToSave));
    }

}

