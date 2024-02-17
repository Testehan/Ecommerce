package com.testehan.ecommerce.backend.setting;

import com.testehan.ecommerce.common.entity.setting.Setting;
import com.testehan.ecommerce.common.entity.setting.SettingCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SettingRepository extends JpaRepository<Setting, String> {

    List<Setting> findByCategory(SettingCategory category);

}
