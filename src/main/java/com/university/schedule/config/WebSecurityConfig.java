package com.university.schedule.config;

import com.university.schedule.handler.CustomAccessDeniedHandler;
import com.university.schedule.handler.CustomAdminAuthenticationSuccessHandler;
import com.university.schedule.handler.CustomLogoutSuccessHandler;
import com.university.schedule.handler.CustomUserAuthenticationSuccessHandler;
import com.university.schedule.service.AdminDetailsService;
import com.university.schedule.service.DefaultUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
@RequiredArgsConstructor
public class WebSecurityConfig {


	private final DefaultUserDetailsService defaultUserDetailsService;
	private final AdminDetailsService adminDetailsService;
	private final CustomUserAuthenticationSuccessHandler customUserAuthenticationSuccessHandler;
	private final CustomLogoutSuccessHandler customLogoutSuccessHandler;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationSuccessHandler adminAuthenticationSuccessHandler() {
		return new CustomAdminAuthenticationSuccessHandler();
	}

	@Bean
	public AccessDeniedHandler accessDeniedHandler() {
		return new CustomAccessDeniedHandler();
	}

	@Bean
	public DaoAuthenticationProvider userAuthenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(defaultUserDetailsService);
		authProvider.setPasswordEncoder(passwordEncoder());

		return authProvider;
	}

	@Bean
	public DaoAuthenticationProvider adminAuthenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(adminDetailsService);
		authProvider.setPasswordEncoder(passwordEncoder());

		return authProvider;
	}


	@Bean
	@Order(2)
	public SecurityFilterChain userFilterChain(HttpSecurity http) throws Exception {
		return http.csrf(AbstractHttpConfigurer::disable).authenticationProvider(userAuthenticationProvider())
				.authorizeHttpRequests(
						authorizeRequests -> authorizeRequests.requestMatchers("/css/**", "/js/**", "/webjars/**",
										"/styles/**", "/assets/**").permitAll().requestMatchers("/admin/login").permitAll()
								.anyRequest().authenticated()).securityMatcher("/**").formLogin(
						login -> login.loginPage("/user/login").usernameParameter("email")
								.loginProcessingUrl("/user/login")
								.successHandler(customUserAuthenticationSuccessHandler).permitAll())
				.logout(logout -> logout.logoutUrl("/logout").logoutSuccessHandler(customLogoutSuccessHandler))
				.exceptionHandling(handler -> handler.accessDeniedHandler(accessDeniedHandler())).build();
	}

	@Bean
	@Order(1)
	public SecurityFilterChain adminFilterChain(HttpSecurity http) throws Exception {
		return http.csrf(AbstractHttpConfigurer::disable).authenticationProvider(adminAuthenticationProvider())
				.authorizeHttpRequests(
						authorizeRequests -> authorizeRequests.requestMatchers("/css/**", "/js/**", "/webjars/**",
								"/styles/**", "/assets/**").permitAll().requestMatchers("/admin/**").hasRole("ADMIN"))
				.securityMatcher("/admin/**").formLogin(
						login -> login.loginPage("/admin/login").usernameParameter("email")
								.loginProcessingUrl("/admin/login").successHandler(adminAuthenticationSuccessHandler())
								.permitAll())
				.exceptionHandling(handler -> handler.accessDeniedHandler(accessDeniedHandler())).build();
	}
}
