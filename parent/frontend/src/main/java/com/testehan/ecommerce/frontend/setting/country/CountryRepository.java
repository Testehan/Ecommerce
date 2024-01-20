package com.testehan.ecommerce.frontend.setting.country;

import com.testehan.ecommerce.common.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CountryRepository extends JpaRepository<Country, Integer> {

    List<Country> findAllByOrderByNameAsc();

}
