package com.testehan.ecommerce.frontend.setting;

import com.testehan.ecommerce.common.entity.setting.Setting;
import com.testehan.ecommerce.common.entity.setting.SettingBag;
import com.testehan.ecommerce.common.entity.setting.SettingsNames;

import java.util.List;

public class PaymentSettingBag extends SettingBag {

    public PaymentSettingBag(List<Setting> listSettings) {
        super(listSettings);
    }

    public String getURL() {
        return super.getValue(SettingsNames.PAYPAL_API_BASE_URL.name());
    }

    public String getClientID() {
        return super.getValue(SettingsNames.PAYPAL_API_CLIENT_ID.name());
    }

    public String getClientSecret() {
        return super.getValue(SettingsNames.PAYPAL_API_CLIENT_SECRET.name());
    }
}