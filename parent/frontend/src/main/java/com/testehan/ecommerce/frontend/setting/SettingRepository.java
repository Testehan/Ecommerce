package com.testehan.ecommerce.frontend.setting;

import com.testehan.ecommerce.common.entity.Setting;
import com.testehan.ecommerce.common.entity.SettingCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SettingRepository extends JpaRepository<Setting, String> {

    List<Setting> findByCategory(SettingCategory category);

    @Query("SELECT s from Setting s WHERE s.category = ?1 OR s.category = ?2 ")
    List<Setting> findByTwoCategories(SettingCategory categoryOne,SettingCategory categoryTwo);

}
