package com.testehan.ecommerce.backend.shipping;

import com.testehan.ecommerce.backend.util.paging.PagingAndSortingHelper;
import com.testehan.ecommerce.backend.util.paging.PagingAndSortingParam;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ShippingRateController {

    private ShippingRateService shippingRateService;

    public ShippingRateController(ShippingRateService shippingRateService) {
        this.shippingRateService = shippingRateService;
    }

    @GetMapping("/shipping_rates")
    public String listFirstPage(){
        return "redirect:/shipping_rates/page/1?sortField=country&sortOrder=asc";
    }

    @GetMapping("/shipping_rates/page/{pageNumber}")
    public String listShippingRatesByPage(@PagingAndSortingParam(moduleURL = "/shipping_rates", listName = "shippingRates") PagingAndSortingHelper pagingAndSortingHelper,
                                          @PathVariable(name = "pageNumber") Integer pageNumber){

        shippingRateService.listShippingRatesByPage(pageNumber, pagingAndSortingHelper);

        // because first is folder from "templates"
        return "shipping_rates/shipping_rates";
    }
}
