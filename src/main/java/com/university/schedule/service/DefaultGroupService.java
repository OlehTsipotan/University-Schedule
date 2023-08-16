package com.university.schedule.service;

import com.university.schedule.exception.ServiceException;
import com.university.schedule.model.Course;
import com.university.schedule.model.Discipline;
import com.university.schedule.model.Group;
import com.university.schedule.repository.GroupRepository;
import com.university.schedule.utility.EntityValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
@Transactional(readOnly = true)
public class DefaultGroupService implements GroupService {

    private final GroupRepository groupRepository;

    private final CourseService courseService;

    private final EntityValidator entityValidator;

    @Override
    public List<Group> findAll() throws ServiceException {
        List<Group> groups = execute(() -> groupRepository.findAll());
        log.debug("Retrieved All {} Groups", groups.size());
        return groups;
    }

    @Override
    public List<Group> findAll(Sort sort) throws ServiceException {
        List<Group> groups = execute(() -> groupRepository.findAll(sort));
        log.debug("Retrieved All {} Groups", groups.size());
        return groups;
    }

    @Override
    @Transactional
    public Long save(Group group) {
        entityValidator.validate(group);
        execute(() -> groupRepository.save(group));
        log.info("saved {}", group);
        return group.getId();
    }

    @Override
    public Group findById(Long id) {
        Group group = execute(() -> groupRepository.findById(id)).orElseThrow(
                () -> new ServiceException("Group not found"));
        log.debug("Retrieved {}", group);
        return group;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        try{
            findById(id);
        } catch (ServiceException e){
            throw new ServiceException("There is no Group to delete with id = "+ id);
        }
        execute(() -> groupRepository.deleteById(id));
        log.info("Deleted id = {}", id);
    }

    @Override
    public List<Group> findByDiscipline(Discipline discipline) {
        List<Group> groups = execute(() -> groupRepository.findByDiscipline(discipline));
        log.debug("Retrieved All {} Groups", groups.size());
        return groups;
    }

    @Override
    @Transactional
    public boolean assignToCourse(Long groupId, Long courseId) {
        Group group = groupRepository.findById(groupId).orElseThrow(
                () -> new ServiceException("There is no Group with id = " + groupId));
        Course course = courseService.findById(courseId);
        boolean result = group.getCourses().add(course);

        save(group);
        log.info("{} assigned to {} - {}", group, course, result);
        return result;
    }

    @Override
    @Transactional
    public boolean removeFromCourse(Long groupId, Long courseId) {
        Group group = groupRepository.findById(groupId).orElseThrow(
                () -> new ServiceException("There is no Group with id = " + groupId));
        Course course = courseService.findById(courseId);
        boolean result = group.getCourses().remove(course);

        save(group);
        log.info("{} removed from {} - {}", group, course, result);
        return result;
    }


    private <T> T execute(DaoSupplier<T> supplier) {
        try {
            return supplier.get();
        } catch (DataAccessException e) {
            throw new ServiceException("DAO operation failed", e);
        }
    }

    private void execute(DaoProcessor processor) {
        try {
            processor.process();
        } catch (DataAccessException e) {
            throw new ServiceException("DAO operation failed", e);
        }
    }

    @FunctionalInterface
    public interface DaoSupplier<T> {
        T get();
    }

    @FunctionalInterface
    public interface DaoProcessor {
        void process();
    }
}
