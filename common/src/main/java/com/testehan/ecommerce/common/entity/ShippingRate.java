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
public class ShippingRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer rate;       // shipping rate/cost
    private Integer days;       // shipping time

    @Column(name = "cod_supported")     // cash on delivery
    private boolean codSupported;

    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;

    @Column(nullable = false, length = 45)
    private String state;


    @Override
    public String toString() {
        return "ShippingRate [id=" + id + ", rate=" + rate + ", days=" + days + ", codSupported=" + codSupported
                + ", country=" + country.getName() + ", state=" + state + "]";
    }
}
