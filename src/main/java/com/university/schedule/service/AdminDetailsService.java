package com.university.schedule.service;

import com.university.schedule.converter.UserDetailsMapper;
import com.university.schedule.exception.ServiceException;
import com.university.schedule.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class AdminDetailsService implements UserDetailsService {

    private final UserService userService;

    private final UserDetailsMapper userDetailsMapper;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (StringUtils.isEmpty(username)) {
            throw new UsernameNotFoundException("No user found with empty email");
        }
        User user;
        try {
            user = userService.findByEmail(username);
        } catch (ServiceException e) {
            throw new UsernameNotFoundException("No user found with email = " + username, e);
        }
        if ("Admin".equals(user.getRole().getName())) {
            return userDetailsMapper.convertToUserDetails(user);
        }
        throw new UsernameNotFoundException("User has not Admin role, " + username);

    }
}
