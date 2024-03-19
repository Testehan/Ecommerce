package com.testehan.ecommerce.frontend.order;

import com.testehan.ecommerce.common.entity.Address;
import com.testehan.ecommerce.common.entity.CartItem;
import com.testehan.ecommerce.common.entity.Customer;
import com.testehan.ecommerce.common.entity.order.*;
import com.testehan.ecommerce.common.exception.OrderNotFoundException;
import com.testehan.ecommerce.frontend.checkout.CheckoutInfo;
import com.testehan.ecommerce.frontend.order.dto.OrderReturnRequest;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@Transactional
public class OrderService {

    public static final int ORDERS_PER_PAGE = 5;

    @Autowired
    private OrderRepository orderRepository;

    public Order createOrder(Customer customer, Address address, List<CartItem> cartItems, PaymentMethod paymentMethod,
                             CheckoutInfo checkoutInfo){
        var newOrder = new Order();
        newOrder.setOrderTime(new Date());
        newOrder.setPaymentMethod(paymentMethod);

        if (PaymentMethod.PAYPAL.equals(paymentMethod)){
            newOrder.setStatus(OrderStatus.PAID);
        } else {
            newOrder.setStatus(OrderStatus.NEW);
        }

        newOrder.setCustomer(customer);
        newOrder.setProductCost(checkoutInfo.getProductCost());
        newOrder.setSubtotal(checkoutInfo.getProductTotal());
        newOrder.setShippingCost(checkoutInfo.getShippingCostTotal());
        newOrder.setTax(0);
        newOrder.setTotal(checkoutInfo.getPaymentTotal());
        newOrder.setDeliverDays(checkoutInfo.getDeliverDays());
        newOrder.setDeliverDate(checkoutInfo.getDeliveryDate());

        if (Objects.isNull(address)){
            newOrder.copyAddressFromCustomer();
        } else {
            newOrder.copyShippingAddress(address);
        }

        Set<OrderDetail> orderDetails = newOrder.getOrderDetails();
        for (CartItem cartItem : cartItems){
            var product = cartItem.getProduct();
            var orderDetail = new OrderDetail();
            orderDetail.setOrder(newOrder);
            orderDetail.setProduct(product);
            orderDetail.setQuantity(cartItem.getQuantity());
            orderDetail.setUnitPrice((long)product.getDiscountedPrice());   // money shouldn't be a float
            orderDetail.setProductCost(product.getCost() * cartItem.getQuantity());
            orderDetail.setSubtotal((long)cartItem.getSubtotal());           // money shouldn't be a float
            orderDetail.setShippingCost(cartItem.getShippingCost());

            orderDetails.add(orderDetail);
        }

        var track = new OrderTrack();
        track.setOrder(newOrder);
        track.setStatus(OrderStatus.NEW);
        track.setNotes(OrderStatus.NEW.defaultDescription());
        track.setUpdatedTime(new Date());
        newOrder.getOrderTracks().add(track);

        return orderRepository.save(newOrder);
    }

    public Page<Order> listForCustomerByPage(Customer customer, int pageNum,
                                             String sortField, String sortDir, String keyword) {

        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNum - 1, ORDERS_PER_PAGE, sort);

        if (keyword != null) {
            return orderRepository.findAll(keyword, customer.getId(), pageable);
        }

        return orderRepository.findAll(customer.getId(), pageable);

    }

    public Order getOrder(Integer id, Customer customer) {
        return orderRepository.findByIdAndCustomer(id, customer);
    }

    public void setOrderReturnRequested(OrderReturnRequest request, Customer customer)
            throws OrderNotFoundException {

        var order = orderRepository.findByIdAndCustomer(request.getOrderId(), customer);

        if (order == null) {
            throw new OrderNotFoundException("Order ID " + request.getOrderId() + " not found");
        }

        if (order.isReturnRequested()) return;

        OrderTrack track = new OrderTrack();
        track.setOrder(order);
        track.setUpdatedTime(new Date());
        track.setStatus(OrderStatus.RETURN_REQUESTED);

        String notes = "Reason: " + request.getReason();
        if (Strings.isNotBlank(request.getNote())) {
            notes += ". " + request.getNote();
        }

        track.setNotes(notes);

        order.getOrderTracks().add(track);
        order.setStatus(OrderStatus.RETURN_REQUESTED);

        orderRepository.save(order);
    }
}
