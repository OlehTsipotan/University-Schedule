package com.university.schedule.validation;

import com.university.schedule.exception.ScheduleGenerationConflictException;
import com.university.schedule.exception.ScheduleGenerationDateException;
import com.university.schedule.exception.ScheduleGenerationException;
import com.university.schedule.model.DayScheduleItem;
import com.university.schedule.model.Group;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Component
public class ScheduleValidator {

	private static final String DATE_EXCEPTION_MSG = "startDate can`t be after endDate";
	private static final String GROUP_CONFLICT_EXCEPTION_MSG =
			"There is conflict by Group ( at the same ClassTime and DayOfWeek, Group has at least two different classes )";
	private static final String TEACHER_CONFLICT_EXCEPTION_MSG =
			"There is conflict by Teacher ( at the same ClassTime and DayOfWeek, Teacher has at least two different classes )";

	public void validate(LocalDate startDate, LocalDate endDate, List<DayScheduleItem> dayScheduleItems)
			throws ScheduleGenerationException {
		if (startDate.isAfter(endDate)) {
			throw new ScheduleGenerationDateException(DATE_EXCEPTION_MSG);
		}
		for (DayScheduleItem dayScheduleItem : dayScheduleItems) {
			if (hasConflictByGroup(dayScheduleItems, dayScheduleItem)) {
				throw new ScheduleGenerationConflictException(GROUP_CONFLICT_EXCEPTION_MSG);
			}
			if (hasConflictByTeacher(dayScheduleItems, dayScheduleItem)) {
				throw new ScheduleGenerationConflictException(TEACHER_CONFLICT_EXCEPTION_MSG);
			}
		}
	}

	private boolean hasConflictByGroup(List<DayScheduleItem> dayScheduleItems, DayScheduleItem currentItem) {
		for (DayScheduleItem dayScheduleItem : dayScheduleItems) {
			if (dayScheduleItem != currentItem && dayScheduleItem.getDayOfWeek() == currentItem.getDayOfWeek() &&
			    // check Time
			    dayScheduleItem.getClassTime().equals(currentItem.getClassTime()) && // check Time
			    hasCommonGroup(dayScheduleItem.getGroups(), currentItem.getGroups()) && // check for the same Group
			    (!dayScheduleItem.getCourse().equals(currentItem.getCourse()) ||
			     !dayScheduleItem.getTeacher().equals(currentItem.getTeacher()) ||
			     !dayScheduleItem.getClassroom().equals(currentItem.getClassroom()) ||
			     !dayScheduleItem.getClassType().equals(currentItem.getClassType()))) {
				return true; // Conflicting found
			}
		}
		return false; // No conflicting found
	}

	private boolean hasConflictByTeacher(List<DayScheduleItem> dayScheduleItems, DayScheduleItem currentItem) {
		for (DayScheduleItem dayScheduleItem : dayScheduleItems) {
			if (dayScheduleItem != currentItem && dayScheduleItem.getDayOfWeek() == currentItem.getDayOfWeek() &&
			    // check Time
			    dayScheduleItem.getClassTime().equals(currentItem.getClassTime()) && // check Time
			    dayScheduleItem.getTeacher().equals(currentItem.getTeacher()) && // check for the same Teacher
			    (!dayScheduleItem.getCourse().equals(currentItem.getCourse()) ||
			     !dayScheduleItem.getGroups().equals(currentItem.getGroups()) ||
			     !dayScheduleItem.getClassroom().equals(currentItem.getClassroom()) ||
			     !dayScheduleItem.getClassType().equals(currentItem.getClassType()))) {
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
