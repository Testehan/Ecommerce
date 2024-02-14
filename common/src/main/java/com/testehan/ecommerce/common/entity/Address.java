package com.testehan.ecommerce.common.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "first_name", nullable = false, length = 45)
    protected String firstName;

    @Column(name = "last_name", nullable = false, length = 45)
    protected String lastName;

    @Column(name = "phone_number", nullable = false, length = 15)
    protected String phoneNumber;

    @Column(name = "address_line_1", nullable = false, length = 64)
    protected String addressLine1;

    @Column(name = "address_line_2", length = 64)
    protected String addressLine2;

    @Column(nullable = false, length = 45)
    protected String city;

    @Column(nullable = false, length = 45)
    protected String state;

    @Column(name = "postal_code", nullable = false, length = 10)
    protected String postalCode;

    @ManyToOne
    @JoinColumn(name = "country_id")
    protected Country country;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(name = "default_address")
    private boolean defaultForShipping;

    @Override
    public String toString() {
        return "Address{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", addressLine1='" + addressLine1 + '\'' +
                ", addressLine2='" + addressLine2 + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", country=" + country +
                ", customer=" + customer +
                ", defaultForShipping=" + defaultForShipping +
                '}';
    }
}
