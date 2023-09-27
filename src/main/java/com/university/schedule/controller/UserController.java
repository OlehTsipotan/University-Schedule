package com.university.schedule.controller;

import com.university.schedule.dto.RoleDTO;
import com.university.schedule.dto.UserDTO;
import com.university.schedule.dto.UserRegisterDTO;
import com.university.schedule.service.RoleService;
import com.university.schedule.service.UserRegistrationService;
import com.university.schedule.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class UserController {

	private final String REGISTER_FORM_TEMPLATE = "login/userRegister";
	private final String LOGIN_FORM_TEMPLATE = "login/userLogin";

	private final UserRegistrationService userRegistrationService;
	private final RoleService roleService;

	@GetMapping("/user/login")
	public String getLogin() {
		return LOGIN_FORM_TEMPLATE;
	}

	@GetMapping("/user/register")
	public String getRegistrationForm(Model model, RedirectAttributes redirectAttributes,
	                                  UserRegisterDTO userRegisterDTO) {
		List<RoleDTO> roleDTOList = roleService.findAllForRegistrationAsDTO();
		model.addAttribute("roleDTOList", roleDTOList);

		return REGISTER_FORM_TEMPLATE;
	}

	@PostMapping("/user/register")
	public String update(@Valid @ModelAttribute UserRegisterDTO userRegisterDTO, BindingResult result, Model model,
	                     RedirectAttributes redirectAttributes) {
		userRegisterDTO.setRoleDTO(roleService.findByIdAsDTO(userRegisterDTO.getRoleDTO().getId()));
		if (!result.hasErrors()) {
			userRegistrationService.register(userRegisterDTO);
			redirectAttributes.addFlashAttribute("registerSuccess", true);
			return "redirect:/user/login";
		}

		List<RoleDTO> roleDTOList = roleService.findAllForRegistrationAsDTO();
		model.addAttribute("roleDTOList", roleDTOList);

		return REGISTER_FORM_TEMPLATE;
	}

}
