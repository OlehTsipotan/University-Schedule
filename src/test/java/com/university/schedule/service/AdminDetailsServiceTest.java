package com.university.schedule.service;

import com.university.schedule.converter.UserDetailsMapper;
import com.university.schedule.exception.ServiceException;
import com.university.schedule.model.Role;
import com.university.schedule.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.Mock;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class AdminDetailsServiceTest {

    private AdminDetailsService adminDetailsService;

    @Mock
    private UserService userService;

    @Mock
    private UserDetailsMapper userDetailsMapper;

    @BeforeEach
    public void setUp() {
        adminDetailsService = new AdminDetailsService(userService, userDetailsMapper);
    }

    @ParameterizedTest
    @CsvSource({"email"})
    public void loadUserByUsername_whenUserIsAdmin_success(String email) {
        User user = User.builder().role(new Role("Admin")).build();
        when(userService.findByEmail(email)).thenReturn(user);

        adminDetailsService.loadUserByUsername(email);

        verify(userService).findByEmail(email);
        verify(userDetailsMapper).convertToUserDetails(user);
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void loadUserByUsername_whenUsernameIsNullOrEmpty_throwsUsernameNotFoundException(String nullUsername) {
        assertThrows(UsernameNotFoundException.class, () -> adminDetailsService.loadUserByUsername(nullUsername));
    }

    @ParameterizedTest
    @CsvSource({"email"})
    public void loadUserByUsername_whenUserIsNotAdmin_throwsUsernameNotFoundException(String email) {
        User user = User.builder().role(new Role("NotAdmin")).build();
        when(userService.findByEmail(email)).thenReturn(user);

        assertThrows(UsernameNotFoundException.class, () -> adminDetailsService.loadUserByUsername(email));

        verify(userService).findByEmail(email);
        verifyNoInteractions(userDetailsMapper);
    }

    @ParameterizedTest
    @CsvSource({"email"})
    public void loadUserByUsername_whenUserServiceThrowsServiceException_throwsUsernameNotFoundException(String email) {
        when(userService.findByEmail(email)).thenThrow(new ServiceException("some exception"));

        assertThrows(UsernameNotFoundException.class, () -> adminDetailsService.loadUserByUsername(email));

        verify(userService).findByEmail(email);
        verifyNoInteractions(userDetailsMapper);
    }

}
