package com.university.schedule.validation;

import com.university.schedule.exception.ServiceException;
import com.university.schedule.exception.ValidationException;
import com.university.schedule.model.ScheduledClass;
import com.university.schedule.repository.ScheduledClassRepository;
import com.university.schedule.service.CourseService;
import com.university.schedule.service.ScheduledClassService;
import jakarta.validation.Validator;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class ScheduledClassEntityValidator extends EntityValidator<ScheduledClass> {

    private final ScheduledClassRepository scheduledClassRepository;

    private final CourseService courseService;


    public ScheduledClassEntityValidator(ScheduledClassRepository scheduledClassRepository, CourseService courseService, Validator validator) {
        super(validator);
        this.scheduledClassRepository = scheduledClassRepository;
        this.courseService = courseService;
    }

    @Override
    public void validate(ScheduledClass scheduledClass) {
        List<String> violations = new ArrayList<>();
        try {
            super.validate(scheduledClass);
        } catch (ValidationException e) {
            violations = e.getViolations();
        }

        Optional<ScheduledClass> scheduledClassToCheck = scheduledClassRepository.findByDateAndClassTimeAndTeacher(
                scheduledClass.getDate(), scheduledClass.getClassTime(), scheduledClass.getTeacher());
        if (scheduledClassToCheck.isPresent() && !scheduledClass.equals(scheduledClassToCheck.get())) {
            violations.add(String.format("ScheduledClass with %s, %s, %s, already exists", scheduledClass.getTeacher(),
                    scheduledClass.getDate(), scheduledClass.getClassTime()));
        }
        // Teacher and Course
        if (!courseService.findByTeacher(scheduledClass.getTeacher()).contains(scheduledClass.getCourse())) {
            violations.add(String.format("%s can`t be assigned to class with %s", scheduledClass.getTeacher(),
                    scheduledClass.getCourse()));
        }
        // Groups and Course
        if (!scheduledClass.getGroups().stream().allMatch(group -> courseService.findByGroup(group).contains(
                scheduledClass.getCourse()))) {
            violations.add(String.format("%s can`t be assigned to class with %s", scheduledClass.getGroups(),
                    scheduledClass.getCourse()));
        }


        if (!violations.isEmpty()) {
            throw new ValidationException("Group is not valid", violations);
        }

    }
}
