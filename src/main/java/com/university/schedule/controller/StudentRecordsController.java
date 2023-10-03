package com.university.schedule.controller;

import com.university.schedule.dto.GroupDTO;
import com.university.schedule.dto.RoleDTO;
import com.university.schedule.dto.StudentDTO;
import com.university.schedule.service.GroupService;
import com.university.schedule.service.RoleService;
import com.university.schedule.service.StudentService;
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
public class StudentRecordsController {

	private static final String UPDATE_FORM_TEMPLATE = "studentsUpdateForm";
	private final StudentService studentService;
	private final GroupService groupService;
	private final RoleService roleService;


	@Secured("VIEW_STUDENTS")
	@GetMapping("/students")
	public String getAll(Model model, @RequestParam(defaultValue = "100") int limit,
	                     @RequestParam(defaultValue = "0") int offset,
	                     @RequestParam(defaultValue = "id,asc") String[] sort) {
		Pageable pageable = PaginationSortingUtility.getPageable(limit, offset, sort);
		List<StudentDTO> studentDTOList = studentService.findAllAsDTO(pageable);

		model.addAttribute("entities", studentDTOList);
		model.addAttribute("currentLimit", limit);
		model.addAttribute("currentOffset", offset);
		model.addAttribute("sortField", sort[0]);
		model.addAttribute("sortDirection", sort[1]);
		model.addAttribute("reverseSortDirection", sort[1].equals("asc") ? "desc" : "asc");

		return "students";
	}

	@Secured("EDIT_STUDENTS")
	@GetMapping("/students/delete/{id}")
	public RedirectView delete(@PathVariable(name = "id") Long id, HttpServletRequest request,
	                           RedirectAttributes redirectAttributes) {
		studentService.deleteById(id);
		redirectAttributes.addFlashAttribute("success", "Record with ID = " + id + ", successfully deleted.");

		String referer = request.getHeader("Referer");
		String redirectTo = (referer != null) ? referer : "/students";

		return new RedirectView(redirectTo);
	}

	@Secured("EDIT_STUDENTS")
	@GetMapping("/students/update/{id}")
	public String getUpdateForm(@PathVariable(name = "id") Long id, Model model, StudentDTO studentDTO) {
		StudentDTO studentDTOToDisplay = studentService.findByIdAsDTO(id);
		List<GroupDTO> groupDTOList = groupService.findAllAsDTO();
		List<RoleDTO> roleDTOList = roleService.findAllAsDTO();
		model.addAttribute("entity", studentDTOToDisplay);
		model.addAttribute("groupDTOList", groupDTOList);
		model.addAttribute("roleDTOList", roleDTOList);

		return UPDATE_FORM_TEMPLATE;
	}

	@Secured("EDIT_STUDENTS")
	@PostMapping("/students/update/{id}")
	public String update(@PathVariable Long id, @Valid @ModelAttribute StudentDTO studentDTO, BindingResult result,
	                     @RequestParam(name = "isEnable", defaultValue = "false") Boolean isEnable, Model model,
	                     RedirectAttributes redirectAttributes) {

		if (!result.hasErrors()) {
			studentDTO.setIsEnable(isEnable);
			studentService.update(studentDTO);
			redirectAttributes.addFlashAttribute("success", true);
			return "redirect:/students/update/" + id;
		}

		StudentDTO studentDTOToDisplay = studentService.findByIdAsDTO(id);
		List<GroupDTO> groupDTOList = groupService.findAllAsDTO();
		List<RoleDTO> roleDTOList = roleService.findAllAsDTO();
		model.addAttribute("entity", studentDTOToDisplay);
		model.addAttribute("groupDTOList", groupDTOList);
		model.addAttribute("roleDTOList", roleDTOList);

		return UPDATE_FORM_TEMPLATE;
	}
}

