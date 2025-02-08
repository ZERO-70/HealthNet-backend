package com.server.HealthNet.SecurityConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // Applies to all endpoints
                        .allowedOrigins("*") // Allow requests from any origin
                        .allowedMethods("*") // Allow all HTTP methods (GET, POST, PUT, etc.)
                        .allowedHeaders("*") // Allow all headers
                        .allowCredentials(false); // Disable credentials (cookies, etc.)
            }
        };
    }
}