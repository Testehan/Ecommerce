package com.testehan.ecommerce.frontend.shipping;

import com.testehan.ecommerce.common.entity.Country;
import com.testehan.ecommerce.common.entity.ShippingRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShippingRateRepository extends JpaRepository<ShippingRate, Integer> {

    ShippingRate findByCountryAndState(Country country, String state);
}
