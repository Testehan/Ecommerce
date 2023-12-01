package com.testehan.ecommerce.common.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 256, nullable = false, unique = true)
    private String name;

    @Column(length = 256, nullable = false, unique = true)
    private String alias;           // will be used in the URL coresponding to the product

    @Column(name="short_description", length = 512, nullable = false)
    private String shortDescription;

    @Column(name="full_description", length = 4096, nullable = false)
    private String fullDescription;

    @Column(name="created_time")
    private Date createdTime;

    @Column(name="updated_time")
    private Date updatedTime;

    private boolean enabled;

    @Column(name="in_stock")
    private boolean inStock;

    private long cost;              // float is a bad idea for money
    private long price;             // float is a bad idea for money

    @Column(name="discount_percent")
    private byte discountPercent;      // maybe float could be used here, but discounts are usually integer values

    private int length;             // again, float complictes things, this is in mm
    private int width;
    private int height;
    private int weight;             // this represents grams

    @ManyToOne
    @JoinColumn(name="category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name="brand_id")
    private Brand brand;

}
