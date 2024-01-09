package com.testehan.ecommerce.backend.setting.country;

import com.testehan.ecommerce.common.entity.Country;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
// obviously the annotation from below means that the docker container needs to run, since it doesn't use a in memory DB, but the actual DB
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class CountryRepositoryTest {
    @Autowired
    private CountryRepository countryRepository;

    @Test
    public void testCreateCountry() {
        Country country = countryRepository.save(new Country("China", "CN"));
        assertThat(country).isNotNull();
        assertThat(country.getId()).isGreaterThan(0);
    }

    @Test
    public void testListCountries() {
        List<Country> listCountries = countryRepository.findAllByOrderByNameAsc();
        listCountries.forEach(System.out::println);

        assertThat(listCountries.size()).isGreaterThan(0);
    }

    @Test
    public void testUpdateCountry() {
        Integer id = 1;
        String name = "Republic of India";

        Country country = countryRepository.findById(id).get();
        country.setName(name);

        Country updatedCountry = countryRepository.save(country);

        assertThat(updatedCountry.getName()).isEqualTo(name);
    }

    @Test
    public void testGetCountry() {
        Integer id = 3;
        Country country = countryRepository.findById(id).get();
        assertThat(country).isNotNull();
    }

    @Test
    public void testDeleteCountry() {
        Integer id = 5;
        countryRepository.deleteById(id);

        Optional<Country> findById = countryRepository.findById(id);
        assertThat(findById.isEmpty());
    }
}
