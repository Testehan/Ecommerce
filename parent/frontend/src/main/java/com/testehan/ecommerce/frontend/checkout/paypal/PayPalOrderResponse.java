package com.testehan.ecommerce.frontend.checkout.paypal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter         // the reponse from paypal has way more information inside it, however right now only this is needed
public class PayPalOrderResponse {
    private String id;
    private String status;

    public boolean validate(String orderId) {
        return id.equals(orderId) && status.equals("COMPLETED");
    }
}
