package com.university.schedule.service;

import com.university.schedule.converter.ConverterService;
import com.university.schedule.dto.CourseDTO;
import com.university.schedule.exception.DeletionFailedException;
import com.university.schedule.exception.ServiceException;
import com.university.schedule.model.Course;
import com.university.schedule.model.Group;
import com.university.schedule.model.Teacher;
import com.university.schedule.model.User;
import com.university.schedule.repository.CourseRepository;
import com.university.schedule.validation.CourseEntityValidator;
import com.university.schedule.visitor.UserPageableCourseVisitor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class DefaultCourseService implements CourseService {

	private final CourseRepository courseRepository;

	private final ConverterService converterService;

	private final CourseEntityValidator courseEntityValidator;

	private final UserPageableCourseVisitor userPageableCourseVisitor;

	private final UserService userService;

	@Override
	@Transactional
	public Long save(Course course) {
		execute(() -> {
			courseEntityValidator.validate(course);
			courseRepository.save(course);
		});
		log.info("saved {}", course);
		return course.getId();
	}

	@Override
	@Transactional
	public Long save(CourseDTO courseDTO) {
		Course course = convertToEntity(courseDTO);
		execute(() -> {
			courseEntityValidator.validate(course);
			courseRepository.save(course);
		});
		log.info("saved {}", course);
		return course.getId();
	}

	@Override
	public CourseDTO findByIdAsDTO(Long id) {
		Course course = execute(() -> courseRepository.findById(id)).orElseThrow(
				() -> new ServiceException("Course not found"));
		log.debug("Retrieved {}", course);
		return convertToDTO(course);
	}

	@Override
	public List<Course> findAll() {
		List<Course> courses = execute(() -> courseRepository.findAll());
		log.debug("Retrieved All {} Courses", courses.size());
		return courses;
	}

	@Override
	public List<CourseDTO> findAllAsDTO() {
		List<CourseDTO> courseDTOList =
				execute(() -> courseRepository.findAll()).stream().map(this::convertToDTO).toList();
		log.debug("Retrieved All {} Courses", courseDTOList.size());
		return courseDTOList;
	}

	@Override
	public List<CourseDTO> findAllAsDTO(String email, Pageable pageable) {
        if (pageable == null) {
            throw new IllegalArgumentException("Pageable is null");
        }
		User user = userService.findByEmail(email);
		List<Course> coursePage = execute(() -> user.accept(userPageableCourseVisitor, pageable));
		List<CourseDTO> courseDTOS = coursePage.stream().map(this::convertToDTO).toList();
		log.debug("Retrieved All {} Courses", courseDTOS.size());
		return courseDTOS;
	}


	@Override
	public List<Course> findByTeacher(Teacher teacher) {
		List<Course> courses = execute(() -> courseRepository.findByTeachers(teacher));
		log.debug("Retrieved All {} Courses", courses.size());
		return courses;
	}

	@Override
	public List<Course> findByGroup(Group group) {
		List<Course> courses = execute(() -> courseRepository.findByGroups(group));
		log.debug("Retrieved All {} Courses", courses.size());
		return courses;
	}


	@Override
	@Transactional
	public void deleteById(Long id) {
        execute(() -> {
            if (!courseRepository.existsById(id)) {
                throw new DeletionFailedException("There is no ClassTime to delete with id = " + id);
            }
            courseRepository.deleteById(id);
        });
		log.info("Deleted id = {}", id);
	}

	@Override
	public List<Course> findByGroupsName(String groupName) {
		List<Course> courses = execute(() -> courseRepository.findByGroupsName(groupName));
		log.debug("Retrieved All {} Courses", courses.size());
		return courses;
	}

	private CourseDTO convertToDTO(Course course) {
		return converterService.convert(course, CourseDTO.class);
	}

	private Course convertToEntity(CourseDTO courseDTO) {
		return converterService.convert(courseDTO, Course.class);
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
