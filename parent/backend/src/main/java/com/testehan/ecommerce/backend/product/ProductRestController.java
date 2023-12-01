package com.testehan.ecommerce.backend.product;

import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductRestController {

    private ProductService productService;

    public ProductRestController(ProductService productService){
        this.productService = productService;
    }

    @PostMapping("/products/check_unique")
    public String checkDuplicateName(@Param("id") Integer id, @Param("name") String name) {
        return productService.isNameUnique(id, name) ? "OK" : "DuplicateName";
    }
}
