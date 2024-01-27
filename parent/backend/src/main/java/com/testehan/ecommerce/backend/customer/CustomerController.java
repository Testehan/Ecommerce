package com.testehan.ecommerce.backend.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class CustomerController {

    private static final String NO_KEYWORD="";

    @Autowired
    private CustomerService customerService;

    @GetMapping("/customers")
    public String listFirstPage(Model model){
        return listCustomersByPage(1,model, "firstName", "asc", NO_KEYWORD);
    }

    @GetMapping("/customers/page/{pageNumber}")
    public String listCustomersByPage(@PathVariable(name = "pageNumber") Integer pageNumber, Model model,
                                     @Param("sortField")String sortField, @Param("sortOrder")String sortOrder,
                                     @Param("keyword")String keyword){

        var pageOfCustomers = customerService.listCustomersByPage(pageNumber, sortField, sortOrder, keyword);
        model.addAttribute("listCustomers",pageOfCustomers.getContent());

        long startCount = (pageNumber-1)* CustomerService.CUSTOMERS_PER_PAGE + 1;
        long endCount = startCount + CustomerService.CUSTOMERS_PER_PAGE - 1;
        if (endCount > pageOfCustomers.getTotalElements()){
            endCount = pageOfCustomers.getTotalElements();
        }
        model.addAttribute("startCount",startCount);
        model.addAttribute("endCount",endCount);
        model.addAttribute("currentPage",pageNumber);

        model.addAttribute("totalItems",pageOfCustomers.getTotalElements());
        model.addAttribute("totalPages", pageOfCustomers.getTotalPages());

        String reverseSortOrder = sortOrder.equalsIgnoreCase("asc") ? "desc" : "asc";
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("reverseSortOrder", reverseSortOrder);
        model.addAttribute("keyword", keyword);


        // because first is folder from "templates"
        return "customers/customers";
    }
}
