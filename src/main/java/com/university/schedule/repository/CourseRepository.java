package com.university.schedule.repository;

import com.university.schedule.model.Course;
import com.university.schedule.model.Group;
import com.university.schedule.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {
	Optional<Course> findByName(String name);

	List<Course> findByTeachers(Teacher teacher);

	List<Course> findByGroupsName(String groupName);

	List<Course> findByGroups(Group group);
}
