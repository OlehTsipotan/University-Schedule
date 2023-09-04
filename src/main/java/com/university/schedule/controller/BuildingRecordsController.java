package com.university.schedule.controller;

import com.university.schedule.exception.ServiceException;
import com.university.schedule.exception.ValidationException;
import com.university.schedule.model.Building;
import com.university.schedule.pageable.OffsetBasedPageRequest;
import com.university.schedule.service.BuildingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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
public class BuildingRecordsController {

    private static final String UPDATE_FORM_TEMPLATE = "buildingsUpdateForm";
    private final BuildingService buildingService;

    @Secured("VIEW_BUILDINGS")
    @GetMapping("/buildings")
    public String getAll(Model model, @RequestParam(defaultValue = "100") int limit,
                         @RequestParam(defaultValue = "0") int offset,
                         @RequestParam(defaultValue = "id,asc") String[] sort) {
        String sortField = sort[0];
        String sortDirection = sort[1];

        Sort.Direction direction = sortDirection.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort.Order order = new Sort.Order(direction, sortField);

        Pageable pageable = OffsetBasedPageRequest.of(limit, offset, Sort.by(order));
        List<Building> buildings = buildingService.findAll(pageable).toList();

        model.addAttribute("entities", buildings);
        model.addAttribute("currentLimit", limit);
        model.addAttribute("currentOffset", offset);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("reverseSortDirection", sortDirection.equals("asc") ? "desc" : "asc");

        return "buildings";
    }

    @Secured("EDIT_BUILDINGS")
    @GetMapping("/buildings/delete/{id}")
    public RedirectView delete(@PathVariable(name = "id") Long id, HttpServletRequest request) {
        buildingService.deleteById(id);

        String referer = request.getHeader("Referer");
        String redirectTo = (referer != null) ? referer : "/buildings";

        return new RedirectView(redirectTo);
    }

    @Secured("EDIT_BUILDINGS")
    @GetMapping("/buildings/update/{id}")
    public String getUpdateForm(@PathVariable(name = "id") Long id, Model model, Building building,
                                @RequestParam(name = "success", required = false) Boolean success,
                                HttpSession session) {
        Building buildingToDisplay = buildingService.findById(id);

        model.addAttribute("entity", buildingToDisplay);

        return UPDATE_FORM_TEMPLATE;
    }

    @Secured("EDIT_BUILDINGS")
    @PostMapping("/buildings/update/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute Building building,
                         BindingResult result, Model model, HttpSession session) {

        if (!result.hasErrors()) {
            try {
                buildingService.save(building);
                return "redirect:/buildings/update/" + id + "?success";
            } catch (ValidationException validationException) {
                model.addAttribute("validationServiceErrors", validationException.getViolations());
            } catch (ServiceException serviceException) {
                model.addAttribute("serviceError", serviceException.getMessage());
            }
        }

        Building buildingToDisplay = buildingService.findById(id);

        model.addAttribute("entity", buildingToDisplay);

        return UPDATE_FORM_TEMPLATE;

    }
}
