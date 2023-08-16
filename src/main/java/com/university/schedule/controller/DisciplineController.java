package com.university.schedule.controller;

import com.university.schedule.model.Discipline;
import com.university.schedule.service.DisciplineService;
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

import java.io.IOException;
import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class DisciplineController {

    private final DisciplineService disciplineService;

    @GetMapping("/disciplines")
    public String getAll(Model model, @RequestParam(defaultValue = "id,asc") String[] sort) {

        String sortField = sort[0];
        String sortDirection = sort[1];

        Sort.Direction direction = sortDirection.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort.Order order = new Sort.Order(direction, sortField);

        List<Discipline> disciplines = disciplineService.findAll(Sort.by(order));

        model.addAttribute("entities", disciplines);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("reverseSortDirection", sortDirection.equals("asc") ? "desc" : "asc");

        return "disciplines";
    }

    @GetMapping("/disciplines/delete/{id}")
    public RedirectView delete(@PathVariable(name = "id") Long id,
                         HttpServletRequest request) {
        disciplineService.deleteById(id);

        String referer = request.getHeader("Referer");
        String redirectTo = (referer != null) ? referer : "/disciplines";


        return new RedirectView(redirectTo);
    }
}
