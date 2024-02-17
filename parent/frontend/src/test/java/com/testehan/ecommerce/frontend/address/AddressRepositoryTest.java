package com.testehan.ecommerce.frontend.address;

import com.testehan.ecommerce.common.entity.Address;
import com.testehan.ecommerce.common.entity.Country;
import com.testehan.ecommerce.common.entity.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class AddressRepositoryTest {

    @Autowired
    private AddressRepository addressRepository;

    @Test
    public void addNewAddress() {
        var customerId = 6;
        var countryId = 2;

        var newAddress = new Address();
        newAddress.setCustomer(new Customer(customerId));
        newAddress.setCountry(new Country(countryId));
        newAddress.setFirstName("Dan");
        newAddress.setLastName("Te");
        newAddress.setPhoneNumber("678");
        newAddress.setAddressLine1("204 Strees of beauty");
        newAddress.setCity("New York");
        newAddress.setState("New York");
        newAddress.setPostalCode("345");

        var savedAddress = addressRepository.save(newAddress);

        assertThat(savedAddress).isNotNull();
        assertThat(savedAddress.getId()).isGreaterThan(0);
    }

    @Test
    public void findAddressByCustomer() {
        var customerId = 6;
        var listAddresses = addressRepository.findByCustomer(new Customer(customerId));
        assertThat(listAddresses.size()).isGreaterThan(0);

        listAddresses.forEach(System.out::println);
    }

    @Test
    public void findAddressByIdAndCustomer() {
        var addressId = 1;
        var customerId = 6;

        var address = addressRepository.findByIdAndCustomer(addressId, customerId);

        assertThat(address).isNotNull();
        System.out.println(address);
    }

    @Test
    public void updateAddress() {
        var addressId = 1;
        var phoneNumber = "123";

        var address = addressRepository.findById(addressId).get();
        address.setPhoneNumber(phoneNumber);

        var updatedAddress = addressRepository.save(address);
        assertThat(updatedAddress.getPhoneNumber()).isEqualTo(phoneNumber);
    }

    @Test
    public void deleteAddressByIdAndCustomer() {
        var addressId = 1;
        var customerId = 6;

        addressRepository.deleteByIdAndCustomer(addressId, customerId);

        var address = addressRepository.findByIdAndCustomer(addressId, customerId);
        assertThat(address).isNull();
    }

    @Test
    public void testGetDefault() {
        var customerId = 6;
        var address = addressRepository.findDefaultByCustomer(customerId);
        assertThat(address).isNotNull();
        System.out.println(address);
    }

}
