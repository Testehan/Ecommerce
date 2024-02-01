package com.testehan.ecommerce.frontend.customer;

import com.testehan.ecommerce.common.exception.CustomerNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ForgotPasswordController {

    @Autowired
    private CustomerService customerService;

    @GetMapping("/forgot_password")
    public String showForgotPasswordForm(){

        return "customer/forgot_password_form";
    }

    @PostMapping("/forgot_password")
    public String processForgotPasswordForm(HttpServletRequest request) throws CustomerNotFoundException {
        var email = request.getParameter("email");
        customerService.updateResetPasswordToken(email);
        return "customer/forgot_password_form";
    }
}
