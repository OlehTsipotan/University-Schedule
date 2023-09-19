package com.university.schedule.controller;

import com.university.schedule.dto.ClassTypeDTO;
import com.university.schedule.service.ClassTypeService;
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
public class ClassTypeRecordsController {

	private static final String UPDATE_FORM_TEMPLATE = "classtypesUpdateForm";
	private final ClassTypeService classTypeService;

	@Secured("VIEW_CLASSTYPES")
	@GetMapping("/classtypes")
	public String getAll(Model model, @RequestParam(defaultValue = "100") int limit,
	                     @RequestParam(defaultValue = "0") int offset,
	                     @RequestParam(defaultValue = "id,asc") String[] sort) {
		Pageable pageable = PaginationSortingUtility.getPageable(limit, offset, sort);
		List<ClassTypeDTO> classTypeDTOList = classTypeService.findAllAsDTO(pageable);

		model.addAttribute("entities", classTypeDTOList);
		model.addAttribute("currentLimit", limit);
		model.addAttribute("currentOffset", offset);
		model.addAttribute("sortField", sort[0]);
		model.addAttribute("sortDirection", sort[1]);
		model.addAttribute("reverseSortDirection", sort[1].equals("asc") ? "desc" : "asc");

		return "classtypes";
	}

	@Secured("EDIT_CLASSTYPES")
	@GetMapping("/classtypes/delete/{id}")
	public RedirectView delete(@PathVariable(name = "id") Long id, HttpServletRequest request,
	                           RedirectAttributes redirectAttributes) {
		classTypeService.deleteById(id);
		redirectAttributes.addFlashAttribute("success", "Record with ID = " + id + ", successfully deleted.");
		String referer = request.getHeader("Referer");
		String redirectTo = (referer != null) ? referer : "/classtypes";

		return new RedirectView(redirectTo);
	}

	@Secured("EDIT_CLASSTYPES")
	@GetMapping("/classtypes/update/{id}")
	public String getUpdateForm(@PathVariable(name = "id") Long id, Model model, ClassTypeDTO classTypeDTO) {
		ClassTypeDTO classTypeDTOToDisplay = classTypeService.findByIdAsDTO(id);
		model.addAttribute("entity", classTypeDTOToDisplay);

		return UPDATE_FORM_TEMPLATE;
	}

	@Secured("EDIT_CLASSTYPES")
	@PostMapping("/classtypes/update/{id}")
	public String update(@PathVariable Long id, @Valid @ModelAttribute ClassTypeDTO classTypeDTO, BindingResult result,
	                     Model model, RedirectAttributes redirectAttributes) {

		if (!result.hasErrors()) {
			classTypeService.save(classTypeDTO);
			redirectAttributes.addFlashAttribute("success", true);
			return "redirect:/classtypes/update/" + id;
		}

		ClassTypeDTO classTypeDTOToDisplay = classTypeService.findByIdAsDTO(id);
		model.addAttribute("entity", classTypeDTOToDisplay);

		return UPDATE_FORM_TEMPLATE;
	}
}
