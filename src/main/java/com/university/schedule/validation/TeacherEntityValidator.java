package com.university.schedule.validation;

import com.university.schedule.exception.ValidationException;
import com.university.schedule.model.Teacher;
import jakarta.validation.Validator;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TeacherEntityValidator extends EntityValidator<Teacher> {
	private final UserEntityValidator userEntityValidator;

	public TeacherEntityValidator(UserEntityValidator userEntityValidator, Validator validator) {
		super(validator);
		this.userEntityValidator = userEntityValidator;
	}

	@Override
	public void validate(Teacher teacher) {
		List<String> violations = new ArrayList<>();
		try {
			super.validate(teacher);
		} catch (ValidationException e) {
			violations = e.getViolations();
		}

		try {
			userEntityValidator.validate(teacher);
		} catch (ValidationException e) {
			violations.addAll(e.getViolations());
		}

		if (!violations.isEmpty()) {
			throw new ValidationException("Teacher is not valid", violations);
		}

	}
}