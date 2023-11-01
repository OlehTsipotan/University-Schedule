package com.university.schedule.validation;

import com.university.schedule.exception.ValidationException;
import com.university.schedule.model.Course;
import com.university.schedule.repository.CourseRepository;
import jakarta.validation.Validator;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class CourseEntityValidator extends EntityValidator<Course> {

    private final CourseRepository courseRepository;

    public CourseEntityValidator(CourseRepository courseRepository, Validator validator) {
        super(validator);
        this.courseRepository = courseRepository;
    }

    @Override
    public void validate(Course course) {
        List<String> violations = new ArrayList<>();
        try {
            super.validate(course);
        } catch (ValidationException e) {
            violations = e.getViolations();
        }

        Optional<Course> courseToCheck = courseRepository.findByName(course.getName());

        if (courseToCheck.isPresent() && !course.equals(courseToCheck.get())) {
            violations.add(String.format("Course with name = %s, already exists.", course.getName()));
        }


        if (!violations.isEmpty()) {
            throw new ValidationException("Course is not valid", violations);
        }

    }
}
