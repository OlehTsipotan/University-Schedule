package com.university.schedule.validation;

import com.university.schedule.exception.ValidationException;
import com.university.schedule.model.ClassType;
import com.university.schedule.repository.ClassTypeRepository;
import jakarta.validation.Validator;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class ClassTypeEntityValidator extends EntityValidator<ClassType> {

	private final ClassTypeRepository classTypeRepository;

	public ClassTypeEntityValidator(ClassTypeRepository classTypeRepository, Validator validator) {
		super(validator);
		this.classTypeRepository = classTypeRepository;
	}

	@Override
	public void validate(ClassType classType) {
		List<String> violations = new ArrayList<>();
		try {
			super.validate(classType);
		} catch (ValidationException e) {
			violations = e.getViolations();
		}

		Optional<ClassType> classTypeToCheck = classTypeRepository.findByName(classType.getName());
		if (classTypeToCheck.isPresent() && !classType.equals(classTypeToCheck.get())) {
			violations.add(String.format("ClassType with name = %s, already exists.", classType.getName()));
		}


		if (!violations.isEmpty()) {
			throw new ValidationException("ClassType is not valid", violations);
		}

	}
}