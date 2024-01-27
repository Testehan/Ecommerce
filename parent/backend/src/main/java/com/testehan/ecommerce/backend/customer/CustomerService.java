package com.testehan.ecommerce.backend.customer;

import com.testehan.ecommerce.backend.setting.country.CountryRepository;
import com.testehan.ecommerce.common.entity.Customer;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    public static final int CUSTOMERS_PER_PAGE = 10;
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public Page<Customer> listCustomersByPage(Integer pageNumber, String sortField, String sortOrder, String keyword) {
        Sort sort = Sort.by(sortField);
        sort = sortOrder.equalsIgnoreCase("ASC") ? sort.ascending() : sort.descending();

        // the first pageNumber displayed in the UI is 1...but the paging starts from 0, hence why we need to substract 1
        Pageable pageable = PageRequest.of(pageNumber -1, CUSTOMERS_PER_PAGE, sort);

        if (Strings.isNotBlank(keyword)){
            return customerRepository.findAllByKeyword(keyword,pageable);
        }

        return customerRepository.findAll(pageable);
    }
}
