package com.testehan.ecommerce.backend.product;

import com.testehan.ecommerce.common.exception.ProductNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductRestController {

    private ProductService productService;

    public ProductRestController(ProductService productService){
        this.productService = productService;
    }

    @PostMapping("/products/check_unique")
    public String checkDuplicateName(Integer id, String name) {
        return productService.isNameUnique(id, name) ? "OK" : "DuplicateName";
    }

    @GetMapping("/products/get/{id}")
    public ProductDTO getProductInfo(@PathVariable("id") Integer id) throws ProductNotFoundException {

        var product = productService.getById(id);
        return new ProductDTO(product.getName(), product.getMainImagePath(),
                (long) product.getDiscountedPrice(), product.getCost());
    }
}
