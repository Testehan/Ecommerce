package com.testehan.ecommerce.backend.brand.controller;

import com.testehan.ecommerce.backend.brand.BrandService;
import com.testehan.ecommerce.backend.category.CategoryService;
import com.testehan.ecommerce.backend.util.AmazonS3Util;
import com.testehan.ecommerce.backend.util.paging.PagingAndSortingHelper;
import com.testehan.ecommerce.backend.util.paging.PagingAndSortingParam;
import com.testehan.ecommerce.common.entity.Brand;
import com.testehan.ecommerce.common.exception.BrandNotFoundException;
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
    public String listFirstPage(){
        return "redirect:/brands/page/1?sortField=name&sortOrder=asc";
    }

    @GetMapping("/brands/page/{pageNumber}")
    public String listBrandsByPage( @PagingAndSortingParam(moduleURL = "/brands", listName = "listBrands") PagingAndSortingHelper pagingAndSortingHelper,
            @PathVariable(name = "pageNumber") Integer pageNumber){

        brandService.listBrandsByPage(pageNumber, pagingAndSortingHelper);

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

            // before S3 migration
//            FileUploadUtil.deletePreviousFiles(uploadDir);
//            FileUploadUtil.saveFile(uploadDir,filename,multipartFile);
            AmazonS3Util.removeFolder(uploadDir);
            AmazonS3Util.uploadFile(uploadDir, filename, multipartFile.getInputStream());
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

            var brandLogosDir = "brand-logos/" + id;

            // before S3 migration
//            FileUploadUtil.deletePreviousFilesAndDirectory(brandLogosDir);
            AmazonS3Util.removeFolder(brandLogosDir);

            redirectAttributes.addFlashAttribute("message", "The brand with ID " + id + " has been deleted successfully");

        } catch (BrandNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }
        return "redirect:/brands";
    }
}
