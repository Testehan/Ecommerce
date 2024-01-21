package com.testehan.ecommerce.frontend.customer;

import com.testehan.ecommerce.common.entity.Country;
import com.testehan.ecommerce.common.entity.Customer;
import com.testehan.ecommerce.frontend.setting.country.CountryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Date;
import java.util.List;

@Service
public class CustomerService {

    @Autowired
    private CountryRepository countryRepo;

    @Autowired
    private CustomerRepository customerRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Country> listAllCountries() {
        return countryRepo.findAllByOrderByNameAsc();
    }

    public boolean isEmailUnique(String email) {
        var customer = customerRepo.findByEmail(email);
        return customer == null;
    }

    public void registerCustomer(Customer customer){
        encodePassword(customer);
        customer.setEnabled(false);
        customer.setCreatedTime(new Date());

        var randomCode = randomString(64);
        customer.setVerificationCode(randomCode);

        System.out.println("Verification code " + randomCode);
    }

    private void encodePassword(Customer customer) {
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
    }

    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static SecureRandom secureRandom = new SecureRandom();

    private String randomString(int len){
        StringBuilder sb = new StringBuilder(len);
        for(int i = 0; i < len; i++)
            sb.append(AB.charAt(secureRandom.nextInt(AB.length())));
        return sb.toString();
    }
}
