package com.testehan.ecommerce.frontend.cart;

import com.testehan.ecommerce.common.entity.CartItem;
import com.testehan.ecommerce.common.entity.ShippingRate;
import com.testehan.ecommerce.common.exception.CustomerNotFoundException;
import com.testehan.ecommerce.frontend.address.AddressService;
import com.testehan.ecommerce.frontend.customer.CustomerService;
import com.testehan.ecommerce.frontend.shipping.ShippingRateService;
import com.testehan.ecommerce.frontend.util.Utility;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Objects;

@Controller
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private AddressService addressService;
    @Autowired
    private ShippingRateService shippingRateService;

    @GetMapping("/cart")        // because of security settings in WebSecurityConfig, the customer will always be logged in and found when calling this method..so the CustomerNotFoundException is added only for code to compile
    public String viewCart(Model model, HttpServletRequest request) throws CustomerNotFoundException {

        var customer = Utility.getAuthenticatedCustomer(customerService,request);
        var cartItems = shoppingCartService.listCartItems(customer);

        float estimatedTotal = 0.0F;
        for (CartItem cartItem : cartItems){
            estimatedTotal = estimatedTotal + cartItem.getSubtotal();
        }

        var usePrimaryAddressAsDefault = false;

        var defaultAddress = addressService.getDefaultAddress(customer.getId());
        ShippingRate shippingRate = null;
        if (Objects.nonNull(defaultAddress)){
            shippingRate = shippingRateService.getShippingRateForAddress(defaultAddress);
        } else {
            usePrimaryAddressAsDefault = true;
            shippingRate = shippingRateService.getShippingRateForCustomer(customer);
        }

        model.addAttribute("shippingSupported",shippingRate != null);
        model.addAttribute("cartItems",cartItems);
        model.addAttribute("usePrimaryAddressAsDefault",usePrimaryAddressAsDefault);
        model.addAttribute("estimatedTotal",estimatedTotal);

        return "cart/shopping_cart";
    }
}
