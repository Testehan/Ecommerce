package com.testehan.ecommerce.frontend.paypal;

import com.testehan.ecommerce.common.entity.setting.Setting;
import com.testehan.ecommerce.common.entity.setting.SettingCategory;
import com.testehan.ecommerce.common.entity.setting.SettingsNames;
import com.testehan.ecommerce.frontend.checkout.paypal.PayPalOrderResponse;
import com.testehan.ecommerce.frontend.setting.SettingRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
// obviously the annotation from below means that the docker container needs to run, since it doesn't use a in memory DB, but the actual DB
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PayPalApiTests {
    private static final String BASE_URL = "https://api.sandbox.paypal.com";
    private static final String GET_ORDER_API = "/v2/checkout/orders/";
    private static String paypalClientId = "PAYPAL_CLIENT_ID";
    private static String paypalClientSecret = "PAYPAL_CLIENT_SECRET";

    @Autowired
    private SettingRepository settingRepository;

    @BeforeAll
    public void setupTests(){
        var paymentSettings = settingRepository.findByCategory(SettingCategory.PAYMENT);
        for (Setting setting : paymentSettings){
            if (setting.getKey().equalsIgnoreCase(SettingsNames.PAYPAL_API_CLIENT_ID.name())){
                paypalClientId = setting.getValue();
            }
            if (setting.getKey().equalsIgnoreCase(SettingsNames.PAYPAL_API_CLIENT_SECRET.name())){
                paypalClientSecret = setting.getValue();
            }
        }
    }

    @Test
    public void getOrderDetails() {
        var orderId = "53V11367VM0680055";
        var requestURL = BASE_URL + GET_ORDER_API + orderId;

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.add("Accept-Language", "en_US");
        headers.setBasicAuth(paypalClientId, paypalClientSecret);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<PayPalOrderResponse> response = restTemplate.exchange(
                requestURL, HttpMethod.GET, request, PayPalOrderResponse.class);
        PayPalOrderResponse orderResponse = response.getBody();

        System.out.println("Order ID: " + orderResponse.getId());
        System.out.println("Validated: " + orderResponse.validate(orderId));
        assertThat(orderResponse.validate(orderId)).isTrue();

    }
}
