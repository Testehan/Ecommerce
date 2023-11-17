package com.testehan.ecommerce.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path userPhotos = Paths.get("user-photos");
        String userPhotosPath = userPhotos.toFile().getAbsolutePath();

        // after getting the user-photos local folder, we make a configuration in the registry so that each time
        // a resource from that folder, is requested from the browser, it will know to take it from the right location
        // userPhotosPath (configured in addResourceLocations)
        registry.addResourceHandler("/user-photos/**")
                .addResourceLocations("file:"+ userPhotosPath +"/");
    }
}
