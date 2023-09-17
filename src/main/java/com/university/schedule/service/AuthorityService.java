package com.university.schedule.service;

import com.university.schedule.model.Authority;
import com.university.schedule.model.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AuthorityService {

	Long save(Authority authority);

	Authority findById(Long id);

	Authority findByName(String name);

	List<Authority> findByRole(Role role);

	List<Authority> findAll();

	Page<Authority> findAll(Pageable pageable);

	void deleteById(Long id);
}
