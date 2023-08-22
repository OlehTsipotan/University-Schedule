package com.university.schedule.service;

import com.university.schedule.exception.ServiceException;

import com.university.schedule.model.Student;
import com.university.schedule.model.Teacher;
import com.university.schedule.model.User;
import com.university.schedule.repository.UserRepository;
import com.university.schedule.utility.EntityValidator;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class DefaultUserService implements UserService {

    private final UserRepository userRepository;

    private final EntityValidator entityValidator;

    @Override
    public List<User> findAll() {
        List<User> users = execute(() -> userRepository.findAll());
        log.debug("Retrieved All {} Groups", users.size());
        return users;
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        Page<User> users = execute(() -> userRepository.findAll(pageable));
        log.debug("Retrieved All {} Groups", users.getTotalElements());
        return users;
    }

    @Override
    public List<User> findAll(Sort sort) {
        List<User> users = execute(() -> userRepository.findAll(sort));
        log.debug("Retrieved All {} Groups", users.size());
        return users;
    }

    @Override
    @Transactional
    public Long save(User user) {
        entityValidator.validate(user);
        execute(() -> userRepository.save(user));
        log.info("saved {}", user);
        return user.getId();
    }

    @Override
    public User findById(Long id) {
        User user = execute(() -> userRepository.findById(id)).orElseThrow(() -> new ServiceException("User not found"));
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
    @Transactional
    public void deleteById(Long id) {
        try{
            findById(id);
        } catch (ServiceException e){
            throw new ServiceException("There is no User to delete with id = "+ id);
        }
        execute(() -> userRepository.deleteById(id));
        log.info("Deleted id = {}", id);
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
