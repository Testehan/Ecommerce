package com.testehan.ecommerce.backend.user.controller;

import com.testehan.ecommerce.backend.user.UserService;
import com.testehan.ecommerce.backend.user.export.UserCsvExporter;
import com.testehan.ecommerce.backend.user.export.UserExcelExporter;
import com.testehan.ecommerce.backend.user.export.UserPdfExporter;
import com.testehan.ecommerce.backend.util.AmazonS3Util;
import com.testehan.ecommerce.backend.util.paging.PagingAndSortingHelper;
import com.testehan.ecommerce.backend.util.paging.PagingAndSortingParam;
import com.testehan.ecommerce.common.entity.User;
import com.testehan.ecommerce.common.exception.UserNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
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
public class UserController {

    private static final String NO_KEYWORD="";

    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("/users")
    public String listFirstPage(){
        // BY DEFAULT we sort by firstName ascending
        return "redirect:/users/page/1?sortField=firstName&sortOrder=asc";
    }

    @GetMapping("/users/page/{pageNumber}")
    public String listUsersByPage(@PagingAndSortingParam(listName = "listUsers", moduleURL="/users") PagingAndSortingHelper pagingAndSortingHelper,
                                  @PathVariable(name = "pageNumber") Integer pageNumber){

        userService.listUsersByPage(pageNumber, pagingAndSortingHelper);
        // because first is folder from "templates"
        return "users/users";
    }

    @GetMapping("/users/new")
    public String newUser(Model model){
        var user = new User();
        var roles = userService.findAllRoles();

        user.setEnabled(true);
        model.addAttribute("user",user);
        model.addAttribute("listRoles",roles);
        model.addAttribute("pageTitle","Create new user");

        return "users/user_form";
    }

    @PostMapping("/users/save")
    public String saveUser(User user, RedirectAttributes redirectAttributes,
                           @RequestParam("image")MultipartFile multipartFile) throws IOException
    {
                                                        // this is for the image
       if (!multipartFile.isEmpty()){
           String filename = StringUtils.cleanPath(multipartFile.getOriginalFilename());
           user.setPhoto(filename);
           User savedUser =  userService.save(user);
           String uploadDir = "user-photos/" + savedUser.getId();

// before S3 migration
//           FileUploadUtil.deletePreviousFiles(uploadDir);
//           FileUploadUtil.saveFile(uploadDir,filename,multipartFile);
           AmazonS3Util.removeFolder(uploadDir);
           AmazonS3Util.uploadFile(uploadDir, filename, multipartFile.getInputStream());
       } else {
           // means that when the user was created, no picture was provided
           if (user.getPhoto()==null || user.getPhoto().isEmpty()) {
               user.setPhoto(null);
           }
           userService.save(user);
       }

        redirectAttributes.addFlashAttribute("message","The user was saved!");
       // cause after editing or creating a new user we want to see him in the list of users..
        return "redirect:/users/page/1?sortField=id&sortOrder=asc&keyword=" + user.getEmail();
    }

    @GetMapping("/users/edit/{id}")
    public String newUser(@PathVariable(name = "id") Integer id, Model model,RedirectAttributes redirectAttributes)  {
        try {
            var user = userService.findById(id);
            var roles = userService.findAllRoles();

            model.addAttribute("user",user);
            model.addAttribute("pageTitle","Edit user with id " + user.getId());
            model.addAttribute("listRoles",roles);

            return "users/user_form";
        } catch(UserNotFoundException e){
            redirectAttributes.addFlashAttribute("message",e.getMessage());
            return "redirect:/users";
        }
    }

    @GetMapping("/users/{id}/enabled/{status}")
    public String updatedUserEnableStatus(@PathVariable(name = "id") Integer id,
                                        @PathVariable(name = "status") boolean enabled,
                                        RedirectAttributes redirectAttributes)  {

        userService.updateEnabledStatus(id,enabled);

        String status = enabled ? "enabled" : "disabled";
        String message = "The user with ID " + id + " has been " + status;

        redirectAttributes.addFlashAttribute("message",message);
        return "redirect:/users";
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {

        try {
            userService.deleteUser(id);

            String userPhotosDir = "user-photos/" + id;
            // before S3 migration
//			FileUploadUtil.deletePreviousFilesAndDirectory(userPhotosDir);
            AmazonS3Util.removeFolder(userPhotosDir);

            redirectAttributes.addFlashAttribute("message", "The user ID " + id + " has been deleted successfully");

        } catch (UserNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }
        return "redirect:/users";
    }

    @GetMapping("/users/export/csv")
    public void exportToCSV(HttpServletResponse response) throws IOException {
        List<User> users = userService.findAllUsers();
        UserCsvExporter exporter = new UserCsvExporter();
        exporter.export(users,response);
    }

    @GetMapping("/users/export/excel")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        List<User> users = userService.findAllUsers();
        UserExcelExporter exporter = new UserExcelExporter();
        exporter.export(users,response);
    }

    @GetMapping("/users/export/pdf")
    public void exportToPdf(HttpServletResponse response) throws IOException {
        List<User> users = userService.findAllUsers();
        UserPdfExporter exporter = new UserPdfExporter();
        exporter.export(users,response);
    }
}
