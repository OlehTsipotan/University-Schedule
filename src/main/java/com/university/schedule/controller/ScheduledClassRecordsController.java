package com.university.schedule.controller;

import com.university.schedule.dto.ClassTimeDTO;
import com.university.schedule.dto.ClassroomDTO;
import com.university.schedule.dto.ScheduledClassDTO;
import com.university.schedule.exception.ServiceException;
import com.university.schedule.exception.ValidationException;
import com.university.schedule.converter.ClassTimeEntityToClassTimeDTOConverter;
import com.university.schedule.converter.ClassroomEntityToClassroomDTOConverter;
import com.university.schedule.converter.ScheduledClassEntityToScheduledClassDTOConverter;
import com.university.schedule.model.*;
import com.university.schedule.pageable.OffsetBasedPageRequest;
import com.university.schedule.service.*;
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
public class ScheduledClassRecordsController {
    private static final String UPDATE_FORM_TEMPLATE = "classesUpdateForm";
    private final ScheduledClassService scheduledClassService;
    private final ScheduledClassDTOService scheduledClassDTOService;
    private final ClassroomDTOService classroomDTOService;
    private final ClassTimeDTOService classTimeDTOService;
    private final CourseService courseService;
    private final TeacherService teacherService;
    private final ClassTypeService classTypeService;
    private final GroupService groupService;

    @Secured("VIEW_CLASSES")
    @GetMapping("/classes")
    public String getAll(Model model, @RequestParam(defaultValue = "100") int limit,
                         @RequestParam(defaultValue = "0") int offset,
                         @RequestParam(defaultValue = "id,asc") String[] sort) {
        String sortField = sort[0];
        String sortDirection = sort[1];

        Sort.Direction direction = sortDirection.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort.Order order = new Sort.Order(direction, sortField);

        Pageable pageable = OffsetBasedPageRequest.of(limit, offset, Sort.by(order));
        List<ScheduledClassDTO> scheduledClassDTOs = scheduledClassDTOService.findAll(pageable);

        model.addAttribute("entities", scheduledClassDTOs);
        model.addAttribute("currentLimit", limit);
        model.addAttribute("currentOffset", offset);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("reverseSortDirection", sortDirection.equals("asc") ? "desc" : "asc");

        return "classes";
    }

    @Secured("EDIT_CLASSES")
    @GetMapping("/classes/delete/{id}")
    public RedirectView delete(@PathVariable(name = "id") Long id, HttpServletRequest request) {
        scheduledClassService.deleteById(id);

        String referer = request.getHeader("Referer");
        String redirectTo = (referer != null) ? referer : "/classes";

        return new RedirectView(redirectTo);
    }

    @Secured("EDIT_CLASSES")
    @GetMapping("/classes/update/{id}")
    public String getUpdateForm(@PathVariable(name = "id") Long id, Model model, ScheduledClass scheduledClass) {
        ScheduledClass scheduledClassToDisplay = scheduledClassService.findById(id);

        List<Course> courses = courseService.findAll();
        List<Teacher> teachers = teacherService.findAll();
        List<ClassroomDTO> classroomDTOs = classroomDTOService.findAll();
        List<ClassTimeDTO> classTimeDTOs = classTimeDTOService.findAll();
        List<ClassType> classTypes = classTypeService.findAll();
        List<Group> groups = groupService.findAll();

        model.addAttribute("entity", scheduledClassToDisplay);
        model.addAttribute("courses", courses);
        model.addAttribute("teachers", teachers);
        model.addAttribute("classrooms", classroomDTOs);
        model.addAttribute("classtimes", classTimeDTOs);
        model.addAttribute("classtypes", classTypes);
        model.addAttribute("groups", groups);


        return UPDATE_FORM_TEMPLATE;
    }

    @Secured("EDIT_CLASSES")
    @PostMapping("/classes/update/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute ScheduledClass scheduledClass, BindingResult result, Model model) {


        if (!result.hasErrors()) {
            try {
                scheduledClassService.save(scheduledClass);
                return "redirect:/classes/update/" + id + "?success";
            } catch (ValidationException validationException) {
                model.addAttribute("validationServiceErrors", validationException.getViolations());
            } catch (ServiceException serviceException) {
                model.addAttribute("serviceError", serviceException.getMessage());
            }
        }

        ScheduledClass scheduledClassToDisplay = scheduledClassService.findById(id);

        List<Course> courses = courseService.findAll();
        List<Teacher> teachers = teacherService.findAll();
        List<ClassroomDTO> classroomDTOs = classroomDTOService.findAll();
        List<ClassTimeDTO> classTimeDTOs = classTimeDTOService.findAll();
        List<ClassType> classTypes = classTypeService.findAll();
        List<Group> groups = groupService.findAll();

        model.addAttribute("entity", scheduledClassToDisplay);
        model.addAttribute("courses", courses);
        model.addAttribute("teachers", teachers);
        model.addAttribute("classrooms", classroomDTOs);
        model.addAttribute("classtimes", classTimeDTOs);
        model.addAttribute("classtypes", classTypes);
        model.addAttribute("groups", groups);


        return UPDATE_FORM_TEMPLATE;
    }
}
