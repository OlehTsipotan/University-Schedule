package com.university.schedule.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ClassDurationFieldValidatorTest {

    private ClassDurationFieldValidator classDurationFieldValidator;

    @BeforeEach
    public void setUp() {
        classDurationFieldValidator = new ClassDurationFieldValidator();
    }

    @Test
    public void isValid_whenDurationIsNull_returnFalse() {
        assertFalse(classDurationFieldValidator.isValid(null, null));
    }

    @Test
    public void isValid_whenDurationIsNegative_returnFalse() {
        assertFalse(classDurationFieldValidator.isValid(java.time.Duration.ofMinutes(-1), null));
    }

    @Test
    public void isValid_whenDurationIsZero_returnTrue() {
        assertTrue(classDurationFieldValidator.isValid(java.time.Duration.ofMinutes(0), null));
    }

    @Test
    public void isValid_whenDurationIsPositive_returnTrue() {
        assertTrue(classDurationFieldValidator.isValid(java.time.Duration.ofMinutes(1), null));
    }
}
