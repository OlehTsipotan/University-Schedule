package com.university.schedule.validation;

import com.university.schedule.exception.ValidationException;
import com.university.schedule.model.Group;
import com.university.schedule.repository.GroupRepository;
import jakarta.validation.Validator;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class GroupEntityValidator extends EntityValidator<Group> {

    private final GroupRepository groupRepository;

    public GroupEntityValidator(GroupRepository groupRepository, Validator validator) {
        super(validator);
        this.groupRepository = groupRepository;
    }

    @Override
    public void validate(Group group) {
        List<String> violations = new ArrayList<>();
        try {
            super.validate(group);
        } catch (ValidationException e) {
            violations = e.getViolations();
        }

        Optional<Group> groupToCheck = groupRepository.findByName(group.getName());

        if (groupToCheck.isPresent() && !group.equals(groupToCheck.get())) {
            violations.add(String.format("Group with name = %s, already exists.", group.getName()));
        }

        if (!violations.isEmpty()) {
            throw new ValidationException("Group is not valid", violations);
        }

    }
}
