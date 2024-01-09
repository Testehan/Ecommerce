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
public class State {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 50, nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name="country_id")
    private Country country;

    public State(String name, Country country) {
        this.name = name;
        this.country = country;
    }

    @Override
    public String toString() {
        return "State{" +
                "name='" + name + '\'' +
                ", country=" + country.getName() +
                '}';
    }
}
