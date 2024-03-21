package com.testehan.ecommerce.backend.util;

import com.testehan.ecommerce.backend.setting.CurrencyRepository;
import com.testehan.ecommerce.backend.setting.SettingService;
import com.testehan.ecommerce.common.entity.Currency;
import com.testehan.ecommerce.common.entity.setting.Setting;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class SettingHelper {

    public static void saveSiteLogo(MultipartFile multipartFile, GeneralSettingBag settingBag) throws IOException {
        if (!multipartFile.isEmpty()) {
            var fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            var value = "/site-logo/" + fileName;
            settingBag.updateSiteLogo(value);

            // before S3 migration
//			String uploadDir = "site-logo/";
//			FileUploadUtil.deletePreviousFiles(uploadDir);
//			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);

            var uploadDir = "site-logo";
            AmazonS3Util.removeFolder(uploadDir);
            AmazonS3Util.uploadFile(uploadDir, fileName, multipartFile.getInputStream());
        }
    }

    public static void saveCurrencySymbol(HttpServletRequest request, GeneralSettingBag settingBag, CurrencyRepository currencyRepo) {
        Integer currencyId = Integer.parseInt(request.getParameter("CURRENCY_ID"));
        Optional<Currency> findByIdResult = currencyRepo.findById(currencyId);

        if (findByIdResult.isPresent()) {
            Currency currency = findByIdResult.get();
            settingBag.updateCurrencySymbol(currency.getSymbol());
        }
    }

    public static void updateSettingValuesFromForm(HttpServletRequest request, List<Setting> listSettings, SettingService service) {
        for (Setting setting : listSettings) {
            String value = request.getParameter(setting.getKey());
            if (value != null) {
                setting.setValue(value);
            }
        }

        service.saveAll(listSettings);
    }
}