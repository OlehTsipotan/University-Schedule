package com.university.schedule.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Profile("test")
public class WebTestConfig implements WebMvcConfigurer {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		return http.csrf(AbstractHttpConfigurer::disable).authorizeHttpRequests(
				authorizeRequests -> authorizeRequests.requestMatchers("/css/**", "/js/**", "/webjars/**", "/styles/**",
								"/assets/**").permitAll().requestMatchers("/admin/login").permitAll()
						.requestMatchers("/user/register").permitAll().requestMatchers("/user/login").permitAll()
						.anyRequest().authenticated()).build();
	}
}
