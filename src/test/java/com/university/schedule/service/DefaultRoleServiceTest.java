package com.university.schedule.service;

import com.university.schedule.converter.ConverterService;
import com.university.schedule.dto.RoleDTO;
import com.university.schedule.exception.ServiceException;
import com.university.schedule.exception.ValidationException;
import com.university.schedule.model.Role;
import com.university.schedule.repository.RoleRepository;
import com.university.schedule.validation.RoleEntityValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.Mock;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.query.BadJpqlGrammarException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class DefaultRoleServiceTest {

    private DefaultRoleService defaultRoleService;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private ConverterService converterService;

    @Mock
    private RoleEntityValidator roleEntityValidator;

    @BeforeEach
    public void setUp() {
        defaultRoleService = new DefaultRoleService(roleRepository, converterService, roleEntityValidator);
    }

    @Test
    public void findAllAsDTO_success() {
        Role role = Role.builder().id(1L).build();
        RoleDTO roleDTO = RoleDTO.builder().id(1L).build();

        when(roleRepository.findAll()).thenReturn(List.of(role));
        when(converterService.convert(role, RoleDTO.class)).thenReturn(roleDTO);

        assertEquals(List.of(roleDTO), defaultRoleService.findAllAsDTO());

        verify(roleRepository).findAll();
        verify(converterService).convert(role, RoleDTO.class);
    }

    @Test
    public void findAllAsDTO_whenNoRoles() {
        when(roleRepository.findAll()).thenReturn(List.of());

        assertEquals(List.of(), defaultRoleService.findAllAsDTO());

        verify(roleRepository).findAll();
    }

    @Test
    public void findAllAsDTO_whenRoleRepositoryThrowsExceptionExtendsDataAccessException_thenServiceException() {
        when(roleRepository.findAll()).thenThrow(BadJpqlGrammarException.class);

        assertThrows(ServiceException.class, () -> defaultRoleService.findAllAsDTO());

        verify(roleRepository).findAll();
    }

    @Test
    public void findAllForRegistrationAsDTO_success() {
        Role role = Role.builder().id(1L).name("Admin").build();
        RoleDTO roleDTO = RoleDTO.builder().id(1L).name("Admin").build();

        when(roleRepository.findAll()).thenReturn(List.of(role));
        when(converterService.convert(role, RoleDTO.class)).thenReturn(roleDTO);

        assertEquals(List.of(), defaultRoleService.findAllForRegistrationAsDTO());

        verify(roleRepository).findAll();
        verify(converterService).convert(role, RoleDTO.class);
    }

    @Test
    public void findAllForRegistrationAsDTO_whenNoRoles() {
        when(roleRepository.findAll()).thenReturn(List.of());

        assertEquals(List.of(), defaultRoleService.findAllForRegistrationAsDTO());

        verify(roleRepository).findAll();
    }

    @Test
    public void findAllForRegistrationAsDTO_whenRoleRepositoryThrowsExceptionExtendsDataAccessException_thenServiceException() {
        when(roleRepository.findAll()).thenThrow(BadJpqlGrammarException.class);

        assertThrows(ServiceException.class, () -> defaultRoleService.findAllForRegistrationAsDTO());

        verify(roleRepository).findAll();
    }

    @Test
    public void findAllAsDTO_whenPageableIsValid_success() {
        Role role = Role.builder().id(1L).build();
        RoleDTO roleDTO = RoleDTO.builder().id(1L).build();

        Pageable pageable = Pageable.unpaged();

        when(roleRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(role)));
        when(converterService.convert(role, RoleDTO.class)).thenReturn(roleDTO);

        assertEquals(List.of(roleDTO), defaultRoleService.findAllAsDTO(pageable));

        verify(roleRepository).findAll(pageable);
        verify(converterService).convert(role, RoleDTO.class);
    }

    @ParameterizedTest
    @NullSource
    public void findAllAsDTO_whenPageableIsNull_throwIllegalArgumentException(Pageable nullPageable) {
        assertThrows(IllegalArgumentException.class, () -> defaultRoleService.findAllAsDTO(nullPageable));
    }

    @Test
    public void findAllAsDTOPageable_whenRoleRepositoryThrowsExceptionExtendsDataAccessException_thenServiceException() {
        Pageable pageable = Pageable.unpaged();

        when(roleRepository.findAll(pageable)).thenThrow(BadJpqlGrammarException.class);

        assertThrows(ServiceException.class, () -> defaultRoleService.findAllAsDTO(pageable));

        verify(roleRepository).findAll(pageable);
    }

    @Test
    public void save_whenRoleIsValid_success() {
        Role role = Role.builder().id(1L).build();

        assertEquals(1L, defaultRoleService.save(role));

        verify(roleEntityValidator).validate(role);
        verify(roleRepository).save(role);
    }

    @ParameterizedTest
    @NullSource
    public void save_whenRoleIsNull_throwIllegalArgumentException(Role nullRole) {
        doThrow(IllegalArgumentException.class).when(roleEntityValidator).validate(nullRole);

        assertThrows(IllegalArgumentException.class, () -> defaultRoleService.save(nullRole));
    }

    @Test
    public void save_whenRoleIsInvalid_throwValidationException() {
        Role role = Role.builder().id(1L).build();

        doThrow(ValidationException.class).when(roleEntityValidator).validate(role);

        assertThrows(ValidationException.class, () -> defaultRoleService.save(role));
    }

    @Test
    public void save_whenRoleRepositoryThrowsExceptionExtendsDataAccessException_thenServiceException() {
        Role role = Role.builder().id(1L).build();

        doThrow(BadJpqlGrammarException.class).when(roleRepository).save(role);

        assertThrows(ServiceException.class, () -> defaultRoleService.save(role));
    }

    @Test
    public void save_whenRoleDTOIsValid_success() {
        RoleDTO roleDTO = RoleDTO.builder().id(1L).build();
        Role role = Role.builder().id(1L).build();

        when(converterService.convert(roleDTO, Role.class)).thenReturn(role);

        assertEquals(1L, defaultRoleService.save(roleDTO));

        verify(roleEntityValidator).validate(role);
        verify(roleRepository).save(role);
    }

    @Test
    public void save_whenRoleDTOIsInvalid_throwValidationException() {
        RoleDTO roleDTO = RoleDTO.builder().id(1L).build();
        Role role = Role.builder().id(1L).build();

        when(converterService.convert(roleDTO, Role.class)).thenReturn(role);
        doThrow(ValidationException.class).when(roleEntityValidator).validate(role);

        assertThrows(ValidationException.class, () -> defaultRoleService.save(roleDTO));
    }

    @ParameterizedTest
    @NullSource
    public void save_whenRoleDTOIsNull_throwIllegalArgumentException(RoleDTO nullRoleDTO) {
        when(converterService.convert(null, Role.class)).thenThrow(IllegalArgumentException.class);
        assertThrows(IllegalArgumentException.class, () -> defaultRoleService.save(nullRoleDTO));

        verify(converterService).convert(null, Role.class);
    }

    @Test
    public void findById_success() {
        Role role = Role.builder().id(1L).build();

        when(roleRepository.findById(1L)).thenReturn(java.util.Optional.of(role));

        assertEquals(role, defaultRoleService.findById(1L));

        verify(roleRepository).findById(1L);
    }

    @ParameterizedTest
    @NullSource
    public void findById_whenIdIsNull_throwServiceException(Long nullId) {
        when(roleRepository.findById(null)).thenThrow(InvalidDataAccessApiUsageException.class);
        assertThrows(ServiceException.class, () -> defaultRoleService.findById(nullId));
    }

    @Test
    public void findByIdAsDTO_success() {
        Role role = Role.builder().id(1L).build();
        RoleDTO roleDTO = RoleDTO.builder().id(1L).build();

        when(roleRepository.findById(1L)).thenReturn(java.util.Optional.of(role));
        when(converterService.convert(role, RoleDTO.class)).thenReturn(roleDTO);

        assertEquals(roleDTO, defaultRoleService.findByIdAsDTO(1L));

        verify(roleRepository).findById(1L);
        verify(converterService).convert(role, RoleDTO.class);
    }

    @ParameterizedTest
    @NullSource
    public void findByIdAsDTO_whenIdIsNull_throwServiceException(Long nullId) {
        when(roleRepository.findById(null)).thenThrow(InvalidDataAccessApiUsageException.class);
        assertThrows(ServiceException.class, () -> defaultRoleService.findByIdAsDTO(nullId));
    }

    @Test
    public void findByName_success() {
        Role role = Role.builder().id(1L).build();

        when(roleRepository.findByName("Admin")).thenReturn(java.util.Optional.of(role));

        assertEquals(role, defaultRoleService.findByName("Admin"));

        verify(roleRepository).findByName("Admin");
    }

    @ParameterizedTest
    @NullSource
    public void findByName_whenNameIsNull_throwServiceException(String nullName) {
        when(roleRepository.findByName(null)).thenThrow(InvalidDataAccessApiUsageException.class);
        assertThrows(ServiceException.class, () -> defaultRoleService.findByName(nullName));
    }

    @Test
    public void deleteById_success() {
        when(roleRepository.existsById(1L)).thenReturn(true);

        defaultRoleService.deleteById(1L);

        verify(roleRepository).existsById(1L);
        verify(roleRepository).deleteById(1L);
    }

    @Test
    public void deleteById_whenRoleRepositoryThrowsExceptionExtendsDataAccessException_thenServiceException() {
        when(roleRepository.existsById(1L)).thenReturn(true);
        doThrow(BadJpqlGrammarException.class).when(roleRepository).deleteById(1L);

        assertThrows(ServiceException.class, () -> defaultRoleService.deleteById(1L));

        verify(roleRepository).existsById(1L);
        verify(roleRepository).deleteById(1L);
    }

    @Test
    public void deleteById_whenRoleDoesNotExist_throwServiceException() {
        when(roleRepository.existsById(1L)).thenReturn(false);

        assertThrows(ServiceException.class, () -> defaultRoleService.deleteById(1L));

        verify(roleRepository).existsById(1L);
    }

    @Test
    public void deleteById_whenIdIsNull_throwServiceException() {
        when(roleRepository.existsById(null)).thenThrow(InvalidDataAccessApiUsageException.class);

        assertThrows(ServiceException.class, () -> defaultRoleService.deleteById(null));

        verify(roleRepository).existsById(null);
    }
}
