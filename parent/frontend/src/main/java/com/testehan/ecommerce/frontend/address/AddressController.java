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
}
