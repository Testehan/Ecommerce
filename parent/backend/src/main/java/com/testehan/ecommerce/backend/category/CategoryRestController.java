package com.testehan.ecommerce.backend.category;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CategoryRestController {

    private final CategoryService categoryService;

    public CategoryRestController(CategoryService categoryService){
        this.categoryService = categoryService;
    }

    @PostMapping("/categories/check_unique")
    public String checkUniqueCategoryNameAndAlias(Integer id, String name, String alias) {
        return categoryService.checkUnique(id, name, alias);
    }
}
