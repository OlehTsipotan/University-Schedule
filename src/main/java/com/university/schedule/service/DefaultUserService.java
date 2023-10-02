package com.university.schedule.service;

import com.university.schedule.converter.ConverterService;
import com.university.schedule.dto.UserDTO;
import com.university.schedule.exception.DeletionFailedException;
import com.university.schedule.exception.ServiceException;
import com.university.schedule.model.Student;
import com.university.schedule.model.Teacher;
import com.university.schedule.model.User;
import com.university.schedule.repository.UserRepository;
import com.university.schedule.validation.UserEntityValidator;
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
public class DefaultUserService implements UserService {

	private final UserRepository userRepository;
	private final UserEntityValidator userEntityValidator;
	private final ConverterService converterService;

	@Override
	public List<User> findAll() {
		List<User> users = execute(() -> userRepository.findAll());
		log.debug("Retrieved All {} Users", users.size());
		return users;
	}

	@Override
	public List<UserDTO> findAllAsDTO() {
		List<UserDTO> userDTOList = execute(() -> userRepository.findAll()).stream().map(this::convertToDTO).toList();
		log.debug("Retrieved All {} Users", userDTOList.size());
		return userDTOList;
	}

	@Override
	public List<UserDTO> findAllAsDTO(Pageable pageable) {
		List<UserDTO> userDTOList =
				execute(() -> userRepository.findAll(pageable)).stream().map(this::convertToDTO).toList();
		log.debug("Retrieved All {} Users", userDTOList.size());
		return userDTOList;
	}

	@Override
	@Transactional
	public Long save(User user) {
		execute(() -> {
			userEntityValidator.validate(user);
			userRepository.save(user);
		});
		log.info("saved {}", user);
		return user.getId();
	}

	@Override
	@Transactional
	public Long update(UserDTO userDTO) {

		User foundedUser = findById(userDTO.getId());

		User userToSave = convertToEntity(userDTO);

		if (foundedUser instanceof Student) {
			Student student = convertToStudentEntity(userDTO);
			student.setGroup(((Student) foundedUser).getGroup());
			userToSave = student;
		} else if (foundedUser instanceof Teacher) {
			Teacher teacher = convertToTeacherEntity(userDTO);
			teacher.setCourses(((Teacher) foundedUser).getCourses());
			userToSave = teacher;
		}

		userToSave.setPassword(foundedUser.getPassword());

		User finalUserToSave = userToSave;
		execute(() -> {
			userEntityValidator.validate(finalUserToSave);
			userRepository.save(finalUserToSave);
		});
		log.info("saved {}", userToSave);
		return userToSave.getId();
	}

	@Override
	public UserDTO findByIdAsDTO(Long id) {
		User user =
				execute(() -> userRepository.findById(id)).orElseThrow(() -> new ServiceException("User not found"));
		log.debug("Retrieved {}", user);
		return convertToDTO(user);
	}

	private User findById(Long id) {
		User user =
				execute(() -> userRepository.findById(id)).orElseThrow(() -> new ServiceException("User not found"));
		log.debug("Retrieved {}", user);
		return user;
	}

	@Override
	public User findByEmail(String email) {
		User user = execute(() -> userRepository.findByEmail(email)).orElseThrow(
				() -> new ServiceException("User not found"));
		log.debug("Retrieved {}", user);
		return user;
	}

	@Override
	public UserDTO findByEmailAsDTO(String email) {
		User user = execute(() -> userRepository.findByEmail(email)).orElseThrow(
				() -> new ServiceException("User not found"));
		log.debug("Retrieved {}", user);
		return convertToDTO(user);
	}

	@Override
	@Transactional
	public void deleteById(Long id) {
		try {
			findById(id);
		} catch (ServiceException e) {
			throw new DeletionFailedException("There is no User to delete with id = " + id);
		}
		execute(() -> userRepository.deleteById(id));
		log.info("Deleted id = {}", id);
	}

	private UserDTO convertToDTO(User source) {
		return converterService.convert(source, UserDTO.class);
	}

	private User convertToEntity(UserDTO source) {
		return converterService.convert(source, User.class);
	}

	private Student convertToStudentEntity(UserDTO source) {
		return converterService.convert(source, Student.class);
	}

	private Teacher convertToTeacherEntity(UserDTO source) {
		return converterService.convert(source, Teacher.class);
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
