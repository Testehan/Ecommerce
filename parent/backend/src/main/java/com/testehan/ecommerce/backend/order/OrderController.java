package com.testehan.ecommerce.backend.order;

import com.testehan.ecommerce.backend.security.ShopUserDetails;
import com.testehan.ecommerce.backend.setting.SettingService;
import com.testehan.ecommerce.backend.util.paging.PagingAndSortingHelper;
import com.testehan.ecommerce.backend.util.paging.PagingAndSortingParam;
import com.testehan.ecommerce.common.entity.Country;
import com.testehan.ecommerce.common.entity.setting.Setting;
import com.testehan.ecommerce.common.exception.OrderNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class OrderController {

    private static final String DEFAULT_REDIRECT_URL = "redirect:/orders/page/1?sortField=orderTime&sortOrder=des";

    @Autowired
    private OrderService orderService;
    @Autowired
    private SettingService settingService;

    @GetMapping("/orders")
    public String listFirstPage(){
        return DEFAULT_REDIRECT_URL;
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

    @GetMapping("/orders/detail/{id}")
    public String viewOrderDetails(@PathVariable("id") Integer id, Model model,
                                   RedirectAttributes ra, HttpServletRequest request,
                                   @AuthenticationPrincipal ShopUserDetails loggedUser) {

        try {
            var order = orderService.get(id);
            loadCurrencySetting(request);

            var isVisibleForAdminOrSalesperson = false;

            if (loggedUser.hasRole("Admin") || loggedUser.hasRole("Salesperson")) {
                isVisibleForAdminOrSalesperson = true;
            }

            model.addAttribute("isVisibleForAdminOrSalesperson", isVisibleForAdminOrSalesperson);
            model.addAttribute("order", order);

            return "orders/order_details_modal";
        } catch (OrderNotFoundException ex) {
            ra.addFlashAttribute("messageError", ex.getMessage());
            return DEFAULT_REDIRECT_URL;
        }

    }

    @GetMapping("/orders/delete/{id}")
    public String deleteOrder(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {

        try {
            orderService.delete(id);
            ra.addFlashAttribute("messageSuccess", "The order with ID " + id + " has been deleted.");
        } catch (OrderNotFoundException ex) {
            ra.addFlashAttribute("messageError", ex.getMessage());
        }
        return DEFAULT_REDIRECT_URL;
    }

    @GetMapping("/orders/edit/{id}")
    public String editOrder(@PathVariable("id") Integer id, Model model, RedirectAttributes ra,
                            HttpServletRequest request) {

        try {
            var order = orderService.get(id);;

            List<Country> listCountries = orderService.listAllCountries();

            model.addAttribute("pageTitle", "Edit Order (ID: " + id + ")");
            model.addAttribute("order", order);
            model.addAttribute("listCountries", listCountries);

            return "orders/order_form";

        } catch (OrderNotFoundException ex) {
            ra.addFlashAttribute("message", ex.getMessage());
            return DEFAULT_REDIRECT_URL;
        }

    }

    public void loadCurrencySetting(HttpServletRequest request) {
        var currencySettings = settingService.getCurrencySettings();
        for (Setting setting : currencySettings) {
            request.setAttribute(setting.getKey(), setting.getValue());
        }
    }
}
