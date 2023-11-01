package com.university.schedule.utility;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DateUtilsTest {

    @Test
    public void getDatesBetween_whenStartDateIsNull_throwIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> DateUtils.getDatesBetween(null, LocalDate.now()));
    }

    @Test
    public void getDatesBetween_whenEndDateIsNull_throwIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> DateUtils.getDatesBetween(LocalDate.now(), null));
    }

    @Test
    public void getDatesBetween_whenDatesAreNull_throwIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> DateUtils.getDatesBetween(null, null));
    }

    @Test
    public void getDatesBetween_whenStartDateIsAfterEndDate_returnEmptyList() {
        assertEquals(DateUtils.getDatesBetween(LocalDate.now().plusDays(1), LocalDate.now()).size(), 0);
    }

    @Test
    public void getDatesBetween_whenStartDateIsBeforeEndDate_returnListOfDates() {
        HashSet<LocalDate> localDates = new HashSet<>();
        localDates.add(LocalDate.now());
        localDates.add(LocalDate.now().plusDays(1));
        localDates.add(LocalDate.now().plusDays(2));
        assertEquals(new HashSet<>(DateUtils.getDatesBetween(LocalDate.now(), LocalDate.now().plusDays(2))),
            localDates);
    }
}
