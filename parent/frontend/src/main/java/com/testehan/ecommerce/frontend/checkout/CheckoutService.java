package com.testehan.ecommerce.frontend.checkout;

import com.testehan.ecommerce.common.entity.CartItem;
import com.testehan.ecommerce.common.entity.ShippingRate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CheckoutService {

    // YOU CAN read more about dimensional weight here : https://www.jaygroup.com/how-to-calculate-dimensional-weight/
    private static final int FED_EX_DIM_FACTOR = 139;

    public CheckoutInfo prepareCheckout(List<CartItem> cartItems, ShippingRate shippingRate) {
        var checkoutInfo = new CheckoutInfo();

        long productCost = calculateProductCost(cartItems);
        long productTotal = calculateProductTotal(cartItems);
        long shippingCostTotal = calculateShippingCost(cartItems, shippingRate);
        long paymentTotal = productTotal + shippingCostTotal;

        checkoutInfo.setProductCost(productCost);
        checkoutInfo.setProductTotal(productTotal);
        checkoutInfo.setDeliverDays(shippingRate.getDays());
        checkoutInfo.setCodSupported(shippingRate.isCodSupported());
        checkoutInfo.setShippingCostTotal(shippingCostTotal);
        checkoutInfo.setPaymentTotal(paymentTotal);



        return checkoutInfo;
    }

    private long calculateShippingCost(List<CartItem> cartItems, ShippingRate shippingRate) {
        long shippingCostTotal=0;
        for (CartItem cartItem : cartItems){
            var product = cartItem.getProduct();
            // see comment above the constant for more info
            long dimensionalWeight = (product.getLength() * product.getWidth() * product.getHeight())/FED_EX_DIM_FACTOR;
            long finalWeight = product.getWeight() > dimensionalWeight ? product.getWeight() : dimensionalWeight;
            long shippingCost = finalWeight * cartItem.getQuantity() * shippingRate.getRate();

            cartItem.setShippingCost(shippingCost);
            shippingCostTotal = shippingCostTotal + shippingCost;
        }

        return shippingCostTotal;
    }

    private long calculateProductTotal(List<CartItem> cartItems) {
        long total = 0;
        for (CartItem cartItem : cartItems){
            total = (long) (total + cartItem.getSubtotal());    // TODO this cast is not a priority right now
        }
        return total;
    }

    private long calculateProductCost(List<CartItem> cartItems) {
        long cost = 0;
        for (CartItem cartItem : cartItems){
            cost = cost + cartItem.getQuantity() * cartItem.getProduct().getCost();
        }
        return cost;
    }
}
