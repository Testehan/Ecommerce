package com.testehan.ecommerce.frontend.customer;

import com.testehan.ecommerce.common.entity.Country;
import com.testehan.ecommerce.common.entity.Customer;
import com.testehan.ecommerce.frontend.security.CustomerUserDetails;
import com.testehan.ecommerce.frontend.security.oauth.CustomerOAuth2User;
import com.testehan.ecommerce.frontend.setting.SettingService;
import com.testehan.ecommerce.frontend.util.CustomerRegisterUtil;
import com.testehan.ecommerce.frontend.util.Utility;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @GetMapping("/verify")
    public String verifyAccount(@RequestParam("code") String code, Model model) {

        boolean verified = customerService.verify(code);

        return "register/" + (verified ? "verify_success" : "verify_fail");
    }

    @GetMapping("/account_details")
    public String viewAccountDetails(Model model, HttpServletRequest request) {
        var customerEmail = Utility.getEmailOfAuthCustomer(request);
        var customer = customerService.getCustomerByEmail(customerEmail);
        var listCountries = customerService.listAllCountries();

        model.addAttribute("listCountries", listCountries);
        model.addAttribute("customer",customer);

        return "customer/account_form";
    }

    @PostMapping("/update_account_details")
    public String updateAccountDetails(Customer customer, Model model, RedirectAttributes redirectAttributes, HttpServletRequest request) {

        customerService.update(customer);
        redirectAttributes.addFlashAttribute("message", "Your account details have been updated");
        updateNameForAuthenticatedCustomer(customer, request);

        var redirectOption = request.getParameter("redirect");
        var redirectUrl = "redirect:/account_details";
        if ("address_book".equalsIgnoreCase(redirectOption)){
            redirectUrl = "redirect:/address_book";
        } else if ("cart".equalsIgnoreCase(redirectOption)){
            redirectUrl = "redirect:/cart";
        } else if ("checkout".equalsIgnoreCase(redirectOption)){
            redirectUrl = "redirect:/address_book?redirect=checkout";
        }

        return redirectUrl;
    }

    private void updateNameForAuthenticatedCustomer(Customer customer, HttpServletRequest request) {
        var principal = request.getUserPrincipal();

        if (principal instanceof UsernamePasswordAuthenticationToken || principal instanceof RememberMeAuthenticationToken){
            CustomerUserDetails customerUserDetails = getCustomerUserDetailsObject(principal);
            var authenticatedCustomer = customerUserDetails.getCustomer();
            authenticatedCustomer.setFirstName(customer.getFirstName());
            authenticatedCustomer.setLastName(customer.getLastName());

        } else  if (principal instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) principal;
            CustomerOAuth2User customerOAuth2User = (CustomerOAuth2User) oAuth2AuthenticationToken.getPrincipal();
            var fullName = customer.getFirstName() + " " + customer.getLastName();
            customerOAuth2User.setFullName(fullName);
        }
    }



    public CustomerUserDetails getCustomerUserDetailsObject(Object principal) {

        CustomerUserDetails userDetails = null;

        if (principal instanceof UsernamePasswordAuthenticationToken) {
            UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
            userDetails = (CustomerUserDetails) token.getPrincipal();
        } else if (principal instanceof RememberMeAuthenticationToken) {
            RememberMeAuthenticationToken token = (RememberMeAuthenticationToken) principal;
            userDetails = (CustomerUserDetails) token.getPrincipal();
        }

        return userDetails;
    }
}
