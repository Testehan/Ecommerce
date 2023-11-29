package com.testehan.ecommerce.backend.category;

import com.testehan.ecommerce.common.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    @Query("SELECT c from Category c WHERE c.parent.id IS NULL")
    List<Category> listRootCategories(Sort sort);

    @Query("SELECT c from Category c WHERE c.parent.id IS NULL")
    Page<Category> listRootCategories(Pageable pageable);

    @Query("SELECT c from Category c WHERE c.name like %?1%")
    Page<Category> search(String keyword, Pageable pageable);

    @Query("UPDATE Category c SET c.enabled = :enabled WHERE c.id = :id")
    // @Modifying is needed so that we can execute not only SELECT queries, but also INSERT, UPDATE, DELETE, and even DDL queries
    // clearAutomatically - Defines whether we should clear the underlying persistence context after executing the modifying query.
    @Modifying(clearAutomatically = true)
    void updateEnabledStatus(Integer id, boolean enabled);

    Category findByName(String name);
    Category findByAlias(String alias);
}
