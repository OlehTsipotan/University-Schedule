package com.university.schedule.utility;

import com.university.schedule.exception.ServiceException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class EntityValidator {

    private final Validator validator;

    public void validate(Object entity) {
        Set<ConstraintViolation<Object>> violations = validator.validate(entity);
        if (!violations.isEmpty()) {
            StringBuilder errorMessage = new StringBuilder("Validation error(s): ");
            violations.forEach(violation -> errorMessage.append(violation.getMessage()).append("; "));
            throw new ServiceException(errorMessage.toString());
        }
    }
}
