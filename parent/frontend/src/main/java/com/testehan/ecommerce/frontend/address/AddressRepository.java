package com.testehan.ecommerce.frontend.address;

import com.testehan.ecommerce.common.entity.Address;
import com.testehan.ecommerce.common.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {

    List<Address> findByCustomer(Customer customer);

    @Query("SELECT a FROM Address a WHERE a.id = ?1 AND a.customer.id = ?2")
    Address findByIdAndCustomer(Integer addressId, Integer customerId);

    @Query("DELETE FROM Address a WHERE a.id = ?1 AND a.customer.id = ?2")
    @Modifying
    void deleteByIdAndCustomer(Integer addressId, Integer customerId);
}
