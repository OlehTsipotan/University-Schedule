package com.university.schedule.controller;

import com.university.schedule.dto.CourseDTO;
import com.university.schedule.dto.RoleDTO;
import com.university.schedule.dto.TeacherDTO;
import com.university.schedule.service.CourseService;
import com.university.schedule.service.RoleService;
import com.university.schedule.service.TeacherService;
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
public class TeacherRecordsController {

	private static final String UPDATE_FORM_TEMPLATE = "teachersUpdateForm";

	private final TeacherService teacherService;

	private final RoleService roleService;

	private final CourseService courseService;


	@Secured("VIEW_TEACHERS")
	@GetMapping("/teachers")
	public String getAll(Model model, @RequestParam(defaultValue = "100") int limit,
	                     @RequestParam(defaultValue = "0") int offset,
	                     @RequestParam(defaultValue = "id,asc") String[] sort) {
		Pageable pageable = PaginationSortingUtility.getPageable(limit, offset, sort);
		List<TeacherDTO> teacherDTOs = teacherService.findAllAsDTO(pageable);

		model.addAttribute("entities", teacherDTOs);
		model.addAttribute("currentLimit", limit);
		model.addAttribute("currentOffset", offset);
		model.addAttribute("sortField", sort[0]);
		model.addAttribute("sortDirection", sort[1]);
		model.addAttribute("reverseSortDirection", sort[1].equals("asc") ? "desc" : "asc");

		return "teachers";
	}

	@Secured("EDIT_TEACHERS")
	@GetMapping("/teachers/delete/{id}")
	public RedirectView delete(@PathVariable(name = "id") Long id, HttpServletRequest request,
	                           RedirectAttributes redirectAttributes) {
		teacherService.deleteById(id);

		redirectAttributes.addFlashAttribute("success", "Record with ID = " + id + ", successfully deleted.");

		String referer = request.getHeader("Referer");
		String redirectTo = (referer != null) ? referer : "/teachers";

		return new RedirectView(redirectTo);
	}

	@Secured("EDIT_TEACHERS")
	@GetMapping("/teachers/update/{id}")
	public String getUpdateForm(@PathVariable(name = "id") Long id, Model model, TeacherDTO teacherDTO) {
		TeacherDTO teacherToDisplay = teacherService.findByIdAsDTO(id);
		List<CourseDTO> courseDTOList = courseService.findAllAsDTO();
		List<RoleDTO> roleDTOList = roleService.findAllAsDTO();
		model.addAttribute("entity", teacherToDisplay);
		model.addAttribute("courseDTOList", courseDTOList);
		model.addAttribute("roleDTOList", roleDTOList);

		return UPDATE_FORM_TEMPLATE;
	}

	@Secured("EDIT_TEACHERS")
	@PostMapping("/teachers/update/{id}")
	public String update(@PathVariable Long id, @Valid @ModelAttribute TeacherDTO teacherDTO, BindingResult result,
	                     @RequestParam(name = "isEnable", defaultValue = "false") Boolean isEnable, Model model,
	                     RedirectAttributes redirectAttributes) {

		teacherDTO.setRoleDTO(roleService.findByIdAsDTO(teacherDTO.getRoleDTO().getId()));
		teacherDTO.setCourseDTOS(
				teacherDTO.getCourseDTOS().stream().map(courseDTO -> courseService.findByIdAsDTO(courseDTO.getId()))
						.toList());

		log.info(teacherDTO.toString());

		if (!result.hasErrors()) {
			teacherDTO.setIsEnable(isEnable);
			teacherService.save(teacherDTO);
			redirectAttributes.addFlashAttribute("success", true);
			return "redirect:/teachers/update/" + id;
		}

		TeacherDTO teacherToDisplay = teacherService.findByIdAsDTO(id);
		List<CourseDTO> courseDTOList = courseService.findAllAsDTO();
		List<RoleDTO> roleDTOList = roleService.findAllAsDTO();
		model.addAttribute("entity", teacherToDisplay);
		model.addAttribute("courseDTOList", courseDTOList);
		model.addAttribute("roleDTOList", roleDTOList);

		return UPDATE_FORM_TEMPLATE;
	}
}
