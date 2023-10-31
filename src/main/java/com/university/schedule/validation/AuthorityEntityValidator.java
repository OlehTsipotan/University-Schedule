package com.university.schedule.validation;

import com.university.schedule.exception.ValidationException;
import com.university.schedule.model.Authority;
import com.university.schedule.repository.AuthorityRepository;
import jakarta.validation.Validator;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class AuthorityEntityValidator extends EntityValidator<Authority> {

    private final AuthorityRepository authorityRepository;

    public AuthorityEntityValidator(AuthorityRepository authorityRepository, Validator validator) {
        super(validator);
        this.authorityRepository = authorityRepository;
    }

    @Override
    public void validate(Authority authority) {
        List<String> violations = new ArrayList<>();
        try {
            super.validate(authority);
        } catch (ValidationException e) {
            violations = e.getViolations();
        }

        Optional<Authority> authorityToCheck = authorityRepository.findByName(authority.getName());
        if (authorityToCheck.isPresent() && !authority.equals(authorityToCheck.get())) {
            violations.add(String.format("Authority with name = %s, already exists.", authority.getName()));
        }

        if (!violations.isEmpty()) {
            throw new ValidationException("Authority is not valid", violations);
        }

    }
}
