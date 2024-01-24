package com.testehan.ecommerce.frontend.util;

import com.testehan.ecommerce.common.entity.Customer;
import com.testehan.ecommerce.frontend.setting.EmailSettingsBag;
import com.testehan.ecommerce.frontend.setting.SettingService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

public class CustomerRegisterUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerRegisterUtil.class);
    public static final String USERNAME_VERIFICATION_EMAIL = "[[user]]";
    public static final String URL_VERIFICATION_EMAIL = "[[URL]]";
    public static final String SERVER_VERIFY_EMAIL_URL = "/verify?code=";


    public static String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        // cause we need to remove inside the siteURL, http://localhost/Shop/create_customer, the /create_customer part
        return siteURL.replace(request.getServletPath(), "");
    }

    public static JavaMailSenderImpl initializeMailSender(EmailSettingsBag settings) {

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost(settings.getHost());
        mailSender.setPort(settings.getPort());
        mailSender.setUsername(settings.getUsername());
        mailSender.setPassword(settings.getPassword());
        mailSender.setDefaultEncoding("utf-8");

        Properties mailProperties = new Properties();
        mailProperties.setProperty("mail.smtp.auth", settings.getSmtpAuth());
        mailProperties.setProperty("mail.smtp.starttls.enable", settings.getSmtpSecured());

        mailSender.setJavaMailProperties(mailProperties);

        return mailSender;
    }

    public static void sendVerificationEmail(HttpServletRequest request, Customer customer, SettingService settingService)
            throws UnsupportedEncodingException, MessagingException {

        EmailSettingsBag emailSettings = settingService.getEmailSettingsBag();
        JavaMailSenderImpl mailSender = initializeMailSender(emailSettings);

        String toAddress = customer.getEmail();
        String subject = emailSettings.getCustomerVerifySubject();
        String content = emailSettings.getCustomerVerifyContent();

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");

        helper.setFrom(emailSettings.getFromAddress(), emailSettings.getSenderName());
        helper.setTo(toAddress);
        helper.setSubject(subject);

        content = content.replace(USERNAME_VERIFICATION_EMAIL, customer.getFullName());

        String verifyURL = getSiteURL(request) + SERVER_VERIFY_EMAIL_URL + customer.getVerificationCode();
        content = content.replace(URL_VERIFICATION_EMAIL, verifyURL);
        helper.setText(content, true);

        mailSender.send(message);

        LOGGER.info("Sending email to : " + toAddress);
        LOGGER.info("Containing verification URL : " + verifyURL);

    }
}
