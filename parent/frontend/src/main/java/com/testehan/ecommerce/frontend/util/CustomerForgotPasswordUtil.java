package com.testehan.ecommerce.frontend.util;

import com.testehan.ecommerce.frontend.setting.EmailSettingsBag;
import com.testehan.ecommerce.frontend.setting.SettingService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.io.UnsupportedEncodingException;

public class CustomerForgotPasswordUtil {

    public static void sendEmail(String changePasswordLink, String emailDestination, SettingService settingService)
            throws UnsupportedEncodingException, MessagingException {

        EmailSettingsBag emailSettings = settingService.getEmailSettingsBag();
        JavaMailSenderImpl mailSender = CustomerRegisterUtil.initializeMailSender(emailSettings);

        String subject = "Shop password reset link";
        String content = "<p>Hello,</p>"
                + "<p>You have requested to reset your password.</p>"
                + "Click the link below to change your password:</p>"
                + "<p><a href=\"" + changePasswordLink + "\">Change my password</a></p>"
                + "<br>"
                + "<p>Ignore this email if you do remember your password, "
                + "or you have not made the request.</p>";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");

        helper.setFrom(emailSettings.getFromAddress(), emailSettings.getSenderName());
        helper.setTo(emailDestination);
        helper.setSubject(subject);
        helper.setText(content, true);


        mailSender.send(message);
    }
}
