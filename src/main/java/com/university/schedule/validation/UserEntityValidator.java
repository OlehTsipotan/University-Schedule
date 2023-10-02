package com.university.schedule.validation;

import com.university.schedule.exception.ValidationException;
import com.university.schedule.model.User;
import com.university.schedule.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class UserEntityValidator extends EntityValidator<User> {

	private final UserRepository userRepository;

	public UserEntityValidator(UserRepository userRepository, jakarta.validation.Validator validator) {
		super(validator);
		this.userRepository = userRepository;
	}

	@Override
	public void validate(User user) {
		List<String> violations = new ArrayList<>();
		try {
			super.validate(user);
		} catch (ValidationException e) {
			violations = e.getViolations();
		}

		Optional<User> userToCheck = userRepository.findByEmail(user.getEmail());
		if (userToCheck.isPresent() && !user.equals(userToCheck.get())) {
			violations.add(String.format("User with email = %s, already exists", user.getEmail()));
		}

		if (!violations.isEmpty()) {
			throw new ValidationException("User is not valid", violations);
		}

	}
}
