package com.testehan.ecommerce.frontend.setting;

import com.testehan.ecommerce.common.constants.AmazonS3Constants;
import com.testehan.ecommerce.common.entity.setting.Setting;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

// the purpose of this filter is to add the settings to the model of each response
@Component
public class SettingFilter implements Filter {

    @Autowired
    private SettingService settingService;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        var url = httpServletRequest.getRequestURL().toString();

        if (url.endsWith(".css") || url.endsWith(".js") || url.endsWith(".png") || url.endsWith(".jpg") || url.endsWith(".jpeg")) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        var settings = settingService.getGeneralSettings();
        for (Setting setting: settings) {
            servletRequest.setAttribute(setting.getKey(),setting.getValue());
        }

        servletRequest.setAttribute("S3_BASE_URI", AmazonS3Constants.S3_BASE_URI);
        filterChain.doFilter(servletRequest,servletResponse);
    }
}
