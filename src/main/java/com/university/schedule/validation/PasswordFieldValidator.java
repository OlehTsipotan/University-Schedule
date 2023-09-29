package com.university.schedule.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordFieldValidator implements ConstraintValidator<Password, String> {
	@Override
	public boolean isValid(String password, ConstraintValidatorContext constraintValidatorContext) {
		String regex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{4,}$";
		return password.matches(regex);
	}
}
