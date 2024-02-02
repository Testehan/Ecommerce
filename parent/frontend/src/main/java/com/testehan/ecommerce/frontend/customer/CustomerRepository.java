package com.testehan.ecommerce.frontend.customer;

import com.testehan.ecommerce.common.entity.AuthenticationType;
import com.testehan.ecommerce.common.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    @Query("SELECT c FROM Customer c WHERE CONCAT(c.email, ' ', c.firstName, ' ', c.lastName, ' ', "
            + "c.addressLine1, ' ', c.addressLine2, ' ', c.city, ' ', c.state, "
            + "' ', c.postalCode, ' ', c.country.name) LIKE %?1%")
    Page<Customer> findAll(String keyword, Pageable pageable);

    @Query("UPDATE Customer c SET c.enabled = true, c.verificationCode = null WHERE c.id = ?1")
    @Modifying
    void enable(Integer id);

    @Query("SELECT c FROM Customer c WHERE c.email = ?1")
    Customer findByEmail(String email);

    @Query("SELECT c FROM Customer c WHERE c.verificationCode = ?1")
    Customer findByVerificationCode(String code);

    Long countById(Integer id);

    @Query("UPDATE Customer c SET c.authenticationType = ?2 WHERE c.id = ?1 ")
    @Modifying
    void updateAuthenticationType(Integer customerId, AuthenticationType authenticationType);

    Customer findByResetPasswordToken(String resetPasswordToken);
}
