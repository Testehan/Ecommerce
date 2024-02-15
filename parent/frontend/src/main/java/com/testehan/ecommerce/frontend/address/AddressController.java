package com.testehan.ecommerce.frontend.address;

import com.testehan.ecommerce.common.entity.Address;
import com.testehan.ecommerce.common.exception.CustomerNotFoundException;
import com.testehan.ecommerce.frontend.customer.CustomerService;
import com.testehan.ecommerce.frontend.util.Utility;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AddressController {

    @Autowired
    private AddressService addressService;
    @Autowired
    private CustomerService customerService;

    @GetMapping("/address_book")
    public String showAddressBook(Model model, HttpServletRequest request) throws CustomerNotFoundException {

        var customer = Utility.getAuthenticatedCustomer(customerService, request);
        var listAddresses = addressService.listAddressBook(customer);
        var usePrimaryAddressAsDefault = true;

        for (Address address : listAddresses) {
            if (address.isDefaultForShipping()) {
                usePrimaryAddressAsDefault = false;
                break;
            }
        }

        model.addAttribute("listAddresses", listAddresses);
        model.addAttribute("customer", customer);
        model.addAttribute("usePrimaryAddressAsDefault", usePrimaryAddressAsDefault);

        return "address_book/addresses";
    }

    @GetMapping("/address_book/new")
    public String addAddress(Model model) {

        var listCountries = customerService.listAllCountries();

        model.addAttribute("listCountries", listCountries);
        model.addAttribute("address", new Address());
        model.addAttribute("pageTitle", "Add New Address");

        return "address_book/address_form";
    }

    @PostMapping("/address_book/save")
    public String saveAddress(Address address, HttpServletRequest request, RedirectAttributes ra) throws CustomerNotFoundException {

        var customer = Utility.getAuthenticatedCustomer(customerService, request);

        address.setCustomer(customer);
        addressService.save(address);

        ra.addFlashAttribute("message", "The address has been saved successfully.");

        var redirectOption = request.getParameter("redirect");
        var redirectURL = "redirect:/address_book";

        if ("checkout".equals(redirectOption)) {
            redirectURL += "?redirect=checkout";
        }

        return redirectURL;

    }

    @GetMapping("/address_book/edit/{id}")
    public String editAddress(@PathVariable("id") Integer addressId, Model model,
                              HttpServletRequest request) throws CustomerNotFoundException {

        var customer = Utility.getAuthenticatedCustomer(customerService, request);
        var listCountries = customerService.listAllCountries();
        var address = addressService.get(addressId, customer.getId());

        model.addAttribute("address", address);
        model.addAttribute("listCountries", listCountries);
        model.addAttribute("pageTitle", "Edit Address (ID: " + addressId + ")");

        return "address_book/address_form";
    }

    @GetMapping("/address_book/delete/{id}")
    public String deleteAddress(@PathVariable("id") Integer addressId, RedirectAttributes ra,
                                HttpServletRequest request) throws CustomerNotFoundException {

        var customer = Utility.getAuthenticatedCustomer(customerService, request);
        addressService.delete(addressId, customer.getId());
        ra.addFlashAttribute("message", "The address ID " + addressId + " has been deleted.");
        return "redirect:/address_book";
    }

}
