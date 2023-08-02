package com.university.schedule.repository;

import com.university.schedule.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    User save(User user);

    List<User> findAll();

    Optional<User> findByEmailAndPassword(String email, String password);

    Optional<User> findById(Long id);

    void deleteById(Long id);
}
