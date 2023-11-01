package com.university.schedule.converter;

import com.university.schedule.dto.AuthorityDTO;
import com.university.schedule.dto.RoleDTO;
import com.university.schedule.model.Authority;
import com.university.schedule.model.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class RoleEntityToRoleDTOConverterTest {

    private RoleEntityToRoleDTOConverter roleEntityToRoleDTOConverter;

    @BeforeEach
    public void setUp() {
        roleEntityToRoleDTOConverter = new RoleEntityToRoleDTOConverter();
    }

    @ParameterizedTest
    @NullSource
    public void convert_whenRoleIsNull_throwIllegalArgumentException(Role nullRole) {
        assertThrows(IllegalArgumentException.class, () -> roleEntityToRoleDTOConverter.convert(nullRole));
    }

    @Test
    public void convert_success() {
        Authority authority = Authority.builder().id(1L).name("authority").build();
        Role role = Role.builder().id(1L).name("role").authorities(Set.of(authority)).build();

        AuthorityDTO authorityDTO = new AuthorityDTO(1L, "authority");
        RoleDTO roleDTO = new RoleDTO(1L, "role", List.of(authorityDTO));

        RoleDTO roleDTOConverted = roleEntityToRoleDTOConverter.convert(role);

        assertEquals(roleDTOConverted.getId(), roleDTO.getId());
        assertEquals(roleDTOConverted.getName(), roleDTO.getName());
        assertEquals(roleDTOConverted.getAuthorityDTOS(), roleDTO.getAuthorityDTOS());
    }

    @Test
    public void convert_whenRoleWithNullFields_success() {
        Role role = new Role();

        RoleDTO roleDTOConverted = roleEntityToRoleDTOConverter.convert(role);

        assertNull(roleDTOConverted.getId());
        assertNull(roleDTOConverted.getName());
        assertNull(roleDTOConverted.getAuthorityDTOS());
    }
}
