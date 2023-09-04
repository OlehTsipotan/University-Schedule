package com.university.schedule.validation;

import com.university.schedule.exception.ValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public abstract class EntityValidator<T>{

    private final Validator validator;

    public void validate(T t) {
        Set<ConstraintViolation<Object>> violations = validator.validate(t);
        if (!violations.isEmpty()) {
            throw new ValidationException(String.format("Validation error with %s", t.toString()),
                    violations.stream().map(ConstraintViolation::getMessage).toList());
        }

    }
}
