package com.university.schedule;

import com.university.schedule.model.Student;
import com.university.schedule.model.Teacher;
import com.university.schedule.model.User;
import com.university.schedule.service.DataGenerationService;
import com.university.schedule.service.RoleService;
import com.university.schedule.service.StudentService;
import com.university.schedule.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableWebSecurity
public class UniversityScheduleApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(UniversityScheduleApplication.class, args);
    }

    @Autowired
    UserService userService;

    @Autowired
    RoleService roleService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
//        User user =new User("admin@gmail.com", passwordEncoder.encode("wbgf92005"), "Admin", "Tochno");
//        user.setRole(roleService.findByName("Admin"));
//        userService.save(user);
        //generationService.generate();
    }
}
