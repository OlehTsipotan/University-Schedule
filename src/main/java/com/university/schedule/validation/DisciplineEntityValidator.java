package com.university.schedule.validation;

import com.university.schedule.exception.ValidationException;
import com.university.schedule.model.Discipline;
import com.university.schedule.repository.DisciplineRepository;
import jakarta.validation.Validator;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class DisciplineEntityValidator extends EntityValidator<Discipline> {

    private final DisciplineRepository disciplineRepository;

    public DisciplineEntityValidator(DisciplineRepository disciplineRepository, Validator validator) {
        super(validator);
        this.disciplineRepository = disciplineRepository;
    }

    @Override
    public void validate(Discipline discipline) {
        List<String> violations = new ArrayList<>();
        try {
            super.validate(discipline);
        } catch (ValidationException e) {
            violations = e.getViolations();
        }

        Optional<Discipline> disciplineToCheck = disciplineRepository.findByName(discipline.getName());

        if (disciplineToCheck.isPresent() && !discipline.equals(disciplineToCheck.get())) {
            violations.add(String.format("Discipline with name = %s, already exists.", discipline.getName()));
        }


        if (!violations.isEmpty()) {
            throw new ValidationException("Discipline is not valid", violations);
        }

    }
}
