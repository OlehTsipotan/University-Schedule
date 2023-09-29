package com.university.schedule.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.Formatter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import java.util.Set;

@Configuration
@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer {

	private static final String[] CLASSPATH_RESOURCE_LOCATIONS =
			{"classpath:/static/assets/", "classpath:/META-INF/resources/webjars/", "classpath:/static/styles/"};

	@Autowired
	private Set<Formatter<?>> formatters;

	@Override
	public void addFormatters(FormatterRegistry registry) {
		formatters.forEach(registry::addFormatter);
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/webjars/**", "/assets/**", "/styles/**")
				.addResourceLocations(CLASSPATH_RESOURCE_LOCATIONS);
	}
}
