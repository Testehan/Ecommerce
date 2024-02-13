package com.testehan.ecommerce.backend.shipping;

import com.testehan.ecommerce.common.entity.ShippingRate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ShippingRateRepository extends JpaRepository<ShippingRate, Integer> {
    @Query("UPDATE ShippingRate sr SET sr.codSupported = :isCashOnDeliveryAllowed WHERE sr.id = :id")
    @Modifying(clearAutomatically = true)
    void updateCashOnDeliveryStatus(Integer id, boolean isCashOnDeliveryAllowed);

    @Query("SELECT sr FROM ShippingRate sr WHERE sr.country.name LIKE %?1% OR sr.state LIKE %?1% ")
    Page<ShippingRate> findAllByKeyword(String keyword, Pageable pageable);

    @Query("SELECT sr FROM ShippingRate sr WHERE sr.country.id = ?1 AND sr.state = ?2")
    ShippingRate findByCountryAndState(Integer countryId, String state);

    Long countById(Integer id);
}
