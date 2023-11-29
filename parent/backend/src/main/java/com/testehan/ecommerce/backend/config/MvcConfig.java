package com.testehan.ecommerce.backend.config;

import com.testehan.ecommerce.backend.category.DatabaseUpdates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Autowired
    private DatabaseUpdates databaseUpdates;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path userPhotos = Paths.get("user-photos");
        String userPhotosPath = userPhotos.toFile().getAbsolutePath();

        // after getting the user-photos local folder, we make a configuration in the registry so that each time
        // a resource from that folder, is requested from the browser, it will know to take it from the right location
        // userPhotosPath (configured in addResourceLocations)
        registry.addResourceHandler("/user-photos/**")
                .addResourceLocations("file:"+ userPhotosPath +"/");

        Path categoryImages = Paths.get("category-images");
        String categoryImagesPath = categoryImages.toFile().getAbsolutePath();

        registry.addResourceHandler("/category-images/**")
                .addResourceLocations("file:"+ categoryImagesPath +"/");
    }

    @Bean
    CommandLineRunner runDatabaseUpdates() {
        return args -> {
            databaseUpdates.alterTableCategoryDropUnwantedUniqueConstraint();
        };
    }
}
