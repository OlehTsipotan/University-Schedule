package com.university.schedule.controller;

import com.university.schedule.dto.BuildingDTO;
import com.university.schedule.dto.ClassroomDTO;
import com.university.schedule.service.BuildingService;
import com.university.schedule.service.ClassroomService;
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
public class ClassroomRecordsController {

	private static final String UPDATE_FORM_TEMPLATE = "classroomsUpdateForm";
	private static final String INSERT_FORM_TEMPLATE = "classroomsInsertForm";
	private final ClassroomService classroomService;
	private final BuildingService buildingService;

	@Secured("VIEW_CLASSROOMS")
	@GetMapping("/classrooms")
	public String getAll(Model model, @RequestParam(defaultValue = "100") int limit,
	                     @RequestParam(defaultValue = "0") int offset,
	                     @RequestParam(defaultValue = "id,asc") String[] sort) {
		Pageable pageable = PaginationSortingUtility.getPageable(limit, offset, sort);
		List<ClassroomDTO> classroomDTOs = classroomService.findAllAsDTO(pageable);

		model.addAttribute("entities", classroomDTOs);
		model.addAttribute("currentLimit", limit);
		model.addAttribute("currentOffset", offset);
		model.addAttribute("sortField", sort[0]);
		model.addAttribute("sortDirection", sort[1]);
		model.addAttribute("reverseSortDirection", sort[1].equals("asc") ? "desc" : "asc");

		return "classrooms";
	}

	@Secured("EDIT_CLASSROOMS")
	@GetMapping("/classrooms/delete/{id}")
	public RedirectView delete(@PathVariable(name = "id") Long id, HttpServletRequest request,
	                           RedirectAttributes redirectAttributes) {
		classroomService.deleteById(id);
		redirectAttributes.addFlashAttribute("success", "Record with ID = " + id + ", successfully deleted.");
		String referer = request.getHeader("Referer");
		String redirectTo = (referer != null) ? referer : "/classrooms";

		return new RedirectView(redirectTo);
	}

	@Secured("EDIT_CLASSROOMS")
	@GetMapping("/classrooms/update/{id}")
	public String getUpdateForm(@PathVariable(name = "id") Long id, Model model, ClassroomDTO classroomDTO) {
		ClassroomDTO classroomDTOToDisplay = classroomService.findByIdAsDTO(id);
		List<BuildingDTO> buildingDTOListToSelect = buildingService.findAllAsDTO();
		model.addAttribute("entity", classroomDTOToDisplay);
		model.addAttribute("buildingDTOList", buildingDTOListToSelect);

		return UPDATE_FORM_TEMPLATE;
	}

	@Secured("EDIT_CLASSROOMS")
	@PostMapping("/classrooms/update/{id}")
	public String update(@PathVariable Long id, @Valid @ModelAttribute ClassroomDTO classroomDTO, BindingResult result,
	                     Model model, RedirectAttributes redirectAttributes,
	                     @RequestParam("buildingDTO.id") Long selectedBuildingDTOId) {
		classroomDTO.setBuildingDTO(buildingService.findByIdAsDTO(selectedBuildingDTOId));
		if (!result.hasErrors()) {
			classroomService.save(classroomDTO);
			redirectAttributes.addFlashAttribute("success", true);
			return "redirect:/classrooms/update/" + id;
		}

		ClassroomDTO classroomDTOToDisplay = classroomService.findByIdAsDTO(id);
		List<BuildingDTO> buildingDTOListToSelect = buildingService.findAllAsDTO();
		model.addAttribute("entity", classroomDTOToDisplay);
		model.addAttribute("buildingDTOList", buildingDTOListToSelect);

		return UPDATE_FORM_TEMPLATE;
	}

	@Secured("INSERT_CLASSROOMS")
	@GetMapping("/classrooms/insert")
	public String getInsertForm(Model model, ClassroomDTO classroomDTO) {
		List<BuildingDTO> buildingDTOListToSelect = buildingService.findAllAsDTO();
		model.addAttribute("buildingDTOList", buildingDTOListToSelect);
		return INSERT_FORM_TEMPLATE;
	}

	@Secured("INSERT_CLASSROOMS")
	@PostMapping("/classrooms/insert")
	public String insert(@Valid @ModelAttribute ClassroomDTO classroomDTO, BindingResult result, Model model,
	                     RedirectAttributes redirectAttributes,
	                     @RequestParam("buildingDTO.id") Long selectedBuildingDTOId) {

		classroomDTO.setBuildingDTO(buildingService.findByIdAsDTO(selectedBuildingDTOId));
		if (!result.hasErrors()) {
			Long id = classroomService.save(classroomDTO);
			redirectAttributes.addFlashAttribute("insertedSuccessId", id);
			return "redirect:/classrooms";
		}

		List<BuildingDTO> buildingDTOListToSelect = buildingService.findAllAsDTO();
		model.addAttribute("buildingDTOList", buildingDTOListToSelect);

		return INSERT_FORM_TEMPLATE;

	}

}
