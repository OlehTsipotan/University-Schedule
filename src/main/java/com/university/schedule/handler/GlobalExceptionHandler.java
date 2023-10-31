package com.university.schedule.handler;

import com.university.schedule.exception.RegistrationFailedException;
import com.university.schedule.exception.ValidationException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public String handleException(RuntimeException runtimeException, Model model,
                                  RedirectAttributes redirectAttributes) {
        log.info(runtimeException.getMessage());
        model.addAttribute("exceptionMessage", runtimeException.getMessage());
        return "error";
    }

    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDenied(RuntimeException runtimeException, Model model,
                                     RedirectAttributes redirectAttributes, HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            log.warn("User: " + auth.getName() + " attempted to access the protected URL: " + request.getRequestURI());
        }
        model.addAttribute("exceptionMessage", runtimeException.getMessage());
        return "error";
    }

    @ExceptionHandler(RegistrationFailedException.class)
    public String handleRegistrationException(RegistrationFailedException registrationFailedException,
                                              RedirectAttributes redirectAttributes, WebRequest request) {
        redirectAttributes.addFlashAttribute("registrationServiceError", registrationFailedException.getMessage());

        log.error("RegistrationException occurs, {}", registrationFailedException.getMessage());
        return "redirect:/user/register";
    }

    @ExceptionHandler(ValidationException.class)
    public String handleValidationException(ValidationException validationException,
                                            RedirectAttributes redirectAttributes, WebRequest request) {
        redirectAttributes.addFlashAttribute("validationServiceErrors", validationException.getViolations());

        log.error("ValidationException occurs, ", validationException);
        String referer = request.getHeader("Referer");
        if (referer != null && !referer.isEmpty()) {
            return "redirect:" + referer;
        } else {
            return "redirect:/welcome?redirect";
        }
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public RedirectView handle404Exception(Authentication authentication) {
        if (authentication.getAuthorities().stream()
            .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"))) {
            return new RedirectView("/admin/dashboard");
        }
        return new RedirectView("/welcome");
    }
}
