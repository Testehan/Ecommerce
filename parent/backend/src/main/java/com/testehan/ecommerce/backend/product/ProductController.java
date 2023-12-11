package com.testehan.ecommerce.backend.product;

import com.testehan.ecommerce.backend.brand.BrandService;
import com.testehan.ecommerce.backend.util.FileUploadUtil;
import com.testehan.ecommerce.common.entity.Product;
import org.apache.logging.log4j.util.Strings;
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
import java.util.Objects;

@Controller
public class ProductController {

    private ProductService productService;
    private BrandService brandService;

    public ProductController(ProductService productService, BrandService brandService){
        this.productService = productService;
        this.brandService = brandService;
    }

    @GetMapping("/products")
    public String listAll(Model model){
        var listProducts = productService.listAllProducts();
        model.addAttribute("listProducts",listProducts);
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

        return "products/product_form";
    }

    @PostMapping("/products/save")
    public String saveProduct(Product product, RedirectAttributes redirectAttributes,
                              @RequestParam("fileImage") MultipartFile mainImage,
                              @RequestParam("extraImage") MultipartFile[] extraImages,
                              @RequestParam(name = "detailNames", required = false) String[] detailNames,
                              @RequestParam(name = "detailValues", required = false) String[] detailValues) throws IOException
    {
        setMainImageName(mainImage,product);
        setExtraImageNames(extraImages,product);
        setProductDetails(product,detailNames,detailValues);
        
        Product savedProduct =  productService.save(product);
        
        saveUploadedImages(savedProduct, mainImage, extraImages);


        redirectAttributes.addFlashAttribute("message","The product has been saved successfully.");
        return "redirect:/products";
    }

    private void setProductDetails(Product product, String[] detailNames, String[] detailValues) {
        if (Objects.nonNull(detailNames) && detailNames.length>0){
            for (int count =0; count < detailNames.length; count++){
                String name = detailNames[count];
                String value = detailValues[count];
                if (!Strings.isBlank(name) && !Strings.isBlank(value)){
                    product.addProductDetail(name,value);
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

    private void setExtraImageNames(MultipartFile[] extraImages, Product product) {
        if (extraImages.length>0) {
            for (MultipartFile extraImage : extraImages) {
                if (!extraImage.isEmpty()) {
                    String filename = StringUtils.cleanPath(extraImage.getOriginalFilename());
                    product.addExtraImage(filename);
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


}