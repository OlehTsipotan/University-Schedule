package com.university.schedule.service;

import com.university.schedule.converter.ConverterService;
import com.university.schedule.dto.StudentDTO;
import com.university.schedule.exception.DeletionFailedException;
import com.university.schedule.exception.ServiceException;
import com.university.schedule.model.Student;
import com.university.schedule.model.User;
import com.university.schedule.repository.StudentRepository;
import com.university.schedule.validation.StudentEntityValidator;
import com.university.schedule.visitor.UserPageableStudentVisitor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class DefaultStudentService implements StudentService {


	private final StudentRepository studentRepository;

	private final UserService userService;

	private final ConverterService converterService;

	private final StudentEntityValidator studentEntityValidator;

	private final UserPageableStudentVisitor userPageableStudentVisitor;

	@Override
	public List<Student> findAll() {
		List<Student> students = execute(() -> studentRepository.findAll());
		log.debug("Retrieved All {} Students", students.size());
		return students;
	}

	@Override
	public List<StudentDTO> findAllAsDTO() {
		List<StudentDTO> studentDTOList =
				execute(() -> studentRepository.findAll()).stream().map(this::convertToDTO).toList();
		log.debug("Retrieved All {} Students", studentDTOList.size());
		return studentDTOList;
	}

	@Override
	public List<StudentDTO> findAllAsDTO(Pageable pageable) {
        if (pageable == null) {
            throw new IllegalArgumentException("Pageable is null");
        }
		List<StudentDTO> studentDTOList =
				execute(() -> studentRepository.findAll(pageable)).stream().map(this::convertToDTO).toList();
		log.debug("Retrieved All {} Students", studentDTOList.size());
		return studentDTOList;
	}

	@Override
	public List<StudentDTO> findAllAsDTO(String email, Pageable pageable) {
        if (pageable == null || email == null) {
            throw new IllegalArgumentException("Pageable or email is null");
        }
		User user = userService.findByEmail(email);
		List<Student> studentPage = execute(() -> user.accept(userPageableStudentVisitor, pageable));
		List<StudentDTO> studentDTOList = studentPage.stream().map(this::convertToDTO).toList();
		log.debug("Retrieved All {} Students", studentDTOList.size());
		return studentDTOList;
	}

	@Override
	@Transactional
	public Long save(Student student) {
		execute(() -> {
			studentEntityValidator.validate(student);
			studentRepository.save(student);
		});
		log.info("saved {}", student);
		return student.getId();
	}

	@Override
	@Transactional
	public Long update(StudentDTO studentDTO) {
        if (studentDTO == null) {
            throw new IllegalArgumentException("StudentDTO is null");
        }
		Student studentToSave = convertToExistingEntity(studentDTO);
		execute(() -> {
			studentEntityValidator.validate(studentToSave);
			studentRepository.save(studentToSave);
		});
		log.info("saved {}", studentToSave);
		return studentToSave.getId();
	}

	public Student findById(Long id) {
		Student student = execute(() -> studentRepository.findById(id)).orElseThrow(
				() -> new ServiceException("Student not found"));
		log.debug("Retrieved {}", student);
		return student;
	}

	@Override
	public StudentDTO findByIdAsDTO(Long id) {
		Student student = execute(() -> studentRepository.findById(id)).orElseThrow(
				() -> new ServiceException("Student not found"));
		log.debug("Retrieved {}", student);
		return convertToDTO(student);
	}


	@Override
	@Transactional
	public void deleteById(Long id) {
        execute(() -> {
            if (!studentRepository.existsById(id)) {
                throw new DeletionFailedException("There is no Student to delete with id = " + id);
            }
            studentRepository.deleteById(id);
        });
		log.info("Deleted id = {}", id);
	}

	private StudentDTO convertToDTO(Student source) {
		return converterService.convert(source, StudentDTO.class);
	}

	private Student convertToEntity(StudentDTO source) {
		return converterService.convert(source, Student.class);
	}

	private Student convertToExistingEntity(StudentDTO studentDTO) {
		Student foundedStudent = findById(studentDTO.getId());
		Student studentResult = convertToEntity(studentDTO);
		studentResult.setPassword(foundedStudent.getPassword());
		return studentResult;
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