package com.testehan.ecommerce.backend.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderRestController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/orders_shipper/update/{id}/{status}")
    public OrderResponseDTO updateOrderStatus(@PathVariable("id") Integer orderId,
                                              @PathVariable("status") String status) {
        orderService.updateStatus(orderId, status);
        return new OrderResponseDTO(orderId, status);
    }
}
