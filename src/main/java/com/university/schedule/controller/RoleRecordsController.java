package com.university.schedule.controller;

import com.university.schedule.dto.AuthorityDTO;
import com.university.schedule.dto.RoleDTO;
import com.university.schedule.service.AuthorityService;
import com.university.schedule.service.RoleService;
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
public class RoleRecordsController {

    private static final String UPDATE_FORM_TEMPLATE = "rolesUpdateForm";

    private static final String INSERT_FORM_TEMPLATE = "rolesInsertForm";
    private final RoleService roleService;
    private final AuthorityService authorityService;

    @Secured("VIEW_ROLES")
    @GetMapping("/roles")
    public String getAll(Model model, @RequestParam(defaultValue = "100") int limit,
                         @RequestParam(defaultValue = "0") int offset,
                         @RequestParam(defaultValue = "id,asc") String[] sort) {
        Pageable pageable = PaginationSortingUtility.getPageable(limit, offset, sort);
        List<RoleDTO> roleDTOS = roleService.findAllAsDTO(pageable);

        model.addAttribute("entities", roleDTOS);
        model.addAttribute("currentLimit", limit);
        model.addAttribute("currentOffset", offset);
        model.addAttribute("sortField", sort[0]);
        model.addAttribute("sortDirection", sort[0]);
        model.addAttribute("reverseSortDirection", sort[1].equals("asc") ? "desc" : "asc");

        return "roles";
    }

    @Secured("EDIT_ROLES")
    @GetMapping("/roles/delete/{id}")
    public RedirectView delete(@PathVariable(name = "id") Long id, HttpServletRequest request,
                               RedirectAttributes redirectAttributes) {
        roleService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Record with ID = " + id + ", successfully deleted.");

        String referer = request.getHeader("Referer");
        String redirectTo = (referer != null) ? referer : "/roles";

        return new RedirectView(redirectTo);
    }

    @Secured("EDIT_ROLES")
    @GetMapping("/roles/update/{id}")
    public String getUpdateForm(@PathVariable(name = "id") Long id, Model model, RoleDTO roleDTO) {
        RoleDTO roleDTOToDisplay = roleService.findByIdAsDTO(id);
        List<AuthorityDTO> authorityDTOList = authorityService.findAllAsDTO();
        model.addAttribute("entity", roleDTOToDisplay);
        model.addAttribute("authorityDTOList", authorityDTOList);

        return UPDATE_FORM_TEMPLATE;
    }

    @Secured("INSERT_ROLES")
    @GetMapping("/roles/insert")
    public String getInsertForm(Model model, RoleDTO roleDTO) {
        List<AuthorityDTO> authorityDTOList = authorityService.findAllAsDTO();
        model.addAttribute("authorityDTOList", authorityDTOList);
        return INSERT_FORM_TEMPLATE;
    }

    @Secured("EDIT_ROLES")
    @PostMapping("/roles/update/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute RoleDTO roleDTO, BindingResult result,
                         Model model, RedirectAttributes redirectAttributes) {

        if (!result.hasErrors()) {
            roleService.save(roleDTO);
            redirectAttributes.addFlashAttribute("success", true);
            return "redirect:/roles/update/" + id;
        }

        RoleDTO roleDTOToDisplay = roleService.findByIdAsDTO(id);
        List<AuthorityDTO> authorityDTOList = authorityService.findAllAsDTO();
        model.addAttribute("entity", roleDTOToDisplay);
        model.addAttribute("authorityDTOList", authorityDTOList);

        return UPDATE_FORM_TEMPLATE;
    }

    @Secured("INSERT_ROLES")
    @PostMapping("/roles/insert")
    public String insert(@Valid @ModelAttribute RoleDTO roleDTO, BindingResult result, Model model,
                         RedirectAttributes redirectAttributes) {

        if (!result.hasErrors()) {
            Long id = roleService.save(roleDTO);
            redirectAttributes.addFlashAttribute("insertedSuccessId", id);
            return "redirect:/authorities";
        }

        List<AuthorityDTO> authorityDTOList = authorityService.findAllAsDTO();
        model.addAttribute("authorityDTOList", authorityDTOList);
        return INSERT_FORM_TEMPLATE;

    }
}
