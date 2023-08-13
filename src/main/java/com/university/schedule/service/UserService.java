package com.university.schedule.service;

import com.university.schedule.model.User;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface UserService {

    List<User> findAll();

    List<User> findAll(Sort sort);

    Long save(User user);

    User findById(Long id);

    User findByEmailAndPassword(String email, String password);

    void deleteById(Long id);

}
