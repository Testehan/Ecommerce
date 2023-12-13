package com.testehan.ecommerce.backend.setting;

import com.testehan.ecommerce.backend.util.GeneralSettingBag;
import com.testehan.ecommerce.common.entity.Setting;
import com.testehan.ecommerce.common.entity.SettingCategory;
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


    public void saveAll(Iterable<Setting> settings) {
        settingRepository.saveAll(settings);
    }
}
