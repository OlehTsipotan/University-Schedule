package com.university.schedule.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordFieldValidator implements ConstraintValidator<Password, String> {

    // (?=.*[A-Za-z]) - at least one letter
    // (?=.*\\d) - at least one digit
    // [A-Za-z\\d]{4,} - at least 4 characters
    // ^ - start of the string
    // $ - end of the string
    // Valid Example: "Password123"
    @Override
    public boolean isValid(String password, ConstraintValidatorContext constraintValidatorContext) {
        if (password == null) {
            return false;
        }
        String regex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{4,}$";
        return password.matches(regex);
    }
}
