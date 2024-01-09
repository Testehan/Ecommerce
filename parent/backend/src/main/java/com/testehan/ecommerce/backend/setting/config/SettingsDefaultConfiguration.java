package com.testehan.ecommerce.backend.setting.config;

import com.testehan.ecommerce.backend.setting.SettingRepository;
import com.testehan.ecommerce.common.entity.Setting;
import com.testehan.ecommerce.common.entity.SettingCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SettingsDefaultConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsDefaultConfiguration.class);

    @Autowired
    private SettingRepository settingRepository;


    @Bean
    CommandLineRunner createDefaultSettingsIfNotPresent(SettingRepository settingRepository) {
        return args -> {

            if (settingRepository.findAll().size()==0) {
                LOGGER.info("Default settings will be persisted in the database !");

                var setting = new Setting("SITE_NAME", "Shop", SettingCategory.GENERAL);
                var setting2 = new Setting("SITE_LOGO", "/images/logo.png", SettingCategory.GENERAL);
                var setting3 = new Setting("COPYRIGHT", "Shop - Copyright © Shopp", SettingCategory.GENERAL);

                settingRepository.saveAll(List.of(setting, setting2, setting3));

                Setting currencyId = new Setting("CURRENCY_ID", "1", SettingCategory.CURRENCY);
                Setting symbol = new Setting("CURRENCY_SYMBOL", "$", SettingCategory.CURRENCY);
                Setting symbolPosition = new Setting("CURRENCY_SYMBOL_POSITION", "before", SettingCategory.CURRENCY);
                Setting decimalPointType = new Setting("DECIMAL_POINT_TYPE", "POINT", SettingCategory.CURRENCY);
                Setting decimalDigits = new Setting("DECIMAL_DIGITS", "2", SettingCategory.CURRENCY);
                Setting thousandsPointType = new Setting("THOUSANDS_POINT_TYPE", "COMMA", SettingCategory.CURRENCY);

                settingRepository.saveAll(List.of(currencyId, symbol, symbolPosition, decimalPointType,
                        decimalDigits, thousandsPointType));
            } else {
                LOGGER.info("Default settings are already present in the database !");
            }

        };
    }
}
