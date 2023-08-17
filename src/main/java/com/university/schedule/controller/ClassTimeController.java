package com.university.schedule.controller;

import com.university.schedule.dto.ClassTimeDTO;
import com.university.schedule.dto.ClassroomDTO;
import com.university.schedule.mapper.ClassTimeMapper;
import com.university.schedule.model.ClassTime;
import com.university.schedule.model.Classroom;
import com.university.schedule.pageable.OffsetBasedPageRequest;
import com.university.schedule.service.ClassTimeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

import java.io.IOException;
import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ClassTimeController {

    private final ClassTimeService classTimeService;

    private final ClassTimeMapper classTimeMapper;

    @GetMapping("/classtimes")
    public String getAll(Model model,
                         @RequestParam(defaultValue = "100") int limit,
                         @RequestParam(defaultValue = "0") int offset,
                         @RequestParam(defaultValue = "id,asc") String[] sort) {

        String sortField = sort[0];
        String sortDirection = sort[1];

        Sort.Direction direction = sortDirection.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort.Order order = new Sort.Order(direction, sortField);

        Pageable pageable = OffsetBasedPageRequest.of(limit, offset, Sort.by(order));
        List<ClassTime> classTimes = classTimeService.findAll(pageable).toList();
        List<ClassTimeDTO> classTimeDTOs = classTimes.stream()
                .map(classTimeMapper::convertToDto).toList();

        model.addAttribute("entities", classTimeDTOs);
        model.addAttribute("currentLimit", limit);
        model.addAttribute("currentOffset", offset);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("reverseSortDirection", sortDirection.equals("asc") ? "desc" : "asc");

        return "classtimes";
    }

    @GetMapping("/classtimes/delete/{id}")
    public RedirectView delete(@PathVariable(name = "id") Long id,
                               HttpServletRequest request) {
        classTimeService.deleteById(id);

        String referer = request.getHeader("Referer");
        String redirectTo = (referer != null) ? referer : "/classtimes";

        return new RedirectView(redirectTo);
    }
}
