package com.university.schedule.controller;

import com.university.schedule.dto.StudentDTO;
import com.university.schedule.dto.StudentUpdateDTO;
import com.university.schedule.exception.ServiceException;
import com.university.schedule.exception.ValidationException;
import com.university.schedule.converter.StudentEntityToStudentDTOConverter;
import com.university.schedule.model.Group;
import com.university.schedule.model.Role;
import com.university.schedule.model.Student;
import com.university.schedule.pageable.OffsetBasedPageRequest;
import com.university.schedule.service.*;
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
public class StudentRecordsController {

    private static final String UPDATE_FORM_TEMPLATE = "studentsUpdateForm";

    private final StudentService studentService;

    private final StudentUpdateDTOService studentUpdateDTOService;
    private final StudentDTOService studentDTOService;

    private final GroupService groupService;

    private final RoleService roleService;


    @Secured("VIEW_STUDENTS")
    @GetMapping("/students")
    public String getAll(Model model,
                         @RequestParam(defaultValue = "100") int limit,
                         @RequestParam(defaultValue = "0") int offset,
                         @RequestParam(defaultValue = "id,asc") String[] sort) {

        String sortField = sort[0];
        String sortDirection = sort[1];

        Sort.Direction direction = sortDirection.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort.Order order = new Sort.Order(direction, sortField);

        Pageable pageable = OffsetBasedPageRequest.of(limit, offset, Sort.by(order));
        List<StudentDTO> studentDTOs = studentDTOService.findAll(pageable);

        model.addAttribute("students", studentDTOs);
        model.addAttribute("currentLimit", limit);
        model.addAttribute("currentOffset", offset);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("reverseSortDirection", sortDirection.equals("asc") ? "desc" : "asc");

        return "students";
    }

    @Secured("EDIT_STUDENTS")
    @GetMapping("/students/delete/{id}")
    public RedirectView delete(@PathVariable(name = "id") Long id,
                         HttpServletRequest request) {
        studentService.deleteById(id);

        String referer = request.getHeader("Referer");
        String redirectTo = (referer != null) ? referer : "/students";

        return new RedirectView(redirectTo);
    }

    @Secured("EDIT_STUDENTS")
    @GetMapping("/students/update/{id}")
    public String getUpdateForm(@PathVariable(name = "id") Long id, Model model, StudentUpdateDTO studentUpdateDTO) {
        StudentUpdateDTO studentToDisplay = studentUpdateDTOService.findById(id);
        List<Group> groups = groupService.findAll();
        List<Role> roles = roleService.findAll();
        model.addAttribute("entity", studentToDisplay);
        model.addAttribute("groups", groups);
        model.addAttribute("roles", roles);

        return UPDATE_FORM_TEMPLATE;
    }

    @Secured("EDIT_STUDENTS")
    @PostMapping("/students/update/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute StudentUpdateDTO studentUpdateDTO,
                         BindingResult result,
                         @RequestParam(name = "isEnable", defaultValue = "false") Boolean isEnable, Model model){

        if (!result.hasErrors()) {
            try {
                studentUpdateDTO.setIsEnable(isEnable);
                studentUpdateDTOService.save(studentUpdateDTO);
                return "redirect:/students/update/" + id + "?success";
            } catch (ValidationException validationException) {
                model.addAttribute("validationServiceErrors", validationException.getViolations());
            } catch (ServiceException serviceException) {
                model.addAttribute("serviceError", serviceException.getMessage());
            }
        }

        Student studentToDisplay = studentService.findById(id);
        List<Group> groups = groupService.findAll();
        List<Role> roles = roleService.findAll();
        model.addAttribute("entity", studentToDisplay);
        model.addAttribute("groups", groups);
        model.addAttribute("roles", roles);

        return UPDATE_FORM_TEMPLATE;
    }
}

