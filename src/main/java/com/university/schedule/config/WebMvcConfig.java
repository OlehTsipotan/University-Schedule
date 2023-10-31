package com.university.schedule.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.format.Formatter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Set;

@Configuration
@EnableWebMvc
@EnableAutoConfiguration(exclude = {ErrorMvcAutoConfiguration.class})
@Profile("dev")
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
