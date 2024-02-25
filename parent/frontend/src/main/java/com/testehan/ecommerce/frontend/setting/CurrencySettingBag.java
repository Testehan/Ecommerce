package com.testehan.ecommerce.frontend.setting;


import com.testehan.ecommerce.common.entity.setting.Setting;
import com.testehan.ecommerce.common.entity.setting.SettingBag;
import com.testehan.ecommerce.common.entity.setting.SettingsNames;

import java.util.List;

public class CurrencySettingBag extends SettingBag {

    public CurrencySettingBag(List<Setting> listSettings) {
        super(listSettings);
    }

    public String getSymbol() {
        return super.getValue(SettingsNames.CURRENCY_SYMBOL.name());
    }

    public String getSymbolPosition() {
        return super.getValue(SettingsNames.CURRENCY_SYMBOL_POSITION.name());
    }

    public String getDecimalPointType() {
        return super.getValue(SettingsNames.DECIMAL_POINT_TYPE.name());
    }

    public String getThousandPointType() {
        return super.getValue(SettingsNames.THOUSANDS_POINT_TYPE.name());
    }

    public int getDecimalDigits() {
        return Integer.parseInt(super.getValue(SettingsNames.DECIMAL_DIGITS.name()));
    }
}
