package com.university.schedule.controller;

import com.university.schedule.exception.ServiceException;
import com.university.schedule.model.Student;
import com.university.schedule.service.StudentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @GetMapping("/students")
    public String getAll(Model model, @RequestParam(defaultValue = "id,asc") String[] sort) {
        try {
            String sortField = sort[0];
            String sortDirection = sort[1];

            Sort.Direction direction = sortDirection.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
            Sort.Order order = new Sort.Order(direction, sortField);

            List<Student> students = studentService.findAll(Sort.by(order));

            model.addAttribute("students", students);
            model.addAttribute("sortField", sortField);
            model.addAttribute("sortDirection", sortDirection);
            model.addAttribute("reverseSortDirection", sortDirection.equals("asc") ? "desc" : "asc");
        } catch (ServiceException e) {
            model.addAttribute("message", e.getMessage());
        }

        return "students";
    }

    @GetMapping("/students/delete/{id}")
    public String delete(Model model, @PathVariable(name = "id") Long id, HttpServletRequest request, HttpServletResponse response) {
        try {
            studentService.deleteById(id);
        } catch (ServiceException e) {
            log.error("Can't delete by id = " + id);
        }

        String referer = request.getHeader("Referer");
        String redirectTo = (referer != null) ? referer : "/students";

        try {
            response.sendRedirect(redirectTo);
        } catch (IOException e) {
            log.error("Error redirecting back to page: " + redirectTo + ", error: " + e.getMessage());
        }

        return null;
    }
}

