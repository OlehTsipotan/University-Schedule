package com.university.schedule.handler;

import com.university.schedule.exception.ServiceException;
import com.university.schedule.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;
import org.springframework.web.servlet.view.RedirectView;

import java.security.Principal;

@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final UserService userService;

    @ExceptionHandler({ServiceException.class, IllegalArgumentException.class})
    public String handleException(RuntimeException e, Model model) {
        log.error("Error occurs, ", e);
        model.addAttribute("message", e.getMessage());
        return "error";
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public RedirectView handle404Exception(NoHandlerFoundException ex, Principal principal) {
        if (userService.findByEmail(principal.getName()).getRole().getName().equals("ADMIN")){
            return new RedirectView("/admin/dashboard");
        }
        return new RedirectView("/welcome?redirect");
    }
}
