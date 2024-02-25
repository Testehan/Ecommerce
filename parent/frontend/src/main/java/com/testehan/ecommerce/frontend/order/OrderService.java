package com.testehan.ecommerce.frontend.order;

import com.testehan.ecommerce.common.entity.Address;
import com.testehan.ecommerce.common.entity.CartItem;
import com.testehan.ecommerce.common.entity.Customer;
import com.testehan.ecommerce.common.entity.order.Order;
import com.testehan.ecommerce.common.entity.order.OrderDetail;
import com.testehan.ecommerce.common.entity.order.OrderStatus;
import com.testehan.ecommerce.common.entity.order.PaymentMethod;
import com.testehan.ecommerce.frontend.checkout.CheckoutInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class OrderService {

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

        return orderRepository.save(newOrder);
    }
}
