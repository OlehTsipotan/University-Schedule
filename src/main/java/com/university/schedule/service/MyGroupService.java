package com.university.schedule.service;

import com.university.schedule.exception.ServiceException;
import com.university.schedule.model.Course;
import com.university.schedule.model.Group;
import com.university.schedule.repository.GroupRepository;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class MyGroupService implements GroupService {

    private final GroupRepository groupRepository;

    private final CourseService courseService;

    private final Logger logger = LoggerFactory.getLogger(MyGroupService.class);

    public MyGroupService(GroupRepository groupRepository, CourseService courseService) {
        this.groupRepository = groupRepository;
        this.courseService = courseService;
    }

    @Override
    public List<Group> findAll() throws ServiceException {
        List<Group> groups = execute(() -> groupRepository.findAll());
        logger.debug("Retrieved All {} Groups", groups.size());
        return groups;
    }

    @Override
    public Long save(String groupName) {
        if (StringUtils.isEmpty(groupName)) {
            throw new ServiceException("groupName can`t be empty or null");
        }
        Group group = new Group(groupName);
        execute(() -> groupRepository.save(group));
        logger.info("saved {}", group);
        return group.getId();
    }

    @Override
    public Long save(Group group) {
        if (StringUtils.isEmpty(group.getName())) {
            throw new ServiceException("groupName can`t be empty or null");
        }
        execute(() -> groupRepository.save(group));
        logger.info("saved {}", group);
        return group.getId();
    }

    @Override
    public Group findById(Long id) {
        Group group = execute(() -> groupRepository.findById(id)).orElseThrow(() -> new ServiceException("Group not found"));
        logger.debug("Retrieved {}", group);
        return group;
    }

    @Override
    public void deleteById(Long id) {
        execute(() -> groupRepository.deleteById(id));
        logger.info("Deleted id = {}", id);
    }

    @Override
    public boolean assignToCourse(Long groupId, Long courseId) {
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new ServiceException("There is no Group with id = " + groupId));
        Course course = courseService.findById(courseId);
        boolean result = group.getCourses().add(course);

        save(group);
        logger.info("{} assigned to {} - {}", group, course, result);
        return result;
    }

    @Override
    public boolean removeFromCourse(Long groupId, Long courseId) {
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new ServiceException("There is no Group with id = " + groupId));
        Course course = courseService.findById(courseId);
        boolean result = group.getCourses().remove(course);

        save(group);
        logger.info("{} removed from {} - {}", group, course, result);
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
