package com.university.schedule.service;

import com.university.schedule.dto.TeacherDTO;
import com.university.schedule.model.Course;
import com.university.schedule.model.Teacher;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TeacherService {

	List<Teacher> findAll();

	List<TeacherDTO> findAllAsDTO();

	List<TeacherDTO> findAllAsDTO(Pageable pageable);

	Long save(Teacher teacher);

	Long update(TeacherDTO teacherDTO);

	TeacherDTO findByIdAsDTO(Long id);

	void deleteById(Long id);

	List<Teacher> findByCourses(Course course);
}
