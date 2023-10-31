package com.university.schedule.controller;

import com.university.schedule.dto.DisciplineDTO;
import com.university.schedule.service.DisciplineService;
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
public class DisciplineRecordsController {

    private static final String UPDATE_FORM_TEMPLATE = "disciplinesUpdateForm";

    private static final String INSERT_FORM_TEMPLATE = "disciplinesInsertForm";
    private final DisciplineService disciplineService;

    @Secured("VIEW_DISCIPLINES")
    @GetMapping("/disciplines")
    public String getAll(Model model, @RequestParam(defaultValue = "100") int limit,
                         @RequestParam(defaultValue = "0") int offset,
                         @RequestParam(defaultValue = "id,asc") String[] sort) {

        Pageable pageable = PaginationSortingUtility.getPageable(limit, offset, sort);
        List<DisciplineDTO> disciplines = disciplineService.findAllAsDTO(pageable);

        model.addAttribute("entities", disciplines);
        model.addAttribute("currentLimit", limit);
        model.addAttribute("currentOffset", offset);
        model.addAttribute("sortField", sort[0]);
        model.addAttribute("sortDirection", sort[1]);
        model.addAttribute("reverseSortDirection", sort[1].equals("asc") ? "desc" : "asc");

        return "disciplines";
    }

    @Secured("EDIT_DISCIPLINES")
    @GetMapping("/disciplines/delete/{id}")
    public RedirectView delete(@PathVariable(name = "id") Long id, HttpServletRequest request,
                               RedirectAttributes redirectAttributes) {
        disciplineService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Record with ID = " + id + ", successfully deleted.");

        String referer = request.getHeader("Referer");
        String redirectTo = (referer != null) ? referer : "/disciplines";

        return new RedirectView(redirectTo);
    }

    @Secured("EDIT_DISCIPLINES")
    @GetMapping("/disciplines/update/{id}")
    public String getUpdateForm(@PathVariable(name = "id") Long id, Model model, DisciplineDTO disciplineDTO) {
        DisciplineDTO disciplineToDisplay = disciplineService.findByIdAsDTO(id);
        model.addAttribute("entity", disciplineToDisplay);

        return UPDATE_FORM_TEMPLATE;
    }

    @Secured("EDIT_DISCIPLINES")
    @PostMapping("/disciplines/update/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute DisciplineDTO disciplineDTO,
                         BindingResult result, Model model, RedirectAttributes redirectAttributes) {

        if (!result.hasErrors()) {
            disciplineService.save(disciplineDTO);
            redirectAttributes.addFlashAttribute("success", true);
            return "redirect:/disciplines/update/" + id;
        }

        DisciplineDTO disciplineToDisplay = disciplineService.findByIdAsDTO(id);
        model.addAttribute("entity", disciplineToDisplay);

        return UPDATE_FORM_TEMPLATE;
    }

    @Secured("INSERT_DISCIPLINES")
    @GetMapping("/disciplines/insert")
    public String getInsertForm(Model model, DisciplineDTO disciplineDTO) {
        return INSERT_FORM_TEMPLATE;
    }

    @Secured("INSERT_DISCIPLINES")
    @PostMapping("/disciplines/insert")
    public String insert(@Valid @ModelAttribute DisciplineDTO disciplineDTO, BindingResult result, Model model,
                         RedirectAttributes redirectAttributes) {

        if (!result.hasErrors()) {
            Long id = disciplineService.save(disciplineDTO);
            redirectAttributes.addFlashAttribute("insertedSuccessId", id);
            return "redirect:/disciplines";
        }

        return INSERT_FORM_TEMPLATE;

    }
}
