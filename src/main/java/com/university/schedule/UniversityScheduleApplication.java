package com.university.schedule;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableWebSecurity
public class UniversityScheduleApplication {

    public static void main(String[] args) {
        SpringApplication.run(UniversityScheduleApplication.class, args);
    }

}
