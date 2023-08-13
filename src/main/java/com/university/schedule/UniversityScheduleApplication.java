package com.university.schedule;

import com.university.schedule.model.Classroom;
import com.university.schedule.model.User;
import com.university.schedule.service.ClassroomService;
import com.university.schedule.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UniversityScheduleApplication{

    public static void main(String[] args) {
        SpringApplication.run(UniversityScheduleApplication.class, args);
    }
}
