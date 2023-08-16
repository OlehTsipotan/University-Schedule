package com.university.schedule.controller;

import com.university.schedule.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final String NO_PAGE_FOUND_MSG = "No page where found by this query.";

    @ExceptionHandler(ServiceException.class)
    public String handleException(RuntimeException e, Model model) {
        log.error("Error occurs, ", e);
        model.addAttribute("message", e.getMessage());
        return "error";
    }
    @ExceptionHandler(NoHandlerFoundException.class)
    public String handle404Exception(Model model) {
        model.addAttribute("message", NO_PAGE_FOUND_MSG);
        return "error";
    }
}
