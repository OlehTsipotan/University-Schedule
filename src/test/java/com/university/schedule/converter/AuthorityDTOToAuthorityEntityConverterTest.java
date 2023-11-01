package com.university.schedule.converter;

import com.university.schedule.dto.AuthorityDTO;
import com.university.schedule.model.Authority;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AuthorityDTOToAuthorityEntityConverterTest {

    private AuthorityDTOToAuthorityEntityConverter authorityDTOToAuthorityEntityConverter;

    @BeforeEach
    public void setUp() {
        authorityDTOToAuthorityEntityConverter = new AuthorityDTOToAuthorityEntityConverter();
    }

    @Test
    public void convert_whenAuthorityDTOIsValid_success() {
        AuthorityDTO authorityDTO = AuthorityDTO.builder().id(1L).name("name").build();
        Authority authority = Authority.builder().id(1L).name("name").build();
        assertEquals(authority, authorityDTOToAuthorityEntityConverter.convert(authorityDTO));
    }

    @ParameterizedTest
    @NullSource
    public void convert_whenAuthorityDTOIsNull_throwIllegalArgumentException(AuthorityDTO nullAuthorityDTO) {
        assertThrows(IllegalArgumentException.class,
            () -> authorityDTOToAuthorityEntityConverter.convert(nullAuthorityDTO));
    }
}
