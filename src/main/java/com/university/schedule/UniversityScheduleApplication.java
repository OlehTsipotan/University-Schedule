package com.university.schedule;

import com.university.schedule.service.DataGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableWebSecurity
public class UniversityScheduleApplication implements CommandLineRunner {

    @Autowired
    DataGenerationService dataGenerationService;

    public static void main(String[] args) {
        SpringApplication.run(UniversityScheduleApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        dataGenerationService.generate();
    }
}
