package com.testehan.ecommerce.frontend.util;

import com.testehan.ecommerce.common.entity.Customer;
import com.testehan.ecommerce.common.exception.CustomerNotFoundException;
import com.testehan.ecommerce.frontend.customer.CustomerService;
import com.testehan.ecommerce.frontend.security.oauth.CustomerOAuth2User;
import com.testehan.ecommerce.frontend.setting.CurrencySettingBag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
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

    public static Customer getAuthenticatedCustomer(CustomerService customerService, HttpServletRequest servletRequest) throws CustomerNotFoundException {
        var email = Utility.getEmailOfAuthCustomer(servletRequest);
        if (Objects.isNull(email)){
            throw new CustomerNotFoundException("Customer is not authenticated !");
        } else {

        }
        return customerService.getCustomerByEmail(email);
    }

    public static String formatCurrency(long amount, CurrencySettingBag settings) {
        var symbol = settings.getSymbol();
        var symbolPosition = settings.getSymbolPosition();
        var decimalPointType = settings.getDecimalPointType();
        var thousandPointType = settings.getThousandPointType();
        int decimalDigits = settings.getDecimalDigits();

        String pattern = symbolPosition.equals("Before price") ? symbol : "";
        pattern += "###,###";

        if (decimalDigits > 0) {
            pattern += ".";
            for (int count = 1; count <= decimalDigits; count++){
                pattern += "#";
            }
        }

        pattern += symbolPosition.equals("After price") ? symbol : "";

        char thousandSeparator = thousandPointType.equals("POINT") ? '.' : ',';
        char decimalSeparator = decimalPointType.equals("POINT") ? '.' : ',';

        DecimalFormatSymbols decimalFormatSymbols = DecimalFormatSymbols.getInstance();
        decimalFormatSymbols.setDecimalSeparator(decimalSeparator);
        decimalFormatSymbols.setGroupingSeparator(thousandSeparator);

        DecimalFormat formatter = new DecimalFormat(pattern, decimalFormatSymbols);

        return formatter.format(amount);
    }
}
