package com.university.schedule.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@PropertySource("classpath:page-data.properties")
@Slf4j
public class WelcomeController {

    @Value("${socialNetwork.twitter.url}")
    private String twitterUrl;

    @Value("${socialNetwork.github.url}")
    private String githubUrl;

    @Value("${socialNetwork.instagram.url}")
    private String instagramUrl;


    @GetMapping("/")
    private String welcome(Model model){
        // Social Networks links
        model.addAttribute("twitterUrl", twitterUrl);
        model.addAttribute("githubUrl", githubUrl);
        model.addAttribute("instagramUrl", instagramUrl);

        return "index";
    }
}
