package com.testehan.ecommerce.backend.order;

import com.testehan.ecommerce.backend.setting.SettingService;
import com.testehan.ecommerce.backend.util.paging.PagingAndSortingHelper;
import com.testehan.ecommerce.backend.util.paging.PagingAndSortingParam;
import com.testehan.ecommerce.common.entity.Setting;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class OrderController {

    private static final String DEFAULT_REDIRECT_URL = "redirect:/customers/page/1?sortField=firstName&sortOrder=asc";

    @Autowired
    private OrderService orderService;
    @Autowired
    private SettingService settingService;

    @GetMapping("/orders")
    public String listFirstPage(){
        return "redirect:/orders/page/1?sortField=orderTime&sortOrder=des";
    }

    @GetMapping("/orders/page/{pageNumber}")
    public String listCustomersByPage( @PagingAndSortingParam(moduleURL = "/orders", listName = "listOrders") PagingAndSortingHelper pagingAndSortingHelper,
                                       @PathVariable(name = "pageNumber") Integer pageNumber,
                                       HttpServletRequest request){

        orderService.listByPage(pageNumber, pagingAndSortingHelper);
        loadCurrencySetting(request);
        // because first is folder from "templates"
        return "orders/orders";
    }

    public void loadCurrencySetting(HttpServletRequest request) {
        var currencySettings = settingService.getCurrencySettings();
        for (Setting setting : currencySettings) {
            request.setAttribute(setting.getKey(), setting.getValue());
        }
    }
}
