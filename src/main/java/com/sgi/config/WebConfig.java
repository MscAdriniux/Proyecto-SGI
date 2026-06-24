package com.sgi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // El prefijo "file:" le dice a Spring que busque físicamente en la carpeta del proyecto
        registry.addResourceHandler("/uploads/evidencias/**")
                .addResourceLocations("file:uploads/evidencias/");
    }
}