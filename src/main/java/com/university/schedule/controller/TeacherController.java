package com.university.schedule.controller;

import com.university.schedule.exception.ServiceException;
import com.university.schedule.model.Teacher;
import com.university.schedule.service.TeacherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;

    @GetMapping("/teachers")
    public String getAll(Model model, @RequestParam(defaultValue = "id,asc") String[] sort) {
        try {
            String sortField = sort[0];
            String sortDirection = sort[1];

            Sort.Direction direction = sortDirection.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
            Sort.Order order = new Sort.Order(direction, sortField);

            List<Teacher> teachers = teacherService.findAll(Sort.by(order));

            model.addAttribute("teachers", teachers);
            model.addAttribute("sortField", sortField);
            model.addAttribute("sortDirection", sortDirection);
            model.addAttribute("reverseSortDirection", sortDirection.equals("asc") ? "desc" : "asc");
        } catch (ServiceException e) {
            model.addAttribute("message", e.getMessage());
        }

        return "teachers";
    }
}
