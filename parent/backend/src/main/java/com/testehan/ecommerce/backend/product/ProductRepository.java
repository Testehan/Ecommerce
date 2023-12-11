package com.testehan.ecommerce.backend.product;

import com.testehan.ecommerce.common.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query("SELECT p FROM Product p WHERE p.name LIKE %?1% "+
            "OR p.shortDescription LIKE %?1% "+
            "OR p.fullDescription LIKE %?1% "+
            "OR p.brand.name LIKE %?1% " +
            "OR p.category.name LIKE %?1% ")
    Page<Product> findAllByKeyword(String keyword, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.category.id = ?1 OR p.category.allParentIds LIKE  %?2%")
    Page<Product> findAllInCategory(Integer categoryId, String categoryIdMatch, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE (p.category.id = ?1 OR p.category.allParentIds LIKE  %?2% ) AND "+
            "(p.name LIKE %?3% "+
            "OR p.shortDescription LIKE %?3% "+
            "OR p.fullDescription LIKE %?3% "+
            "OR p.brand.name LIKE %?3% " +
            "OR p.category.name LIKE %?3% )")
    Page<Product> findAllInCategoryWithKeyword(Integer categoryId, String categoryIdMatch, String keyword, Pageable pageable);
}
