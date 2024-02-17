package com.testehan.ecommerce.frontend.setting;

import com.testehan.ecommerce.common.entity.setting.Setting;
import com.testehan.ecommerce.common.entity.setting.SettingCategory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
// obviously the annotation from below means that the docker container needs to run, since it doesn't use a in memory DB, but the actual DB
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@Rollback(false)
public class SettingRepositoryTest {


    @Autowired
    private SettingRepository settingRepository;

    @Test
    public void whenPersistingNewSetting_settingIsPersistedInDB(){
        var settingGeneral = new Setting("SITE_NAME_DUMMY","Shop", SettingCategory.GENERAL);
        var settingCurrency = new Setting("CURRENCY_ID_DUMMY", "1", SettingCategory.CURRENCY);

        settingRepository.saveAll(List.of(settingGeneral,settingCurrency));

        var generalAndCurrencySettings = settingRepository.findByTwoCategories(SettingCategory.GENERAL,SettingCategory.CURRENCY);

        assertThat(generalAndCurrencySettings.contains(settingGeneral)).isTrue();
        assertThat(generalAndCurrencySettings.contains(settingCurrency)).isTrue();

    }

}
