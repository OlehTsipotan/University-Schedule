package com.university.schedule.service;

import com.university.schedule.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface UserService {

    List<User> findAll();

    Page<User> findAll(Pageable pageable);

    List<User> findAll(Sort sort);

    Long save(User user);

    User findById(Long id);

    User findByEmail(String email);

    void deleteById(Long id);

}
