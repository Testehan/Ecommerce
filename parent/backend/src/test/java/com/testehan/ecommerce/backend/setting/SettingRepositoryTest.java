package com.testehan.ecommerce.backend.setting;

import com.testehan.ecommerce.common.entity.Setting;
import com.testehan.ecommerce.common.entity.SettingCategory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
// obviously the annotation from below means that the docker container needs to run, since it doesn't use a in memory DB, but the actual DB
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class SettingRepositoryTest {

    @Autowired
    private SettingRepository settingRepository;

    @Test
    public void whenPersistingNewSetting_settingIsPersistedInDB(){
        var setting = new Setting("SITE_NAME","Shop", SettingCategory.GENERAL);
        var setting2 = new Setting("SITE_LOGO","logo.png", SettingCategory.GENERAL);
        var setting3 = new Setting("COPYRIGHT","Shop - Copyright © Shopp", SettingCategory.GENERAL);

        var savedSetting = settingRepository.save(setting);
        settingRepository.saveAll(List.of(setting2,setting3));

        assertThat(savedSetting.getKey()).isEqualTo("SITE_NAME");
        assertThat(savedSetting.getValue()).isEqualTo("Shop");
        assertThat(savedSetting.getCategory()).isEqualTo(SettingCategory.GENERAL);
    }
}
