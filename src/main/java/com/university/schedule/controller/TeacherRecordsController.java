package com.university.schedule.controller;

import com.university.schedule.dto.TeacherDTO;
import com.university.schedule.exception.ServiceException;
import com.university.schedule.exception.ValidationException;
import com.university.schedule.mapper.TeacherMapper;
import com.university.schedule.model.*;
import com.university.schedule.pageable.OffsetBasedPageRequest;
import com.university.schedule.service.CourseService;
import com.university.schedule.service.RoleService;
import com.university.schedule.service.TeacherService;
import com.university.schedule.validation.UpdateValidation;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class TeacherRecordsController {

    private static final String UPDATE_FORM_TEMPLATE = "teachersUpdateForm";

    private final TeacherService teacherService;

    private final RoleService roleService;

    private final CourseService courseService;

    private final TeacherMapper teacherMapper;

    @Secured("VIEW_TEACHERS")
    @GetMapping("/teachers")
    public String getAll(Model model,
                         @RequestParam(defaultValue = "100") int limit,
                         @RequestParam(defaultValue = "0") int offset,
                         @RequestParam(defaultValue = "id,asc") String[] sort) {
        String sortField = sort[0];
        String sortDirection = sort[1];

        Sort.Direction direction = sortDirection.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort.Order order = new Sort.Order(direction, sortField);

        Pageable pageable = OffsetBasedPageRequest.of(limit, offset, Sort.by(order));
        List<Teacher> teachers = teacherService.findAll(pageable).toList();
        List<TeacherDTO> teacherDTOs = teachers.stream()
                .map(teacherMapper::convertToDto).toList();

        model.addAttribute("teachers", teacherDTOs);
        model.addAttribute("currentLimit", limit);
        model.addAttribute("currentOffset", offset);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("reverseSortDirection", sortDirection.equals("asc") ? "desc" : "asc");

        return "teachers";
    }

    @Secured("EDIT_TEACHERS")
    @GetMapping("/teachers/delete/{id}")
    public RedirectView delete(@PathVariable(name = "id") Long id,
                         HttpServletRequest request) {
        teacherService.deleteById(id);

        String referer = request.getHeader("Referer");
        String redirectTo = (referer != null) ? referer : "/teachers";

        return new RedirectView(redirectTo);
    }

    @Secured("EDIT_TEACHERS")
    @GetMapping("/teachers/update/{id}")
    public String getUpdateForm(@PathVariable(name = "id") Long id, Model model, Teacher teacher) {
        Teacher teacherToDisplay = teacherService.findById(id);
        List<Course> courses = courseService.findAll();
        List<Role> roles = roleService.findAll();
        model.addAttribute("entity", teacherToDisplay);
        model.addAttribute("courses", courses);
        model.addAttribute("roles", roles);

        return UPDATE_FORM_TEMPLATE;
    }

    @Secured("EDIT_TEACHERS")
    @PostMapping("/teachers/update/{id}")
    public String update(@PathVariable Long id, @Validated(UpdateValidation.class) @ModelAttribute Teacher teacher,
                         BindingResult result,
                         @RequestParam(name = "isEnable", defaultValue = "false") Boolean isEnable, Model model){

        if (!result.hasErrors()) {
            try {
                teacher.setPassword(teacherService.findById(id).getPassword());
                teacher.setIsEnable(isEnable);
                teacherService.save(teacher);

                return "redirect:/teachers/update/" + id + "?success";
            } catch (ValidationException validationException) {
                model.addAttribute("validationServiceErrors", validationException.getViolations());
            } catch (ServiceException serviceException) {
                model.addAttribute("serviceError", serviceException.getMessage());
            }
        }
        Teacher teacherToDisplay = teacherService.findById(id);
        List<Course> courses = courseService.findAll();
        List<Role> roles = roleService.findAll();
        model.addAttribute("entity", teacherToDisplay);
        model.addAttribute("courses", courses);
        model.addAttribute("roles", roles);

        return UPDATE_FORM_TEMPLATE;
    }
}
