package com.university.schedule.exception;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ValidationException extends ServiceException{

    private List<String> violations = new ArrayList<>();

    public ValidationException(String errorMessage) {
        super(errorMessage);
    }

    public ValidationException(String errorMessage, List<String> violations) {
        super(errorMessage);
        this.violations = violations;
    }

}
