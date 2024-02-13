package com.testehan.ecommerce.backend.category;

import com.testehan.ecommerce.backend.category.export.CategoryCsvExporter;
import com.testehan.ecommerce.backend.util.FileUploadUtil;
import com.testehan.ecommerce.common.entity.Category;
import com.testehan.ecommerce.common.exception.CategoryNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.List;

@Controller
public class CategoryController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryController.class);

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/categories")
    public String listFirstPage(String sortDir, Model model){
        return listCategoriesByPage(1,model,sortDir,null);
    }

    @GetMapping("/categories/page/{pageNumber}")
    public String listCategoriesByPage(@PathVariable(name = "pageNumber") Integer pageNumber, Model model,
                                       String sortOrder, String keyword)
    {
        if (sortOrder == null || sortOrder.isEmpty()){
            sortOrder="asc";
        }
        var categoryPageInfo = new CategoryPageInfo();
        List<Category> categoryList = categoryService.findAllCategories(categoryPageInfo, sortOrder, pageNumber, keyword);
        String reverseSortOrder = sortOrder.equals("asc") ? "desc" : "asc";

        model.addAttribute("listCategories", categoryList);
        model.addAttribute("reverseSortOrder", reverseSortOrder);

        model.addAttribute("totalPages", categoryPageInfo.getTotalPages());
        model.addAttribute("totalItems", categoryPageInfo.getTotalElements());
        model.addAttribute("currentPage", pageNumber);

        model.addAttribute("sortField", "name");
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("keyword", keyword);

        long startCount = (pageNumber-1)* CategoryService.ROOT_CATEGORIES_PER_PAGE + 1;
        long endCount = startCount + CategoryService.ROOT_CATEGORIES_PER_PAGE - 1;
        if (endCount > categoryPageInfo.getTotalElements()){
            endCount = categoryPageInfo.getTotalElements();
        }
        model.addAttribute("startCount",startCount);
        model.addAttribute("endCount",endCount);
        model.addAttribute("moduleURL", "/categories");

        return "categories/categories";


    }

    @GetMapping("/categories/new")
    public String newCategory(Model model){
        List<Category> listFormCategories = categoryService.listCategoriesInForm();

        model.addAttribute("category", new Category());
        model.addAttribute("listCategories", listFormCategories);
        model.addAttribute("pageTitle", "Create new Category");

        return "categories/category_form";
    }

    @PostMapping("/categories/save")
    public String saveCategory(Category category, @RequestParam("fileImage")MultipartFile multipartFile,
                               RedirectAttributes redirectAttributes) throws IOException
    {

        if (!multipartFile.isEmpty()) {
            String filename = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            category.setImage(filename);
            Category savedCategory = categoryService.save(category);
            String uploadDir = "category-images/" + savedCategory.getId();

            FileUploadUtil.deletePreviousFiles(uploadDir);
            FileUploadUtil.saveFile(uploadDir, filename, multipartFile);
        } else {
            categoryService.save(category);
        }

        redirectAttributes.addFlashAttribute("message","The category was saved!");
        return "redirect:/categories";
    }

    @GetMapping("/categories/edit/{id}")
    public String editCategory(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes)  {
        try {
            var category = categoryService.findById(id);
            var listFormCategories = categoryService.listCategoriesInForm();

            model.addAttribute("category",category);
            model.addAttribute("listCategories", listFormCategories);
            model.addAttribute("pageTitle","Edit category with id " + category.getId());

            return "categories/category_form";
        } catch(CategoryNotFoundException e){
            redirectAttributes.addFlashAttribute("message",e.getMessage());
            return "redirect:/categories";
        }
    }

    @GetMapping("/categories/{id}/enabled/{status}")
    public String updatedUserEnableStatus(@PathVariable(name = "id") Integer id,
                                          @PathVariable(name = "status") boolean enabled,
                                          RedirectAttributes redirectAttributes)  {

        categoryService.updateEnabledStatus(id,enabled);

        String status = enabled ? "enabled" : "disabled";
        String message = "The category with ID " + id + " has been " + status;

        redirectAttributes.addFlashAttribute("message",message);
        return "redirect:/categories";
    }

    @GetMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {

        try {
            categoryService.delete(id);

            String categoryImagesDir = "category-images/" + id;
            FileUploadUtil.deletePreviousFilesAndDirectory(categoryImagesDir);

            redirectAttributes.addFlashAttribute("message", "The category with ID " + id + " has been deleted successfully");

        } catch (CategoryNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }
        return "redirect:/categories";
    }

    @GetMapping("/categories/export/csv")
    public void exportToCSV(HttpServletResponse response) throws IOException {
        var categories = categoryService.listCategoriesInForm();
        var exporter = new CategoryCsvExporter();
        exporter.export(categories,response);
    }

    @GetMapping("/categories/export/excel")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        LOGGER.warn("Export to excel is not yet supported. A CSV file is provided instead.");
        exportToCSV(response);
    }

    @GetMapping("/categories/export/pdf")
    public void exportToPdf(HttpServletResponse response) throws IOException {
        LOGGER.warn("Export to pdf is not yet supported. A CSV file is provided instead.");
        exportToCSV(response);
    }
}
