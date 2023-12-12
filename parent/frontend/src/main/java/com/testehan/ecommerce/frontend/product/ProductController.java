package com.testehan.ecommerce.frontend.product;

import com.testehan.ecommerce.common.entity.Product;
import com.testehan.ecommerce.frontend.category.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Objects;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/c/{category_alias}")
    public String viewCategoryFirstPage(@PathVariable("category_alias")String categoryAlias, Model model){

        return viewCategoryByPage(categoryAlias,1,model);
    }

    @GetMapping("/c/{category_alias}/page/{pageNumber}")
    public String viewCategoryByPage(@PathVariable("category_alias")String categoryAlias,
                               @PathVariable("pageNumber")int  pageNumber, Model model){
        var category = categoryService.getCategoryByAlias(categoryAlias);
        if (Objects.isNull(category)){
            return "error/404";
        }

        var listCategoryParents = categoryService.getCategoryParents(category);
        Page<Product> pageProducts = productService.listByCategory(pageNumber,category.getId());
        var listProducts = pageProducts.getContent();

        model.addAttribute("pageTitle",category.getName());
        model.addAttribute("listCategoryParents",listCategoryParents);

        long startCount = (pageNumber-1)* ProductService.PRODUCTS_PER_PAGE + 1;
        long endCount = startCount + ProductService.PRODUCTS_PER_PAGE - 1;
        if (endCount > pageProducts.getTotalElements()){
            endCount = pageProducts.getTotalElements();
        }
        model.addAttribute("startCount",startCount);
        model.addAttribute("endCount",endCount);
        model.addAttribute("currentPage",pageNumber);

        model.addAttribute("totalItems",pageProducts.getTotalElements());
        model.addAttribute("totalPages", pageProducts.getTotalPages());
        model.addAttribute("listProducts", listProducts);
        model.addAttribute("category", category);

        return "products_by_category";
    }
}
