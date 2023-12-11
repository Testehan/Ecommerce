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
        exposeDirectory("user-photos", registry, "/user-photos/**");

        exposeDirectory("category-images", registry, "/category-images/**");

        exposeDirectory("brand-logos", registry, "/brand-logos/**");

        exposeDirectory("product-images", registry, "/product-images/**");
    }

    private static void exposeDirectory(String directoryName, ResourceHandlerRegistry registry, String logicalPath) {
        Path categoryImages = Paths.get(directoryName);
        String categoryImagesPath = categoryImages.toFile().getAbsolutePath();

        registry.addResourceHandler(logicalPath)
                .addResourceLocations("file:" + categoryImagesPath + "/");
    }

    @Bean
    CommandLineRunner runDatabaseUpdates() {
        return args -> {
            databaseUpdates.alterTableCategoryDropUnwantedUniqueConstraint();
        };
    }
}
