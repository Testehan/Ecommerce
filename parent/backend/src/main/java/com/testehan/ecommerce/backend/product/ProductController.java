package com.testehan.ecommerce.backend.product;

import com.testehan.ecommerce.backend.brand.BrandService;
import com.testehan.ecommerce.backend.category.CategoryService;
import com.testehan.ecommerce.backend.util.FileUploadUtil;
import com.testehan.ecommerce.common.entity.Product;
import com.testehan.ecommerce.common.entity.ProductImage;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Controller
public class ProductController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);
    private static final String NO_KEYWORD="";
    public static final int ALL_CATEGORIES = 0;

    private ProductService productService;
    private BrandService brandService;
    private CategoryService categoryService;

    public ProductController(ProductService productService, BrandService brandService,CategoryService categoryService){
        this.productService = productService;
        this.brandService = brandService;
        this.categoryService = categoryService;
    }

    @GetMapping("/products")
    public String listFirstPage(Model model){
        return listBrandsByPage(1,model, "name", "asc", NO_KEYWORD, ALL_CATEGORIES);
    }

    @GetMapping("/products/page/{pageNumber}")
    public String listBrandsByPage(@PathVariable(name = "pageNumber") Integer pageNumber, Model model,
                                   @Param("sortField")String sortField, @Param("sortOrder")String sortOrder,
                                   @Param("keyword")String keyword,
                                   @Param("categoryId")Integer categoryId ){

        var pageOfProducts = productService.listProductsByPage(pageNumber, sortField, sortOrder, keyword,categoryId);
        model.addAttribute("listProducts",pageOfProducts.getContent());

        var listCategories = categoryService.listCategoriesInForm();

        long startCount = (pageNumber-1)* ProductService.PRODUCTS_PER_PAGE + 1;
        long endCount = startCount + ProductService.PRODUCTS_PER_PAGE - 1;
        if (endCount > pageOfProducts.getTotalElements()){
            endCount = pageOfProducts.getTotalElements();
        }
        model.addAttribute("startCount",startCount);
        model.addAttribute("endCount",endCount);
        model.addAttribute("currentPage",pageNumber);

        model.addAttribute("totalItems",pageOfProducts.getTotalElements());
        model.addAttribute("totalPages", pageOfProducts.getTotalPages());

        String reverseSortOrder = sortOrder.equalsIgnoreCase("asc") ? "desc" : "asc";
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("reverseSortOrder", reverseSortOrder);
        model.addAttribute("keyword", keyword);
        model.addAttribute("listCategories", listCategories);
        if (Objects.nonNull(categoryId)) {
            model.addAttribute("categoryId", categoryId);
        }

        // because first is folder from "templates"
        return "products/products";
    }

    @GetMapping("/products/new")
    public String newProduct(Model model){
        var listBrands = brandService.findAll();
        Product product = new Product();
        product.setInStock(true);
        product.setEnabled(true);

        model.addAttribute("product",product);
        model.addAttribute("listBrands",listBrands);
        model.addAttribute("pageTitle","Create new Product");
        Integer numberOfExistingExtraImages = product.getImages().size();
        model.addAttribute("numberOfExistingExtraImages",numberOfExistingExtraImages);

        return "products/product_form";
    }

    @PostMapping("/products/save")
    public String saveProduct(Product product, RedirectAttributes redirectAttributes,
                              @RequestParam("fileImage") MultipartFile mainImage,
                              @RequestParam("extraImage") MultipartFile[] extraImages,
                              @RequestParam(name = "detailIds", required = false) String[] detailIds,
                              @RequestParam(name = "detailNames", required = false) String[] detailNames,
                              @RequestParam(name = "detailValues", required = false) String[] detailValues,
                              @RequestParam(name = "imageIds", required = false) String[] imageIds,
                              @RequestParam(name = "imageNames", required = false) String[] imageNames) throws IOException
    {
        setMainImageName(mainImage,product);
        setExistingExtraImageNames(product,imageIds,imageNames);
        setNewExtraImageNames(extraImages,product);
        setProductDetails(product,detailNames,detailValues, detailIds);
        
        Product savedProduct =  productService.save(product);
        
        saveUploadedImages(savedProduct, mainImage, extraImages);

        deleteExtraImagesThatWereRemovedInForm(product);

        redirectAttributes.addFlashAttribute("message","The product has been saved successfully.");
        return "redirect:/products";
    }

    private void deleteExtraImagesThatWereRemovedInForm(Product product) {
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

    private void setExistingExtraImageNames(Product product, String[] imageIds, String[] imageNames) {
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

    private void setProductDetails(Product product, String[] detailNames, String[] detailValues, String[] detailIds) {
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

    private void saveUploadedImages(Product product, MultipartFile mainImage, MultipartFile[] extraImages) throws IOException {
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

    private void setNewExtraImageNames(MultipartFile[] extraImages, Product product) {
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

    private void setMainImageName(MultipartFile mainImage, Product product) {
        if (!mainImage.isEmpty()) {
            String filename = StringUtils.cleanPath(mainImage.getOriginalFilename());
            product.setMainImage(filename);
        }
    }

    @GetMapping("/products/{id}/enabled/{status}")
    public String updatedProductEnableStatus(@PathVariable(name = "id") Integer id,
                                            @PathVariable(name = "status") boolean enabled,
                                            RedirectAttributes redirectAttributes)  {

        productService.updateEnabledStatus(id,enabled);

        String status = enabled ? "enabled" : "disabled";
        String message = "The category with ID " + id + " has been " + status;

        redirectAttributes.addFlashAttribute("message",message);
        return "redirect:/products";
    }

    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {

        try {
            productService.delete(id);

            String uploadDir = "product-images/" + id;
            String uploadDirExtraImages = uploadDir + "/extras";
            FileUploadUtil.deletePreviousFilesAndDirectory(uploadDirExtraImages);
            FileUploadUtil.deletePreviousFilesAndDirectory(uploadDir);

            redirectAttributes.addFlashAttribute("message", "The product with ID " + id + " has been deleted successfully");

        } catch (ProductNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }
        return "redirect:/products";
    }
    @GetMapping("/products/edit/{id}")
    public String updateProduct(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            var product = productService.getById(id);
            var listBrands = brandService.findAll();

            model.addAttribute("product", product);
            model.addAttribute("pageTitle", "Edit Product with ID " + id);
            model.addAttribute("listBrands",listBrands);
            Integer numberOfExistingExtraImages = product.getImages().size();
            model.addAttribute("numberOfExistingExtraImages",numberOfExistingExtraImages);

            return "products/product_form";

        } catch (ProductNotFoundException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/products";
        }
    }

    @GetMapping("/products/detail/{id}")
    public String viewProductDetails(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            var product = productService.getById(id);

            model.addAttribute("product", product);

            return "products/product_detail_modal";

        } catch (ProductNotFoundException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/products";
        }
    }


}
