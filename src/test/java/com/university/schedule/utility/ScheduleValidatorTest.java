package com.university.schedule.utility;

import com.university.schedule.exception.ScheduleGenerationException;
import com.university.schedule.model.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
public class ScheduleValidatorTest {

    @Autowired
    public ScheduleValidator scheduleValidator;

    @ParameterizedTest
    @CsvSource(value = {"CourseName1:CourseName2:test@example.co:password:John:Doe:1:9:0:90:Lecture:GroupName:ClassroomName"}, delimiter = ':')
    public void isValid_whenConflictByGroupAndCourse_returnFalse(String courseName1, String courseName2, String email, String password, String firstName, String lastName, Integer orderNumber, int hour, int minute, int durationMinutes, String classTypeName, String groupName, String classroomName) {

        Course course1 = new Course(courseName1);
        Course course2 = new Course(courseName2);
        Teacher teacher = new Teacher(email, password, firstName, lastName);
        ClassTime classTime = new ClassTime(orderNumber, LocalTime.of(hour, minute), Duration.ofMinutes(durationMinutes));
        DayOfWeek dayOfWeek = DayOfWeek.MONDAY;
        ClassType classType = new ClassType(classTypeName);
        Classroom classroom = new Classroom(classroomName);
        Group group = new Group(groupName);

        LocalDate startDate = LocalDate.of(2023, 1, 2);
        LocalDate endDate = LocalDate.of(2023, 1, 8);

        List<DayScheduleItem> dayScheduleItemList = new ArrayList<>();
        DayScheduleItem dayScheduleItem1 = DayScheduleItem.builder()
            .course(course1)
            .teacher(teacher)
            .classTime(classTime)
            .dayOfWeek(dayOfWeek)
            .classType(classType)
            .groups(Set.of(group))
            .classroom(classroom)
            .build();

        DayScheduleItem dayScheduleItem2 = DayScheduleItem.builder()
            .course(course2)
            .teacher(teacher)
            .classTime(classTime)
            .dayOfWeek(dayOfWeek)
            .classType(classType)
            .groups(Set.of(group))
            .classroom(classroom)
            .build();

        dayScheduleItemList.add(dayScheduleItem1);
        dayScheduleItemList.add(dayScheduleItem2);

        assertThrows(ScheduleGenerationException.class, () -> scheduleValidator.validate(LocalDate.of(2000, 5, 1), LocalDate.of(2000, 5, 10), dayScheduleItemList));
    }

    @ParameterizedTest
    @CsvSource(value = {"CourseName:test@example.co:password:John:Doe:1:9:0:90:Lecture:GroupName:ClassroomName"}, delimiter = ':')
    public void isValid_whenNoConflicts_returnTrue(String courseName, String email, String password, String firstName, String lastName, Integer orderNumber, int hour, int minute, int durationMinutes, String classTypeName, String groupName, String classroomName) {

        Course course = new Course(courseName);
        Teacher teacher = new Teacher(email, password, firstName, lastName);
        ClassTime classTime = new ClassTime(orderNumber, LocalTime.of(hour, minute), Duration.ofMinutes(durationMinutes));
        ClassType classType = new ClassType(classTypeName);
        Classroom classroom = new Classroom(classroomName);
        Group group = new Group(groupName);

        LocalDate startDate = LocalDate.of(2023, 1, 2);
        LocalDate endDate = LocalDate.of(2023, 1, 8);

        List<DayScheduleItem> dayScheduleItemList = new ArrayList<>();
        DayScheduleItem dayScheduleItem1 = DayScheduleItem.builder()
            .course(course)
            .teacher(teacher)
            .classTime(classTime)
            .dayOfWeek(DayOfWeek.MONDAY)
            .classType(classType)
            .groups(Set.of(group))
            .classroom(classroom)
            .build();

        DayScheduleItem dayScheduleItem2 = DayScheduleItem.builder()
            .course(course)
            .teacher(teacher)
            .classTime(classTime)
            .dayOfWeek(DayOfWeek.THURSDAY)
            .classType(classType)
            .groups(Set.of(group))
            .classroom(classroom)
            .build();

        dayScheduleItemList.add(dayScheduleItem1);
        dayScheduleItemList.add(dayScheduleItem2);

        assertDoesNotThrow(() -> scheduleValidator.validate(LocalDate.of(2000, 5, 1), LocalDate.of(2000, 5, 10), dayScheduleItemList));
    }

    @ParameterizedTest
    @CsvSource(value = {"CourseName1:test1@example.co:test2@example.co:password:John:Doe:1:9:0:90:Lecture:GroupName:ClassroomName"}, delimiter = ':')
    public void isValid_whenConflictByGroupAndTeacher_returnFalse(String courseName, String email1, String email2, String password, String firstName, String lastName, Integer orderNumber, int hour, int minute, int durationMinutes, String classTypeName, String groupName, String classroomName) {

        Course course = new Course(courseName);
        Teacher teacher1 = new Teacher(email1, password, firstName, lastName);
        Teacher teacher2 = new Teacher(email2, password, firstName, lastName);
        ClassTime classTime = new ClassTime(orderNumber, LocalTime.of(hour, minute), Duration.ofMinutes(durationMinutes));
        DayOfWeek dayOfWeek = DayOfWeek.MONDAY;
        ClassType classType = new ClassType(classTypeName);
        Classroom classroom = new Classroom(classroomName);
        Group group = new Group(groupName);

        LocalDate startDate = LocalDate.of(2023, 1, 2);
        LocalDate endDate = LocalDate.of(2023, 1, 8);

        List<DayScheduleItem> dayScheduleItemList = new ArrayList<>();
        DayScheduleItem dayScheduleItem1 = DayScheduleItem.builder()
            .course(course)
            .teacher(teacher1)
            .classTime(classTime)
            .dayOfWeek(dayOfWeek)
            .classType(classType)
            .groups(Set.of(group))
            .classroom(classroom)
            .build();

        DayScheduleItem dayScheduleItem2 = DayScheduleItem.builder()
            .course(course)
            .teacher(teacher2)
            .classTime(classTime)
            .dayOfWeek(dayOfWeek)
            .classType(classType)
            .groups(Set.of(group))
            .classroom(classroom)
            .build();

        dayScheduleItemList.add(dayScheduleItem1);
        dayScheduleItemList.add(dayScheduleItem2);

        assertThrows(ScheduleGenerationException.class, () -> scheduleValidator.validate(LocalDate.of(2000, 5, 1), LocalDate.of(2000, 5, 10), dayScheduleItemList));

    }

    @TestConfiguration
    static class MenuPropertiesImplTestContextConfiguration {

        @Bean
        public ScheduleValidator scheduleGeneratorValidator() {
            return new ScheduleValidator();
        }

    }
}
