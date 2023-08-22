package com.university.schedule.config;

import com.university.schedule.handler.CustomAdminAuthenticationSuccessHandler;
import com.university.schedule.handler.CustomUserAuthenticationSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Order(1)
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationSuccessHandler userAuthenticationSuccessHandler(){
        return new CustomUserAuthenticationSuccessHandler();
    }

    @Bean
    public AuthenticationSuccessHandler adminAuthenticationSuccessHandler(){
        return new CustomAdminAuthenticationSuccessHandler();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain userFilterChain(HttpSecurity http) throws Exception {
        return
                http
                        .csrf(AbstractHttpConfigurer::disable)
                        .authorizeHttpRequests(authorizeRequests ->
                                authorizeRequests
                                        .requestMatchers("/css/**", "/js/**", "/webjars/**", "/style/**").permitAll()
                                        .requestMatchers("/students**").hasAuthority("VIEW_STUDENTS")
                                        .requestMatchers("/admin-login").permitAll()
                                        .anyRequest().authenticated()
                                        )
                        .formLogin(login ->
                                login
                                        .loginPage("/user-login")
                                        .usernameParameter("email")
                                        .loginProcessingUrl("/user-login")
                                        .successHandler(adminAuthenticationSuccessHandler())
                                        .permitAll()
                                        )
                        .logout(logout ->
                                logout
                                        .logoutUrl("/user-logout"))
                        .build();
    }

    @Bean
    public SecurityFilterChain adminFilterChain(HttpSecurity http) throws Exception {
        return
                http
                        .csrf(AbstractHttpConfigurer::disable)
                        .authorizeHttpRequests(authorizeRequests ->
                                authorizeRequests
                                        .requestMatchers("/css/**", "/js/**", "/webjars/**", "/style/**").permitAll()
                                        .requestMatchers("/admin/**").hasRole("ADMIN")
                                        .anyRequest().authenticated()
                        )
                        .formLogin(login ->
                                login
                                        .loginPage("/admin-login")
                                        .usernameParameter("email")
                                        .loginProcessingUrl("/admin-login")
                                        .successHandler(adminAuthenticationSuccessHandler())
                                        .permitAll()
                        )
                        .logout(logout ->
                                logout
                                        .logoutUrl("/admin-logout"))
                        .build();
    }
}
