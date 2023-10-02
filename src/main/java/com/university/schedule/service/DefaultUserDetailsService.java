package com.university.schedule.service;

import com.university.schedule.converter.UserDetailsMapper;
import com.university.schedule.exception.ServiceException;
import com.university.schedule.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultUserDetailsService implements UserDetailsService {

	private final UserService userService;

	private final UserDetailsMapper userDetailsMapper;


	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user;
		try {
			user = userService.findByEmail(username);
		} catch (ServiceException e) {
			throw new UsernameNotFoundException("No user found with email = " + username, e);
		}
		if ("Admin".equals(user.getRole().getName())) {
			throw new UsernameNotFoundException("User has Admin role, " + username);
		}
		return userDetailsMapper.convertToUserDetails(user);

	}
}
