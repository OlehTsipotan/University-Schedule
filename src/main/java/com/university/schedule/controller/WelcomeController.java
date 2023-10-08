package com.university.schedule.controller;

import com.university.schedule.model.User;
import com.university.schedule.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
@Slf4j
@RequiredArgsConstructor
public class WelcomeController {

	private final UserService userService;

	@Secured("VIEW_WELCOME")
	@GetMapping({"/", "/welcome"})
	public String welcome(Model model, Principal principal) {
		User user = userService.findByEmail(principal.getName());
		model.addAttribute("userFullName", user.getFullName());
		model.addAttribute("role", user.getRole().getName());
		return "index/userIndex";
	}
}
