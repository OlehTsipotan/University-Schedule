package com.university.schedule.controller;

import com.university.schedule.dto.ClassTimeDTO;
import com.university.schedule.exception.ServiceException;
import com.university.schedule.exception.ValidationException;
import com.university.schedule.mapper.ClassTimeMapper;
import com.university.schedule.model.ClassTime;
import com.university.schedule.pageable.OffsetBasedPageRequest;
import com.university.schedule.service.ClassTimeService;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ClassTimeRecordsController {

    private final ClassTimeService classTimeService;

    private final ClassTimeMapper classTimeMapper;

    private static final String UPDATE_FORM_TEMPLATE = "classtimesUpdateForm";

    @Secured("VIEW_CLASSTIMES")
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

    @Secured("EDIT_CLASSTIMES")
    @GetMapping("/classtimes/delete/{id}")
    public RedirectView delete(@PathVariable(name = "id") Long id,
                               HttpServletRequest request) {
        classTimeService.deleteById(id);

        String referer = request.getHeader("Referer");
        String redirectTo = (referer != null) ? referer : "/classtimes";

        return new RedirectView(redirectTo);
    }

    @Secured("EDIT_CLASSTIMES")
    @GetMapping("/classtimes/update/{id}")
    public String getUpdateForm(@PathVariable(name = "id") Long id, Model model, ClassTime classTime) {
        ClassTimeDTO classTimeDTO = classTimeMapper.convertToDto(classTimeService.findById(id));
        model.addAttribute("entity", classTimeDTO);

        return UPDATE_FORM_TEMPLATE;
    }

    @Secured("EDIT_CLASSTIMES")
    @PostMapping("/classtimes/update/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute ClassTime classTime,
                         BindingResult result, Model model){

        if (!result.hasErrors()) {
            try {
                classTimeService.save(classTime);
                return "redirect:/classtimes/update/" + id + "?success";
            } catch (ValidationException validationException) {
                model.addAttribute("validationServiceErrors", validationException.getViolations());
            } catch (ServiceException serviceException) {
                model.addAttribute("serviceError", serviceException.getMessage());
            }
        }

        ClassTimeDTO classTimeDTO = classTimeMapper.convertToDto(classTimeService.findById(id));
        model.addAttribute("entity", classTimeDTO);

        return UPDATE_FORM_TEMPLATE;
    }
}
