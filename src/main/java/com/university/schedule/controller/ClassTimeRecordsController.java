package com.university.schedule.controller;

import com.university.schedule.dto.ClassTimeDTO;
import com.university.schedule.service.ClassTimeService;
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
public class ClassTimeRecordsController {

	private static final String UPDATE_FORM_TEMPLATE = "classtimesUpdateForm";
	private final ClassTimeService classTimeService;

	@Secured("VIEW_CLASSTIMES")
	@GetMapping("/classtimes")
	public String getAll(Model model, @RequestParam(defaultValue = "100") int limit,
	                     @RequestParam(defaultValue = "0") int offset,
	                     @RequestParam(defaultValue = "id,asc") String[] sort) {

		Pageable pageable = PaginationSortingUtility.getPageable(limit, offset, sort);
		List<ClassTimeDTO> classTimeDTOs = classTimeService.findAllAsDTO(pageable);

		model.addAttribute("entities", classTimeDTOs);
		model.addAttribute("currentLimit", limit);
		model.addAttribute("currentOffset", offset);
		model.addAttribute("sortField", sort[0]);
		model.addAttribute("sortDirection", sort[1]);
		model.addAttribute("reverseSortDirection", sort[1].equals("asc") ? "desc" : "asc");

		return "classtimes";
	}

	@Secured("EDIT_CLASSTIMES")
	@GetMapping("/classtimes/delete/{id}")
	public RedirectView delete(@PathVariable(name = "id") Long id, HttpServletRequest request,
	                           RedirectAttributes redirectAttributes) {
		classTimeService.deleteById(id);
		redirectAttributes.addFlashAttribute("success", "Record with ID = " + id + ", successfully deleted.");
		String referer = request.getHeader("Referer");
		String redirectTo = (referer != null) ? referer : "/groups";

		return new RedirectView(redirectTo);
	}

	@Secured("EDIT_CLASSTIMES")
	@GetMapping("/classtimes/update/{id}")
	public String getUpdateForm(@PathVariable(name = "id") Long id, Model model, ClassTimeDTO classTimeDTO) {
		ClassTimeDTO classTimeDTOToDisplay = classTimeService.findByIdAsDTO(id);
		model.addAttribute("entity", classTimeDTOToDisplay);

		return UPDATE_FORM_TEMPLATE;
	}

	@Secured("EDIT_CLASSTIMES")
	@PostMapping("/classtimes/update/{id}")
	public String update(@PathVariable Long id, @Valid @ModelAttribute ClassTimeDTO classTimeDTO, BindingResult result,
	                     Model model, RedirectAttributes redirectAttributes) {

		if (!result.hasErrors()) {
			classTimeService.save(classTimeDTO);
			redirectAttributes.addFlashAttribute("success", true);
			return "redirect:/classtimes/update/" + id;
		}

		classTimeDTO = classTimeService.findByIdAsDTO(id);
		model.addAttribute("entity", classTimeDTO);

		return UPDATE_FORM_TEMPLATE;
	}
}
