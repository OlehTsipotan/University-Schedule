package com.university.schedule.validation;

import com.university.schedule.dto.UserRegisterDTO;
import com.university.schedule.exception.ValidationException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserRegisterDTOValidator extends EntityValidator<UserRegisterDTO> {

	public UserRegisterDTOValidator(jakarta.validation.Validator validator) {
		super(validator);
	}

	@Override
	public void validate(UserRegisterDTO userRegisterDTO) {
		List<String> violations = new ArrayList<>();
		try {
			super.validate(userRegisterDTO);
		} catch (ValidationException e) {
			violations = e.getViolations();
		}

		if (!userRegisterDTO.getPassword().equals(userRegisterDTO.getConfirmationPassword())) {
			violations.add("Password and Confirmation password should be equals");
		}

		if (!violations.isEmpty()) {
			throw new ValidationException("User is not valid", violations);
		}

	}
}
