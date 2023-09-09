package com.university.schedule.controller;

import com.university.schedule.dto.GroupDTO;
import com.university.schedule.exception.ServiceException;
import com.university.schedule.exception.ValidationException;
import com.university.schedule.converter.GroupEntityToGroupDTOConverter;
import com.university.schedule.model.Discipline;
import com.university.schedule.model.Group;
import com.university.schedule.pageable.OffsetBasedPageRequest;
import com.university.schedule.service.DisciplineService;
import com.university.schedule.service.GroupDTOService;
import com.university.schedule.service.GroupService;
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
public class GroupRecordsController {

    private static final String UPDATE_FORM_TEMPLATE = "groupsUpdateForm";
    private final GroupService groupService;

    private final GroupDTOService groupDTOService;

    private final DisciplineService disciplineService;

    @Secured("EDIT_GROUPS")
    @GetMapping("/groups")
    public String getAll(Model model,
                         @RequestParam(defaultValue = "100") int limit,
                         @RequestParam(defaultValue = "0") int offset,
                         @RequestParam(defaultValue = "id,asc") String[] sort) {
        String sortField = sort[0];
        String sortDirection = sort[1];

        Sort.Direction direction = sortDirection.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort.Order order = new Sort.Order(direction, sortField);

        Pageable pageable = OffsetBasedPageRequest.of(limit, offset, Sort.by(order));
        List<GroupDTO> groupDTOs = groupDTOService.findAll(pageable);

        model.addAttribute("entities", groupDTOs);
        model.addAttribute("currentLimit", limit);
        model.addAttribute("currentOffset", offset);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("reverseSortDirection", sortDirection.equals("asc") ? "desc" : "asc");

        return "groups";
    }

    @Secured("EDIT_GROUPS")
    @GetMapping("/groups/delete/{id}")
    public RedirectView delete(@PathVariable(name = "id") Long id,
                         HttpServletRequest request) {
        groupService.deleteById(id);

        String referer = request.getHeader("Referer");
        String redirectTo = (referer != null) ? referer : "/groups";


        return new RedirectView(redirectTo);
    }

    @Secured("EDIT_GROUPS")
    @GetMapping("/groups/update/{id}")
    public String getUpdateForm(@PathVariable(name = "id") Long id, Model model, Group group) {
        Group groupToDisplay = groupService.findById(id);
        List<Discipline> disciplines = disciplineService.findAll();
        model.addAttribute("entity", groupToDisplay);
        model.addAttribute("disciplines", disciplines);

        return UPDATE_FORM_TEMPLATE;
    }

    @Secured("EDIT_GROUPS")
    @PostMapping("/groups/update/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute Group group,
                         BindingResult result, Model model) {

        if (!result.hasErrors()) {
            try {
                groupService.save(group);
                return "redirect:/groups/update/" + id + "?success";
            } catch (ValidationException validationException) {
                model.addAttribute("validationServiceErrors", validationException.getViolations());
            } catch (ServiceException serviceException) {
                model.addAttribute("serviceError", serviceException.getMessage());
            }
        }

        Group groupToDisplay = groupService.findById(id);
        List<Discipline> disciplines = disciplineService.findAll();
        model.addAttribute("entity", groupToDisplay);
        model.addAttribute("disciplines", disciplines);

        return UPDATE_FORM_TEMPLATE;
    }
}
