package com.university.schedule.validation;

import com.university.schedule.exception.ValidationException;
import com.university.schedule.model.ClassTime;
import com.university.schedule.repository.ClassTimeRepository;
import jakarta.validation.Validator;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class ClassTimeEntityValidator extends EntityValidator<ClassTime> {

	private final ClassTimeRepository classTimeRepository;

	public ClassTimeEntityValidator(ClassTimeRepository classTimeRepository, Validator validator) {
		super(validator);
		this.classTimeRepository = classTimeRepository;
	}

	@Override
	public void validate(ClassTime classTime) {
		List<String> violations = new ArrayList<>();
		try {
			super.validate(classTime);
		} catch (ValidationException e) {
			violations = e.getViolations();
		}

		Optional<ClassTime> classTimeToCheck = classTimeRepository.findByOrderNumber(classTime.getOrderNumber());

		if (classTimeToCheck.isPresent() && !classTime.equals(classTimeToCheck.get())) {
			violations.add(
					String.format("ClassTime with Order Number = %d, already exists.", classTime.getOrderNumber()));
		}


		if (!violations.isEmpty()) {
			throw new ValidationException("ClassTime is not valid", violations);
		}

	}
}
