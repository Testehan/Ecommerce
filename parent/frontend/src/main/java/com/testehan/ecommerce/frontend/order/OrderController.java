package com.testehan.ecommerce.frontend.order;

import com.testehan.ecommerce.common.entity.order.Order;
import com.testehan.ecommerce.common.exception.CustomerNotFoundException;
import com.testehan.ecommerce.frontend.customer.CustomerService;
import com.testehan.ecommerce.frontend.util.Utility;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class OrderController {
    private OrderService orderService;
    private CustomerService customerService;

    @Autowired
    public OrderController(OrderService orderService, CustomerService customerService) {
        super();
        this.orderService = orderService;
        this.customerService = customerService;
    }


    @GetMapping("/orders")
    public String listFirstPage(Model model, HttpServletRequest request) throws CustomerNotFoundException {
//        return listOrdersByPage(model, request, 1, "orderTime", "desc", null);
        return "redirect:/orders/page/1?sortField=orderTime&sortOrder=desc";
    }

    @GetMapping("/orders/page/{pageNum}")
    public String listOrdersByPage(Model model, HttpServletRequest request,
                                   @PathVariable(name = "pageNum") int pageNum,
                                   String sortField, String sortOrder, String keyword) throws CustomerNotFoundException
    {

        var customer = Utility.getAuthenticatedCustomer(customerService, request);
        Page<Order> page = orderService.listForCustomerByPage(customer, pageNum, sortField, sortOrder, keyword);

        List<Order> listOrders = page.getContent();

        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("listOrders", listOrders);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("keyword", keyword);
        model.addAttribute("moduleURL", "/orders");
        model.addAttribute("reverseSortOrder", sortOrder.equals("asc") ? "desc" : "asc");

        long startCount = (pageNum - 1) * OrderService.ORDERS_PER_PAGE + 1;

        model.addAttribute("startCount", startCount);

        long endCount = startCount + OrderService.ORDERS_PER_PAGE - 1;

        if (endCount > page.getTotalElements()) {
            endCount = page.getTotalElements();
        }

        model.addAttribute("endCount", endCount);

        return "orders/orders_customer";
    }

    @GetMapping("/orders/detail/{id}")
    public String viewOrderDetails(Model model, @PathVariable(name = "id") Integer id, HttpServletRequest request)
            throws CustomerNotFoundException
    {
        var customer = Utility.getAuthenticatedCustomer(customerService, request);
        var order = orderService.getOrder(id, customer);

        model.addAttribute("order", order);

        return "orders/order_details_modal";
    }
}
