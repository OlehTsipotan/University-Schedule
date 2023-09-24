package com.university.schedule.controller;

import com.university.schedule.dto.AuthorityDTO;
import com.university.schedule.dto.BuildingDTO;
import com.university.schedule.service.AuthorityService;
import com.university.schedule.service.BuildingService;
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
public class AuthorityRecordsController {

	private static final String UPDATE_FORM_TEMPLATE = "authoritiesUpdateForm";
	private static final String INSERT_FORM_TEMPLATE = "authoritiesInsertForm";
	private final AuthorityService authorityService;

	@Secured("VIEW_AUTHORITIES")
	@GetMapping("/authorities")
	public String getAll(Model model, @RequestParam(defaultValue = "100") int limit,
	                     @RequestParam(defaultValue = "0") int offset,
	                     @RequestParam(defaultValue = "id,asc") String[] sort) {
		Pageable pageable = PaginationSortingUtility.getPageable(limit, offset, sort);
		List<AuthorityDTO> buildings = authorityService.findAllAsDTO(pageable);

		model.addAttribute("entities", buildings);
		model.addAttribute("currentLimit", limit);
		model.addAttribute("currentOffset", offset);
		model.addAttribute("sortField", sort[0]);
		model.addAttribute("sortDirection", sort[1]);
		model.addAttribute("reverseSortDirection", sort[1].equals("asc") ? "desc" : "asc");

		return "authorities";
	}

	@Secured("EDIT_AUTHORITIES")
	@GetMapping("/authorities/delete/{id}")
	public RedirectView delete(@PathVariable(name = "id") Long id, HttpServletRequest request,
	                           RedirectAttributes redirectAttributes) {
		authorityService.deleteById(id);
		redirectAttributes.addFlashAttribute("success", "Record with ID = " + id + ", successfully deleted.");
		String referer = request.getHeader("Referer");
		String redirectTo = (referer != null) ? referer : "/authorities";

		return new RedirectView(redirectTo);
	}

	@Secured("EDIT_AUTHORITIES")
	@GetMapping("/authorities/update/{id}")
	public String getUpdateForm(@PathVariable(name = "id") Long id, Model model, AuthorityDTO authorityDTO) {
		AuthorityDTO authorityDTOToDisplay = authorityService.findByIdAsDTO(id);

		model.addAttribute("entity", authorityDTOToDisplay);

		return UPDATE_FORM_TEMPLATE;
	}

	@Secured("INSERT_AUTHORITIES")
	@GetMapping("/authorities/insert")
	public String getInsertForm(Model model, AuthorityDTO authorityDTO) {
		return INSERT_FORM_TEMPLATE;
	}

	@Secured("EDIT_AUTHORITIES")
	@PostMapping("/authorities/update/{id}")
	public String update(@PathVariable Long id, @Valid @ModelAttribute AuthorityDTO authorityDTO, BindingResult result,
	                     Model model, RedirectAttributes redirectAttributes) {

		if (!result.hasErrors()) {
			authorityService.save(authorityDTO);
			redirectAttributes.addFlashAttribute("success", true);
			return "redirect:/authorities/update/" + id;
		}

		AuthorityDTO authorityDTOToDisplay = authorityService.findByIdAsDTO(id);

		model.addAttribute("entity", authorityDTOToDisplay);

		return UPDATE_FORM_TEMPLATE;

	}

	@Secured("INSERT_AUTHORITIES")
	@PostMapping("/authorities/insert")
	public String insert(@Valid @ModelAttribute AuthorityDTO authorityDTO, BindingResult result,
	                     RedirectAttributes redirectAttributes) {

		if (!result.hasErrors()) {
			Long id = authorityService.save(authorityDTO);
			redirectAttributes.addFlashAttribute("insertedSuccessId", id);
			return "redirect:/authorities";
		}

		return INSERT_FORM_TEMPLATE;

	}
}
