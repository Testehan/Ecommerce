package com.testehan.ecommerce.backend.product;

import com.testehan.ecommerce.backend.util.FileUploadUtil;
import com.testehan.ecommerce.common.entity.product.Product;
import com.testehan.ecommerce.common.entity.product.ProductImage;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ProductSaveHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductSaveHelper.class);

    static void deleteExtraImagesThatWereRemovedInForm(Product product) {
        String uploadDirExtras = "product-images/" + product.getId() + "/extras";
        Path path = Paths.get(uploadDirExtras);

        try{
            Files.list(path).forEach(image -> {
                String imageName = image.toFile().getName();
                if (!product.containsImageName(imageName)){
                    try {
                        Files.delete(image);
                        LOGGER.info("Deleted extra image " + imageName + " from " + uploadDirExtras);
                    } catch (IOException e) {
                        LOGGER.error("Could not delete file " + imageName + " from " + uploadDirExtras);
                    }
                }
            });
        } catch (IOException e) {
            LOGGER.error("Problem listing the files from " + uploadDirExtras);
        }
    }

    static void setExistingExtraImageNames(Product product, String[] imageIds, String[] imageNames) {
        if (imageIds != null && imageIds.length>0){
            Set<ProductImage> extraProductImages = new HashSet<>();
            for (int i = 0; i < imageIds.length; i++){
                Integer imageId = Integer.parseInt(imageIds[i]);
                String imageName = imageNames[i];
                extraProductImages.add(new ProductImage(imageId,imageName,product));
            }

            product.setImages(extraProductImages);
        }
    }

    static void setProductDetails(Product product, String[] detailNames, String[] detailValues, String[] detailIds) {
        if (Objects.nonNull(detailNames) && detailNames.length>0){
            for (int count =0; count < detailNames.length; count++){
                String name = detailNames[count];
                String value = detailValues[count];
                Integer id = Integer.parseInt(detailIds[count]);

                if (id != 0){   // means existing product detail, perhaps changed or unchanged
                    product.updateExistingProductDetail(id,name,value);
                } else {      // means new product detail added in form
                    if (!Strings.isBlank(name) && !Strings.isBlank(value)) {
                        product.addNewProductDetail(name, value);
                    }
                }
            }
        }
    }

    static void saveUploadedImages(Product product, MultipartFile mainImage, MultipartFile[] extraImages) throws IOException {
        if (!mainImage.isEmpty()) {
            String filename = StringUtils.cleanPath(mainImage.getOriginalFilename());
            String uploadDir = "product-images/" + product.getId();

            FileUploadUtil.deletePreviousFiles(uploadDir);
            FileUploadUtil.saveFile(uploadDir, filename, mainImage);
        }

        if (extraImages.length>0) {
            String uploadDirExtras = "product-images/" + product.getId() + "/extras";
            for (MultipartFile extraImage : extraImages) {
                if (extraImage.isEmpty()) continue;

                String filename = StringUtils.cleanPath(extraImage.getOriginalFilename());
                FileUploadUtil.saveFile(uploadDirExtras, filename, extraImage);
            }
        }
    }

    static void setNewExtraImageNames(MultipartFile[] extraImages, Product product) {
        if (extraImages.length>0) {
            for (MultipartFile extraImage : extraImages) {
                if (!extraImage.isEmpty()) {
                    String filename = StringUtils.cleanPath(extraImage.getOriginalFilename());

                    if (!product.containsImageName(filename)) {
                        product.addExtraImage(filename);
                    }
                }
            }
        }
    }

    static void setMainImageName(MultipartFile mainImage, Product product) {
        if (!mainImage.isEmpty()) {
            String filename = StringUtils.cleanPath(mainImage.getOriginalFilename());
            product.setMainImage(filename);
        }
    }
}
