package com.testehan.ecommerce.frontend.order;

import com.testehan.ecommerce.common.exception.CustomerNotFoundException;
import com.testehan.ecommerce.common.exception.OrderNotFoundException;
import com.testehan.ecommerce.frontend.customer.CustomerService;
import com.testehan.ecommerce.frontend.order.dto.OrderReturnRequest;
import com.testehan.ecommerce.frontend.order.dto.OrderReturnResponse;
import com.testehan.ecommerce.frontend.util.Utility;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderRestController {

    private OrderService orderService;
    private CustomerService customerService;

    @Autowired
    public OrderRestController(OrderService orderService, CustomerService customerService) {
        super();
        this.orderService = orderService;
        this.customerService = customerService;
    }

    @PostMapping("/orders/return")
    public ResponseEntity<?> handleOrderReturnRequest(@RequestBody OrderReturnRequest returnRequest,
                                                      HttpServletRequest servletRequest) throws CustomerNotFoundException
    {

        var customer = Utility.getAuthenticatedCustomer(customerService, servletRequest);

        if(customer == null) {
            return new ResponseEntity<>("Authentication required", HttpStatus.BAD_REQUEST);
        }


        try {
            orderService.setOrderReturnRequested(returnRequest, customer);
        } catch (OrderNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new OrderReturnResponse(returnRequest.getOrderId()), HttpStatus.OK);
    }
}
