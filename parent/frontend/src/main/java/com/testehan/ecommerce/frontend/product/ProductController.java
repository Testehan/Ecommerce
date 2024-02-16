package com.testehan.ecommerce.frontend.product;

import com.testehan.ecommerce.common.entity.product.Product;
import com.testehan.ecommerce.common.exception.CategoryNotFoundException;
import com.testehan.ecommerce.common.exception.ProductNotFoundException;
import com.testehan.ecommerce.frontend.category.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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
        try{
            var category = categoryService.getCategoryByAlias(categoryAlias);

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

            return "product/products_by_category";
        }
        catch (CategoryNotFoundException e){
            return "error/404";
        }
    }

    @GetMapping("/p/{product_alias}")
    public String viewProductDetails(@PathVariable("product_alias")String productAlias, Model model){
        try{
            var product = productService.getProductByAlias(productAlias);

            var listCategoryParents = categoryService.getCategoryParents(product.getCategory());

            model.addAttribute("product",product);
            model.addAttribute("pageTitle",product.getProductShortName());
            model.addAttribute("listCategoryParents",listCategoryParents);

            return "product/product_detail";

        } catch (ProductNotFoundException e){
            return "error/404";
        }

    }

    @GetMapping("/search")
    public String searchFirstPage(String keyword, Model model){
        return searchByPage(1,keyword,model);
    }
    @GetMapping("/search/page/{pageNum}")
    public String searchByPage(@PathVariable("pageNum") int pageNum, String keyword, Model model){

        var pageProducts = productService.search(keyword,pageNum);
        model.addAttribute("pageTitle",keyword+" - Search Result");
        model.addAttribute("keyword",keyword);

        long startCount = (pageNum-1) * ProductService.PRODUCT_SEARCH_RESULTS_PER_PAGE + 1;
        long endCount = startCount + ProductService.PRODUCT_SEARCH_RESULTS_PER_PAGE - 1;
        if (endCount > pageProducts.getTotalElements()){
            endCount = pageProducts.getTotalElements();
        }
        model.addAttribute("startCount",startCount);
        model.addAttribute("endCount",endCount);
        model.addAttribute("currentPage",pageNum);

        model.addAttribute("totalItems", pageProducts.getTotalElements());
        model.addAttribute("totalPages", pageProducts.getTotalPages());
        model.addAttribute("listResult", pageProducts.getContent());

        return "product/search_result";
    }
}
