package com.testehan.ecommerce.frontend.cart;

import com.testehan.ecommerce.common.entity.CartItem;
import com.testehan.ecommerce.common.entity.Customer;
import com.testehan.ecommerce.common.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class ShoppingCartService {

    public static final int MAXIMUM_QUANTITY_OF_SAME_ITEMS = 5;
    @Autowired
    private CartItemRepository cartItemRepository;

    public Integer addProduct(Integer productId, Integer quantity, Customer customer) throws ShoppingCartException {
        var updatedQuantity = quantity;
        var product = new Product(productId);

        var cartItem = cartItemRepository.findByCustomerAndProduct(customer, product);
        if (Objects.nonNull(cartItem)){
            // cart item is present in DB
            updatedQuantity = cartItem.getQuantity() + quantity;

            if (updatedQuantity > MAXIMUM_QUANTITY_OF_SAME_ITEMS){
                throw new ShoppingCartException("Could not add " + quantity + " more item(s). Maximum limit of 5 items reached!");
            }
        } else {
            cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setCustomer(customer);
        }

        cartItem.setQuantity(updatedQuantity);
        cartItemRepository.save(cartItem);

        return updatedQuantity;
    }

    public List<CartItem> listCartItems(Customer customer){
        return cartItemRepository.findByCustomer(customer);
    }
}
