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

        var address = firstName;

        if (lastName != null && !lastName.isEmpty()){
            address += " " + lastName;
        }

        if (!addressLine1.isEmpty()){
            address += ", " + addressLine1;
        }

        if (addressLine2 != null && !addressLine2.isEmpty()){
            address += ", " + addressLine2;
        }

        if (!city.isEmpty()){
            address += ", " + city;
        }

        if (state != null && !state.isEmpty()){
            address += ", " + state;
        }

        address += ", " + country.getName();

        if (!postalCode.isEmpty()){
            address += ". Postal Code: " + postalCode;
        }
        if (!phoneNumber.isEmpty()){
            address += ". Phone Number: " + phoneNumber;
        }

        return address;
    }
}
