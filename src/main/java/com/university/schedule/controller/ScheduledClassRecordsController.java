package com.university.schedule.controller;

import com.university.schedule.dto.*;
import com.university.schedule.service.*;
import com.university.schedule.utility.DateUtils;
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

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ScheduledClassRecordsController {
	private static final String UPDATE_FORM_TEMPLATE = "classesUpdateForm";

	private static final String INSERT_FORM_TEMPLATE = "classesInsertForm";

	private final ClassroomService classroomService;
	private final ClassTimeService classTimeService;
	private final ScheduledClassService scheduledClassService;
	private final CourseService courseService;
	private final TeacherService teacherService;
	private final ClassTypeService classTypeService;
	private final GroupService groupService;

	@Secured("VIEW_CLASSES")
	@GetMapping("/classes")
	public String getAll(Model model, @RequestParam(defaultValue = "100") int limit,
	                     @RequestParam(defaultValue = "0") int offset,
	                     @RequestParam(defaultValue = "id,asc") String[] sort) {
		Pageable pageable = PaginationSortingUtility.getPageable(limit, offset, sort);
		List<ScheduledClassDTO> scheduledClassDTOs = scheduledClassService.findAllAsDTO(pageable);

		model.addAttribute("entities", scheduledClassDTOs);
		model.addAttribute("currentLimit", limit);
		model.addAttribute("currentOffset", offset);
		model.addAttribute("sortField", sort[0]);
		model.addAttribute("sortDirection", sort[1]);
		model.addAttribute("reverseSortDirection", sort[1].equals("asc") ? "desc" : "asc");

		return "classes";
	}

	@Secured("VIEW_SCHEDULE")
	@GetMapping("/schedule")
	public String getSchedule(Model model, Principal principal, ScheduleFilterItem scheduleFilterItem) {

		scheduleFilterItem = ScheduleFilterItem.builder().email(principal.getName()).build();

		List<ScheduledClassDTO> scheduledClassDTOS =
				scheduledClassService.findAllAsDTOByScheduleFilterItem(scheduleFilterItem);

		List<TeacherDTO> teacherDTOS = teacherService.findAllAsDTO();
		List<GroupDTO> groupDTOS = groupService.findAllAsDTO();
		List<ClassTypeDTO> classTypeDTOS = classTypeService.findAllAsDTO();
		List<ClassTimeDTO> classTimeDTOS = classTimeService.findAllAsDTO();
		List<LocalDate> filteredDates =
				DateUtils.getDatesBetween(scheduleFilterItem.getStartDate(), scheduleFilterItem.getEndDate());

		model.addAttribute("filteredDates", filteredDates);
		model.addAttribute("scheduledClassDTOS", scheduledClassDTOS);
		model.addAttribute("teacherDTOS", teacherDTOS);
		model.addAttribute("groupDTOS", groupDTOS);
		model.addAttribute("classTypeDTOS", classTypeDTOS);
		model.addAttribute("classTimeDTOS", classTimeDTOS);

		model.addAttribute("scheduleFilterItem", scheduleFilterItem);

		return "schedule";
	}

	@Secured("VIEW_SCHEDULE")
	@PostMapping("/schedule")
	public String getScheduleFiltered(Model model, Principal principal,
	                                @ModelAttribute ScheduleFilterItem scheduleFilterItem) {

		scheduleFilterItem.setEmail(principal.getName());

		List<ScheduledClassDTO> scheduledClassDTOS =
				scheduledClassService.findAllAsDTOByScheduleFilterItem(scheduleFilterItem);

		List<TeacherDTO> teacherDTOS = teacherService.findAllAsDTO();
		List<GroupDTO> groupDTOS = groupService.findAllAsDTO();
		List<ClassTypeDTO> classTypeDTOS = classTypeService.findAllAsDTO();
		List<ClassTimeDTO> classTimeDTOS = classTimeService.findAllAsDTO();
		List<LocalDate> filteredDates =
				DateUtils.getDatesBetween(scheduleFilterItem.getStartDate(), scheduleFilterItem.getEndDate());

		model.addAttribute("filteredDates", filteredDates);
		model.addAttribute("scheduledClassDTOS", scheduledClassDTOS);
		model.addAttribute("teacherDTOS", teacherDTOS);
		model.addAttribute("groupDTOS", groupDTOS);
		model.addAttribute("classTypeDTOS", classTypeDTOS);
		model.addAttribute("classTimeDTOS", classTimeDTOS);

		model.addAttribute("scheduleFilterItem", scheduleFilterItem);
		model.addAttribute("filtered", true);

		return "schedule";
	}


	@Secured("EDIT_CLASSES")
	@GetMapping("/classes/delete/{id}")
	public RedirectView delete(@PathVariable(name = "id") Long id, HttpServletRequest request,
	                           RedirectAttributes redirectAttributes) {
		scheduledClassService.deleteById(id);

		redirectAttributes.addFlashAttribute("success", "Record with ID = " + id + ", successfully deleted.");
		String referer = request.getHeader("Referer");
		String redirectTo = (referer != null) ? referer : "/classes";

		return new RedirectView(redirectTo);
	}

	@Secured("EDIT_CLASSES")
	@GetMapping("/classes/update/{id}")
	public String getUpdateForm(@PathVariable(name = "id") Long id, Model model, ScheduledClassDTO scheduledClassDTO) {
		ScheduledClassDTO scheduledClassDTOToDisplay = scheduledClassService.findByIdAsDTO(id);

		List<CourseDTO> courseDTOList = courseService.findAllAsDTO();
		List<TeacherDTO> teacherDTOList = teacherService.findAllAsDTO();
		List<ClassroomDTO> classroomDTOList = classroomService.findAllAsDTO();
		List<ClassTimeDTO> classTimeDTOList = classTimeService.findAllAsDTO();
		List<ClassTypeDTO> classTypeDTOList = classTypeService.findAllAsDTO();
		List<GroupDTO> groupDTOList = groupService.findAllAsDTO();

		model.addAttribute("entity", scheduledClassDTOToDisplay);
		model.addAttribute("courseDTOList", courseDTOList);
		model.addAttribute("teacherDTOList", teacherDTOList);
		model.addAttribute("classroomDTOList", classroomDTOList);
		model.addAttribute("classTimeDTOList", classTimeDTOList);
		model.addAttribute("classTypeDTOList", classTypeDTOList);
		model.addAttribute("groupDTOList", groupDTOList);

		return UPDATE_FORM_TEMPLATE;
	}

	@Secured("EDIT_CLASSES")
	@PostMapping("/classes/update/{id}")
	public String update(@PathVariable Long id, @Valid @ModelAttribute ScheduledClassDTO scheduledClassDTO,
	                     BindingResult result, Model model, RedirectAttributes redirectAttributes) {

		log.info(scheduledClassDTO.toString());
		if (!result.hasErrors()) {
			scheduledClassService.save(scheduledClassDTO);
			redirectAttributes.addFlashAttribute("success", true);
			return "redirect:/classes/update/" + id;
		}

		ScheduledClassDTO scheduledClassDTOToDisplay = scheduledClassService.findByIdAsDTO(id);

		List<CourseDTO> courseDTOList = courseService.findAllAsDTO();
		List<TeacherDTO> teacherDTOList = teacherService.findAllAsDTO();
		List<ClassroomDTO> classroomDTOList = classroomService.findAllAsDTO();
		List<ClassTimeDTO> classTimeDTOList = classTimeService.findAllAsDTO();
		List<ClassTypeDTO> classTypeDTOList = classTypeService.findAllAsDTO();
		List<GroupDTO> groupDTOList = groupService.findAllAsDTO();

		model.addAttribute("entity", scheduledClassDTOToDisplay);
		model.addAttribute("courseDTOList", courseDTOList);
		model.addAttribute("teacherDTOList", teacherDTOList);
		model.addAttribute("classroomDTOList", classroomDTOList);
		model.addAttribute("classTimeDTOList", classTimeDTOList);
		model.addAttribute("classTypeDTOList", classTypeDTOList);
		model.addAttribute("groupDTOList", groupDTOList);

		return UPDATE_FORM_TEMPLATE;
	}

	@Secured("INSERT_CLASSES")
	@GetMapping("/classes/insert")
	public String getInsertForm(Model model, ScheduledClassDTO scheduledClassDTO) {
		List<CourseDTO> courseDTOList = courseService.findAllAsDTO();
		List<TeacherDTO> teacherDTOList = teacherService.findAllAsDTO();
		List<ClassroomDTO> classroomDTOList = classroomService.findAllAsDTO();
		List<ClassTimeDTO> classTimeDTOList = classTimeService.findAllAsDTO();
		List<ClassTypeDTO> classTypeDTOList = classTypeService.findAllAsDTO();
		List<GroupDTO> groupDTOList = groupService.findAllAsDTO();

		model.addAttribute("courseDTOList", courseDTOList);
		model.addAttribute("teacherDTOList", teacherDTOList);
		model.addAttribute("classroomDTOList", classroomDTOList);
		model.addAttribute("classTimeDTOList", classTimeDTOList);
		model.addAttribute("classTypeDTOList", classTypeDTOList);
		model.addAttribute("groupDTOList", groupDTOList);
		return INSERT_FORM_TEMPLATE;
	}

	@Secured("INSERT_CLASSES")
	@PostMapping("/classes/insert")
	public String insert(@Valid @ModelAttribute ScheduledClassDTO scheduledClassDTO, BindingResult result, Model model,
	                     RedirectAttributes redirectAttributes) {

		if (!result.hasErrors()) {
			Long id = scheduledClassService.save(scheduledClassDTO);
			redirectAttributes.addFlashAttribute("insertedSuccessId", id);
			return "redirect:/classes";
		}

		List<CourseDTO> courseDTOList = courseService.findAllAsDTO();
		List<TeacherDTO> teacherDTOList = teacherService.findAllAsDTO();
		List<ClassroomDTO> classroomDTOList = classroomService.findAllAsDTO();
		List<ClassTimeDTO> classTimeDTOList = classTimeService.findAllAsDTO();
		List<ClassTypeDTO> classTypeDTOList = classTypeService.findAllAsDTO();
		List<GroupDTO> groupDTOList = groupService.findAllAsDTO();

		model.addAttribute("courseDTOList", courseDTOList);
		model.addAttribute("teacherDTOList", teacherDTOList);
		model.addAttribute("classroomDTOList", classroomDTOList);
		model.addAttribute("classTimeDTOList", classTimeDTOList);
		model.addAttribute("classTypeDTOList", classTypeDTOList);
		model.addAttribute("groupDTOList", groupDTOList);

		return INSERT_FORM_TEMPLATE;

	}
}
