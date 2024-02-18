package com.testehan.ecommerce.frontend.checkout;

import com.testehan.ecommerce.common.entity.ShippingRate;
import com.testehan.ecommerce.common.exception.CustomerNotFoundException;
import com.testehan.ecommerce.frontend.address.AddressService;
import com.testehan.ecommerce.frontend.cart.ShoppingCartService;
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
public class CheckoutController {

    @Autowired
    private CheckoutService checkoutService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private AddressService addressService;
    @Autowired
    private ShippingRateService shippingRateService;
    @Autowired
    private ShoppingCartService shoppingCartService;

    @GetMapping("/checkout")
    public String showCheckoutPage(Model model, HttpServletRequest request) throws CustomerNotFoundException {
        var customer = Utility.getAuthenticatedCustomer(customerService,request);

        var defaultAddress = addressService.getDefaultAddress(customer.getId());
        ShippingRate shippingRate = null;
        if (Objects.nonNull(defaultAddress)){
            model.addAttribute("shippingAddress", defaultAddress.toString());
            shippingRate = shippingRateService.getShippingRateForAddress(defaultAddress);
        } else {
            model.addAttribute("shippingAddress", customer.getAddress());
            shippingRate = shippingRateService.getShippingRateForCustomer(customer);
        }

        if (Objects.isNull(shippingRate)){
            return "redirect:/cart";
        }

        var cartItems = shoppingCartService.listCartItems(customer);
        var checkoutInfo = checkoutService.prepareCheckout(cartItems,shippingRate);

        model.addAttribute("checkoutInfo", checkoutInfo);
        model.addAttribute("cartItems", cartItems);

        return "checkout/checkout";
    }

}
