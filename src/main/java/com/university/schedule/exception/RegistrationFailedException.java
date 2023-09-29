package com.university.schedule.exception;

public class RegistrationFailedException extends ServiceException {

	public RegistrationFailedException(String errorMessage) {
		super(errorMessage);
	}

	public RegistrationFailedException(String errorMessage, Throwable err) {
		super(errorMessage, err);
	}
}
