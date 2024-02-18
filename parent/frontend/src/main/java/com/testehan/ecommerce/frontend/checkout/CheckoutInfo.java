package com.testehan.ecommerce.frontend.checkout;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Calendar;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CheckoutInfo {

    private long productCost;
    private long productTotal;
    private long shippingCostTotal;
    private long paymentTotal;
    private int deliverDays;
    private Date deliveryDate;
    private boolean codSupported;

    public Date getDeliveryDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, deliverDays);
        return calendar.getTime();
    }
}
