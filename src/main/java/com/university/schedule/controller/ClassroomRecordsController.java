package com.university.schedule.controller;

import com.university.schedule.dto.ClassroomDTO;
import com.university.schedule.exception.ServiceException;
import com.university.schedule.exception.ValidationException;
import com.university.schedule.converter.ClassroomEntityToClassroomDTOConverter;
import com.university.schedule.model.Building;
import com.university.schedule.model.Classroom;
import com.university.schedule.pageable.OffsetBasedPageRequest;
import com.university.schedule.service.BuildingService;
import com.university.schedule.service.ClassroomDTOService;
import com.university.schedule.service.ClassroomService;
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
public class ClassroomRecordsController {

    private final ClassroomDTOService classroomDTOService;

    private final ClassroomService classroomService;
    private final BuildingService buildingService;
    private static final String UPDATE_FORM_TEMPLATE = "classroomsUpdateForm";

    @Secured("VIEW_CLASSROOMS")
    @GetMapping("/classrooms")
    public String getAll(Model model,
                         @RequestParam(defaultValue = "100") int limit,
                         @RequestParam(defaultValue = "0") int offset,
                         @RequestParam(defaultValue = "id,asc") String[] sort) {
        String sortField = sort[0];
        String sortDirection = sort[1];

        Sort.Direction direction = sortDirection.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort.Order order = new Sort.Order(direction, sortField);

        Pageable pageable = OffsetBasedPageRequest.of(limit, offset, Sort.by(order));
        List<ClassroomDTO> classroomDTOs = classroomDTOService.findAll(pageable);


        model.addAttribute("entities", classroomDTOs);
        model.addAttribute("currentLimit", limit);
        model.addAttribute("currentOffset", offset);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("reverseSortDirection", sortDirection.equals("asc") ? "desc" : "asc");

        return "classrooms";
    }

    @Secured("EDIT_CLASSROOMS")
    @GetMapping("/classrooms/delete/{id}")
    public RedirectView delete(@PathVariable(name = "id") Long id, HttpServletRequest request) {
        classroomService.deleteById(id);

        String referer = request.getHeader("Referer");
        String redirectTo = (referer != null) ? referer : "/classrooms";

        return new RedirectView(redirectTo);
    }

    @Secured("EDIT_CLASSROOMS")
    @GetMapping("/classrooms/update/{id}")
    public String getUpdateForm(@PathVariable(name = "id") Long id, Model model, Classroom classroom) {
        Classroom classroomToDisplay = classroomService.findById(id);
        List<Building> buildingsToSelect = buildingService.findAll();
        model.addAttribute("entity", classroomToDisplay);
        model.addAttribute("buildings", buildingsToSelect);

        return UPDATE_FORM_TEMPLATE;
    }

    @Secured("EDIT_CLASSROOMS")
    @PostMapping("/classrooms/update/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute Classroom classroom,
                         BindingResult result, Model model){

        if (!result.hasErrors()) {
            try {
                classroomService.save(classroom);
                return "redirect:/classrooms/update/" + id + "?success";
            } catch (ValidationException validationException) {
                model.addAttribute("validationServiceErrors", validationException.getViolations());
            } catch (ServiceException serviceException) {
                model.addAttribute("serviceError", serviceException.getMessage());
            }
        }

        Classroom classroomToDisplay = classroomService.findById(id);
        List<Building> buildingsToSelect = buildingService.findAll();
        model.addAttribute("entity", classroomToDisplay);
        model.addAttribute("buildings", buildingsToSelect);

        return UPDATE_FORM_TEMPLATE;
    }

}
