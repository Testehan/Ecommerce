package com.testehan.ecommerce.backend.brand.controller;

import com.testehan.ecommerce.common.exception.BrandNotFoundException;
import com.testehan.ecommerce.backend.brand.BrandService;
import com.testehan.ecommerce.backend.category.CategoryService;
import com.testehan.ecommerce.backend.util.FileUploadUtil;
import com.testehan.ecommerce.common.entity.Brand;
import org.springframework.data.repository.query.Param;
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

@Controller
public class BrandController {

    private static final String NO_KEYWORD="";
    private BrandService brandService;
    private CategoryService categoryService;

    public BrandController(BrandService brandService, CategoryService categoryService) {
        this.brandService = brandService;
        this.categoryService = categoryService;
    }

    @GetMapping("/brands")
    public String listFirstPage(Model model){
        return listBrandsByPage(1,model, "name", "asc", NO_KEYWORD);
    }

    @GetMapping("/brands/page/{pageNumber}")
    public String listBrandsByPage(@PathVariable(name = "pageNumber") Integer pageNumber, Model model,
                                   @Param("sortField")String sortField, @Param("sortOrder")String sortOrder,
                                   @Param("keyword")String keyword){
        var pageOfBrands = brandService.listBrandsByPage(pageNumber, sortField, sortOrder, keyword);
        model.addAttribute("listBrands",pageOfBrands.getContent());

        long startCount = (pageNumber-1)* BrandService.BRAND_PAGE_SIZE + 1;
        long endCount = startCount + BrandService.BRAND_PAGE_SIZE - 1;
        if (endCount > pageOfBrands.getTotalElements()){
            endCount = pageOfBrands.getTotalElements();
        }
        model.addAttribute("startCount",startCount);
        model.addAttribute("endCount",endCount);
        model.addAttribute("currentPage",pageNumber);

        model.addAttribute("totalItems",pageOfBrands.getTotalElements());
        model.addAttribute("totalPages", pageOfBrands.getTotalPages());

        String reverseSortOrder = sortOrder.equalsIgnoreCase("asc") ? "desc" : "asc";
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("reverseSortOrder", reverseSortOrder);
        model.addAttribute("keyword", keyword);

        // because first is folder from "templates"
        return "brands/brands";
    }


    @GetMapping("/brands/new")
    public String newBrand(Model model){
        var listFormCategories = categoryService.listCategoriesInForm();

        model.addAttribute("brand", new Brand());
        model.addAttribute("listCategories", listFormCategories);
        model.addAttribute("pageTitle", "Create new Brand");

        return "brands/brand_form";
    }

    @PostMapping("/brands/save")
    public String saveBrand(Brand brand, RedirectAttributes redirectAttributes,
                           @RequestParam("fileImage") MultipartFile multipartFile) throws IOException
    {
        // this is for the image
        if (!multipartFile.isEmpty()){
            String filename = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            brand.setLogo(filename);
            Brand savedBrand =  brandService.save(brand);
            String uploadDir = "brand-logos/" + savedBrand.getId();

            FileUploadUtil.deletePreviousFiles(uploadDir);
            FileUploadUtil.saveFile(uploadDir,filename,multipartFile);
        } else {
            brandService.save(brand);
        }

        redirectAttributes.addFlashAttribute("message","The brand was saved!");
        return "redirect:/brands";
    }

    @GetMapping("/brands/edit/{id}")
    public String editBrand(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes)  {
        try {
            var brand = brandService.findById(id);
            var listFormCategories = categoryService.listCategoriesInForm();

            model.addAttribute("brand",brand);
            model.addAttribute("listCategories", listFormCategories);
            model.addAttribute("pageTitle","Edit brand with id " + brand.getId());

            return "brands/brand_form";
        } catch(BrandNotFoundException e){
            redirectAttributes.addFlashAttribute("message",e.getMessage());
            return "redirect:/brands";
        }
    }

    @GetMapping("/brands/delete/{id}")
    public String deleteBrand(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {

        try {
            brandService.deleteBrand(id);

            String brandLogosDir = "brand-logos/" + id;
            FileUploadUtil.deletePreviousFilesAndDirectory(brandLogosDir);

            redirectAttributes.addFlashAttribute("message", "The brand with ID " + id + " has been deleted successfully");

        } catch (BrandNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }
        return "redirect:/brands";
    }
}
