package com.testehan.ecommerce.frontend.cart;

import com.testehan.ecommerce.common.entity.CartItem;
import com.testehan.ecommerce.common.entity.Customer;
import com.testehan.ecommerce.common.entity.product.Product;
import com.testehan.ecommerce.frontend.product.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class ShoppingCartService {

    public static final int MAXIMUM_QUANTITY_OF_SAME_ITEMS = 5;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private ProductRepository productRepository;

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

    public float updateQuantity(Integer productId, Integer quantity, Customer customer) {
        cartItemRepository.updateQuantity(quantity, customer.getId(), productId);
        var product = productRepository.findById(productId).get();

        // because in DB i store cents
        float subtotal = product.getDiscountedPrice() / 100 * quantity;
        return subtotal;
    }

    public void removeProductFromShoppingCart(Integer productId, Customer customer) {
        cartItemRepository.deleteByCustomerAndProduct(customer.getId(), productId);
    }

    public void deleteByCustomer(Customer customer){
        cartItemRepository.deleteByCustomer(customer.getId());
    }
}
