package com.university.schedule.controller;

import com.university.schedule.exception.ServiceException;
import com.university.schedule.exception.ValidationException;
import com.university.schedule.model.ClassType;
import com.university.schedule.model.Course;
import com.university.schedule.pageable.OffsetBasedPageRequest;
import com.university.schedule.service.CourseService;
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
public class CourseRecordsController {

    private final CourseService courseService;

    private static final String UPDATE_FORM_TEMPLATE = "coursesUpdateForm";

    @GetMapping("/courses")
    public String getAll(Model model,
                         @RequestParam(defaultValue = "100") int limit,
                         @RequestParam(defaultValue = "0") int offset,
                         @RequestParam(defaultValue = "id,asc") String[] sort) {
        String sortField = sort[0];
        String sortDirection = sort[1];

        Sort.Direction direction = sortDirection.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort.Order order = new Sort.Order(direction, sortField);

        Pageable pageable = OffsetBasedPageRequest.of(limit, offset, Sort.by(order));
        List<Course> courses = courseService.findAll(pageable).toList();

        model.addAttribute("entities", courses);
        model.addAttribute("currentLimit", limit);
        model.addAttribute("currentOffset", offset);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("reverseSortDirection", sortDirection.equals("asc") ? "desc" : "asc");

        return "courses";
    }

    @GetMapping("/courses/delete/{id}")
    public RedirectView delete(@PathVariable(name = "id") Long id,
                         HttpServletRequest request) {
        courseService.deleteById(id);

        String referer = request.getHeader("Referer");
        String redirectTo = (referer != null) ? referer : "/courses";

        return new RedirectView(redirectTo);
    }

    @Secured("EDIT_COURSES")
    @GetMapping("/courses/update/{id}")
    public String getUpdateForm(@PathVariable(name = "id") Long id, Model model, Course course) {
        Course courseToDisplay = courseService.findById(id);
        model.addAttribute("entity", courseToDisplay);

        return UPDATE_FORM_TEMPLATE;
    }

    @Secured("EDIT_COURSES")
    @PostMapping("/courses/update/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute Course course,
                         BindingResult result, Model model){

        if (!result.hasErrors()) {
            try {
                courseService.save(course);
                return "redirect:/courses/update/" + id + "?success";
            } catch (ValidationException validationException) {
                model.addAttribute("validationServiceErrors", validationException.getViolations());
            } catch (ServiceException serviceException) {
                model.addAttribute("serviceError", serviceException.getMessage());
            }
        }

        Course courseToDisplay = courseService.findById(id);
        model.addAttribute("entity", courseToDisplay);

        return UPDATE_FORM_TEMPLATE;
    }
}
