package com.testehan.ecommerce.frontend.customer;

import com.testehan.ecommerce.common.entity.Country;
import com.testehan.ecommerce.frontend.setting.country.CountryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    @Autowired
    private CountryRepository countryRepo;

    @Autowired
    private CustomerRepository customerRepo;

    public List<Country> listAllCountries() {
        return countryRepo.findAllByOrderByNameAsc();
    }

    public boolean isEmailUnique(String email) {
        var customer = customerRepo.findByEmail(email);
        return customer == null;
    }
}
