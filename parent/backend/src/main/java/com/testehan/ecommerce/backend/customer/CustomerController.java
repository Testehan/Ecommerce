package com.testehan.ecommerce.backend.customer;

import com.testehan.ecommerce.common.entity.Country;
import com.testehan.ecommerce.common.entity.Customer;
import com.testehan.ecommerce.common.exception.CustomerNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class CustomerController {

    private static final String NO_KEYWORD="";

    private static final String DEFAULT_REDIRECT_URL = "redirect:/customers/page/1?sortField=firstName&sortOrder=asc";

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
        model.addAttribute("moduleURL", "/customers");

        // because first is folder from "templates"
        return "customers/customers";
    }

    @GetMapping("/customers/{id}/enabled/{status}")
    public String updateCustomerEnabledStatus(@PathVariable("id") Integer id,
                                              @PathVariable("status") boolean enabled, RedirectAttributes redirectAttributes) {

        customerService.updateCustomerEnabledStatus(id, enabled);
        String status = enabled ? "enabled" : "disabled";
        String message = "The Customer ID " + id + " has been " + status;
        redirectAttributes.addFlashAttribute("message", message);

        return DEFAULT_REDIRECT_URL;
    }

    @GetMapping("/customers/delete/{id}")
    public String deleteCustomer(@PathVariable Integer id, RedirectAttributes ra) {

        try {
            customerService.delete(id);
            ra.addFlashAttribute("message", "The customer ID " + id + " has been deleted successfully.");
        } catch (CustomerNotFoundException ex) {
            ra.addFlashAttribute("message", ex.getMessage());
        }

        return DEFAULT_REDIRECT_URL;
    }

    @GetMapping("/customers/detail/{id}")
    public String viewCustomer(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {

        try {
            Customer customer = customerService.get(id);
            model.addAttribute("customer", customer);

            return "customers/customer_detail_modal";
        } catch (CustomerNotFoundException ex) {
            ra.addFlashAttribute("message", ex.getMessage());

            return DEFAULT_REDIRECT_URL;
        }
    }

    @GetMapping("/customers/edit/{id}")
    public String editCustomer(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {

        try {
            Customer customer = customerService.get(id);
            List<Country> countries = customerService.listAllCountries();

            model.addAttribute("listCountries", countries);
            model.addAttribute("customer", customer);
            model.addAttribute("pageTitle", String.format("Edit Customer (ID: %d)", id));

            return "customers/customer_form";

        } catch (CustomerNotFoundException ex) {
            ra.addFlashAttribute("message", ex.getMessage());
            return DEFAULT_REDIRECT_URL;
        }
    }

    @PostMapping("/customers/save")
    public String saveCustomer(Customer customer, Model model, RedirectAttributes ra) {

        customerService.save(customer);
        ra.addFlashAttribute("message", "The customer ID " + customer.getId() + " has been updated successfully.");

        return DEFAULT_REDIRECT_URL;
    }

}
