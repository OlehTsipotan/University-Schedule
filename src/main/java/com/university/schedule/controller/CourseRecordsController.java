package com.university.schedule.controller;

import com.university.schedule.dto.CourseDTO;
import com.university.schedule.service.CourseService;
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
public class CourseRecordsController {

	private static final String UPDATE_FORM_TEMPLATE = "coursesUpdateForm";
	private final CourseService courseService;

	@Secured("VIEW_COURSES")
	@GetMapping("/courses")
	public String getAll(Model model, @RequestParam(defaultValue = "100") int limit,
	                     @RequestParam(defaultValue = "0") int offset,
	                     @RequestParam(defaultValue = "id,asc") String[] sort) {
		Pageable pageable = PaginationSortingUtility.getPageable(limit, offset, sort);
		List<CourseDTO> courseDTOList = courseService.findAllAsDTO(pageable);

		model.addAttribute("entities", courseDTOList);
		model.addAttribute("currentLimit", limit);
		model.addAttribute("currentOffset", offset);
		model.addAttribute("sortField", sort[0]);
		model.addAttribute("sortDirection", sort[1]);
		model.addAttribute("reverseSortDirection", sort[1].equals("asc") ? "desc" : "asc");

		return "courses";
	}

	@Secured("EDIT_COURSES")
	@GetMapping("/courses/delete/{id}")
	public RedirectView delete(@PathVariable(name = "id") Long id, HttpServletRequest request,
	                           RedirectAttributes redirectAttributes) {
		courseService.deleteById(id);
		redirectAttributes.addFlashAttribute("success", "Record with ID = " + id + ", successfully deleted.");

		String referer = request.getHeader("Referer");
		String redirectTo = (referer != null) ? referer : "/courses";

		return new RedirectView(redirectTo);
	}

	@Secured("EDIT_COURSES")
	@GetMapping("/courses/update/{id}")
	public String getUpdateForm(@PathVariable(name = "id") Long id, Model model, CourseDTO courseDTO) {
		CourseDTO courseDTOToDisplay = courseService.findByIdAsDTO(id);
		model.addAttribute("entity", courseDTOToDisplay);

		return UPDATE_FORM_TEMPLATE;
	}

	@Secured("EDIT_COURSES")
	@PostMapping("/courses/update/{id}")
	public String update(@PathVariable Long id, @Valid @ModelAttribute CourseDTO courseDTO, BindingResult result,
	                     Model model, RedirectAttributes redirectAttributes) {

		if (!result.hasErrors()) {
			courseService.save(courseDTO);
			redirectAttributes.addFlashAttribute("success", true);
			return "redirect:/courses/update/" + id;
		}

		CourseDTO courseDTOToDisplay = courseService.findByIdAsDTO(id);
		model.addAttribute("entity", courseDTOToDisplay);

		return UPDATE_FORM_TEMPLATE;
	}
}
