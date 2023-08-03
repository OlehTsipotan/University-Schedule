package com.university.schedule.service;

import com.university.schedule.exception.ServiceException;

import com.university.schedule.model.User;
import com.university.schedule.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class DefaultUserService implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<User> findAll() {
        List<User> users = execute(() -> userRepository.findAll());
        log.debug("Retrieved All {} Groups", users.size());
        return users;
    }

    @Override
    @Transactional
    public Long save(User user) {
        if (StringUtils.isEmpty(user.getEmail())) {
            throw new ServiceException("Email can`t be empty or null");
        }
        if (StringUtils.isEmpty(user.getPassword())) {
            throw new ServiceException("Password can`t be empty or null");
        }
        if (StringUtils.isEmpty(user.getFirstName())) {
            throw new ServiceException("FirstName can`t be empty or null");
        }
        if (StringUtils.isEmpty(user.getLastName())) {
            throw new ServiceException("LastName can`t be empty or null");
        }
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
    public User findByEmailAndPassword(String email, String password) {
        User user = execute(() -> userRepository.findByEmailAndPassword(email, password)).orElseThrow(() -> new ServiceException("User not found"));
        log.debug("Retrieved {}", user);
        return user;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
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
