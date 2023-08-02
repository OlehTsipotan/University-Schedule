package com.university.schedule.utility;

import com.university.schedule.model.DayScheduleItem;
import com.university.schedule.model.Group;
import com.university.schedule.service.MyUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Component
public class ScheduleValidator {

    private final Logger logger = LoggerFactory.getLogger(ScheduleValidator.class);

    public ScheduleValidator(){}

    public boolean isValid(LocalDate startDate, LocalDate endTime, List<DayScheduleItem> dayScheduleItems) {
        if (startDate.isAfter(endTime)){
            return false;
        }
        for (DayScheduleItem scheduleItem : dayScheduleItems) {
            if (hasConflictByGroup(dayScheduleItems, scheduleItem)) {
                return false;
            }
            if (hasConflictByTeacher(dayScheduleItems, scheduleItem)){
                return false;
            }
        }
        return true; // If no conflicts found, the schedule is valid
    }

    private boolean hasConflictByGroup(List<DayScheduleItem> dayScheduleItems, DayScheduleItem currentItem) {
        for (DayScheduleItem scheduleItem : dayScheduleItems) {
            if (scheduleItem != currentItem &&
                    scheduleItem.getDayOfWeek() == currentItem.getDayOfWeek() && // check Time
                    scheduleItem.getClassTime().equals(currentItem.getClassTime()) && // check Time
                    hasCommonGroup(scheduleItem.getGroups(), currentItem.getGroups()) && // check for the same Group
                    (!scheduleItem.getCourse().equals(currentItem.getCourse())
                            || !scheduleItem.getTeacher().equals(currentItem.getTeacher())
                            || !scheduleItem.getClassroom().equals(currentItem.getClassroom())
                            || !scheduleItem.getClassType().equals(currentItem.getClassType()))) {
                return true; // Conflicting found
            }
        }
        return false; // No conflicting found
    }

    private boolean hasConflictByTeacher(List<DayScheduleItem> dayScheduleItems, DayScheduleItem currentItem) {
        for (DayScheduleItem scheduleItem : dayScheduleItems) {
            if (scheduleItem != currentItem &&
                    scheduleItem.getDayOfWeek() == currentItem.getDayOfWeek() && // check Time
                    scheduleItem.getClassTime().equals(currentItem.getClassTime()) && // check Time
                    scheduleItem.getTeacher().equals(currentItem.getTeacher()) && // check for the same Teacher
                    (!scheduleItem.getCourse().equals(currentItem.getCourse())
                            || !scheduleItem.getGroups().equals(currentItem.getGroups())
                            || !scheduleItem.getClassroom().equals(currentItem.getClassroom())
                            || !scheduleItem.getClassType().equals(currentItem.getClassType()))) {
                return true; // Conflicting found
            }
        }
        return false; // No conflicting found
    }

    private boolean hasCommonGroup(Set<Group> groups1, Set<Group> groups2) {
        for (Group group : groups1) {
            if (groups2.contains(group)) {
                return true;
            }
        }
        return false;
    }

}
