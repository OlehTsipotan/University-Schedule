package com.university.schedule.controller;

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
public class AdminController {

    private final UserService userService;

    @GetMapping("/admin/login")
    public String getLogin() {
        return "login/adminLogin";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/admin/dashboard")
    public String getDashboard(Model model, Principal principal) {
        String userFullName = userService.findByEmail(principal.getName()).getFullName();
        model.addAttribute("userFullName", userFullName);
        return "index/adminDashboard";
    }
}
