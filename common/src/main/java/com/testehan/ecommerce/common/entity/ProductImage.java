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
@Table(name="product_images")
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 256, nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    public ProductImage(String name, Product product){
        this.name = name;
        this.product = product;
    }
}
