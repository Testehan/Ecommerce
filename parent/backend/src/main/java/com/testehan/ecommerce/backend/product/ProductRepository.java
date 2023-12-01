package com.testehan.ecommerce.backend.product;

import com.testehan.ecommerce.common.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    Long countById(Integer id);

    Product getProductByName(String name);

    @Query("UPDATE Product p SET p.enabled = :enabled WHERE p.id = :id")
    @Modifying(clearAutomatically = true)
    void updateEnabledStatus(Integer id, boolean enabled);
}
