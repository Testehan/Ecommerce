package com.testehan.ecommerce.common.entity;

import com.testehan.ecommerce.common.entity.product.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cart_items")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantity;

    @Transient
    public float getSubtotal() {            // because in DB i store cents
        return product.getDiscountedPrice() / 100 * quantity;
    }


    @Override
    public String toString() {
        return "CartItem{" +
                "id =" + id +
                ", customer full name =" + customer.getFullName() +
                ", product name =" + product.getName() +
                ", quantity =" + quantity +
                '}';
    }
}
