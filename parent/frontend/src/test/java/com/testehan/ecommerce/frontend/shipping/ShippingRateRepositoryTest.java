package com.testehan.ecommerce.frontend.shipping;

import com.testehan.ecommerce.common.entity.Country;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class ShippingRateRepositoryTest {

    @Autowired
    private ShippingRateRepository shippingRateRepository;

    @Test
    public void findByCountryAndState() {
        var country = new Country(2);
        var state = "Karnataka";
        var shippingRate = shippingRateRepository.findByCountryAndState(country,state);
        assertThat(shippingRate).isNotNull();
        System.out.println(shippingRate);
    }
}
