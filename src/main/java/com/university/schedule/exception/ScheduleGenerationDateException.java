package com.university.schedule.exception;

public class ScheduleGenerationDateException extends ScheduleGenerationException {
    public ScheduleGenerationDateException(String errorMessage) {
        super(errorMessage);
    }

    public ScheduleGenerationDateException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
