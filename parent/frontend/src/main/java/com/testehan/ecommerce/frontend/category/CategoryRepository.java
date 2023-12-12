package com.testehan.ecommerce.frontend.category;

import com.testehan.ecommerce.common.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    @Query("SELECT c FROM Category c WHERE c.enabled=true ORDER BY c.name ASC")
    List<Category> findAllEnabled();
}
