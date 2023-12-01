package com.testehan.ecommerce.backend.product;

import com.testehan.ecommerce.backend.brand.BrandService;
import com.testehan.ecommerce.common.entity.Product;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String saveProduct(Product product, RedirectAttributes redirectAttributes){
        System.out.println(product.getName());
        System.out.println(product.getBrand().getName());
        System.out.println(product.getCategory().getName());
        return "redirect:/products";
    }

}
