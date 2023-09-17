package com.university.schedule.exception;

public class ScheduleGenerationConflictException extends ScheduleGenerationException {

	public ScheduleGenerationConflictException(String errorMessage) {
		super(errorMessage);
	}

	public ScheduleGenerationConflictException(String errorMessage, Throwable err) {
		super(errorMessage, err);
	}
}