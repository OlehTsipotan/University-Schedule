package com.university.schedule.controller;

import com.university.schedule.exception.ServiceException;
import com.university.schedule.exception.ValidationException;
import com.university.schedule.model.Course;
import com.university.schedule.model.Discipline;
import com.university.schedule.pageable.OffsetBasedPageRequest;
import com.university.schedule.service.DisciplineService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class DisciplineRecordsController {

    private final DisciplineService disciplineService;

    private static final String UPDATE_FORM_TEMPLATE = "disciplinesUpdateForm";

    @Secured("VIEW_DISCIPLINES")
    @GetMapping("/disciplines")
    public String getAll(Model model,
                         @RequestParam(defaultValue = "100") int limit,
                         @RequestParam(defaultValue = "0") int offset,
                         @RequestParam(defaultValue = "id,asc") String[] sort) {

        String sortField = sort[0];
        String sortDirection = sort[1];

        Sort.Direction direction = sortDirection.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort.Order order = new Sort.Order(direction, sortField);

        Pageable pageable = OffsetBasedPageRequest.of(limit, offset, Sort.by(order));
        List<Discipline> disciplines = disciplineService.findAll(pageable).toList();

        model.addAttribute("entities", disciplines);
        model.addAttribute("currentLimit", limit);
        model.addAttribute("currentOffset", offset);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("reverseSortDirection", sortDirection.equals("asc") ? "desc" : "asc");

        return "disciplines";
    }

    @Secured("EDIT_DISCIPLINES")
    @GetMapping("/disciplines/delete/{id}")
    public RedirectView delete(@PathVariable(name = "id") Long id,
                         HttpServletRequest request) {
        disciplineService.deleteById(id);

        String referer = request.getHeader("Referer");
        String redirectTo = (referer != null) ? referer : "/disciplines";


        return new RedirectView(redirectTo);
    }

    @Secured("EDIT_DISCIPLINES")
    @GetMapping("/disciplines/update/{id}")
    public String getUpdateForm(@PathVariable(name = "id") Long id, Model model, Discipline discipline) {
        Discipline disciplineToDisplay = disciplineService.findById(id);
        model.addAttribute("entity", disciplineToDisplay);

        return UPDATE_FORM_TEMPLATE;
    }

    @Secured("EDIT_DISCIPLINES")
    @PostMapping("/disciplines/update/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute Discipline discipline,
                         BindingResult result, Model model){

        if (!result.hasErrors()) {
            try {
                disciplineService.save(discipline);
                return "redirect:/disciplines/update/" + id + "?success";
            } catch (ValidationException validationException) {
                model.addAttribute("validationServiceErrors", validationException.getViolations());
            } catch (ServiceException serviceException) {
                model.addAttribute("serviceError", serviceException.getMessage());
            }
        }

        Discipline disciplineToDisplay = disciplineService.findById(id);
        model.addAttribute("entity", disciplineToDisplay);

        return UPDATE_FORM_TEMPLATE;
    }
}
