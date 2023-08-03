package com.university.schedule.exception;

public class ScheduleGenerationException extends Exception {

    public ScheduleGenerationException(String errorMessage) {
        super(errorMessage);
    }

    public ScheduleGenerationException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
