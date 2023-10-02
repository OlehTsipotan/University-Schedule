package com.university.schedule.validation;

import com.university.schedule.exception.ValidationException;
import com.university.schedule.model.Classroom;
import com.university.schedule.repository.ClassroomRepository;
import jakarta.validation.Validator;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class ClassroomEntityValidator extends EntityValidator<Classroom> {

	private final ClassroomRepository classroomRepository;

	public ClassroomEntityValidator(ClassroomRepository classroomRepository, Validator validator) {
		super(validator);
		this.classroomRepository = classroomRepository;
	}

	@Override
	public void validate(Classroom classroom) {
		List<String> violations = new ArrayList<>();
		try {
			super.validate(classroom);
		} catch (ValidationException e) {
			violations = e.getViolations();
		}

		Optional<Classroom> classroomToCheck =
				classroomRepository.findByNameAndBuilding(classroom.getName(), classroom.getBuilding());
		if (classroomToCheck.isPresent() && !classroom.equals(classroomToCheck.get())) {
			violations.add(String.format("Classroom with name = %s and %s, already exists.", classroom.getName(),
					classroom.getBuilding()));
		}


		if (!violations.isEmpty()) {
			throw new ValidationException("Classroom is not valid", violations);
		}

	}
}
