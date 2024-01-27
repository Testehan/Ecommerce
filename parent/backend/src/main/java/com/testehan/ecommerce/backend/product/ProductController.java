package com.testehan.ecommerce.backend.product;

import com.testehan.ecommerce.backend.brand.BrandService;
import com.testehan.ecommerce.backend.category.CategoryService;
import com.testehan.ecommerce.backend.security.ShopUserDetails;
import com.testehan.ecommerce.backend.util.FileUploadUtil;
import com.testehan.ecommerce.common.entity.Product;
import com.testehan.ecommerce.common.exception.ProductNotFoundException;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Objects;

import static com.testehan.ecommerce.backend.product.ProductSaveHelper.*;

@Controller
public class ProductController {
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
        return listProductsByPage(1,model, "name", "asc", NO_KEYWORD, ALL_CATEGORIES);
    }

    @GetMapping("/products/page/{pageNumber}")
    public String listProductsByPage(@PathVariable(name = "pageNumber") Integer pageNumber, Model model,
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
        model.addAttribute("numberOfExistingExtraImages",0);

        return "products/product_form";
    }

    @PostMapping("/products/save")
    public String saveProduct(Product product, RedirectAttributes redirectAttributes,
                              @RequestParam(value="fileImage", required = false) MultipartFile mainImage,
                              @RequestParam(value="extraImage", required = false) MultipartFile[] extraImages,
                              @RequestParam(name = "detailIds", required = false) String[] detailIds,
                              @RequestParam(name = "detailNames", required = false) String[] detailNames,
                              @RequestParam(name = "detailValues", required = false) String[] detailValues,
                              @RequestParam(name = "imageIds", required = false) String[] imageIds,
                              @RequestParam(name = "imageNames", required = false) String[] imageNames,
                              @AuthenticationPrincipal ShopUserDetails loggedUser) throws IOException
    {
        // salesperson can only update price related fields from UI...
        if (loggedUser.hasRole("Salesperson")){
            productService.saveProductPrice(product);
            redirectAttributes.addFlashAttribute("message","The product price fields have been saved successfully.");
            return "redirect:/products";
        }

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
