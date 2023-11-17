package com.testehan.ecommerce.backend.user;

import com.testehan.ecommerce.common.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    User getUserByEmail(@Param("email") String email);

    Long countById(Integer id);

    @Query("UPDATE User u SET u.enabled = :enabled WHERE u.id = :id")
    // @Modifying is needed so that we can execute not only SELECT queries, but also INSERT, UPDATE, DELETE, and even DDL queries
    // clearAutomatically - Defines whether we should clear the underlying persistence context after executing the modifying query.
    @Modifying(clearAutomatically = true)
    void updateEnabledStatus(Integer id, boolean enabled);


//    @Query("SELECT u FROM User u WHERE u.firstName like %?1% OR u.lastName like %?1% OR u.email like %?1%")
        // the purpose of the concatenation from below is so that if someone searches for a keyword like "Dan T" that
        // represents the full name of a user, the result will be found
    @Query("SELECT u FROM User u WHERE CONCAT(u.id, ' ' , u.firstName, ' ', u.lastName, ' ', u.email,' ') like %?1%")
    Page<User> findAllByKeyword(String keyword, Pageable pageable);
}
