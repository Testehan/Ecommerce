package com.testehan.ecommerce.backend.setting.state;

import com.testehan.ecommerce.common.entity.Country;
import com.testehan.ecommerce.common.entity.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StateRepository extends JpaRepository<State, Integer> {

    List<State> findByCountryOrderByNameAsc(Country country);

}
