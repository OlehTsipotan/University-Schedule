package com.university.schedule.repository;

import com.university.schedule.model.Authority;
import com.university.schedule.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {

    List<Authority> findByRoles(Role role);

    Optional<Authority> findByName(String name);
}
