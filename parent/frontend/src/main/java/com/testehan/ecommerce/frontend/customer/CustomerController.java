package com.testehan.ecommerce.frontend.customer;

import com.testehan.ecommerce.common.entity.Country;
import com.testehan.ecommerce.common.entity.Customer;
import com.testehan.ecommerce.frontend.setting.SettingService;
import com.testehan.ecommerce.frontend.util.CustomerRegisterUtil;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.UnsupportedEncodingException;
import java.util.List;

@Controller
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private SettingService settingService;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {

        List<Country> listCountries = customerService.listAllCountries();

        model.addAttribute("listCountries", listCountries);
        model.addAttribute("pageTitle", "Customer Registration");
        model.addAttribute("customer", new Customer());

        return "register/register_form";
    }

    @PostMapping("/create_customer")
    public String createCustomer(Customer customer, Model model,
                                 HttpServletRequest request) throws UnsupportedEncodingException, MessagingException {


        customerService.registerCustomer(customer);
        CustomerRegisterUtil.sendVerificationEmail(request, customer, settingService);

        model.addAttribute("pageTitle", "Registration Succeeded!");

        return "register/register_success";
    }


}
