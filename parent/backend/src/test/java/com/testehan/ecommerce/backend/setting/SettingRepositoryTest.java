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
// TODO When deploying the app, the settings from the 2 tests from below should be added as defaults in some
    // kind of initializer ...for example see DatabaseUpdates and how it is used
    @Test
    public void whenPersistingNewSetting_settingIsPersistedInDB(){
        var setting = new Setting("SITE_NAME","Shop", SettingCategory.GENERAL);
        var setting2 = new Setting("SITE_LOGO","/images/logo.png", SettingCategory.GENERAL);
        var setting3 = new Setting("COPYRIGHT","Shop - Copyright © Shopp", SettingCategory.GENERAL);

        var savedSetting = settingRepository.save(setting);
        settingRepository.saveAll(List.of(setting2,setting3));

        assertThat(savedSetting.getKey()).isEqualTo("SITE_NAME");
        assertThat(savedSetting.getValue()).isEqualTo("Shop");
        assertThat(savedSetting.getCategory()).isEqualTo(SettingCategory.GENERAL);
    }

    @Test
    public void testCreateCurrencySettings() {
        Setting currencyId = new Setting("CURRENCY_ID", "1", SettingCategory.CURRENCY);
        Setting symbol = new Setting("CURRENCY_SYMBOL", "$", SettingCategory.CURRENCY);
        Setting symbolPosition = new Setting("CURRENCY_SYMBOL_POSITION", "before", SettingCategory.CURRENCY);
        Setting decimalPointType = new Setting("DECIMAL_POINT_TYPE", "POINT", SettingCategory.CURRENCY);
        Setting decimalDigits = new Setting("DECIMAL_DIGITS", "2", SettingCategory.CURRENCY);
        Setting thousandsPointType = new Setting("THOUSANDS_POINT_TYPE", "COMMA", SettingCategory.CURRENCY);

        settingRepository.saveAll(List.of(currencyId, symbol, symbolPosition, decimalPointType,
                decimalDigits, thousandsPointType));

    }

    @Test
    public void testListSettingsByCategory() {
        List<Setting> settings = settingRepository.findByCategory(SettingCategory.GENERAL);

        settings.forEach(System.out::println);
    }
}
