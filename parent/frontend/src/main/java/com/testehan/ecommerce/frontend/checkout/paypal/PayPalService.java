package com.testehan.ecommerce.frontend.checkout.paypal;

import com.testehan.ecommerce.common.exception.PayPalApiException;
import com.testehan.ecommerce.frontend.setting.PaymentSettingBag;
import com.testehan.ecommerce.frontend.setting.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Service
public class PayPalService {
    private static final String GET_ORDER_API = "/v2/checkout/orders/";

    @Autowired
    private SettingService settingService;

    public boolean validateOrder(String orderId) throws PayPalApiException {
        PayPalOrderResponse orderResponse = getOrderDetails(orderId);

        return orderResponse.validate(orderId);
    }

    private PayPalOrderResponse getOrderDetails(String orderId) throws PayPalApiException {
        ResponseEntity<PayPalOrderResponse> response = makeRequest(orderId);

        HttpStatus statusCode = (HttpStatus) response.getStatusCode();

        if (!statusCode.equals(HttpStatus.OK)) {
            throwExceptionForNonOKResponse(statusCode);
        }

        return response.getBody();
    }

    private ResponseEntity<PayPalOrderResponse> makeRequest(String orderId) {
        PaymentSettingBag paymentSettings = settingService.getPaymentSettings();
        var baseURL = paymentSettings.getURL();
        var requestURL = baseURL + GET_ORDER_API + orderId;
        var clientId = paymentSettings.getClientID();
        var clientSecret = paymentSettings.getClientSecret();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.add("Accept-Language", "en_US");
        headers.setBasicAuth(clientId, clientSecret);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();

        return restTemplate.exchange(requestURL, HttpMethod.GET, request, PayPalOrderResponse.class);
    }

    private void throwExceptionForNonOKResponse(HttpStatus statusCode) throws PayPalApiException {
        String message = null;

        switch (statusCode) {
            case NOT_FOUND:
                message = "Order ID not found";
                break;
            case BAD_REQUEST:
                message = "Bad Request to PayPal Checkout API";
                break;
            case INTERNAL_SERVER_ERROR:
                message = "PayPal server error";
                break;
            default:
                message = "PayPal returned non-OK status code";
        }

        throw new PayPalApiException(message);
    }

}
