package com.university.schedule.service;

import com.university.schedule.exception.ServiceException;
import com.university.schedule.model.Role;
import com.university.schedule.repository.RoleRepository;
import com.university.schedule.utility.EntityValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
@Transactional(readOnly = true)
public class DefaultRoleService implements RoleService{


    private final RoleRepository roleRepository;

    private final EntityValidator entityValidator;

    @Override
    public List<Role> findAll() {
        List<Role> roles = execute(() -> roleRepository.findAll());
        log.debug("Retrieved All {} Role", roles.size());
        return roles;
    }

    @Override
    public Page<Role> findAll(Pageable pageable) {
        Page<Role> roles = execute(() -> roleRepository.findAll(pageable));
        log.debug("Retrieved All {} Role", roles.getTotalElements());
        return roles;
    }

    @Override
    @Transactional
    public Long save(Role role) {
        entityValidator.validate(role);
        execute(() -> roleRepository.save(role));
        log.info("saved {}", role);
        return role.getId();
    }

    @Override
    public Role findById(Long id) {
        Role role = execute(() -> roleRepository.findById(id)).orElseThrow(
                () -> new ServiceException("Role not found"));
        log.debug("Retrieved {}", role);
        return role;
    }

    @Override
    public Role findByName(String name) {
        Role role = execute(() -> roleRepository.findByName(name)).orElseThrow(
                () -> new ServiceException("Role not found"));
        log.debug("Retrieved {}", role);
        return role;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        try{
            findById(id);
        } catch (ServiceException e){
            throw new ServiceException("There is no Role to delete with id = "+ id);
        }
        execute(() -> roleRepository.deleteById(id));
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
