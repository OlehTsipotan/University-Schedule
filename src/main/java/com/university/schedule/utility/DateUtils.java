package com.university.schedule.utility;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DateUtils {

	public static List<LocalDate> getDatesBetween(LocalDate startDate, LocalDate endDate) {
		LocalDate currentDate = startDate;
		List<LocalDate> datesBetween = new ArrayList<>();
		while (!currentDate.isAfter(endDate)) {
			datesBetween.add(currentDate);
			currentDate = currentDate.plusDays(1);
		}
		return datesBetween;
	}
}
