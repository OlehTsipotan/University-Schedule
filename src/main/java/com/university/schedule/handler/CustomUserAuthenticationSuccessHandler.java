package com.university.schedule.handler;

import com.university.schedule.dto.UserDTO;
import com.university.schedule.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomUserAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        UserDTO userDTO = userService.findByEmailAsDTO(authentication.getName());
        log.debug("Logged as user, {}: {}", userDTO.getRoleDTO().getName(), authentication.getName());

        response.sendRedirect("/welcome");
    }
}
