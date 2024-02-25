package com.testehan.ecommerce.frontend.checkout;

import com.testehan.ecommerce.common.entity.ShippingRate;
import com.testehan.ecommerce.common.entity.order.Order;
import com.testehan.ecommerce.common.entity.order.PaymentMethod;
import com.testehan.ecommerce.common.exception.CustomerNotFoundException;
import com.testehan.ecommerce.frontend.address.AddressService;
import com.testehan.ecommerce.frontend.cart.ShoppingCartService;
import com.testehan.ecommerce.frontend.customer.CustomerService;
import com.testehan.ecommerce.frontend.order.OrderService;
import com.testehan.ecommerce.frontend.setting.SettingService;
import com.testehan.ecommerce.frontend.shipping.ShippingRateService;
import com.testehan.ecommerce.frontend.util.CustomerRegisterUtil;
import com.testehan.ecommerce.frontend.util.Utility;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Objects;

@Controller
public class CheckoutController {

    @Autowired
    private CheckoutService checkoutService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private AddressService addressService;
    @Autowired
    private ShippingRateService shippingRateService;
    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private SettingService settingService;

    @GetMapping("/checkout")
    public String showCheckoutPage(Model model, HttpServletRequest request) throws CustomerNotFoundException {
        var customer = Utility.getAuthenticatedCustomer(customerService, request);

        var defaultAddress = addressService.getDefaultAddress(customer.getId());
        ShippingRate shippingRate = null;
        if (Objects.nonNull(defaultAddress)) {
            model.addAttribute("shippingAddress", defaultAddress.toString());
            shippingRate = shippingRateService.getShippingRateForAddress(defaultAddress);
        } else {
            model.addAttribute("shippingAddress", customer.getAddress());
            shippingRate = shippingRateService.getShippingRateForCustomer(customer);
        }

        if (Objects.isNull(shippingRate)) {
            return "redirect:/cart";
        }

        var cartItems = shoppingCartService.listCartItems(customer);
        var checkoutInfo = checkoutService.prepareCheckout(cartItems, shippingRate);
        var currencyCode = settingService.getCurrencyCode();
        var paymentSettings = settingService.getPaymentSettings();
        var paypalClientId = paymentSettings.getClientID();

        model.addAttribute("checkoutInfo", checkoutInfo);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("currencyCode", currencyCode);
        model.addAttribute("customer", customer);
        model.addAttribute("paypalClientId", paypalClientId);

        return "checkout/checkout";
    }

    @PostMapping("/place_order")
    public String placeOrder(HttpServletRequest request) throws CustomerNotFoundException, MessagingException, UnsupportedEncodingException {
        var paymentType = request.getParameter("paymentMethod");
        PaymentMethod paymentMethod = PaymentMethod.valueOf(paymentType);

        var customer = Utility.getAuthenticatedCustomer(customerService, request);

        var defaultAddress = addressService.getDefaultAddress(customer.getId());
        ShippingRate shippingRate = null;
        if (Objects.nonNull(defaultAddress)) {
            shippingRate = shippingRateService.getShippingRateForAddress(defaultAddress);
        } else {
            shippingRate = shippingRateService.getShippingRateForCustomer(customer);
        }

        var cartItems = shoppingCartService.listCartItems(customer);
        var checkoutInfo = checkoutService.prepareCheckout(cartItems, shippingRate);

        var order = orderService.createOrder(customer,defaultAddress,cartItems,paymentMethod,checkoutInfo);
        shoppingCartService.deleteByCustomer(customer);
        sendOrderConfirmationEmail(request, order);

        return "checkout/order_completed";
    }

    private void sendOrderConfirmationEmail(HttpServletRequest request, Order order) throws MessagingException, UnsupportedEncodingException {
        var emailSettings = settingService.getEmailSettingsBag();
        var currencySettings = settingService.getCurrencySettings();

        JavaMailSenderImpl mailSender = CustomerRegisterUtil.initializeMailSender(emailSettings);

        var emailDestination = order.getCustomer().getEmail();
        var subject = emailSettings.getOrderConfirmationSubject();
        var content = emailSettings.getOrderConfirmationContent();

        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss E, dd MMM yyyy");
        var orderTime = dateFormat.format(order.getOrderTime());
        var totalAmount = Utility.formatCurrency(order.getTotal(),currencySettings);

        content = content.replace("[[user]]",order.getCustomer().getFullName());
        content = content.replace("[[orderId]]",String.valueOf(order.getId()));
        content = content.replace("[[orderTime]]",orderTime);
        content = content.replace("[[shippingAddress]]",order.getShippingAddress());
        content = content.replace("[[total]]",totalAmount);
        content = content.replace("[[paymentMethod]]",order.getPaymentMethod().toString());

        //content = content.replace("[[orderLink]]",order.getCustomer().getFullName());

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");

        helper.setFrom(emailSettings.getFromAddress(), emailSettings.getSenderName());
        helper.setTo(emailDestination);
        helper.setSubject(subject);
        helper.setText(content, true);

        mailSender.send(message);
    }

}
