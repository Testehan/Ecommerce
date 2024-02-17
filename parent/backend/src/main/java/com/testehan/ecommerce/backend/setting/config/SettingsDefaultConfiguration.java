package com.testehan.ecommerce.backend.setting.config;

import com.testehan.ecommerce.backend.setting.SettingRepository;
import com.testehan.ecommerce.common.entity.setting.Setting;
import com.testehan.ecommerce.common.entity.setting.SettingCategory;
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
                Setting symbolPosition = new Setting("CURRENCY_SYMBOL_POSITION", "Before price", SettingCategory.CURRENCY);
                Setting decimalPointType = new Setting("DECIMAL_POINT_TYPE", "POINT", SettingCategory.CURRENCY);
                Setting decimalDigits = new Setting("DECIMAL_DIGITS", "2", SettingCategory.CURRENCY);
                Setting thousandsPointType = new Setting("THOUSANDS_POINT_TYPE", "COMMA", SettingCategory.CURRENCY);

                settingRepository.saveAll(List.of(currencyId, symbol, symbolPosition, decimalPointType,
                        decimalDigits, thousandsPointType));

                LOGGER.info("Mail settings will be persisted in the database !");
                Setting mailHost = new Setting("MAIL_HOST", "smtp.gmail.com", SettingCategory.MAIL_SERVER);
                Setting mailPort = new Setting("MAIL_PORT", "587", SettingCategory.MAIL_SERVER);
                // this needs to be your gmail address
                Setting mailUsername = new Setting("MAIL_USERNAME", "username", SettingCategory.MAIL_SERVER);
                // this needs to be your gmail password generated as described here: https://www.jotform.com/help/392-how-to-use-your-gmail-account-as-your-email-sender-via-smtp/
                Setting mailPassword = new Setting("MAIL_PASSWORD", "password", SettingCategory.MAIL_SERVER);
                Setting mailFrom = new Setting("MAIL_FROM", "shop@gmail.com", SettingCategory.MAIL_SERVER);
                Setting mailSmtpAuth = new Setting("SMTP_AUTH", "true", SettingCategory.MAIL_SERVER);
                Setting mailSmtpSecured = new Setting("SMTP_SECURED", "true", SettingCategory.MAIL_SERVER);
                Setting mailSenderName = new Setting("MAIL_SENDER_NAME", "Shop Team", SettingCategory.MAIL_SERVER);
                Setting mailCustomerVerifySubject = new Setting("CUSTOMER_VERIFY_SUBJECT", "Shop Verify your email", SettingCategory.MAIL_TEMPLATES);
                // this needs to contain variables [[user]] and [[URL]]
                Setting mailCustomerVerifyContent = new Setting("CUSTOMER_VERIFY_CONTENT", "Dear [[user]],<div><br></div><div>Click on the link below to verify your Shop account:</div><a href=\"[[URL]]\" target=\"_self\">Verify</a><div><a href=\"[[url]]\" target=\"_self\"></a></div><div><br></div><div><br></div><div>Have a nice day!</div><div><br></div>", SettingCategory.MAIL_TEMPLATES);
                Setting mailOrderConfirmationSubject = new Setting("ORDER_CONFIRMATION_SUBJECT", "Your Shop order is confirmed", SettingCategory.MAIL_TEMPLATES);
                Setting mailOrderConfirmationContent = new Setting("ORDER_CONFIRMATION_CONTENT", "Dear user, we herby inform you that we recieved your order and will deliver it ASAP.", SettingCategory.MAIL_TEMPLATES);

                settingRepository.saveAll(List.of(mailHost, mailPort, mailUsername, mailPassword, mailFrom, mailSmtpAuth,
                        mailSmtpSecured, mailSenderName, mailCustomerVerifySubject, mailCustomerVerifyContent,
                        mailOrderConfirmationSubject, mailOrderConfirmationContent));

            } else {
                LOGGER.info("Default settings are already present in the database !");
            }

        };
    }
}
