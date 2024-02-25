package com.testehan.ecommerce.frontend.setting;

import com.testehan.ecommerce.common.entity.setting.Setting;
import com.testehan.ecommerce.common.entity.setting.SettingBag;
import com.testehan.ecommerce.common.entity.setting.SettingsNames;

import java.util.List;

public class EmailSettingsBag extends SettingBag {
    public EmailSettingsBag(List<Setting> settingList) {
        super(settingList);
    }

    public String getHost() {
        return super.getValue(SettingsNames.MAIL_HOST.name());
    }

    public int getPort() {
        return Integer.parseInt(super.getValue(SettingsNames.MAIL_PORT.name()));
    }

    public String getUsername() {
        return super.getValue(SettingsNames.MAIL_USERNAME.name());
    }

    public String getPassword() {
        return super.getValue(SettingsNames.MAIL_PASSWORD.name());
    }

    public String getSmtpAuth() {
        return super.getValue(SettingsNames.SMTP_AUTH.name());
    }

    public String getSmtpSecured() {
        return super.getValue(SettingsNames.SMTP_SECURED.name());
    }

    public String getFromAddress() {
        return super.getValue(SettingsNames.MAIL_FROM.name());
    }

    public String getSenderName() {
        return super.getValue(SettingsNames.MAIL_SENDER_NAME.name());
    }

    public String getCustomerVerifySubject() {
        return super.getValue(SettingsNames.CUSTOMER_VERIFY_SUBJECT.name());
    }

    public String getCustomerVerifyContent() {
        return super.getValue(SettingsNames.CUSTOMER_VERIFY_CONTENT.name());
    }

    public String getOrderConfirmationSubject() {
        return super.getValue(SettingsNames.ORDER_CONFIRMATION_SUBJECT.name());
    }

    public String getOrderConfirmationContent() {
        return super.getValue(SettingsNames.ORDER_CONFIRMATION_CONTENT.name());
    }
}
