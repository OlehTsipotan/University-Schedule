package com.university.schedule.controller;

import com.university.schedule.exception.ServiceException;
import com.university.schedule.model.Group;
import com.university.schedule.service.GroupService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @GetMapping("/groups")
    public String getAll(Model model, @RequestParam(defaultValue = "id,asc") String[] sort) {
        try {
            String sortField = sort[0];
            String sortDirection = sort[1];

            Sort.Direction direction = sortDirection.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
            Sort.Order order = new Sort.Order(direction, sortField);

            List<Group> groups = groupService.findAll(Sort.by(order));

            model.addAttribute("entities", groups);
            model.addAttribute("sortField", sortField);
            model.addAttribute("sortDirection", sortDirection);
            model.addAttribute("reverseSortDirection", sortDirection.equals("asc") ? "desc" : "asc");
        } catch (ServiceException e) {
            model.addAttribute("message", e.getMessage());
        }

        return "groups";
    }

    @GetMapping("/groups/delete/{id}")
    public String delete(Model model, @PathVariable(name = "id") Long id, HttpServletRequest request, HttpServletResponse response) {
        try {
            groupService.deleteById(id);
        } catch (ServiceException e) {
            log.error("Can't delete by id = " + id);
        }

        String referer = request.getHeader("Referer");
        String redirectTo = (referer != null) ? referer : "/groups";

        try {
            response.sendRedirect(redirectTo);
        } catch (IOException e) {
            log.error("Error redirecting back to page: " + redirectTo + ", error: " + e.getMessage());
        }

        return null;
    }
}
