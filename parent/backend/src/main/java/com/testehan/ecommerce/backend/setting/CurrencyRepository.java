package com.testehan.ecommerce.backend.setting;

import com.testehan.ecommerce.common.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Integer> {

    List<Currency> findAllByOrderByNameAsc();

}
