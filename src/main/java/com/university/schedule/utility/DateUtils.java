package com.university.schedule.utility;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DateUtils {

	public static List<LocalDate> getDatesBetween(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and end date must be not null");
        }
		LocalDate currentDate = startDate;
		List<LocalDate> datesBetween = new ArrayList<>();
		while (!currentDate.isAfter(endDate)) {
			datesBetween.add(currentDate);
			currentDate = currentDate.plusDays(1);
		}
		return datesBetween;
	}
}
