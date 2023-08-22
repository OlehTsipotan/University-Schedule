package com.university.schedule.controller;

import com.university.schedule.dto.ScheduledClassDTO;
import com.university.schedule.mapper.ScheduledClassMapper;
import com.university.schedule.model.ScheduledClass;
import com.university.schedule.pageable.OffsetBasedPageRequest;
import com.university.schedule.service.ScheduledClassService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
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
public class ScheduledClassController {

    private final ScheduledClassService scheduledClassService;

    private final ScheduledClassMapper scheduledClassMapper;

    @GetMapping("/classes")
    public String getAll(Model model,
                         @RequestParam(defaultValue = "100") int limit,
                         @RequestParam(defaultValue = "0") int offset,
                         @RequestParam(defaultValue = "id,asc") String[] sort) {
        String sortField = sort[0];
        String sortDirection = sort[1];

        Sort.Direction direction = sortDirection.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort.Order order = new Sort.Order(direction, sortField);

        Pageable pageable = OffsetBasedPageRequest.of(limit, offset, Sort.by(order));
        List<ScheduledClass> scheduledClasses = scheduledClassService.findAll(pageable).toList();
        List<ScheduledClassDTO> scheduledClassDTOs = scheduledClasses.stream()
                .map(scheduledClassMapper::convertToDto).toList();

        model.addAttribute("entities", scheduledClassDTOs);
        model.addAttribute("currentLimit", limit);
        model.addAttribute("currentOffset", offset);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("reverseSortDirection", sortDirection.equals("asc") ? "desc" : "asc");

        return "classes";
    }

    @GetMapping("/classes/delete/{id}")
    public RedirectView delete(@PathVariable(name = "id") Long id,
                         HttpServletRequest request) {
        scheduledClassService.deleteById(id);

        String referer = request.getHeader("Referer");
        String redirectTo = (referer != null) ? referer : "/classes";

        return new RedirectView(redirectTo);
    }
}
