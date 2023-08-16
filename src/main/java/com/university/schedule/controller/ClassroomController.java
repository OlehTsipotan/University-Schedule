package com.university.schedule.controller;

import com.university.schedule.dto.ClassroomDTO;
import com.university.schedule.mapper.ClassroomMapper;
import com.university.schedule.model.Classroom;
import com.university.schedule.service.ClassroomService;
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
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ClassroomController {

    private final ClassroomService classroomService;

    private final ClassroomMapper classroomMapper;

    @GetMapping("/classrooms")
    public String getAll(Model model, @RequestParam(defaultValue = "id,asc") String[] sort) {
        String sortField = sort[0];
        String sortDirection = sort[1];

        Sort.Direction direction = sortDirection.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort.Order order = new Sort.Order(direction, sortField);

        List<ClassroomDTO> classrooms = classroomService.findAll(Sort.by(order)).stream()
                .map(classroomMapper::convertToDto).toList();


        model.addAttribute("entities", classrooms);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("reverseSortDirection", sortDirection.equals("asc") ? "desc" : "asc");

        return "classrooms";
    }

    @GetMapping("/classrooms/delete/{id}")
    public RedirectView delete(@PathVariable(name = "id") Long id, HttpServletRequest request) {
        classroomService.deleteById(id);

        String referer = request.getHeader("Referer");
        String redirectTo = (referer != null) ? referer : "/classrooms";

        return new RedirectView(redirectTo);
    }

}
