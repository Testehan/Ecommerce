package com.testehan.ecommerce.frontend.security.oauth;

import com.testehan.ecommerce.common.entity.AuthenticationType;
import com.testehan.ecommerce.frontend.customer.CustomerService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Autowired
    private CustomerService customerService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        CustomerOAuth2User customerOAuth2User = (CustomerOAuth2User)authentication.getPrincipal();

        var customer = customerService.getCustomerByEmail(customerOAuth2User.getEmail());
        // code of the country of the current locale in the users browser ...it might not be exactly what we want, but it is
        // a nice default value
        var countryCode = request.getLocale().getCountry();
        var customerName = customerOAuth2User.getName();
        var customerEmail = customerOAuth2User.getEmail();

        var authenticationType = getAuthenticationType(customerOAuth2User.getClientName());
        if (customer == null){
            customerService.addNewCustomerAfterOAuth2Login(customerName, customerEmail,countryCode, authenticationType);
        } else {
            customerOAuth2User.setFullName(customer.getFullName());
            customerService.updateAuthenticationType(customer, authenticationType);
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }

    private AuthenticationType getAuthenticationType(String clientName){
        if (clientName.equalsIgnoreCase("google")){
            return AuthenticationType.GOOGLE;
        } else if (clientName.equalsIgnoreCase("facebook")){
            return AuthenticationType.FACEBOOK;
        }
        return AuthenticationType.DATABASE;
    }

}
