package com.university.schedule.service;

import com.university.schedule.converter.ConverterService;
import com.university.schedule.dto.UserRegisterDTO;
import com.university.schedule.exception.RegistrationFailedException;
import com.university.schedule.exception.ServiceException;
import com.university.schedule.model.Student;
import com.university.schedule.model.Teacher;
import com.university.schedule.model.User;
import com.university.schedule.validation.UserRegisterDTOValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DefaultUserRegistrationService implements UserRegistrationService {

	private final PasswordEncoder passwordEncoder;

	private final TeacherService teacherService;

	private final StudentService studentService;

	private final ConverterService converterService;

	private final UserRegisterDTOValidator userRegisterDTOValidator;


	@Transactional
	public Long register(UserRegisterDTO userRegisterDTO) {
		userRegisterDTOValidator.validate(userRegisterDTO);

		User user = convertToEntity(userRegisterDTO);
		user.setPassword(passwordEncoder.encode(user.getPassword()));

		if ("Teacher".equals(user.getRole().getName())) {
			return execute(() -> teacherService.save(new Teacher(user)));
		} else if ("Student".equals(user.getRole().getName())) {
			return execute(() -> studentService.save(new Student(user)));
		}
		throw new RegistrationFailedException("Can`t register user with role = " + user.getRole().getName());

	}

	private User convertToEntity(UserRegisterDTO source) {
		return converterService.convert(source, User.class);
	}

	private <T> T execute(DaoSupplier<T> supplier) {
		try {
			return supplier.get();
		} catch (ServiceException e) {
			throw new RegistrationFailedException("User registration failed", e);
		}
	}

	@FunctionalInterface
	public interface DaoSupplier<T> {
		T get();
	}
}
