package com.university.schedule.exception;

public class DeletionFailedException extends ServiceException {
	public DeletionFailedException(String errorMessage) {
		super(errorMessage);
	}

	public DeletionFailedException(String errorMessage, Throwable err) {
		super(errorMessage, err);
	}
}
