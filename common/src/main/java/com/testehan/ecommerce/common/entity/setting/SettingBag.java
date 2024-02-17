package com.testehan.ecommerce.common.entity.setting;

import java.util.List;

public class SettingBag {

    private List<Setting> settingList;

    public SettingBag(List<Setting> settingList) {
        this.settingList = settingList;
    }

    public Setting get(String key){
        var setting = new Setting();
        setting.setKey(key);

        var index = settingList.indexOf(setting);

        if (index >=0 ) {
            return settingList.get(index);
        }
        else {
            return null;
        }
    }

    public String getValue(String key){
        var setting = get(key);
        if (setting != null){
            return setting.getValue();
        } else {
            return null;
        }
    }

    public void update(String key, String value) {
        Setting setting = get(key);
        if (setting != null && value != null) {
            setting.setValue(value);
        }
    }

    public List<Setting> list() {
        return settingList;
    }
}
