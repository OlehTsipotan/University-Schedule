package com.university.schedule.converter;

import com.university.schedule.dto.AuthorityDTO;
import com.university.schedule.model.Authority;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AuthorityEntityToAuthorityDTOConverterTest {

    private AuthorityEntityToAuthorityDTOConverter authorityEntityToAuthorityDTOConverter;

    @BeforeEach
    public void setUp() {
        authorityEntityToAuthorityDTOConverter = new AuthorityEntityToAuthorityDTOConverter();
    }

    @Test
    public void convert_whenAuthorityDTOIsValid_success() {
        AuthorityDTO authorityDTO = AuthorityDTO.builder().id(1L).name("name").build();
        Authority authority = Authority.builder().id(1L).name("name").build();
        assertEquals(authorityDTO, authorityEntityToAuthorityDTOConverter.convert(authority));
    }

    @ParameterizedTest
    @NullSource
    public void convert_whenAuthorityIsNull_throwIllegalArgumentException(Authority nullAuthority) {
        assertThrows(IllegalArgumentException.class,
            () -> authorityEntityToAuthorityDTOConverter.convert(nullAuthority));
    }
}
