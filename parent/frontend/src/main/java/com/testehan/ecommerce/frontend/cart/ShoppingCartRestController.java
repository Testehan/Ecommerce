package com.testehan.ecommerce.frontend.cart;

import com.testehan.ecommerce.common.entity.Customer;
import com.testehan.ecommerce.common.exception.CustomerNotFoundException;
import com.testehan.ecommerce.frontend.customer.CustomerService;
import com.testehan.ecommerce.frontend.util.Utility;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

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
            var customer = getAuthenticatedCustomer(servletRequest);
            var updatedQuantity = shoppingCartService.addProduct(productId,quantity,customer);
            return updatedQuantity + " item(s) of this product were added to your cart.";
        } catch (CustomerNotFoundException e) {
            return "You must login to add products to cart!";
        } catch (ShoppingCartException e){
            return e.getMessage();
        }

    }

    private Customer getAuthenticatedCustomer(HttpServletRequest servletRequest) throws CustomerNotFoundException {
        var email = Utility.getEmailOfAuthCustomer(servletRequest);
        if (Objects.isNull(email)){
            throw new CustomerNotFoundException("Customer is not authenticated !");
        } else {

        }
        return customerService.getCustomerByEmail(email);
    }

}