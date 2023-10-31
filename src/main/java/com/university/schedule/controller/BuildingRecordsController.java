package com.university.schedule.controller;

import com.university.schedule.dto.BuildingDTO;
import com.university.schedule.service.BuildingService;
import com.university.schedule.utility.PaginationSortingUtility;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class BuildingRecordsController {

    private static final String UPDATE_FORM_TEMPLATE = "buildingsUpdateForm";
    private static final String INSERT_FORM_TEMPLATE = "buildingsInsertForm";
    private final BuildingService buildingService;

    @Secured("VIEW_BUILDINGS")
    @GetMapping("/buildings")
    public String getAll(Model model, @RequestParam(defaultValue = "100") int limit,
                         @RequestParam(defaultValue = "0") int offset,
                         @RequestParam(defaultValue = "id,asc") String[] sort) {
        Pageable pageable = PaginationSortingUtility.getPageable(limit, offset, sort);
        List<BuildingDTO> buildings = buildingService.findAllAsDTO(pageable);

        model.addAttribute("entities", buildings);
        model.addAttribute("currentLimit", limit);
        model.addAttribute("currentOffset", offset);
        model.addAttribute("sortField", sort[0]);
        model.addAttribute("sortDirection", sort[1]);
        model.addAttribute("reverseSortDirection", sort[1].equals("asc") ? "desc" : "asc");

        return "buildings";
    }

    @Secured("EDIT_BUILDINGS")
    @GetMapping("/buildings/delete/{id}")
    public RedirectView delete(@PathVariable(name = "id") Long id, HttpServletRequest request,
                               RedirectAttributes redirectAttributes) {
        buildingService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Record with ID = " + id + ", successfully deleted.");
        String referer = request.getHeader("Referer");
        String redirectTo = (referer != null) ? referer : "/buildings";

        return new RedirectView(redirectTo);
    }

    @Secured("EDIT_BUILDINGS")
    @GetMapping("/buildings/update/{id}")
    public String getUpdateForm(@PathVariable(name = "id") Long id, Model model, BuildingDTO buildingDTO) {
        BuildingDTO buildingDTOToDisplay = buildingService.findByIdAsDTO(id);

        model.addAttribute("entity", buildingDTOToDisplay);

        return UPDATE_FORM_TEMPLATE;
    }

    @Secured("INSERT_BUILDINGS")
    @GetMapping("/buildings/insert")
    public String getInsertForm(Model model, BuildingDTO buildingDTO) {
        return INSERT_FORM_TEMPLATE;
    }

    @Secured("EDIT_BUILDINGS")
    @PostMapping("/buildings/update/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute BuildingDTO buildingDTO, BindingResult result,
                         Model model, RedirectAttributes redirectAttributes) {

        if (!result.hasErrors()) {
            buildingService.save(buildingDTO);
            redirectAttributes.addFlashAttribute("success", true);
            return "redirect:/buildings/update/" + id;
        }

        BuildingDTO buildingDTOToDisplay = buildingService.findByIdAsDTO(id);

        model.addAttribute("entity", buildingDTOToDisplay);

        return UPDATE_FORM_TEMPLATE;

    }

    @Secured("INSERT_BUILDINGS")
    @PostMapping("/buildings/insert")
    public String insert(@Valid @ModelAttribute BuildingDTO buildingDTO, BindingResult result, Model model,
                         RedirectAttributes redirectAttributes) {

        if (!result.hasErrors()) {
            Long id = buildingService.save(buildingDTO);
            redirectAttributes.addFlashAttribute("insertedSuccessId", id);
            return "redirect:/buildings";
        }

        return INSERT_FORM_TEMPLATE;

    }
}
