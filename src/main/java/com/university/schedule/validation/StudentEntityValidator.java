package com.university.schedule.validation;

import com.university.schedule.exception.ValidationException;
import com.university.schedule.model.Student;
import jakarta.validation.Validator;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class StudentEntityValidator extends EntityValidator<Student> {
    private final UserEntityValidator userEntityValidator;

    public StudentEntityValidator(UserEntityValidator userEntityValidator, Validator validator) {
        super(validator);
        this.userEntityValidator = userEntityValidator;
    }

    @Override
    public void validate(Student student) {
        List<String> violations = new ArrayList<>();
        try {
            super.validate(student);
        } catch (ValidationException e) {
            violations = e.getViolations();
        }

        try {
            userEntityValidator.validate(student);
        } catch (ValidationException e) {
            violations.addAll(e.getViolations());
        }

        if (!violations.isEmpty()) {
            throw new ValidationException("Student is not valid", violations);
        }

    }
}
