package com.testehan.ecommerce.frontend.cart;

import com.testehan.ecommerce.common.exception.CustomerNotFoundException;
import com.testehan.ecommerce.frontend.customer.CustomerService;
import com.testehan.ecommerce.frontend.util.Utility;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ShoppingCartRestController {

    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private CustomerService customerService;

    @PostMapping("/cart/add/{productId}/{quantity}")
    public String addProductToCart(@PathVariable(name = "productId") Integer productId,
                                   @PathVariable(name = "quantity") Integer quantity,
                                   HttpServletRequest servletRequest){

        try {
            var customer = Utility.getAuthenticatedCustomer(customerService,servletRequest);
            var updatedQuantity = shoppingCartService.addProduct(productId,quantity,customer);
            return updatedQuantity + " item(s) of this product were added to your cart.";
        } catch (CustomerNotFoundException e) {
            return "You must login to add products to cart!";
        } catch (ShoppingCartException e){
            return e.getMessage();
        }

    }

    @PostMapping("/cart/update/{productId}/{quantity}")
    public String updateQuantity(@PathVariable(name = "productId") Integer productId,
                                   @PathVariable(name = "quantity") Integer quantity,
                                   HttpServletRequest servletRequest){

        try {
            var customer = Utility.getAuthenticatedCustomer(customerService,servletRequest);
            float subtotal = shoppingCartService.updateQuantity(productId,quantity,customer);
            return String.valueOf(subtotal);
        } catch (CustomerNotFoundException e) {
            return "You must login to change product quantity!";
        }

    }

    @DeleteMapping("/cart/remove/{productId}")
    public String removeProduct(@PathVariable("productId") Integer productId,
                                HttpServletRequest request) throws CustomerNotFoundException {

        var customer = Utility.getAuthenticatedCustomer(customerService,request);

        if (customer != null) {
            shoppingCartService.removeProductFromShoppingCart(productId, customer);
            return "The product was removed from your shopping cart.";
        }else {
            return "Login to remove product.";
        }

    }
}
