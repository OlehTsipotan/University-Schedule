package com.university.schedule.config;

import com.university.schedule.formatter.StringToCourseDTOFormatter;
import com.university.schedule.formatter.StringToGroupDTOFormatter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer {

	private static final String[] CLASSPATH_RESOURCE_LOCATIONS =
			{"classpath:/static/assets/", "classpath:/META-INF/resources/webjars/", "classpath:/static/styles/"};


	@Override
	public void addFormatters(FormatterRegistry registry) {
		registry.addFormatter(new StringToCourseDTOFormatter());
		registry.addFormatter(new StringToGroupDTOFormatter());
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/webjars/**", "/assets/**", "/styles/**")
				.addResourceLocations(CLASSPATH_RESOURCE_LOCATIONS);
	}
}
