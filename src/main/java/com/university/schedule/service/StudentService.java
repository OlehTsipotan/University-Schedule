package com.university.schedule.service;

import com.university.schedule.dto.StudentDTO;
import com.university.schedule.model.Student;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StudentService {

	List<Student> findAll();

	List<StudentDTO> findAllAsDTO();

	List<StudentDTO> findAllAsDTO(Pageable pageable);

	List<StudentDTO> findAllAsDTO(String email, Pageable pageable);

	Long save(Student student);

	Long update(StudentDTO studentDTO);

	Student findById(Long id);

	StudentDTO findByIdAsDTO(Long id);

	void deleteById(Long id);
}
