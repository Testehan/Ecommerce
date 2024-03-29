package com.testehan.ecommerce.backend.order;

import com.testehan.ecommerce.common.entity.Customer;
import com.testehan.ecommerce.common.entity.order.*;
import com.testehan.ecommerce.common.entity.product.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class OrderRepositoryTests {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void createNewOrderWithSingleProduct() {
        var customer = entityManager.find(Customer.class, 6);
        var product = entityManager.find(Product.class, 1);

        var mainOrder = new Order();
        mainOrder.setOrderTime(new Date());
        mainOrder.setCustomer(customer);
        mainOrder.copyAddressFromCustomer();

        mainOrder.setShippingCost(10);
        mainOrder.setProductCost(product.getCost());
        mainOrder.setTax(0);
        mainOrder.setSubtotal(product.getPrice());
        mainOrder.setTotal(product.getPrice() + 10);

        mainOrder.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        mainOrder.setStatus(OrderStatus.NEW);
        mainOrder.setDeliverDate(new Date());
        mainOrder.setDeliverDays(1);

        var orderDetail = new OrderDetail();
        orderDetail.setProduct(product);
        orderDetail.setOrder(mainOrder);
        orderDetail.setProductCost(product.getCost());
        orderDetail.setShippingCost(10);
        orderDetail.setQuantity(1);
        orderDetail.setSubtotal(product.getPrice());
        orderDetail.setUnitPrice(product.getPrice());

        mainOrder.getOrderDetails().add(orderDetail);

        var savedOrder = orderRepository.save(mainOrder);

        assertThat(savedOrder.getId()).isGreaterThan(0);
    }

    @Test
    public void createNewOrderWithMultipleProducts() {
        var customer = entityManager.find(Customer.class, 6);
        var product1 = entityManager.find(Product.class, 1);
        var product2 = entityManager.find(Product.class, 2);

        var mainOrder = new Order();
        mainOrder.setOrderTime(new Date());
        mainOrder.setCustomer(customer);
        mainOrder.copyAddressFromCustomer();

        var orderDetail1 = new OrderDetail();
        orderDetail1.setProduct(product1);
        orderDetail1.setOrder(mainOrder);
        orderDetail1.setProductCost(product1.getCost());
        orderDetail1.setShippingCost(10);
        orderDetail1.setQuantity(1);
        orderDetail1.setSubtotal(product1.getPrice());
        orderDetail1.setUnitPrice(product1.getPrice());

        var orderDetail2 = new OrderDetail();
        orderDetail2.setProduct(product2);
        orderDetail2.setOrder(mainOrder);
        orderDetail2.setProductCost(product2.getCost());
        orderDetail2.setShippingCost(20);
        orderDetail2.setQuantity(2);
        orderDetail2.setSubtotal(product2.getPrice() * 2);
        orderDetail2.setUnitPrice(product2.getPrice());

        mainOrder.getOrderDetails().add(orderDetail1);
        mainOrder.getOrderDetails().add(orderDetail2);

        mainOrder.setShippingCost(30);
        mainOrder.setProductCost(product1.getCost() + product2.getCost());
        mainOrder.setTax(0);
        long subtotal = product1.getPrice() + product2.getPrice() * 2;
        mainOrder.setSubtotal(subtotal);
        mainOrder.setTotal(subtotal + 30);

        mainOrder.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        mainOrder.setStatus(OrderStatus.PACKAGED);
        mainOrder.setDeliverDate(new Date());
        mainOrder.setDeliverDays(3);

        Order savedOrder = orderRepository.save(mainOrder);
        assertThat(savedOrder.getId()).isGreaterThan(0);
    }

    @Test
    public void listOrders() {
        Iterable<Order> orders = orderRepository.findAll();

        assertThat(orders).hasSizeGreaterThan(0);

        orders.forEach(System.out::println);
    }

    @Test
    public void updateOrder() {
        var orderId = 1;
        var order = orderRepository.findById(orderId).get();

        order.setStatus(OrderStatus.SHIPPING);
        order.setPaymentMethod(PaymentMethod.COD);
        order.setOrderTime(new Date());
        order.setDeliverDays(2);

        var updatedOrder = orderRepository.save(order);

        assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.SHIPPING);
    }

    @Test
    public void getOrder() {
        var orderId = 1;
        var order = orderRepository.findById(orderId).get();

        assertThat(order).isNotNull();
        System.out.println(order);
    }

    @Test
    public void testDeleteOrder() {
        var orderId = 2;
        orderRepository.deleteById(orderId);

        Optional<Order> result = orderRepository.findById(orderId);
        assertThat(result).isNotPresent();
    }

    @Test
    public void testUpdateOrderTracks() {
        var orderId = 16;
        var order = orderRepository.findById(orderId).get();

        var newTrack = new OrderTrack();
        newTrack.setOrder(order);
        newTrack.setUpdatedTime(new Date());
        newTrack.setStatus(OrderStatus.NEW);
        newTrack.setNotes(OrderStatus.NEW.defaultDescription());

        var processingTrack = new OrderTrack();
        processingTrack.setOrder(order);
        processingTrack.setUpdatedTime(new Date());
        processingTrack.setStatus(OrderStatus.PROCESSING);
        processingTrack.setNotes(OrderStatus.PROCESSING.defaultDescription());

        List<OrderTrack> orderTracks = order.getOrderTracks();
        orderTracks.add(newTrack);
        orderTracks.add(processingTrack);

        var updatedOrder = orderRepository.save(order);

        assertThat(updatedOrder.getOrderTracks()).hasSizeGreaterThan(1);
    }
}
