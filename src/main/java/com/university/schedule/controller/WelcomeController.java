package com.university.schedule.controller;

import com.university.schedule.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.Arrays;

@Controller
@Slf4j
@RequiredArgsConstructor
public class WelcomeController {

    private final UserService userService;


    @GetMapping({"/", "/welcome"})
    private String welcome(Model model, Principal principal) {
        String userFullName = userService.findByEmail(principal.getName()).getFullName();
        model.addAttribute("userFullName", userFullName);
        return "index";
    }
}
