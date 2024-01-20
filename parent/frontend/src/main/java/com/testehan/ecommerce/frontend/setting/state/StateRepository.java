package com.testehan.ecommerce.frontend.setting.state;

import com.testehan.ecommerce.common.entity.Country;
import com.testehan.ecommerce.common.entity.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StateRepository extends JpaRepository<State, Integer> {

    List<State> findByCountryOrderByNameAsc(Country country);

    @Query("SELECT s FROM State s LEFT JOIN s.country ON s.country.id = s.id WHERE s.name = :name")
    public State findByName(String name);

}
