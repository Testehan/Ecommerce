package com.testehan.ecommerce.frontend.cart;

import com.testehan.ecommerce.common.entity.CartItem;
import com.testehan.ecommerce.common.entity.Customer;
import com.testehan.ecommerce.common.entity.product.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
// obviously the annotation from below means that the docker container needs to run, since it doesn't use a in memory DB, but the actual DB
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class CartItemRepositoryTest {

    private CartItemRepository cartItemRepository;

    private TestEntityManager entityManager;

    @Autowired
    public CartItemRepositoryTest(CartItemRepository repo, TestEntityManager entityManager) {
        this.cartItemRepository = repo;
        this.entityManager = entityManager;
    }

    @Test
    public void testSaveItem() {
        var customerId = 1;
        var productId = 1;

        var customer = entityManager.find(Customer.class, customerId);
        var product = entityManager.find(Product.class, productId);

        var newItem = new CartItem();
        newItem.setCustomer(customer);
        newItem.setProduct(product);
        newItem.setQuantity(1);

        var savedItem = cartItemRepository.save(newItem);

        assertThat(savedItem.getId()).isGreaterThan(0);
    }

    @Test
    public void testFindByCustomer() {
        var customerId = 1;
        List<CartItem> listItems = cartItemRepository.findByCustomer(new Customer(customerId));

        listItems.forEach(System.out::println);

        assertThat(listItems.size()).isEqualTo(1);
    }

    @Test
    public void testFindByCustomerAndProduct() {
        var customerId = 1;
        var productId = 1;

        var item = cartItemRepository.findByCustomerAndProduct(new Customer(customerId), new Product(productId));

        assertThat(item).isNotNull();
    }

    @Test
    public void testUpdateQuantity() {
        var customerId = 1;
        var productId = 1;
        var quantity = 4;

        cartItemRepository.updateQuantity(quantity, customerId, productId);

        var updatedCartItem = cartItemRepository.findByCustomerAndProduct(new Customer(customerId), new Product(productId));

        assertThat(updatedCartItem.getQuantity()).isEqualTo(4);
    }

    @Test
    public void testDeleteByCustomerAndProduct() {
        var customerId = 1;
        var productId = 1;

        cartItemRepository.deleteByCustomerAndProduct(customerId, productId);

        var item = cartItemRepository.findByCustomerAndProduct(new Customer(customerId), new Product(productId));

        assertThat(item).isNull();
    }
}
