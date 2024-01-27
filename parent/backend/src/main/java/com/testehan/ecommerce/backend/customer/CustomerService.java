package com.testehan.ecommerce.backend.customer;

import com.testehan.ecommerce.backend.setting.country.CountryRepository;
import com.testehan.ecommerce.common.entity.Country;
import com.testehan.ecommerce.common.entity.Customer;
import com.testehan.ecommerce.common.exception.CustomerNotFoundException;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class CustomerService {

    public static final int CUSTOMERS_PER_PAGE = 3;     // i put a smaller value so that i can test pagination with less data
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

    public void updateCustomerEnabledStatus(Integer id, boolean enabled) {
        customerRepository.updateCustomerEnabledStatus(id, enabled);
    }

    public void delete(Integer id) throws CustomerNotFoundException {
        try {
            customerRepository.findById(id).get();
        } catch (NoSuchElementException e){
            throw new CustomerNotFoundException("Could not find any customer with ID " + id);
        }
        customerRepository.deleteById(id);

    }

    public Customer get(Integer id) throws CustomerNotFoundException {
        try {
            return customerRepository.findById(id).get();
        } catch (NoSuchElementException e){
            throw new CustomerNotFoundException("Could not find any customer with ID " + id);
        }
    }

    public List<Country> listAllCountries() {
        return countryRepository.findAllByOrderByNameAsc();
    }

    public void save(Customer customerInForm) {
        var customerInDB = customerRepository.findById(customerInForm.getId()).get();

        if (!customerInForm.getPassword().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(customerInForm.getPassword());
            customerInForm.setPassword(encodedPassword);
        } else {
            customerInForm.setPassword(customerInDB.getPassword());
        }

        customerInForm.setEnabled(customerInDB.isEnabled());
        customerInForm.setCreatedTime(customerInDB.getCreatedTime());
        customerInForm.setVerificationCode(customerInDB.getVerificationCode());
        customerInForm.setAuthenticationType(customerInDB.getAuthenticationType());
//        customerInForm.setResetPasswordToken(customerInDB.getResetPasswordToken());

        customerRepository.save(customerInForm);
    }

    public boolean isEmailUnique(Integer id, String email) {
        Customer existCustomer = customerRepository.findByEmail(email);

        if (existCustomer != null && existCustomer.getId() != id) {
            // found another customer having the same email
            return false;
        }

        return true;
    }
}
