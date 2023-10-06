package com.university.schedule.service;

import com.university.schedule.dto.ScheduleFilterItem;
import com.university.schedule.exception.ServiceException;
import com.university.schedule.model.Student;
import com.university.schedule.model.Teacher;
import com.university.schedule.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ScheduleFilterItemService {

	private final UserService userService;

	public void processRawItem(ScheduleFilterItem scheduleFilterItem) {
		replaceForUserByEmail(scheduleFilterItem);
		replaceNullValues(scheduleFilterItem);
	}

	private void replaceForUserByEmail(ScheduleFilterItem scheduleFilterItem) {
		User user = userService.findByEmail(scheduleFilterItem.getEmail());
		if (user instanceof Teacher) {
			scheduleFilterItem.setTeacherId(user.getId());
		} else if (user instanceof Student student) {
			List<Long> groupDTOS = new ArrayList<>();
			if (student.getGroup() != null) {
				groupDTOS.add(student.getGroup().getId());
			}
			scheduleFilterItem.setGroupIdList(groupDTOS);
		} else {
			throw new ServiceException("Admin should not have access to this method.");
		}
	}

	private void replaceNullValues(ScheduleFilterItem scheduleFilterItem) {
		if (scheduleFilterItem.getStartDate() == null) {
			LocalDate countFrom = scheduleFilterItem.getEndDate() == null? LocalDate.now() :
					scheduleFilterItem.getEndDate();
			LocalDate previousOrSameMonday = countFrom.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
			scheduleFilterItem.setStartDate(previousOrSameMonday);
		}
		if (scheduleFilterItem.getEndDate() == null) {
			LocalDate countFrom = scheduleFilterItem.getStartDate() == null? LocalDate.now() :
					scheduleFilterItem.getStartDate();
			LocalDate nextOrSameSunday = countFrom.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
			scheduleFilterItem.setEndDate(nextOrSameSunday);
		}
	}
}
