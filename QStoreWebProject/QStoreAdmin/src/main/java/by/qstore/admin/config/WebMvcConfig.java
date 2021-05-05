package by.qstore.admin.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;
@Configuration

public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String userPhotosDirName = "user-photos";
        Path userPhotosDir = Paths.get(userPhotosDirName);

        String userPhotosPath = userPhotosDir.toFile().getAbsolutePath();

        registry.addResourceHandler("/" + userPhotosDirName + "/**")
                .addResourceLocations("file:/" + userPhotosPath + "/");



        String categoryImagesDirName = "../category-images";
        Path categoryImagesDir = Paths.get(categoryImagesDirName);

        String categoryImagesPath = categoryImagesDir.toFile().getAbsolutePath();

        registry.addResourceHandler("/category-images/**")
                .addResourceLocations("file:/" + categoryImagesPath + "/");



        String productImagesDirName = "../product-images";
        Path productImagesDir = Paths.get(productImagesDirName);

        String productImagesPath = productImagesDir.toFile().getAbsolutePath();

        registry.addResourceHandler("/product-images/**")
                .addResourceLocations("file:/" + productImagesPath + "/");
    }
}
