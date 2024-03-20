package com.testehan.ecommerce.backend.setting.config;

import com.testehan.ecommerce.backend.setting.SettingRepository;
import com.testehan.ecommerce.common.entity.setting.Setting;
import com.testehan.ecommerce.common.entity.setting.SettingCategory;
import com.testehan.ecommerce.common.entity.setting.SettingsNames;
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

                var setting = new Setting(SettingsNames.SITE_NAME.name(), "Shop", SettingCategory.GENERAL);
                var setting2 = new Setting(SettingsNames.SITE_LOGO.name(), "/site-logo/logo.png", SettingCategory.GENERAL);
                var setting3 = new Setting(SettingsNames.COPYRIGHT.name(), "Shop - Copyright © Shopp", SettingCategory.GENERAL);

                settingRepository.saveAll(List.of(setting, setting2, setting3));

                var currencyId = new Setting(SettingsNames.CURRENCY_ID.name(), "1", SettingCategory.CURRENCY);
                var symbol = new Setting(SettingsNames.CURRENCY_SYMBOL.name(), "$", SettingCategory.CURRENCY);
                var symbolPosition = new Setting(SettingsNames.CURRENCY_SYMBOL_POSITION.name(), "Before price", SettingCategory.CURRENCY);
                var decimalPointType = new Setting(SettingsNames.DECIMAL_POINT_TYPE.name(), "POINT", SettingCategory.CURRENCY);
                var decimalDigits = new Setting(SettingsNames.DECIMAL_DIGITS.name(), "2", SettingCategory.CURRENCY);
                var thousandsPointType = new Setting(SettingsNames.THOUSANDS_POINT_TYPE.name(), "COMMA", SettingCategory.CURRENCY);

                settingRepository.saveAll(List.of(currencyId, symbol, symbolPosition, decimalPointType,
                        decimalDigits, thousandsPointType));

                LOGGER.info("Mail settings will be persisted in the database !");
                var mailHost = new Setting(SettingsNames.MAIL_HOST.name(), "smtp.gmail.com", SettingCategory.MAIL_SERVER);
                var mailPort = new Setting(SettingsNames.MAIL_PORT.name(), "587", SettingCategory.MAIL_SERVER);
                // this needs to be your gmail address
                var mailUsername = new Setting(SettingsNames.MAIL_USERNAME.name(), "username", SettingCategory.MAIL_SERVER);
                // this needs to be your gmail password generated as described here: https://www.jotform.com/help/392-how-to-use-your-gmail-account-as-your-email-sender-via-smtp/
                var mailPassword = new Setting(SettingsNames.MAIL_PASSWORD.name(), "password", SettingCategory.MAIL_SERVER);
                var mailFrom = new Setting(SettingsNames.MAIL_FROM.name(), "shop@gmail.com", SettingCategory.MAIL_SERVER);
                var mailSmtpAuth = new Setting(SettingsNames.SMTP_AUTH.name(), "true", SettingCategory.MAIL_SERVER);
                var mailSmtpSecured = new Setting(SettingsNames.SMTP_SECURED.name(), "true", SettingCategory.MAIL_SERVER);
                var mailSenderName = new Setting(SettingsNames.MAIL_SENDER_NAME.name(), "Shop Team", SettingCategory.MAIL_SERVER);
                var mailCustomerVerifySubject = new Setting(SettingsNames.CUSTOMER_VERIFY_SUBJECT.name(), "Shop Verify your email", SettingCategory.MAIL_TEMPLATES);
                // this needs to contain variables [[user]] and [[URL]]
                var mailCustomerVerifyContent = new Setting(SettingsNames.CUSTOMER_VERIFY_CONTENT.name(), "Dear [[user]],<div><br></div><div>Click on the link below to verify your Shop account:</div><a href=\"[[URL]]\" target=\"_self\">Verify</a><div><a href=\"[[url]]\" target=\"_self\"></a></div><div><br></div><div><br></div><div>Have a nice day!</div><div><br></div>", SettingCategory.MAIL_TEMPLATES);
                var mailOrderConfirmationSubject = new Setting(SettingsNames.ORDER_CONFIRMATION_SUBJECT.name(), "Your Shop order is confirmed", SettingCategory.MAIL_TEMPLATES);
                var mailOrderConfirmationContent = new Setting(SettingsNames.ORDER_CONFIRMATION_CONTENT.name(), "Dear user, we herby inform you that we recieved your order and will deliver it ASAP.", SettingCategory.MAIL_TEMPLATES);

                var paypalApiUrl = new Setting(SettingsNames.PAYPAL_API_BASE_URL.name(), "https://api-m.sandbox.paypal.com", SettingCategory.PAYMENT);
                var paypalClientId = new Setting(SettingsNames.PAYPAL_API_CLIENT_ID.name(), "PAYPAL_API_CLIENT_ID", SettingCategory.PAYMENT);
                var paypalClientSecret = new Setting(SettingsNames.PAYPAL_API_CLIENT_SECRET.name(), "PAYPAL_API_CLIENT_SECRET", SettingCategory.PAYMENT);

                settingRepository.saveAll(List.of(mailHost, mailPort, mailUsername, mailPassword, mailFrom, mailSmtpAuth,
                        mailSmtpSecured, mailSenderName, mailCustomerVerifySubject, mailCustomerVerifyContent,
                        mailOrderConfirmationSubject, mailOrderConfirmationContent, paypalApiUrl, paypalClientId,
                        paypalClientSecret));

            } else {
                LOGGER.info("Default settings are already present in the database !");
            }

        };
    }
}
