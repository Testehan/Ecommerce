package com.testehan.ecommerce.frontend.product;

import com.testehan.ecommerce.common.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    @Query("SELECT p from Product p WHERE p.enabled = true AND (p.category.id = ?1 OR p.category.allParentIds LIKE %?2% )"
    + " ORDER BY p.name ASC")
    Page<Product> listByCategory(Integer categoryId, String categoryIdMatch, Pageable pageable);

    // this will generate automatically a query behind the scenes, as "findBy.." is a spring data convention that
    // searches for the entity based on the mentioned field
    Product findByAlias(String alias);
}
