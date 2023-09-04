package com.university.schedule.validation;

import com.university.schedule.exception.ValidationException;
import com.university.schedule.model.Role;
import com.university.schedule.repository.RoleRepository;
import jakarta.validation.Validator;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class RoleEntityValidator extends EntityValidator<Role> {

    private final RoleRepository roleRepository;

    public RoleEntityValidator(RoleRepository roleRepository, Validator validator) {
        super(validator);
        this.roleRepository = roleRepository;
    }

    @Override
    public void validate(Role role) {
        List<String> violations = new ArrayList<>();
        try {
            super.validate(role);
        } catch (ValidationException e) {
            violations = e.getViolations();
        }

        Optional<Role> roleToCheck = roleRepository.findByName(role.getName());
        if (roleToCheck.isPresent() && !role.equals(roleToCheck.get())) {
            violations.add(String.format("Role with name = %s, already exists.", role.getName()));
        }

        if (!violations.isEmpty()) {
            throw new ValidationException("Role is not valid", violations);
        }

    }
}

