package com.university.schedule.handler;

import com.university.schedule.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.view.RedirectView;


@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    @ExceptionHandler({ServiceException.class, IllegalArgumentException.class})
    public String handleException(RuntimeException e, Model model) {
        log.error("Error occurs, ", e);
        model.addAttribute("message", e.getMessage());
        return "error";
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public RedirectView handle404Exception(Authentication authentication) {
        if (authentication.getAuthorities().stream().anyMatch(authority ->
                authority.getAuthority().equals("ROLE_ADMIN"))) {
            return new RedirectView("/admin/dashboard");
        }
        return new RedirectView("/welcome?redirect");
    }
}
