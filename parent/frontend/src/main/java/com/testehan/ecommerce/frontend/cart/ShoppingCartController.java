package com.testehan.ecommerce.frontend.cart;

import com.testehan.ecommerce.common.entity.CartItem;
import com.testehan.ecommerce.common.exception.CustomerNotFoundException;
import com.testehan.ecommerce.frontend.customer.CustomerService;
import com.testehan.ecommerce.frontend.util.Utility;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private CustomerService customerService;

    @GetMapping("/cart")        // because of security settings in WebSecurityConfig, the customer will always be logged in and found when calling this method..so the CustomerNotFoundException is added only for code to compile
    public String viewCart(Model model, HttpServletRequest request) throws CustomerNotFoundException {

        var customer = Utility.getAuthenticatedCustomer(customerService,request);
        var cartItems = shoppingCartService.listCartItems(customer);

        float estimatedTotal = 0.0F;
        for (CartItem cartItem : cartItems){
            estimatedTotal = estimatedTotal + cartItem.getSubtotal();
        }

        model.addAttribute("cartItems",cartItems);
        model.addAttribute("estimatedTotal",estimatedTotal);

        return "cart/shopping_cart";
    }
}
