package com.university.schedule.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/user-login")
    public String userLogin() {
        return "login/userLogin";
    }

    @GetMapping("/admin-login")
    public String adminLogin() {
        return "login/adminLogin";
    }
}
