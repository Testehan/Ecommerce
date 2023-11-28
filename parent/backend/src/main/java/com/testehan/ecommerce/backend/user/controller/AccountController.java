package com.testehan.ecommerce.backend.user.controller;

import com.testehan.ecommerce.backend.security.ShopUserDetails;
import com.testehan.ecommerce.backend.user.UserNotFoundException;
import com.testehan.ecommerce.backend.user.UserService;
import com.testehan.ecommerce.backend.util.FileUploadUtil;
import com.testehan.ecommerce.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
public class AccountController {

    @Autowired
    private UserService userService;

    @GetMapping("/account")
    public String viewAccountPage(@AuthenticationPrincipal ShopUserDetails loggedInUser, Model model) throws UserNotFoundException {
        String email = loggedInUser.getUsername();      // this overwritten method return the email in our case

        User user = userService.findByEmail(email);
        model.addAttribute("user",user);

        return "users/account_form";
    }

    @PostMapping("/account/update")
    public String saveUserDetails(User user, RedirectAttributes redirectAttributes,
                           @RequestParam("image") MultipartFile multipartFile,
                           @AuthenticationPrincipal ShopUserDetails principal) throws IOException
    {
        // this is for the image
        if (!multipartFile.isEmpty()){
            String filename = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            user.setPhoto(filename);
            User savedUser =  userService.updateAccount(user);
            String uploadDir = "user-photos/" + savedUser.getId();

            FileUploadUtil.deletePreviousFiles(uploadDir);
            FileUploadUtil.saveFile(uploadDir,filename,multipartFile);
        } else {
            // means that when the user was created, no picture was provided
            if (user.getPhoto()==null || user.getPhoto().isEmpty()) {
                user.setPhoto(null);
            }
            userService.updateAccount(user);
        }

        principal.setFirstName(user.getFirstName());
        principal.setLastName(user.getLastName());

        redirectAttributes.addFlashAttribute("message","Account details were updated!");

        return "redirect:/account";
    }

}
