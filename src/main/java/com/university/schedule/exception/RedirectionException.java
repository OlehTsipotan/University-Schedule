package com.university.schedule.exception;

public class RedirectionException extends RuntimeException {
    public RedirectionException(String message, Throwable cause) {
        super(message, cause);
    }
}