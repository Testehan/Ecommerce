package com.testehan.ecommerce.backend.shipping;

import com.testehan.ecommerce.common.entity.Country;
import com.testehan.ecommerce.common.entity.ShippingRate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class ShippingRateRepositoryTest {

    private ShippingRateRepository shippingRateRepository;

    private TestEntityManager entityManager;

    @Autowired
    public ShippingRateRepositoryTest(ShippingRateRepository repo, TestEntityManager entityManager) {
        super();
        this.shippingRateRepository = repo;
        this.entityManager = entityManager;
    }

    @Test
    public void createNew() {
        var india = new Country(2);
        var newRate = new ShippingRate();
        newRate.setCountry(india);
        newRate.setState("abc");
        newRate.setRate(8);
        newRate.setDays(3);
        newRate.setCodSupported(true);

        var savedRate = shippingRateRepository.save(newRate);
        assertThat(savedRate).isNotNull();
        assertThat(savedRate.getId()).isGreaterThan(0);
    }

    @Test
    public void update() {
        var rateId = 1;
        var rate = entityManager.find(ShippingRate.class, rateId);
        rate.setRate(9);
        rate.setDays(2);
        var updatedRate = shippingRateRepository.save(rate);

        assertThat(updatedRate.getRate()).isEqualTo(9);
        assertThat(updatedRate.getDays()).isEqualTo(2);
    }

    @Test
    public void findAll() {
        var rates = shippingRateRepository.findAll();
        assertThat(rates.size()).isGreaterThan(0);

        rates.forEach(System.out::println);
    }

    @Test
    public void findByCountryAndState() {
        var countryId = 2;
        var state = "abc";
        var rate = shippingRateRepository.findByCountryAndState(countryId, state);

        assertThat(rate).isNotNull();
        System.out.println(rate);
    }

    @Test
    public void updateCashOnDeliverySupport() {
        var rateId = 1;
        shippingRateRepository.updateCashOnDeliveryStatus(rateId, false);

        var rate = entityManager.find(ShippingRate.class, rateId);
        assertThat(rate.isCodSupported()).isFalse();
    }

    @Test
    public void delete() {
        var rateId = 1;
        shippingRateRepository.deleteById(rateId);

        var rate = entityManager.find(ShippingRate.class, rateId);
        assertThat(rate).isNull();
    }
}
