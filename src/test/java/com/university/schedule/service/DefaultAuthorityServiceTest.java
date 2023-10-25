package com.university.schedule.service;

import com.university.schedule.converter.ConverterService;
import com.university.schedule.dto.AuthorityDTO;
import com.university.schedule.exception.DeletionFailedException;
import com.university.schedule.exception.ServiceException;
import com.university.schedule.exception.ValidationException;
import com.university.schedule.model.Authority;
import com.university.schedule.model.Role;
import com.university.schedule.pageable.OffsetBasedPageRequest;
import com.university.schedule.repository.AuthorityRepository;
import com.university.schedule.validation.AuthorityEntityValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.Mock;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.query.BadJpqlGrammarException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class DefaultAuthorityServiceTest {

    private DefaultAuthorityService defaultAuthorityService;

    @Mock
    private AuthorityRepository authorityRepository;

    @Mock
    private ConverterService converterService;

    @Mock
    private AuthorityEntityValidator authorityEntityValidator;

    @BeforeEach
    public void setUp() {
        defaultAuthorityService =
            new DefaultAuthorityService(authorityRepository, converterService, authorityEntityValidator);
    }

    @ParameterizedTest
    @NullSource
    public void save_whenAuthorityIsNull_throwIllegalArgumentException(Authority nullAuthority) {
        doThrow(IllegalArgumentException.class).when(authorityEntityValidator).validate(nullAuthority);
        assertThrows(IllegalArgumentException.class, () -> defaultAuthorityService.save(nullAuthority));

        verify(authorityEntityValidator).validate(nullAuthority);
        verifyNoInteractions(authorityRepository);
    }

    @Test
    public void save_whenAuthorityIsValid_success() {
        Authority authority = Authority.builder().id(1L).name("name").build();
        when(authorityRepository.save(authority)).thenReturn(authority);
        assertDoesNotThrow(() -> defaultAuthorityService.save(authority));

        verify(authorityEntityValidator).validate(authority);
        verify(authorityRepository).save(authority);
    }

    @Test
    public void save_whenAuthorityIsNotValid_throwValidationException() {
        Authority authority = Authority.builder().id(1L).name("name").build();
        doThrow(ValidationException.class).when(authorityEntityValidator).validate(authority);
        assertThrows(ValidationException.class, () -> defaultAuthorityService.save(authority));

        verify(authorityEntityValidator).validate(authority);
        verifyNoInteractions(authorityRepository);
    }

    @Test
    public void save_whenAuthorityRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        Authority authority = Authority.builder().id(1L).name("name").build();
        when(authorityRepository.save(authority)).thenThrow(DuplicateKeyException.class);
        assertThrows(ServiceException.class, () -> defaultAuthorityService.save(authority));

        verify(authorityEntityValidator).validate(authority);
        verify(authorityRepository).save(authority);
    }

    @Test
    public void findById_whenAuthorityRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        Long id = 1L;
        when(authorityRepository.findById(id)).thenThrow(BadJpqlGrammarException.class);
        assertThrows(ServiceException.class, () -> defaultAuthorityService.findById(id));

        verify(authorityRepository).findById(id);
    }

    @Test
    public void findById_whenAuthorityRepositoryReturnEmptyOptional_throwServiceException() {
        Long id = 1L;
        when(authorityRepository.findById(id)).thenReturn(java.util.Optional.empty());
        assertThrows(ServiceException.class, () -> defaultAuthorityService.findById(id));

        verify(authorityRepository).findById(id);
    }

    @ParameterizedTest
    @NullSource
    public void findById_whenIdIsNull_throwServiceException(Long nullId) {
        assertThrows(ServiceException.class, () -> defaultAuthorityService.findById(nullId));
        verify(authorityRepository).findById(nullId);
    }

    @Test
    public void findById_whenIdIsValid_success() {
        Long id = 1L;
        Authority authority = Authority.builder().id(id).name("name").build();
        when(authorityRepository.findById(id)).thenReturn(java.util.Optional.of(authority));
        assertEquals(authority, defaultAuthorityService.findById(id));

        verify(authorityRepository).findById(id);
    }

    @Test
    public void findByIdAsDTO_whenAuthorityRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        Long id = 1L;
        when(authorityRepository.findById(id)).thenThrow(BadJpqlGrammarException.class);
        assertThrows(ServiceException.class, () -> defaultAuthorityService.findByIdAsDTO(id));

        verify(authorityRepository).findById(id);
    }

    @Test
    public void findByIdAsDTO_whenAuthorityRepositoryReturnEmptyOptional_throwServiceException() {
        Long id = 1L;
        when(authorityRepository.findById(id)).thenReturn(java.util.Optional.empty());
        assertThrows(ServiceException.class, () -> defaultAuthorityService.findByIdAsDTO(id));

        verify(authorityRepository).findById(id);
    }

    @Test
    public void findByIdAsDTO_whenIdIsValid_success() {
        Long id = 1L;
        Authority authority = Authority.builder().id(id).name("name").build();
        AuthorityDTO authorityDTO = AuthorityDTO.builder().id(id).name("name").build();
        when(authorityRepository.findById(id)).thenReturn(java.util.Optional.of(authority));
        when(converterService.convert(authority, AuthorityDTO.class)).thenReturn(authorityDTO);

        assertEquals(authorityDTO, defaultAuthorityService.findByIdAsDTO(id));

        verify(converterService).convert(authority, AuthorityDTO.class);
        verify(authorityRepository).findById(id);
    }

    @Test
    public void findByName_whenAuthorityRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        String name = "name";
        when(authorityRepository.findByName(name)).thenThrow(BadJpqlGrammarException.class);
        assertThrows(ServiceException.class, () -> defaultAuthorityService.findByName(name));

        verify(authorityRepository).findByName(name);
    }

    @Test
    public void findByName_whenAuthorityRepositoryReturnEmptyOptional_throwServiceException() {
        String name = "name";
        when(authorityRepository.findByName(name)).thenReturn(java.util.Optional.empty());
        assertThrows(ServiceException.class, () -> defaultAuthorityService.findByName(name));

        verify(authorityRepository).findByName(name);
    }

    @Test
    public void findByName_whenNameIsValid_success() {
        String name = "name";
        Authority authority = Authority.builder().id(1L).name(name).build();
        when(authorityRepository.findByName(name)).thenReturn(java.util.Optional.of(authority));
        assertEquals(authority, defaultAuthorityService.findByName(name));

        verify(authorityRepository).findByName(name);
    }

    @Test
    public void findByRole_whenAuthorityRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        Role role = new Role("name");
        when(authorityRepository.findByRoles(role)).thenThrow(BadJpqlGrammarException.class);
        assertThrows(ServiceException.class, () -> defaultAuthorityService.findByRole(role));

        verify(authorityRepository).findByRoles(role);
    }

    @Test
    public void findByRole_whenRoleIsValid_success() {
        Role role = new Role("name");
        Authority authority = Authority.builder().id(1L).name("name").build();
        when(authorityRepository.findByRoles(role)).thenReturn(java.util.List.of(authority));
        assertEquals(java.util.List.of(authority), defaultAuthorityService.findByRole(role));

        verify(authorityRepository).findByRoles(role);
    }

    @Test
    public void findAll_whenAuthorityRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        when(authorityRepository.findAll()).thenThrow(BadJpqlGrammarException.class);
        assertThrows(ServiceException.class, () -> defaultAuthorityService.findAll());

        verify(authorityRepository).findAll();
    }

    @Test
    public void findAll_whenAuthorityRepositoryReturnEmptyList_returnEmptyList() {
        when(authorityRepository.findAll()).thenReturn(java.util.List.of());
        assertEquals(new ArrayList<>(), defaultAuthorityService.findAll());

        verify(authorityRepository).findAll();
    }

    @Test
    public void findAll_whenAuthorityRepositoryReturnList_returnList() {
        Authority authority = Authority.builder().id(1L).name("name").build();
        when(authorityRepository.findAll()).thenReturn(java.util.List.of(authority));
        assertEquals(List.of(authority), defaultAuthorityService.findAll());

        verify(authorityRepository).findAll();
    }

    @Test
    public void findAllAsDTO_whenAuthorityRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        when(authorityRepository.findAll()).thenThrow(BadJpqlGrammarException.class);
        assertThrows(ServiceException.class, () -> defaultAuthorityService.findAllAsDTO());

        verify(authorityRepository).findAll();
    }

    @Test
    public void findAllAsDTO_whenAuthorityRepositoryReturnEmptyList_returnEmptyList() {
        when(authorityRepository.findAll()).thenReturn(java.util.List.of());
        assertEquals(new ArrayList<>(), defaultAuthorityService.findAllAsDTO());

        verify(authorityRepository).findAll();
    }

    @Test
    public void findAllAsDTO_whenAuthorityRepositoryReturnList_returnList() {
        Authority authority = Authority.builder().id(1L).name("name").build();
        AuthorityDTO authorityDTO = AuthorityDTO.builder().id(1L).name("name").build();
        when(authorityRepository.findAll()).thenReturn(java.util.List.of(authority));
        when(converterService.convert(authority, AuthorityDTO.class)).thenReturn(authorityDTO);
        assertEquals(List.of(authorityDTO), defaultAuthorityService.findAllAsDTO());

        verify(authorityRepository).findAll();
        verify(converterService).convert(authority, AuthorityDTO.class);
    }

    @Test
    public void findAllAsDTO_whenPageableIsNull_throwIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> defaultAuthorityService.findAllAsDTO(null));

        verifyNoInteractions(authorityRepository);
    }

    @Test
    public void findAllAsDTO_whenPageableIsValid_success() {
        Authority authority = Authority.builder().id(1L).name("name").build();
        AuthorityDTO authorityDTO = AuthorityDTO.builder().id(1L).name("name").build();
        Pageable pageable = OffsetBasedPageRequest.of(1, 1);

        when(authorityRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(authority)));
        when(converterService.convert(authority, AuthorityDTO.class)).thenReturn(authorityDTO);

        assertEquals(List.of(authorityDTO), defaultAuthorityService.findAllAsDTO(pageable));

        verify(authorityRepository).findAll(any(Pageable.class));
        verify(converterService).convert(authority, AuthorityDTO.class);

    }

    @Test
    public void deleteById_whenAuthorityRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        Long id = 1L;
        when(authorityRepository.existsById(id)).thenReturn(true);
        doThrow(BadJpqlGrammarException.class).when(authorityRepository).deleteById(id);
        assertThrows(ServiceException.class, () -> defaultAuthorityService.deleteById(id));

        verify(authorityRepository).deleteById(id);
    }

    @Test
    public void deleteById_whenAuthorityDoNotExist_throwDeletionFailedException() {
        Long id = 1L;
        when(authorityRepository.existsById(id)).thenReturn(false);
        assertThrows(DeletionFailedException.class, () -> defaultAuthorityService.deleteById(id));

        verify(authorityRepository).existsById(id);
        verifyNoMoreInteractions(authorityRepository);
    }

    @Test
    public void deleteById_success() {
        Long id = 1L;
        when(authorityRepository.existsById(id)).thenReturn(true);

        assertDoesNotThrow(() -> defaultAuthorityService.deleteById(id));

        verify(authorityRepository).existsById(id);
        verify(authorityRepository).deleteById(id);
    }


}
