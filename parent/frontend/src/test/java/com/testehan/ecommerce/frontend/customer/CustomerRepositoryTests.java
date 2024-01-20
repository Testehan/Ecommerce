package com.testehan.ecommerce.frontend.customer;

import com.testehan.ecommerce.common.entity.Country;
import com.testehan.ecommerce.common.entity.Customer;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class CustomerRepositoryTests {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TestEntityManager entityManager;


    @Test
    public void createCustomer1() {
        var countryId = 9; // coresponds to US
        var country = entityManager.find(Country.class, countryId);

        var customer = new Customer();
        customer.setCountry(country);
        customer.setFirstName("Dan");
        customer.setLastName("t");
        customer.setPassword("password123");
        customer.setEmail("dan@yahoo.com");
        customer.setPhoneNumber("123456789");
        customer.setAddressLine1("batcave");
        customer.setCity("Gotham");
        customer.setState("California");
        customer.setPostalCode("1010");
        customer.setCreatedTime(new Date());

        var savedCustomer = customerRepository.save(customer);

        assertThat(savedCustomer).isNotNull();
        assertThat(savedCustomer.getId()).isGreaterThan(0);
    }

    @Test
    public void createCustomer2() {
        var countryId = 2; // India
        var country = entityManager.find(Country.class, countryId);

        var customer = new Customer();
        customer.setCountry(country);
        customer.setFirstName("Batman");
        customer.setLastName("Wayne");
        customer.setPassword("alfredIsCool");
        customer.setEmail("batman@batman.com");
        customer.setPhoneNumber("66666666");
        customer.setAddressLine1("batcave nr 5");
        customer.setAddressLine2("catwoman;s apartment");
        customer.setCity("Gotham");
        customer.setState("Punjab");
        customer.setPostalCode("234");
        customer.setCreatedTime(new Date());

        var savedCustomer = customerRepository.save(customer);

        assertThat(savedCustomer).isNotNull();
        assertThat(savedCustomer.getId()).isGreaterThan(0);
    }

    @Test
    public void listCustomers() {
        Iterable<Customer> customers = customerRepository.findAll();
        customers.forEach(System.out::println);

        assertThat(customers).hasSizeGreaterThan(1);
    }

    @Test
    public void updateCustomer() {
        var customerId = 1;
        var lastName = "Muhahahaha";

        var customer = customerRepository.findById(customerId).get();
        customer.setLastName(lastName);
        customer.setEnabled(true);

        var updatedCustomer = customerRepository.save(customer);
        assertThat(updatedCustomer.getLastName()).isEqualTo(lastName);
    }

    @Test
    public void getCustomer() {
        var customerId = 2;
        Optional<Customer> findById = customerRepository.findById(customerId);

        assertThat(findById).isPresent();

        var customer = findById.get();
        System.out.println(customer);
    }

    @Test
    public void deleteCustomer() {
        var customerId = 2;
        customerRepository.deleteById(customerId);

        Optional<Customer> findById = customerRepository.findById(customerId);
        assertThat(findById).isNotPresent();
    }

    @Test
    public void findByEmail() {
        var email = "dan@yahoo.com";
        var customer = customerRepository.findByEmail(email);

        assertThat(customer).isNotNull();
        System.out.println(customer);
    }

    @Test
    @Disabled
    public void findByVerificationCode() {
        var code = "code_123";
        var customer = customerRepository.findByVerificationCode(code);

        assertThat(customer).isNotNull();
        System.out.println(customer);
    }


}
