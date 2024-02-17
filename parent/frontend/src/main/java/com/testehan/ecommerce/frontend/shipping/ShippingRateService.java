package com.testehan.ecommerce.frontend.shipping;

import com.testehan.ecommerce.common.entity.Address;
import com.testehan.ecommerce.common.entity.Customer;
import com.testehan.ecommerce.common.entity.ShippingRate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShippingRateService {

    @Autowired
    private ShippingRateRepository shippingRateRepository;

    public ShippingRate getShippingRateForCustomer(Customer customer){
        var state = customer.getState();
        if (state == null || state.isEmpty()){
            state = customer.getCity();
        }

        return shippingRateRepository.findByCountryAndState(customer.getCountry(),state);
    }

    public ShippingRate getShippingRateForAddress(Address address){
        var state = address.getState();
        if (state == null || state.isEmpty()){
            state = address.getCity();
        }

        return shippingRateRepository.findByCountryAndState(address.getCountry(),state);
    }
}
