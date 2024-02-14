package com.testehan.ecommerce.backend.shipping;

import com.testehan.ecommerce.backend.util.paging.PagingAndSortingHelper;
import com.testehan.ecommerce.backend.util.paging.PagingAndSortingParam;
import com.testehan.ecommerce.common.entity.ShippingRate;
import com.testehan.ecommerce.common.exception.ShippingRateAlreadyExistsException;
import com.testehan.ecommerce.common.exception.ShippingRateNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ShippingRateController {

    public static final String DEFAULT_REDIRECT_URL = "redirect:/shipping_rates/page/1?sortField=country&sortOrder=asc";
    private ShippingRateService shippingRateService;

    public ShippingRateController(ShippingRateService shippingRateService) {
        this.shippingRateService = shippingRateService;
    }

    @GetMapping("/shipping_rates")
    public String listFirstPage(){
        return DEFAULT_REDIRECT_URL;
    }

    @GetMapping("/shipping_rates/page/{pageNumber}")
    public String listShippingRatesByPage(@PagingAndSortingParam(moduleURL = "/shipping_rates", listName = "shippingRates") PagingAndSortingHelper pagingAndSortingHelper,
                                          @PathVariable(name = "pageNumber") Integer pageNumber){

        shippingRateService.listShippingRatesByPage(pageNumber, pagingAndSortingHelper);

        // because first is folder from "templates"
        return "shipping_rates/shipping_rates";
    }

    @GetMapping("/shipping_rates/new")
    public String newRate(Model model) {

        var listCountries = shippingRateService.listAllCountries();

        model.addAttribute("rate", new ShippingRate());
        model.addAttribute("listCountries", listCountries);
        model.addAttribute("pageTitle", "New Rate");

        return "shipping_rates/shipping_rate_form";
    }

    @PostMapping("/shipping_rates/save")
    public String saveRate(ShippingRate rate, RedirectAttributes ra) {

        try {
            shippingRateService.save(rate);
            ra.addFlashAttribute("message", "The shipping rate has been saved.");
        } catch (ShippingRateAlreadyExistsException ex) {
            ra.addFlashAttribute("message", ex.getMessage());
        }
        return DEFAULT_REDIRECT_URL;
    }

    @GetMapping("/shipping_rates/edit/{id}")
    public String editRate(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes ra) {

        try {
            var rate = shippingRateService.get(id);
            var listCountries = shippingRateService.listAllCountries();

            model.addAttribute("listCountries", listCountries);
            model.addAttribute("rate", rate);
            model.addAttribute("pageTitle", "Edit Rate (ID: " + id + ")");

            return "shipping_rates/shipping_rate_form";
        } catch (ShippingRateNotFoundException ex) {

            ra.addFlashAttribute("message", ex.getMessage());
            return DEFAULT_REDIRECT_URL;
        }
    }

    @GetMapping("/shipping_rates/cod/{id}/enabled/{supported}")
    public String updateCashOnDeliveryStatus(@PathVariable(name = "id") Integer id,
                                            @PathVariable(name = "supported") Boolean supported,
                                            Model model, RedirectAttributes ra) {

        try {
            shippingRateService.updateCashOnDeliveryStatus(id, supported);
            ra.addFlashAttribute("message", "COD support for shipping rate ID " + id + " has been updated.");
        } catch (ShippingRateNotFoundException ex) {
            ra.addFlashAttribute("message", ex.getMessage());
        }
        return DEFAULT_REDIRECT_URL;
    }
}
