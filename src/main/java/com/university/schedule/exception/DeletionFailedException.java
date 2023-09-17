package com.university.schedule.exception;

public class DeletionFailedException extends ServiceException {
	public DeletionFailedException(String errorMessage) {
		super(errorMessage);
	}
}
