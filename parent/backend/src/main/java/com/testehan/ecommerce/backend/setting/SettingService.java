package com.testehan.ecommerce.backend.setting;

import com.testehan.ecommerce.backend.util.GeneralSettingBag;
import com.testehan.ecommerce.common.entity.setting.Setting;
import com.testehan.ecommerce.common.entity.setting.SettingCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SettingService {

    @Autowired
    private SettingRepository settingRepository;

    public List<Setting> listAllSettings(){
        return settingRepository.findAll();
    }

    public GeneralSettingBag getGeneralSettings(){
        var allSettings = new ArrayList<Setting>();
        var generalSettings = settingRepository.findByCategory(SettingCategory.GENERAL);
        var currencySettings = settingRepository.findByCategory(SettingCategory.CURRENCY);

        allSettings.addAll(generalSettings);
        allSettings.addAll(currencySettings);

        return new GeneralSettingBag(allSettings);
    }

    public List<Setting> getMailServerSettings() {
        var mailServerSettings = settingRepository.findByCategory(SettingCategory.MAIL_SERVER);

        return mailServerSettings;
    }

    public List<Setting> getMailTemplateSettings() {
        var mailTemplateSettings = settingRepository.findByCategory(SettingCategory.MAIL_TEMPLATES);

        return mailTemplateSettings;
    }

    public List<Setting> getCurrencySettings() {
        var currencySettings = settingRepository.findByCategory(SettingCategory.CURRENCY);

        return currencySettings;
    }


    public void saveAll(Iterable<Setting> settings) {
        settingRepository.saveAll(settings);
    }
}
