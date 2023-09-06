package com.university.schedule.controller;

import com.university.schedule.dto.UserUpdateDTO;
import com.university.schedule.exception.ServiceException;
import com.university.schedule.exception.ValidationException;
import com.university.schedule.model.Course;
import com.university.schedule.model.Role;
import com.university.schedule.model.Teacher;
import com.university.schedule.model.User;
import com.university.schedule.pageable.OffsetBasedPageRequest;
import com.university.schedule.service.RoleService;
import com.university.schedule.service.UserService;
import com.university.schedule.service.UserUpdateDTOService;
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
public class UserRecordsController {

    private final UserService userService;

    private final UserUpdateDTOService userUpdateDTOService;

    private final RoleService roleService;

    private static final String UPDATE_FORM_TEMPLATE = "usersUpdateForm";

    @Secured("VIEW_USERS")
    @GetMapping("/users")
    public String getAll(Model model,
                         @RequestParam(defaultValue = "100") int limit,
                         @RequestParam(defaultValue = "0") int offset,
                         @RequestParam(defaultValue = "id,asc") String[] sort) {
        String sortField = sort[0];
        String sortDirection = sort[1];

        Sort.Direction direction = sortDirection.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort.Order order = new Sort.Order(direction, sortField);

        Pageable pageable = OffsetBasedPageRequest.of(limit, offset, Sort.by(order));
        List<User> users = userService.findAll(pageable).toList();

        model.addAttribute("entities", users);
        model.addAttribute("currentLimit", limit);
        model.addAttribute("currentOffset", offset);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("reverseSortDirection", sortDirection.equals("asc") ? "desc" : "asc");

        return "users";
    }

    @Secured("EDIT_USERS")
    @GetMapping("/users/delete/{id}")
    public RedirectView delete(@PathVariable(name = "id") Long id,
                         HttpServletRequest request) {
        userService.deleteById(id);

        String referer = request.getHeader("Referer");
        String redirectTo = (referer != null) ? referer : "/users";

        return new RedirectView(redirectTo);
    }

    @Secured("EDIT_USERS")
    @GetMapping("/users/update/{id}")
    public String getUpdateForm(@PathVariable(name = "id") Long id, Model model, UserUpdateDTO userUpdateDTO) {
        UserUpdateDTO userToDisplay = userUpdateDTOService.findById(id);
        List<Role> roles = roleService.findAll();
        model.addAttribute("entity", userToDisplay);
        model.addAttribute("roles", roles);

        return UPDATE_FORM_TEMPLATE;
    }

    @Secured("EDIT_USERS")
    @PostMapping("/users/update/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute UserUpdateDTO userUpdateDTO,
                         BindingResult result,
                         @RequestParam(name = "isEnable", defaultValue = "false") Boolean isEnable, Model model){

        if (!result.hasErrors()) {
            try {
                userUpdateDTO.setIsEnable(isEnable);
                userUpdateDTOService.save(userUpdateDTO);
                return "redirect:/users/update/" + id + "?success";
            } catch (ValidationException validationException) {
                model.addAttribute("validationServiceErrors", validationException.getViolations());
            } catch (ServiceException serviceException) {
                model.addAttribute("serviceError", serviceException.getMessage());
            }
        }

        UserUpdateDTO userToDisplay = userUpdateDTOService.findById(id);
        List<Role> roles = roleService.findAll();
        model.addAttribute("entity", userToDisplay);
        model.addAttribute("roles", roles);

        return UPDATE_FORM_TEMPLATE;
    }
}
