package com.university.schedule.service;

import com.university.schedule.dto.ScheduleFilterItem;
import com.university.schedule.exception.ServiceException;
import com.university.schedule.model.Group;
import com.university.schedule.model.Student;
import com.university.schedule.model.Teacher;
import com.university.schedule.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class ScheduledFilterItemServiceTest {

    private ScheduleFilterItemService scheduledFilterItemService;

    @Mock
    private UserService userService;

    @BeforeEach
    public void setUp() {
        scheduledFilterItemService = new ScheduleFilterItemService(userService);
    }

    @ParameterizedTest
    @NullSource
    public void processRawItem_whenScheduleFilterItemIsNull_thenThrowIllegalArgumentException(
        ScheduleFilterItem nullScheduleFilterItem) {
        assertThrows(IllegalArgumentException.class,
            () -> scheduledFilterItemService.processRawItem(nullScheduleFilterItem));
    }

    @Test
    public void processRawItem_whenUserIsATeacher_success() {
        ScheduleFilterItem scheduleFilterItem = new ScheduleFilterItem();
        scheduleFilterItem.setEmail("email");

        Teacher teacher = new Teacher();
        teacher.setId(1L);

        when(userService.findByEmail("email")).thenReturn(teacher);

        scheduledFilterItemService.processRawItem(scheduleFilterItem);

        assertEquals(1L, scheduleFilterItem.getTeacherId());
        assertNull(scheduleFilterItem.getGroupIdList());
        assertNull(scheduleFilterItem.getClassTypeId());
        assertEquals("email", scheduleFilterItem.getEmail());
        assertNotNull(scheduleFilterItem.getStartDate());
        assertNotNull(scheduleFilterItem.getEndDate());
    }

    @Test
    public void processRawItem_whenUserIsAnUser_throwServiceException() {
        ScheduleFilterItem scheduleFilterItem = new ScheduleFilterItem();
        scheduleFilterItem.setEmail("email");

        Teacher teacher = new Teacher();
        teacher.setId(1L);

        when(userService.findByEmail("email")).thenReturn(new User());

        assertThrows(ServiceException.class, () -> scheduledFilterItemService.processRawItem(scheduleFilterItem));
    }

    @Test
    public void processRawItem_whenUserIsAStudent_success() {
        ScheduleFilterItem scheduleFilterItem = new ScheduleFilterItem();
        scheduleFilterItem.setEmail("email");

        Student student = new Student();
        Group group = new Group();
        group.setId(1L);
        student.setGroup(group);

        when(userService.findByEmail("email")).thenReturn(student);

        scheduledFilterItemService.processRawItem(scheduleFilterItem);

        assertNull(scheduleFilterItem.getTeacherId());
        assertEquals(1L, scheduleFilterItem.getGroupIdList().get(0));
        assertNull(scheduleFilterItem.getClassTypeId());
        assertEquals("email", scheduleFilterItem.getEmail());
        assertNotNull(scheduleFilterItem.getStartDate());
        assertNotNull(scheduleFilterItem.getEndDate());
    }

}
