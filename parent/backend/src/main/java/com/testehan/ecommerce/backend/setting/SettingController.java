package com.testehan.ecommerce.backend.setting;

import com.testehan.ecommerce.backend.util.GeneralSettingBag;
import com.testehan.ecommerce.backend.util.SettingHelper;
import com.testehan.ecommerce.common.constants.AmazonS3Constants;
import com.testehan.ecommerce.common.entity.setting.Setting;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
public class SettingController {

    @Autowired
    private SettingService settingService;

    @Autowired
    private CurrencyRepository currencyRepository;

    @GetMapping("/settings")
    public String listAll(Model model){
        var listSettings = settingService.listAllSettings();
        var listCurrencies = currencyRepository.findAllByOrderByNameAsc();

        model.addAttribute("listCurrencies",listCurrencies);
        model.addAttribute("S3_BASE_URI", AmazonS3Constants.S3_BASE_URI);

        for (Setting setting : listSettings){
            model.addAttribute(setting.getKey(),setting.getValue());
        }

        return "settings/settings";
    }

    @PostMapping("/settings/save_general")
    public String saveGeneralSettings(@RequestParam("fileImage") MultipartFile multipartFile,
                                      HttpServletRequest request, RedirectAttributes ra) throws IOException {


        GeneralSettingBag settingBag = settingService.getGeneralSettings();

        SettingHelper.saveSiteLogo(multipartFile, settingBag);
        SettingHelper.saveCurrencySymbol(request, settingBag,currencyRepository);

        SettingHelper.updateSettingValuesFromForm(request, settingBag.list(), settingService);

        ra.addFlashAttribute("message", "General settings have been saved.");

        return "redirect:/settings";
    }

    @PostMapping("/settings/save_mail_server")
    public String saveMailSettings(HttpServletRequest request, RedirectAttributes ra) throws IOException {

        var mailServerSettings = settingService.getMailServerSettings();
        SettingHelper.updateSettingValuesFromForm(request, mailServerSettings, settingService);

        ra.addFlashAttribute("message", "Mail Server settings have been saved.");

        return "redirect:/settings";
    }

    @PostMapping("/settings/save_mail_templates")
    public String saveTemplateSettings(HttpServletRequest request, RedirectAttributes ra) throws IOException {

        var mailTemplateSettings = settingService.getMailTemplateSettings();
        SettingHelper.updateSettingValuesFromForm(request, mailTemplateSettings, settingService);

        ra.addFlashAttribute("message", "Mail Template settings have been saved.");

        return "redirect:/settings";
    }

    @PostMapping("/settings/save_payment")
    public String savePaymentSettings(HttpServletRequest request, RedirectAttributes ra) throws IOException {

        var paymentSettings = settingService.getPaymentSettings();
        SettingHelper.updateSettingValuesFromForm(request, paymentSettings, settingService);

        ra.addFlashAttribute("message", "Payment settings have been saved.");

        return "redirect:/settings";
    }

}
