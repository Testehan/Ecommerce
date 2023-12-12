package com.testehan.ecommerce.common.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

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

    @Column(name="main_image", nullable = false)
    private String mainImage;

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

    @OneToMany(mappedBy = "product", cascade=CascadeType.ALL, orphanRemoval=true)
    private Set<ProductImage> images = new HashSet<>();

    @OneToMany(mappedBy = "product", cascade=CascadeType.ALL, orphanRemoval=true)
    private List<ProductDetail> details = new ArrayList<>();

    public void addExtraImage(String imageName){
        this.images.add(new ProductImage(imageName,this));
    }

    public void addNewProductDetail(String name, String value){
        this.details.add(new ProductDetail(name,value,this));
    }

    public void updateExistingProductDetail(Integer id, String name, String value) {
        this.details.add(new ProductDetail(id,name,value, this));
    }

    public boolean containsImageName(String imageName) {
        return this.images.contains(new ProductImage(imageName,this));
    }

    @Transient
    public String getMainImagePath() {
        if (this.id == null || this.mainImage == null){
            return "/images/image-thumbnail.png";
        } else {
            return "/product-images/" + this.id + "/" + this.mainImage;
        }
    }

    @Transient
    public String getProductShortName(){
        if (this.name.length()>70) {
            return this.name.substring(0,70).concat("...");
        }
        else {
            return this.name;
        }
    }

    @Transient
    public float getDiscountedPrice(){
        if (discountPercent > 0){
            return price * ((float) (100 - discountPercent) /100);
        } else {
            return price;
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product product)) return false;
        return Objects.equals(getName(), product.getName()) && Objects.equals(getAlias(), product.getAlias());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getAlias());
    }



}
