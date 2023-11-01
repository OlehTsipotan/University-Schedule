package com.university.schedule.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Duration;

public class ClassDurationFieldValidator implements ConstraintValidator<ClassDuration, Duration> {
    @Override
    public boolean isValid(Duration duration, ConstraintValidatorContext constraintValidatorContext) {
        if (duration == null) {
            return false;
        }
        return !duration.isNegative();
    }
}
