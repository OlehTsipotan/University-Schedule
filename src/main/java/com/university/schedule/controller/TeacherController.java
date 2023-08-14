package com.university.schedule.controller;

import com.university.schedule.exception.RedirectionException;
import com.university.schedule.exception.ServiceException;
import com.university.schedule.model.Teacher;
import com.university.schedule.service.TeacherService;
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
public class TeacherController {

    private final TeacherService teacherService;

    @GetMapping("/teachers")
    public String getAll(Model model, @RequestParam(defaultValue = "id,asc") String[] sort) {
        String sortField = sort[0];
        String sortDirection = sort[1];

        Sort.Direction direction = sortDirection.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort.Order order = new Sort.Order(direction, sortField);

        List<Teacher> teachers = teacherService.findAll(Sort.by(order));

        model.addAttribute("teachers", teachers);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("reverseSortDirection", sortDirection.equals("asc") ? "desc" : "asc");

        return "teachers";
    }

    @GetMapping("/teachers/delete/{id}")
    public String delete(Model model, @PathVariable(name = "id") Long id,
                         HttpServletRequest request, HttpServletResponse response) throws IOException {
        teacherService.deleteById(id);

        String referer = request.getHeader("Referer");
        String redirectTo = (referer != null) ? referer : "/teachers";

        try {
            response.sendRedirect(redirectTo);
        } catch (IOException e){
            throw new RedirectionException("Can`t redirect to " + redirectTo, e);
        }

        return null;
    }
}
