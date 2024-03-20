package com.testehan.ecommerce.common.entity;

import com.testehan.ecommerce.common.constants.AmazonS3Constants;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Brand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 128, nullable = false, unique = true)
    private String name;

    @Column(length = 1000 , nullable = false)
    private String logo;

    @ManyToMany
    @JoinTable(name = "brands_categories",
               joinColumns = @JoinColumn(name="brand_id", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name="category_id", referencedColumnName = "id"))
    private Set<Category> categories = new HashSet<>();

    public Brand(String name, String logo, Set<Category> categories) {
        this.name = name;
        this.logo = logo;
        this.categories = categories;
    }

    public Brand(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    @Transient
    public String getLogoPath() {
        if (this.id == null  ){
            return "/images/default-category.png";
        } else {
            return AmazonS3Constants.S3_BASE_URI + "/brand-logos/" + this.id + "/" + this.logo;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Brand brand)) return false;
        return Objects.equals(getName(), brand.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }

    @Override
    public String toString() {
        return "Brand{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", logo='" + logo + '\'' +
                '}';
    }
}
