package com.testehan.ecommerce.backend.setting.country;

import com.testehan.ecommerce.common.entity.Country;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CountryRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CountryRestController.class);

    @Autowired
    private CountryRepository countryRepository;

    @GetMapping("/countries/list")
    public List<Country> listAllCountries(){
        LOGGER.info("Request was made to obtain a list with all countries");
        return countryRepository.findAllByOrderByNameAsc();
    }

    @PostMapping("/countries/save")
    public String save(@RequestBody Country country){
        var savedCountry = countryRepository.save(country);
        return String.valueOf(savedCountry.getId());
    }

    @GetMapping("/countries/delete/{id}")
    public void deleteCountryWithId(@PathVariable("id") Integer id){
        countryRepository.deleteById(id);
    }
}
