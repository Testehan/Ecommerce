package com.testehan.ecommerce.backend.brand.controller;

import com.testehan.ecommerce.backend.brand.BrandNotFoundRestException;
import com.testehan.ecommerce.backend.brand.BrandService;
import com.testehan.ecommerce.backend.brand.CategoryDTO;
import com.testehan.ecommerce.common.exception.BrandNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class BrandRestController {

    private final BrandService brandService;

    public BrandRestController(BrandService brandService){
        this.brandService = brandService;
    }

    @PostMapping("/brands/check_unique")
    public String checkDuplicateName(Integer id, String name) {
        return brandService.isNameUnique(id, name) ? "OK" : "DuplicateName";
    }

    @GetMapping("/brands/{id}/categories")
    public List<CategoryDTO> listCategoriesByBrand(@PathVariable("id") Integer brandId) throws BrandNotFoundRestException {
        List<CategoryDTO> categoriesResult = new ArrayList<>();
        try {
            var brand = brandService.findById(brandId);
            brand.getCategories().forEach(c -> categoriesResult.add(new CategoryDTO(c.getId(),c.getName())));
        } catch (BrandNotFoundException e) {
            throw new BrandNotFoundRestException();
        }

        return categoriesResult;
    }
}
