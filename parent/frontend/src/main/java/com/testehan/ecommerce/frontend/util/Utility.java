package com.testehan.ecommerce.frontend.util;

import com.testehan.ecommerce.frontend.security.oauth.CustomerOAuth2User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import java.util.Objects;

public class Utility {

    public static String getEmailOfAuthCustomer(HttpServletRequest request){
        var principal = request.getUserPrincipal();
        if (Objects.isNull(principal)){
            return null;
        }
        String customerEmail = null;

        if (principal instanceof UsernamePasswordAuthenticationToken || principal instanceof RememberMeAuthenticationToken){
            customerEmail = principal.getName();
        } else  if (principal instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) principal;
            CustomerOAuth2User customer = (CustomerOAuth2User) oAuth2AuthenticationToken.getPrincipal();
            customerEmail = customer.getEmail();
        }

        return customerEmail;
    }
}
