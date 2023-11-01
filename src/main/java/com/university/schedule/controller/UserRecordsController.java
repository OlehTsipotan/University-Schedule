package com.university.schedule.controller;

import com.university.schedule.dto.RoleDTO;
import com.university.schedule.dto.UserDTO;
import com.university.schedule.service.RoleService;
import com.university.schedule.service.UserService;
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
public class UserRecordsController {

    private static final String UPDATE_FORM_TEMPLATE = "usersUpdateForm";
    private final UserService userService;
    private final RoleService roleService;

    @Secured("VIEW_USERS")
    @GetMapping("/users")
    public String getAll(Model model, @RequestParam(defaultValue = "100") int limit,
                         @RequestParam(defaultValue = "0") int offset,
                         @RequestParam(defaultValue = "id,asc") String[] sort) {
        Pageable pageable = PaginationSortingUtility.getPageable(limit, offset, sort);
        List<UserDTO> userDTOList = userService.findAllAsDTO(pageable);

        model.addAttribute("entities", userDTOList);
        model.addAttribute("currentLimit", limit);
        model.addAttribute("currentOffset", offset);
        model.addAttribute("sortField", sort[0]);
        model.addAttribute("sortDirection", sort[1]);
        model.addAttribute("reverseSortDirection", sort[1].equals("asc") ? "desc" : "asc");

        return "users";
    }

    @Secured("EDIT_USERS")
    @GetMapping("/users/delete/{id}")
    public RedirectView delete(@PathVariable(name = "id") Long id, HttpServletRequest request,
                               RedirectAttributes redirectAttributes) {
        userService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Record with ID = " + id + ", successfully deleted.");

        String referer = request.getHeader("Referer");
        String redirectTo = (referer != null) ? referer : "/users";

        return new RedirectView(redirectTo);
    }

    @Secured("EDIT_USERS")
    @GetMapping("/users/update/{id}")
    public String getUpdateForm(@PathVariable(name = "id") Long id, Model model, UserDTO userDTO) {
        UserDTO userDTOToDisplay = userService.findByIdAsDTO(id);
        List<RoleDTO> roleDTOList = roleService.findAllAsDTO();
        model.addAttribute("entity", userDTOToDisplay);
        model.addAttribute("roleDTOList", roleDTOList);

        return UPDATE_FORM_TEMPLATE;
    }

    @Secured("EDIT_USERS")
    @PostMapping("/users/update/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute UserDTO userDTO, BindingResult result,
                         @RequestParam(name = "isEnable", defaultValue = "false") Boolean isEnable, Model model,
                         RedirectAttributes redirectAttributes) {
        if (!result.hasErrors()) {
            userDTO.setIsEnable(isEnable);
            userService.update(userDTO);
            redirectAttributes.addFlashAttribute("success", true);
            return "redirect:/users/update/" + id;
        }

        UserDTO userDTOToDisplay = userService.findByIdAsDTO(id);
        List<RoleDTO> roleDTOList = roleService.findAllAsDTO();
        model.addAttribute("entity", userDTOToDisplay);
        model.addAttribute("roleDTOList", roleDTOList);

        return UPDATE_FORM_TEMPLATE;
    }
}
