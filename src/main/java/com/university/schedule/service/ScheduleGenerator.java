package com.university.schedule.service;

import com.university.schedule.exception.ScheduleGenerationConflictException;
import com.university.schedule.exception.ScheduleGenerationDateException;
import com.university.schedule.exception.ScheduleGenerationException;
import com.university.schedule.exception.ServiceException;
import com.university.schedule.model.DayScheduleItem;
import com.university.schedule.model.ScheduledClass;
import com.university.schedule.validation.ScheduleValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * The ScheduleGenerator class is responsible for generating and saving scheduled classes for a given time period,
 * based on a list of DayScheduleItems. It ensures that there are no conflicts in the schedule and utilizes a
 * ScheduleValidator to perform the validation.
 *
 * <p>Dependencies:
 * - The class uses the ScheduleValidator to check for conflicts in the provided schedule items.
 * - It relies on the ScheduledClassService to save the generated scheduled classes to the database.
 * <p>
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ScheduleGenerator {

	/**
	 * The ScheduleValidator instance used to check for conflicts in the schedule items.
	 */
	private final ScheduleValidator scheduleValidator;

	/**
	 * The ScheduledClassService instance used to save the generated scheduled classes to the database.
	 */
	private final ScheduledClassService scheduledClassService;

	/**
	 * Generates and saves scheduled classes for a specified time period, based on the provided list of DayScheduleItems.
	 * It checks for conflicts in the schedule using the ScheduleValidator before generating the classes.
	 *
	 * <p>If there are conflicts in the dayScheduleItems or the start/end dates are invalid, a ServiceException
	 * will be thrown.
	 *
	 * <p>The generated scheduled classes are created for each DayScheduleItem and repeated weekly until the endTime
	 * is reached.
	 *
	 * @param startDate        The start date from which to generate the scheduled classes.
	 * @param endDate          The end date until which to generate the scheduled classes (inclusive).
	 * @param dayScheduleItems The list of DayScheduleItems containing information about the classes to schedule.
	 * @throws ServiceException If there are conflicts in the dayScheduleItems or the start/end dates are invalid.
	 */
	public void generate(LocalDate startDate, LocalDate endDate, List<DayScheduleItem> dayScheduleItems)
			throws ServiceException {
		// from different ScheduleGenerationExceptions - different ServiceExceptions. For right error processing on UI
		try {
			scheduleValidator.validate(startDate, endDate, dayScheduleItems);
		} catch (ScheduleGenerationException e) {
			if (e instanceof ScheduleGenerationDateException) {
				throw new ServiceException(e);
			} else if (e instanceof ScheduleGenerationConflictException) {
				throw new ServiceException(e);
			} else {
				throw new ServiceException(e);
			}
		}

		ScheduledClass scheduledClass;
		try {
			for (DayScheduleItem dayScheduleItem : dayScheduleItems) {
				log.debug("Generation schedule from {} to {}, for {}", dayScheduleItem, startDate, endDate);
				LocalDate date = startDate;
				while (date.isBefore(endDate)) {
					if (date.getDayOfWeek() == dayScheduleItem.getDayOfWeek()) {
						scheduledClass = ScheduledClass.builder().groups(dayScheduleItem.getGroups())
								.classroom(dayScheduleItem.getClassroom()).teacher(dayScheduleItem.getTeacher())
								.course(dayScheduleItem.getCourse()).classType(dayScheduleItem.getClassType())
								.classTime(dayScheduleItem.getClassTime()).date(date).build();
						scheduledClassService.save(scheduledClass);
						date = date.plusWeeks(1);
						continue;
					}
					date = date.plusDays(1);
				}
			}
		} catch (ServiceException e) {
			throw new ServiceException("Can't generate schedule due to data access issues");
		}
	}
}
