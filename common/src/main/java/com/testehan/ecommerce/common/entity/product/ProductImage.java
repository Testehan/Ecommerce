package com.testehan.ecommerce.common.entity.product;

import com.testehan.ecommerce.common.constants.AmazonS3Constants;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

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

    @Transient
    public String getImagePath() {
        return AmazonS3Constants.S3_BASE_URI + "/product-images/" + this.product.getId() + "/extras/" + this.name;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductImage that)) return false;
        return Objects.equals(getName(), that.getName()) && Objects.equals(getProduct(), that.getProduct());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getProduct());
    }
}
