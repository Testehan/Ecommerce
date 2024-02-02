package com.testehan.ecommerce.frontend.customer;

import com.testehan.ecommerce.common.exception.CustomerNotFoundException;
import com.testehan.ecommerce.frontend.setting.SettingService;
import com.testehan.ecommerce.frontend.util.CustomerForgotPasswordUtil;
import com.testehan.ecommerce.frontend.util.CustomerRegisterUtil;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.UnsupportedEncodingException;
import java.util.Objects;

@Controller
public class ForgotPasswordController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ForgotPasswordController.class);

    @Autowired
    private CustomerService customerService;
    @Autowired
    private SettingService  settingService;

    @GetMapping("/forgot_password")
    public String showForgotPasswordForm(){

        return "customer/forgot_password_form";
    }

    @PostMapping("/forgot_password")
    public String processForgotPasswordForm(HttpServletRequest request, Model model) {
        var email = request.getParameter("email");
        try {
            var token = customerService.updateResetPasswordToken(email);
            var resetPasswordLink = CustomerRegisterUtil.getSiteURL(request) + "/reset_password?token=" + token;
            CustomerForgotPasswordUtil.sendEmail(resetPasswordLink,email,settingService);
        } catch (CustomerNotFoundException e) {
            // for security reasons, the app should not behave different if the email is used or not
            LOGGER.error("Email provided for password reset is not used by any customer. Email: " + email);
        } catch (MessagingException | UnsupportedEncodingException e) {
            LOGGER.error("Could not send password reset email !");
        }

        model.addAttribute("message", "You should receive an email shortly");

        return "customer/forgot_password_form";
    }

    @GetMapping("/reset_password")
    public String showResetForm(@Param("token") String token, Model model){
        var customer = customerService.getByResetPasswordToken(token);
        if (Objects.nonNull(customer)){
            model.addAttribute("token",token);
        } else {
            model.addAttribute("message","Invalid password reset token");
            return "message";
        }
        return "customer/reset_password_form";
    }

    @PostMapping("/reset_password")
    public String processResetPasswordForm(HttpServletRequest request, Model model){
        var token = request.getParameter("token");
        var newPassword = request.getParameter("password");

        try {
            customerService.updatePassword(token,newPassword);
            model.addAttribute("message","You have successfully changed the password!");
            return "message";
        } catch (CustomerNotFoundException e) {
            model.addAttribute("message","Invalid password reset token");
            return "message";
        }
    }
}
