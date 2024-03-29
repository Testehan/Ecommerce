package com.testehan.ecommerce.backend.user.controller;

import com.testehan.ecommerce.backend.user.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserRestController {

    private final UserService userService;

    public UserRestController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/users/check_email")
    public String checkDuplicateEmail(Integer id, String email) {
        System.out.println("recieved email " + email + " for checking for duplicated");
        return userService.isEmailUnique(id, email) ? "OK" : "Duplicated";
    }
}
