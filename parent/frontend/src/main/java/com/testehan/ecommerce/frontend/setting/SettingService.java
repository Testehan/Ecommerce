package com.testehan.ecommerce.frontend.setting;

import com.testehan.ecommerce.common.entity.Setting;
import com.testehan.ecommerce.common.entity.SettingCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SettingService {

    @Autowired
    private SettingRepository settingRepository;

    public List<Setting> getGeneralSettings(){
        var generalSettings = settingRepository.findByTwoCategories(SettingCategory.GENERAL,SettingCategory.CURRENCY);

        return generalSettings;
    }

    public EmailSettingsBag getEmailSettingsBag(){
        var emailSettings = settingRepository.findByTwoCategories(SettingCategory.MAIL_SERVER, SettingCategory.MAIL_TEMPLATES);
        return new EmailSettingsBag(emailSettings);
    }

}
