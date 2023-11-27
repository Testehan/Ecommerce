package com.testehan.ecommerce.backend.user;

import com.testehan.ecommerce.backend.util.FileUploadUtil;
import com.testehan.ecommerce.common.entity.User;
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
public class UserController {

    private static final String NO_KEYWORD="";

    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("/users")
    public String listFirstPage(Model model){       // BY DEFAULT we sort by firstName ascending
        return listUsersByPage(1,model, "firstName", "asc", NO_KEYWORD);
    }

    @GetMapping("/users/page/{pageNumber}")
    public String listUsersByPage(@PathVariable(name = "pageNumber") Integer pageNumber, Model model,
                                  @Param("sortField")String sortField, @Param("sortOrder")String sortOrder,
                                  @Param("keyword")String keyword){
        var pageOfUsers = userService.listUsersByPage(pageNumber, sortField, sortOrder, keyword);
        model.addAttribute("listUsers",pageOfUsers.getContent());

        long startCount = (pageNumber-1)*UserService.USER_PAGE_SIZE + 1;
        long endCount = startCount + UserService.USER_PAGE_SIZE - 1;
        if (endCount > pageOfUsers.getTotalElements()){
            endCount = pageOfUsers.getTotalElements();
        }
        model.addAttribute("startCount",startCount);
        model.addAttribute("endCount",endCount);
        model.addAttribute("currentPage",pageNumber);

        model.addAttribute("totalItems",pageOfUsers.getTotalElements());
        model.addAttribute("totalPages", pageOfUsers.getTotalPages());

        String reverseSortOrder = sortOrder.equalsIgnoreCase("asc") ? "desc" : "asc";
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("reverseSortOrder", reverseSortOrder);
        model.addAttribute("keyword", keyword);

        return "users";
    }

    @GetMapping("/users/new")
    public String newUser(Model model){
        var user = new User();
        var roles = userService.findAllRoles();

        user.setEnabled(true);
        model.addAttribute("user",user);
        model.addAttribute("listRoles",roles);
        model.addAttribute("pageTitle","Create new user");

        return "user_form";
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

           FileUploadUtil.deletePreviousFiles(uploadDir);
           FileUploadUtil.saveFile(uploadDir,filename,multipartFile);
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

            return "user_form";
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
			FileUploadUtil.deletePreviousFilesAndDirectory(userPhotosDir);

            redirectAttributes.addFlashAttribute("message", "The user ID " + id + " has been deleted successfully");

        } catch (UserNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }
        return "redirect:/users";
    }


}
