package com.university.schedule.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ErrorController {

    @GetMapping("/accessDenied")
    public String accessDenied(Model model){
        model.addAttribute("message", "Access Denied");
        return "error";
    }
}
