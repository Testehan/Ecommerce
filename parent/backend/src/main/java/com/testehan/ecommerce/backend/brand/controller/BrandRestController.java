package com.testehan.ecommerce.backend.brand.controller;

import com.testehan.ecommerce.backend.brand.BrandService;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BrandRestController {

    private final BrandService brandService;

    public BrandRestController(BrandService brandService){
        this.brandService = brandService;
    }

    @PostMapping("/brands/check_unique")
    public String checkDuplicateName(@Param("id") Integer id, @Param("name") String name) {
        return brandService.isNameUnique(id, name) ? "OK" : "DuplicateName";
    }
}
