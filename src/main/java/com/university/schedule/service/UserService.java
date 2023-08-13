package com.university.schedule.service;

import com.university.schedule.model.Group;
import com.university.schedule.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> findAll();

    Long save(User user);

    User findById(Long id);

    User findByEmailAndPassword(String email, String password);

    void deleteById(Long id);

}
