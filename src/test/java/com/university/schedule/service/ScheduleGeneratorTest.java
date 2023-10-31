package com.university.schedule.service;

import com.university.schedule.exception.ScheduleGenerationException;
import com.university.schedule.exception.ServiceException;
import com.university.schedule.model.*;
import com.university.schedule.validation.ScheduleValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class ScheduleGeneratorTest {

    public ScheduleGenerator scheduleGenerator;

    @Mock
    public DefaultScheduledClassService scheduledClassService;

    @Mock
    public ScheduleValidator scheduleValidator;

    @BeforeEach
    public void beforeEach() {
        scheduleGenerator = new ScheduleGenerator(scheduleValidator, scheduledClassService);
    }

    @Test
    public void generate_whenScheduledClassRepositoryThrowsScheduleGenerationException_thenThrowServiceException()
        throws ScheduleGenerationException {
        Mockito.doThrow(ScheduleGenerationException.class).when(scheduleValidator).validate(any(), any(), any());

        LocalDate startDate = LocalDate.of(2000, 1, 1);
        LocalDate endDate = LocalDate.of(2000, 1, 2);

        assertThrows(ServiceException.class, () -> scheduleGenerator.generate(startDate, endDate, new ArrayList<>()));
        Mockito.verify(scheduleValidator).validate(startDate, endDate, new ArrayList<>());
    }

    @ParameterizedTest
    @CsvSource(value = {
        "CourseName:test@example.co:password:John:Doe:1:9:0:90:Lecture:" +
            "GroupName:ClassroomName:BuildingName:BuildingAddress:DisciplineName"}, delimiter = ':')
    public void generate_whenScheduledClassServiceThrowsServiceException_thenThrowServiceException(String courseName,
                                                                                                   String email,
                                                                                                   String password,
                                                                                                   String firstName,
                                                                                                   String lastName,
                                                                                                   Integer orderNumber,
                                                                                                   int hour, int minute,
                                                                                                   int durationMinutes,
                                                                                                   String classTypeName,
                                                                                                   String groupName,
                                                                                                   String classroomName,
                                                                                                   String buildingName,
                                                                                                   String buildingAddress,
                                                                                                   String disciplineName)
        throws ScheduleGenerationException {

        Mockito.doThrow(ServiceException.class).when(scheduledClassService).save((ScheduledClass) any());
        Mockito.doNothing().when(scheduleValidator).validate(any(), any(), any());

        Discipline discipline = new Discipline("DisciplineName");
        Course course = new Course(courseName);
        Teacher teacher = new Teacher(email, password, firstName, lastName);
        ClassTime classTime =
            new ClassTime(orderNumber, LocalTime.of(hour, minute), Duration.ofMinutes(durationMinutes));
        DayOfWeek dayOfWeek = DayOfWeek.SUNDAY;
        ClassType classType = new ClassType(classTypeName);
        Building building = new Building(buildingName, buildingAddress);
        Classroom classroom = new Classroom(classroomName, building);
        Group group = new Group(groupName, discipline);

        LocalDate startDate = LocalDate.of(2000, 1, 1);
        LocalDate endDate = LocalDate.of(2000, 1, 10);

        List<DayScheduleItem> dayScheduleItemList = new ArrayList<>();
        DayScheduleItem dayScheduleItem =
            DayScheduleItem.builder().course(course).teacher(teacher).classTime(classTime).dayOfWeek(dayOfWeek)
                .classType(classType).groups(Set.of(group)).classroom(classroom).build();

        dayScheduleItemList.add(dayScheduleItem);

        assertThrows(ServiceException.class, () -> scheduleGenerator.generate(startDate, endDate, dayScheduleItemList));
        Mockito.verify(scheduleValidator).validate(startDate, endDate, dayScheduleItemList);
        Mockito.verify(scheduledClassService).save((ScheduledClass) any());
    }
}
