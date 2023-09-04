package com.university.schedule.controller;

import com.university.schedule.exception.ServiceException;
import com.university.schedule.exception.ValidationException;
import com.university.schedule.model.ClassType;
import com.university.schedule.pageable.OffsetBasedPageRequest;
import com.university.schedule.service.ClassTypeService;
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
public class ClassTypeRecordsController {

    private final ClassTypeService classTypeService;

    private static final String UPDATE_FORM_TEMPLATE = "classtypesUpdateForm";

    @Secured("VIEW_CLASSTYPES")
    @GetMapping("/classtypes")
    public String getAll(Model model,
                         @RequestParam(defaultValue = "100") int limit,
                         @RequestParam(defaultValue = "0") int offset,
                         @RequestParam(defaultValue = "id,asc") String[] sort) {
        String sortField = sort[0];
        String sortDirection = sort[1];

        Sort.Direction direction = sortDirection.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort.Order order = new Sort.Order(direction, sortField);

        Pageable pageable = OffsetBasedPageRequest.of(limit, offset, Sort.by(order));
        List<ClassType> classTypes = classTypeService.findAll(pageable).toList();

        model.addAttribute("entities", classTypes);
        model.addAttribute("currentLimit", limit);
        model.addAttribute("currentOffset", offset);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("reverseSortDirection", sortDirection.equals("asc") ? "desc" : "asc");

        return "classtypes";
    }

    @Secured("EDIT_CLASSTYPES")
    @GetMapping("/classtypes/delete/{id}")
    public RedirectView delete(@PathVariable(name = "id") Long id,
                         HttpServletRequest request) {
        classTypeService.deleteById(id);

        String referer = request.getHeader("Referer");
        String redirectTo = (referer != null) ? referer : "/classtypes";

        return new RedirectView(redirectTo);
    }

    @Secured("EDIT_CLASSTYPES")
    @GetMapping("/classtypes/update/{id}")
    public String getUpdateForm(@PathVariable(name = "id") Long id, Model model, ClassType classType) {
        ClassType classTypeToDisplay = classTypeService.findById(id);
        model.addAttribute("entity", classTypeToDisplay);

        return UPDATE_FORM_TEMPLATE;
    }

    @Secured("EDIT_CLASSTYPES")
    @PostMapping("/classtypes/update/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute ClassType classType,
                         BindingResult result, Model model){

        if (!result.hasErrors()) {
            try {
                classTypeService.save(classType);
                return "redirect:/classtypes/update/" + id + "?success";
            } catch (ValidationException validationException) {
                model.addAttribute("validationServiceErrors", validationException.getViolations());
            } catch (ServiceException serviceException) {
                model.addAttribute("serviceError", serviceException.getMessage());
            }
        }

        ClassType classTypeToDisplay = classTypeService.findById(id);
        model.addAttribute("entity", classTypeToDisplay);

        return UPDATE_FORM_TEMPLATE;
    }
}
