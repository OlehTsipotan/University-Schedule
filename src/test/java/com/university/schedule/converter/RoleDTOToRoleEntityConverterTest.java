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

public class RoleDTOToRoleEntityConverterTest {

    private RoleDTOToRoleEntityConverter roleDTOToRoleEntityConverter;

    @BeforeEach
    public void setUp() {
        roleDTOToRoleEntityConverter = new RoleDTOToRoleEntityConverter();
    }

    @ParameterizedTest
    @NullSource
    public void convert_whenRoleDTOIsNull_throwIllegalArgumentException(RoleDTO nullRoleDTO) {
        assertThrows(IllegalArgumentException.class, () -> roleDTOToRoleEntityConverter.convert(nullRoleDTO));
    }

    @Test
    public void convert_success() {
        Authority authority = Authority.builder().id(1L).name("authority").build();
        Role role = Role.builder().id(1L).name("role").authorities(Set.of(authority)).build();

        AuthorityDTO authorityDTO = new AuthorityDTO(1L, "authority");
        RoleDTO roleDTO = new RoleDTO(1L, "role", List.of(authorityDTO));

        Role roleConverted = roleDTOToRoleEntityConverter.convert(roleDTO);

        assertEquals(roleConverted.getId(), role.getId());
        assertEquals(roleConverted.getName(), role.getName());
        assertEquals(roleConverted.getAuthorities(), role.getAuthorities());
    }

    @Test
    public void convert_whenRoleDTOWithNullFields_success() {
        RoleDTO roleDTO = new RoleDTO();

        Role roleConverted = roleDTOToRoleEntityConverter.convert(roleDTO);

        assertNull(roleConverted.getId());
        assertNull(roleConverted.getName());
        assertNull(roleConverted.getAuthorities());
    }
}
