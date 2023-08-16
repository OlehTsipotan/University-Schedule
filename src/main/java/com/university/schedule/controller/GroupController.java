package com.university.schedule.controller;

import com.university.schedule.dto.GroupDTO;
import com.university.schedule.mapper.GroupMapper;
import com.university.schedule.service.GroupService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    private final GroupMapper groupMapper;

    @GetMapping("/groups")
    public String getAll(Model model, @RequestParam(defaultValue = "id,asc") String[] sort) {
        String sortField = sort[0];
        String sortDirection = sort[1];

        Sort.Direction direction = sortDirection.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort.Order order = new Sort.Order(direction, sortField);

        List<GroupDTO> groups = groupService.findAll(Sort.by(order)).stream()
                .map(groupMapper::convertToDto).toList();

        model.addAttribute("entities", groups);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("reverseSortDirection", sortDirection.equals("asc") ? "desc" : "asc");

        return "groups";
    }

    @GetMapping("/groups/delete/{id}")
    public RedirectView delete(@PathVariable(name = "id") Long id,
                         HttpServletRequest request) {
        groupService.deleteById(id);

        String referer = request.getHeader("Referer");
        String redirectTo = (referer != null) ? referer : "/groups";


        return new RedirectView(redirectTo);
    }
}
