package com.testehan.ecommerce.frontend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        exposeDirectory("category-images", registry, "/category-images/**");

        exposeDirectory("brand-logos", registry, "/brand-logos/**");

        exposeDirectory("product-images", registry, "/product-images/**");

        exposeDirectory("site-logo", registry, "/site-logo/**");
    }

    private static void exposeDirectory(String directoryName, ResourceHandlerRegistry registry, String logicalPath) {
        Path categoryImages = Paths.get(directoryName);
        String categoryImagesPath = categoryImages.toFile().getAbsolutePath();

        registry.addResourceHandler(logicalPath)
                .addResourceLocations("file:" + categoryImagesPath + "/");
    }

}
