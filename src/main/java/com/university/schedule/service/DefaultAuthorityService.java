package com.university.schedule.service;

import com.university.schedule.converter.ConverterService;
import com.university.schedule.dto.AuthorityDTO;
import com.university.schedule.exception.DeletionFailedException;
import com.university.schedule.exception.ServiceException;
import com.university.schedule.model.Authority;
import com.university.schedule.model.Role;
import com.university.schedule.repository.AuthorityRepository;
import com.university.schedule.validation.AuthorityEntityValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class DefaultAuthorityService implements AuthorityService {

    private final AuthorityRepository authorityRepository;

    private final ConverterService converterService;

    private final AuthorityEntityValidator authorityEntityValidator;

    @Override
    @Transactional
    public Long save(Authority authority) {
        execute(() -> {
            authorityEntityValidator.validate(authority);
            authorityRepository.save(authority);
        });
        log.info("saved {}", authority);
        return authority.getId();
    }


    @Override
    public Authority findById(Long id) {
        Authority authority = execute(() -> authorityRepository.findById(id)).orElseThrow(
            () -> new ServiceException("Authority not found"));
        log.debug("Retrieved {}", authority);
        return authority;
    }

    @Override
    public AuthorityDTO findByIdAsDTO(Long id) {
        Authority authority = execute(() -> authorityRepository.findById(id)).orElseThrow(
            () -> new ServiceException("Authority not found"));
        log.debug("Retrieved {}", authority);
        return convertToDTO(authority);
    }

    @Override
    public Authority findByName(String name) {
        Authority authority = execute(() -> authorityRepository.findByName(name)).orElseThrow(
            () -> new ServiceException("Authority not found"));
        log.debug("Retrieved {}", authority);
        return authority;
    }

    @Override
    public List<Authority> findByRole(Role role) {
        List<Authority> authorities = execute(() -> authorityRepository.findByRoles(role));
        log.debug("Retrieved All {} Authorities", authorities.size());
        return authorities;
    }

    @Override
    public List<Authority> findAll() {
        List<Authority> authorities = execute(() -> authorityRepository.findAll());
        log.debug("Retrieved All {} Authorities", authorities.size());
        return authorities;
    }

    @Override
    public List<AuthorityDTO> findAllAsDTO() {
        List<AuthorityDTO> authorityDTOList =
            execute(() -> authorityRepository.findAll()).stream().map(this::convertToDTO).toList();
        log.debug("Retrieved All {} Authorities", authorityDTOList.size());
        return authorityDTOList;
    }

    @Override
    public List<AuthorityDTO> findAllAsDTO(Pageable pageable) {
        if (pageable == null) {
            throw new IllegalArgumentException("Pageable is null");
        }
        List<AuthorityDTO> authorityDTOList =
            execute(() -> authorityRepository.findAll(pageable)).stream().map(this::convertToDTO).toList();
        log.debug("Retrieved All {} Authorities", authorityDTOList.size());
        return authorityDTOList;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        execute(() -> {
            if (!authorityRepository.existsById(id)) {
                throw new DeletionFailedException("There is no Authority to delete with id = " + id);
            }
            authorityRepository.deleteById(id);
        });
        execute(() -> authorityRepository.deleteById(id));
        log.info("Deleted id = {}", id);

    }

    private AuthorityDTO convertToDTO(Authority source) {
        return converterService.convert(source, AuthorityDTO.class);
    }

    private Authority convertToEntity(AuthorityDTO source) {
        return converterService.convert(source, Authority.class);
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
