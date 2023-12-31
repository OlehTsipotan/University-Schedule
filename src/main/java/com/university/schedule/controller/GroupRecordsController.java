package com.university.schedule.controller;

import com.university.schedule.dto.CourseDTO;
import com.university.schedule.dto.DisciplineDTO;
import com.university.schedule.dto.GroupDTO;
import com.university.schedule.service.CourseService;
import com.university.schedule.service.DisciplineService;
import com.university.schedule.service.GroupService;
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
public class GroupRecordsController {

    private static final String UPDATE_FORM_TEMPLATE = "groupsUpdateForm";

    private static final String INSERT_FORM_TEMPLATE = "groupsInsertForm";
    private final GroupService groupService;
    private final DisciplineService disciplineService;
    private final CourseService courseService;

    @Secured("VIEW_GROUPS")
    @GetMapping("/groups")
    public String getAll(Model model, @RequestParam(defaultValue = "100") int limit,
                         @RequestParam(defaultValue = "0") int offset,
                         @RequestParam(defaultValue = "id,asc") String[] sort) {
        Pageable pageable = PaginationSortingUtility.getPageable(limit, offset, sort);
        List<GroupDTO> groupDTOs = groupService.findAllAsDTO(pageable);

        model.addAttribute("entities", groupDTOs);
        model.addAttribute("currentLimit", limit);
        model.addAttribute("currentOffset", offset);
        model.addAttribute("sortField", sort[0]);
        model.addAttribute("sortDirection", sort[0]);
        model.addAttribute("reverseSortDirection", sort[1].equals("asc") ? "desc" : "asc");

        return "groups";
    }

    @Secured("EDIT_GROUPS")
    @GetMapping("/groups/delete/{id}")
    public RedirectView delete(@PathVariable(name = "id") Long id, HttpServletRequest request,
                               RedirectAttributes redirectAttributes) {
        groupService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Record with ID = " + id + ", successfully deleted.");

        String referer = request.getHeader("Referer");
        String redirectTo = (referer != null) ? referer : "/groups";

        return new RedirectView(redirectTo);
    }

    @Secured("EDIT_GROUPS")
    @GetMapping("/groups/update/{id}")
    public String getUpdateForm(@PathVariable(name = "id") Long id, Model model, GroupDTO groupDTO) {
        GroupDTO groupDTOToDisplay = groupService.findByIdAsDTO(id);
        List<DisciplineDTO> disciplines = disciplineService.findAllAsDTO();
        List<CourseDTO> courseDTOListToSelect = courseService.findAllAsDTO();
        model.addAttribute("entity", groupDTOToDisplay);
        model.addAttribute("disciplineDTOList", disciplines);
        model.addAttribute("courseDTOList", courseDTOListToSelect);

        return UPDATE_FORM_TEMPLATE;
    }

    @Secured("EDIT_GROUPS")
    @PostMapping("/groups/update/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute GroupDTO groupDTO, BindingResult result,
                         Model model, RedirectAttributes redirectAttributes) {

        if (!result.hasErrors()) {
            groupService.save(groupDTO);
            redirectAttributes.addFlashAttribute("success", true);
            return "redirect:/groups/update/" + id;
        }

        GroupDTO groupDTOToDisplay = groupService.findByIdAsDTO(id);
        List<DisciplineDTO> disciplines = disciplineService.findAllAsDTO();
        List<CourseDTO> courseDTOListToSelect = courseService.findAllAsDTO();
        model.addAttribute("entity", groupDTOToDisplay);
        model.addAttribute("disciplineDTOList", disciplines);
        model.addAttribute("courseDTOList", courseDTOListToSelect);

        return UPDATE_FORM_TEMPLATE;
    }

    @Secured("INSERT_GROUPS")
    @GetMapping("/groups/insert")
    public String getInsertForm(Model model, GroupDTO groupDTO) {
        List<DisciplineDTO> disciplines = disciplineService.findAllAsDTO();
        List<CourseDTO> courseDTOListToSelect = courseService.findAllAsDTO();
        model.addAttribute("disciplineDTOList", disciplines);
        model.addAttribute("courseDTOList", courseDTOListToSelect);
        return INSERT_FORM_TEMPLATE;
    }

    @Secured("INSERT_GROUPS")
    @PostMapping("/groups/insert")
    public String insert(@Valid @ModelAttribute GroupDTO groupDTO, BindingResult result, Model model,
                         RedirectAttributes redirectAttributes) {

        if (!result.hasErrors()) {
            Long id = groupService.save(groupDTO);
            redirectAttributes.addFlashAttribute("insertedSuccessId", id);
            return "redirect:/groups";
        }

        List<DisciplineDTO> disciplines = disciplineService.findAllAsDTO();
        List<CourseDTO> courseDTOListToSelect = courseService.findAllAsDTO();
        model.addAttribute("disciplineDTOList", disciplines);
        model.addAttribute("courseDTOList", courseDTOListToSelect);

        return INSERT_FORM_TEMPLATE;

    }
}
